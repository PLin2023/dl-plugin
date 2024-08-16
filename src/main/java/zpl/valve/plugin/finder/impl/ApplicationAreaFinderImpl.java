package zpl.valve.plugin.finder.impl;

import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.finders.Finder;
import zpl.valve.plugin.extension.ApplicationArea;
import zpl.valve.plugin.extension.Banner;
import zpl.valve.plugin.finder.ApplicationAreaFinder;
import zpl.valve.plugin.finder.BannerFinder;
import zpl.valve.plugin.vo.ApplicationAreaVo;
import zpl.valve.plugin.vo.BannerVo;

import static org.springframework.data.domain.Sort.Order.asc;

@Finder("applicationAreaFinder")
public class ApplicationAreaFinderImpl implements ApplicationAreaFinder {
    private final ReactiveExtensionClient client;

    public ApplicationAreaFinderImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Flux<ApplicationAreaVo> listAll() {
        return client.listAll(ApplicationArea.class, new ListOptions(), defaultLinkSort())
            .map(ApplicationAreaVo::from);
    }

    static Sort defaultLinkSort() {
        return Sort.by(asc("spec.priority"),
            asc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }
}
