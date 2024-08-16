package zpl.valve.plugin.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.PageRequest;
import run.halo.app.extension.index.query.QueryFactory;
import run.halo.app.extension.index.query.Query;
import run.halo.app.extension.router.selector.FieldSelector;
import zpl.valve.plugin.extension.Product;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductSearchApi {

    private final ReactiveExtensionClient client;

    public ProductSearchApi(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Bean
    public RouterFunction<ServerResponse> productSearchRoute() {
        return route(GET("/apis/console.api.product.plugin.valve.zpl/v1alpha1/products"), this::listProducts);
    }

    private Mono<ServerResponse> listProducts(ServerRequest request) {
        return Mono.just(request)
            .map(this::buildListOptions)
            .flatMap(options -> {
                PageRequestImpl pageRequest = buildPageRequest(request);
                System.out.println("pageRequest: " + options.toString());
                return client.listBy(Product.class, options, pageRequest);
            })
            .flatMap(result -> ServerResponse.ok().bodyValue(result));
    }

    private PageRequestImpl buildPageRequest(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("1"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortParam = request.queryParam("sort").orElse("metadata.creationTimestamp,desc");
        String[] sortParams = sortParam.split(",");
        Sort sort = Sort.by(sortParams[0]);
        if (sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return PageRequestImpl.of(page, size, sort);
    }

    private ListOptions buildListOptions(ServerRequest request) {
        ListOptions options = new ListOptions();
        List<Query> queries = new ArrayList<>();

        // 处理搜索关键词
        request.queryParam("keyword").ifPresent(keyword -> {
            if (StringUtils.hasText(keyword)) {
                Query keywordQuery = QueryFactory.or(
                    QueryFactory.contains("spec.name", keyword),
                    QueryFactory.contains("spec.description", keyword)
                );
                queries.add(keywordQuery);
            }
        });

        // 处理分类过滤
        request.queryParam("category").ifPresent(category -> {
            if (StringUtils.hasText(category)) {
                Query categoryQuery = QueryFactory.equal("spec.categoryMetadataName", category);
                queries.add(categoryQuery);
            }
        });

        // 处理价格范围过滤
        request.queryParam("minPrice").ifPresent(minPrice -> {
            if (StringUtils.hasText(minPrice)) {
                Query minPriceQuery = QueryFactory.greaterThanOrEqual("spec.price", minPrice);
                queries.add(minPriceQuery);
            }
        });

        request.queryParam("maxPrice").ifPresent(maxPrice -> {
            if (StringUtils.hasText(maxPrice)) {
                Query maxPriceQuery = QueryFactory.lessThanOrEqual("spec.price", maxPrice);
                queries.add(maxPriceQuery);
            }
        });

        // 处理创建时间范围过滤
        request.queryParam("createdAfter").ifPresent(createdAfter -> {
            if (StringUtils.hasText(createdAfter)) {
                Query createdAfterQuery = QueryFactory.greaterThanOrEqual("metadata.creationTimestamp", createdAfter);
                queries.add(createdAfterQuery);
            }
        });

        request.queryParam("createdBefore").ifPresent(createdBefore -> {
            if (StringUtils.hasText(createdBefore)) {
                Query createdBeforeQuery = QueryFactory.lessThanOrEqual("metadata.creationTimestamp", createdBefore);
                queries.add(createdBeforeQuery);
            }
        });

        // 组合所有查询条件
        if (!queries.isEmpty()) {
            Query finalQuery = queries.size() == 1 ? queries.get(0) : QueryFactory.and(queries);
            options.setFieldSelector(FieldSelector.of(finalQuery));
        }

        return options;
    }
}