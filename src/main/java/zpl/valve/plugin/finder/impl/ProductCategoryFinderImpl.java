package zpl.valve.plugin.finder.impl;

import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.finders.Finder;
import zpl.valve.plugin.extension.Banner;
import zpl.valve.plugin.extension.ProductCategory;
import zpl.valve.plugin.finder.BannerFinder;
import zpl.valve.plugin.finder.ProductCategoryFinder;
import zpl.valve.plugin.vo.BannerVo;
import zpl.valve.plugin.vo.ProductCategoryVo;

import static org.springframework.data.domain.Sort.Order.asc;

@Finder("productCategoryFinder")
public class ProductCategoryFinderImpl implements ProductCategoryFinder {
    private final ReactiveExtensionClient client;

    public ProductCategoryFinderImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Flux<ProductCategoryVo> listAll() {
        return client.listAll(ProductCategory.class, new ListOptions(), defaultLinkSort())
            .map(ProductCategoryVo::from);
    }

    static Sort defaultLinkSort() {
        return Sort.by(asc("spec.priority"),
            asc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }
}
