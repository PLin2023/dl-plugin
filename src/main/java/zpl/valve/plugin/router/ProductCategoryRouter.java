package zpl.valve.plugin.router;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.TemplateNameResolver;
import java.util.HashMap;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ProductCategoryRouter {
    private final TemplateNameResolver templateNameResolver;
    @Bean
    RouterFunction<ServerResponse> productListRouterFunction() {
        return route(GET("/productlist/{id}/{page}"), this::renderProductListPage);
    }

    Mono<ServerResponse> renderProductListPage(ServerRequest request) {
        String id = request.pathVariable("id");
        String page = request.pathVariable("page");
        var model = new HashMap<String, Object>();
        model.put("id", id);
        model.put("page", page);
        return templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "productlist")
            .flatMap(templateName -> ServerResponse.ok().render(templateName, model));
    }
}