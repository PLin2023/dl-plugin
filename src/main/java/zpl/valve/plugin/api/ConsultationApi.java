package zpl.valve.plugin.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.Query;
import run.halo.app.extension.index.query.QueryFactory;
import run.halo.app.extension.router.selector.FieldSelector;
import zpl.valve.plugin.extension.Consultation;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ConsultationApi {

    private final ReactiveExtensionClient client;

    public ConsultationApi(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Bean
    public RouterFunction<ServerResponse> consultationRoutes() {
        return route(GET("/apis/api.consultation.plugin.halo.run/v1alpha1/consultations"),
            this::listConsultations)
            .andRoute(POST("/apis/api.consultation.plugin.halo.run/v1alpha1/consultations"),
                this::createConsultation);
    }

    private Mono<ServerResponse> listConsultations(ServerRequest request) {
        return Mono.just(request)
            .map(this::buildListOptions)
            .flatMap(options -> {
                PageRequestImpl pageRequest = buildPageRequest(request);
                return client.listBy(Consultation.class, options, pageRequest);
            })
            .flatMap(result -> ServerResponse.ok().bodyValue(result));
    }

    private Mono<ServerResponse> createConsultation(ServerRequest request) {
        return request.bodyToMono(Consultation.ConsultationSpec.class)
            .map(spec -> {
                Consultation consultation = new Consultation();
                consultation.setSpec(spec);
                consultation.setMetadata(new Metadata());
                consultation.getMetadata().setGenerateName("consultation-");
                return consultation;
            })
            .flatMap(client::create)
            .flatMap(created -> ServerResponse.ok().bodyValue(created));
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
            if (!keyword.isEmpty()) {
                Query keywordQuery = QueryFactory.or(
                    QueryFactory.contains("spec.name", keyword),
                    QueryFactory.contains("spec.email", keyword),
                    QueryFactory.contains("spec.content", keyword)
                );
                queries.add(keywordQuery);
            }
        });

        // 处理状态
        request.queryParam("status").ifPresent(status -> {
            if (!status.isEmpty()) {
                Query statusQuery =
                    QueryFactory.equal("spec.status", status);
                queries.add(statusQuery);
            }
        });


        // 处理开始日期
        request.queryParam("startDate").ifPresent(startDate -> {
            if (!startDate.isEmpty()) {
                Query startDateQuery =
                    QueryFactory.greaterThanOrEqual("metadata.creationTimestamp", startDate);
                queries.add(startDateQuery);
            }
        });

        // 处理结束日期
        request.queryParam("endDate").ifPresent(endDate -> {
            if (!endDate.isEmpty()) {
                Query endDateQuery =
                    QueryFactory.lessThanOrEqual("metadata.creationTimestamp", endDate);
                queries.add(endDateQuery);
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