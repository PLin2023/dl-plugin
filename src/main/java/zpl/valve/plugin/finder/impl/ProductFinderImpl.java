package zpl.valve.plugin.finder.impl;

import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.index.query.EqualQuery;
import run.halo.app.theme.finders.Finder;
import zpl.valve.plugin.extension.Product;
import zpl.valve.plugin.finder.ProductFinder;
import zpl.valve.plugin.vo.ProductVo;

import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Order.asc;

@Finder("productFinder")
public class ProductFinderImpl implements ProductFinder {
    private final ReactiveExtensionClient client;

    public ProductFinderImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Flux<ProductVo> listAll() {
        return client.listAll(Product.class, new ListOptions(), defaultLinkSort())
            .map(ProductVo::from);
    }

    @Override
    public Mono<ListResult<ProductVo>> listByCategory(String categoryMetadataName, Integer page, Integer size) {
        PageRequestImpl pageRequest = new PageRequestImpl(page, size, defaultLinkSort());
        ListOptions.ListOptionsBuilder listOptionsBuilder = new ListOptions.ListOptionsBuilder();
        listOptionsBuilder.andQuery(
            new EqualQuery("spec.categoryMetadataName", categoryMetadataName)
        );
        return client.listBy(Product.class, listOptionsBuilder.build(), pageRequest).map(
            (resultList) -> new ListResult<>(
                resultList.getPage(),
                resultList.getSize(),
                resultList.getTotal(),
                resultList.getItems().stream().map(ProductVo::from).collect(Collectors.toList())
            )
        );
    }

    static Sort defaultLinkSort() {
        return Sort.by(asc("spec.priority"),
            asc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }
}
