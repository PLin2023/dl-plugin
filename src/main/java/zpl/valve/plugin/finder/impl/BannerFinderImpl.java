package zpl.valve.plugin.finder.impl;

import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.finders.Finder;
import zpl.valve.plugin.extension.Banner;
import zpl.valve.plugin.finder.BannerFinder;
import zpl.valve.plugin.vo.BannerVo;

import static org.springframework.data.domain.Sort.Order.asc;
import static run.halo.app.extension.index.query.QueryFactory.*;

@Finder("bannerFinder")
public class BannerFinderImpl implements BannerFinder {
    private final ReactiveExtensionClient client;

    public BannerFinderImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Flux<BannerVo> listAll() {
        return client.listAll(Banner.class, new ListOptions(), defaultLinkSort())
            .map(BannerVo::from);
    }

    static Sort defaultLinkSort() {
        return Sort.by(asc("spec.priority"),
            asc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }
}
