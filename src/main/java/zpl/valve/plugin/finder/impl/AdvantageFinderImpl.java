package zpl.valve.plugin.finder.impl;

import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.finders.Finder;
import zpl.valve.plugin.extension.Advantage;
import zpl.valve.plugin.extension.Banner;
import zpl.valve.plugin.finder.AdvantageFinder;
import zpl.valve.plugin.finder.BannerFinder;
import zpl.valve.plugin.vo.AdvantageVo;
import zpl.valve.plugin.vo.BannerVo;

import static org.springframework.data.domain.Sort.Order.asc;

@Finder("advantageFinder")
public class AdvantageFinderImpl implements AdvantageFinder {
    private final ReactiveExtensionClient client;

    public AdvantageFinderImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Flux<AdvantageVo> listAll() {
        return client.listAll(Advantage.class, new ListOptions(), defaultLinkSort())
            .map(AdvantageVo::from);
    }

    static Sort defaultLinkSort() {
        return Sort.by(asc("spec.priority"),
            asc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }
}
