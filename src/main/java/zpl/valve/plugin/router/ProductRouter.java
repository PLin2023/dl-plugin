package zpl.valve.plugin.router;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.QueryFactory;
import run.halo.app.extension.router.selector.FieldSelector;
import run.halo.app.theme.TemplateNameResolver;
import zpl.valve.plugin.extension.Product;
import zpl.valve.plugin.extension.ProductCategory;
import zpl.valve.plugin.vo.ProductCategoryVo;
import zpl.valve.plugin.vo.ProductVo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.data.domain.Sort.Order.asc;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ProductRouter {
    private final TemplateNameResolver templateNameResolver;
    private final ReactiveExtensionClient client;


    @Bean
    RouterFunction<ServerResponse> productRouterFunction() {
        return route(GET("/product/{id}"), this::renderProductPage);
    }

    Mono<ServerResponse> renderProductPage(ServerRequest request) {
        String id = request.pathVariable("id");

        System.out.println("product id: " + id);
        ListOptions options = new ListOptions();
        options.setFieldSelector(FieldSelector.of(QueryFactory.equal("metadata.name", id)));

        Mono<Product> productMono = client.listBy(Product.class, options, new PageRequestImpl(1,1, Sort.unsorted()))
            .flatMap(productList -> Mono.justOrEmpty(productList.getItems().stream().findFirst()))
            .switchIfEmpty(Mono.error(new ProductNotFoundException("Product not found: " + id)));

        Mono<String> templateNameMono = templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "productdetail");

        return productMono.flatMap(product -> {
                ListOptions categoryOptions = new ListOptions();
                categoryOptions.setFieldSelector(FieldSelector.of(QueryFactory.equal("metadata.name", product.getSpec().getCategoryMetadataName())));

                Mono<ProductCategory> currentCategoryMono = client.listBy(ProductCategory.class, categoryOptions, new PageRequestImpl(1,1, Sort.unsorted()))
                    .flatMap(categoryList -> Mono.justOrEmpty(categoryList.getItems().stream().findFirst()))
                    .switchIfEmpty(Mono.error(new RuntimeException("Category not found for product: " + id)));

                Mono<List<ProductCategory>> allCategoriesMono = client.listAll(ProductCategory.class, new ListOptions(), Sort.by(
                    asc("spec.priority"),
                    asc("metadata.creationTimestamp"),
                    asc("metadata.name")
                )).collectList();  // 这里使用 collectList() 将 Flux 转换为 Mono<List<>>
                ListOptions relativeProductOptions =  new ListOptions();
                relativeProductOptions.setFieldSelector(FieldSelector.of(QueryFactory.equal("spec.categoryMetadataName", product.getSpec().getCategoryMetadataName())));
                Mono<List<Product>> relativeProductsMono = client.listBy(Product.class, relativeProductOptions, new PageRequestImpl(1, 6, Sort.by(
                    asc("spec.priority"),
                    asc("metadata.creationTimestamp"),
                    asc("metadata.name")
                ))).map(ListResult::getItems);

                return Mono.zip(Mono.just(product), currentCategoryMono, allCategoriesMono, templateNameMono, relativeProductsMono)
                    .flatMap(tuple -> {
                        Product p = tuple.getT1();
                        ProductCategory currentCategory = tuple.getT2();
                        List<ProductCategory> allCategories = tuple.getT3();
                        String templateName = tuple.getT4();
                        List<Product>  relativeProducts = tuple.getT5();



                        Map<String, Object> model = Map.of(
                            "id", id,
                            "product", p,
                            "currentCategory", ProductCategoryVo.from(currentCategory),
                            "categories", allCategories.stream().map(ProductCategoryVo::from).toList(),
                            "relativeProducts", relativeProducts.stream().map(ProductVo::from).toList()
                        );
                        return ServerResponse.ok().render(templateName, model);
                    });
            }).onErrorResume(ProductNotFoundException.class, e -> ServerResponse.notFound().build())
            .onErrorResume(RuntimeException.class, e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    // 自定义异常类
    private static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }
}