package zpl.valve.plugin.router;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.QueryFactory;
import run.halo.app.extension.router.selector.FieldSelector;
import run.halo.app.theme.TemplateNameResolver;
import zpl.valve.plugin.extension.Product;
import zpl.valve.plugin.extension.ProductCategory;
import zpl.valve.plugin.vo.ProductCategoryVo;
import zpl.valve.plugin.vo.ProductVo;

import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ProductCategoryRouter {
    private final TemplateNameResolver templateNameResolver;
    private final ReactiveExtensionClient client;

    @Bean
    RouterFunction<ServerResponse> productListRouterFunction() {
        return route(GET("/productlist/{id}/{page}"), this::renderProductListPage)
            .andRoute(GET("/productlist/{id}"), this::renderProductListPage); // 为没有page参数的请求添加一个路由
    }

    Mono<ServerResponse> renderProductListPage(ServerRequest request) {
        String id = request.pathVariable("id");

        // 手动检查 page 是否存在并提供默认值
        String page = request.pathVariables().getOrDefault("page", "1");
        int pageNum = Integer.parseInt(page);

        ListOptions options = new ListOptions();
        options.setFieldSelector(
            FieldSelector.of(QueryFactory.equal("spec.categoryMetadataName", id)));

        Mono<Map<String, Object>> productsMono = client.listBy(Product.class, options,
            new PageRequestImpl(pageNum, 1, Sort.by(
                asc("spec.priority"),
                asc("metadata.creationTimestamp"),
                asc("metadata.name")
            ))).map((result) -> {
            int currentPage = pageNum;
            return Map.of(
                "currentPage", currentPage,
                "totalPage", result.getTotalPages(),
                "items", result.getItems().stream().map(ProductVo::from).toList(),
                "hasPrevious", currentPage > 1,
                "hasNext", currentPage < result.getTotalPages()
            );
        });

        Mono<List<ProductCategory>> allCategoriesMono = client.listAll(ProductCategory.class, new ListOptions(), Sort.by(
            asc("spec.priority"),
            asc("metadata.creationTimestamp"),
            asc("metadata.name")
        )).collectList();

        ListOptions categoryOptions = new ListOptions();
        categoryOptions.setFieldSelector(FieldSelector.of(QueryFactory.equal("metadata.name", id)));

        Mono<ProductCategory> currentCategoryMono = client.listBy(ProductCategory.class, categoryOptions, new PageRequestImpl(1,1, Sort.unsorted()))
            .flatMap(categoryList -> Mono.justOrEmpty(categoryList.getItems().stream().findFirst()))
            .switchIfEmpty(Mono.error(new RuntimeException("Category not found for product: " + id)));


        Mono<String> templateNameMono = templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "productlist");

        return Mono.zip(productsMono, allCategoriesMono, currentCategoryMono, templateNameMono)
            .flatMap(tuple -> {
                Map<String, Object> p = tuple.getT1();
                List<ProductCategory> allCategories = tuple.getT2();
                ProductCategory productCategory = tuple.getT3();
                String templateName = tuple.getT4();

                Map<String, Object> model = Map.of(
                    "id", id,
                    "pageUtil", p,
                    "currentCategory", productCategory,
                    "categories", allCategories.stream().map(ProductCategoryVo::from).toList()
                );
                return ServerResponse.ok().render(templateName, model);
            });
    }
}
