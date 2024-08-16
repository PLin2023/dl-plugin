package zpl.valve.plugin.finder.impl;

import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.finders.Finder;
import zpl.valve.plugin.extension.Banner;
import zpl.valve.plugin.extension.Timeline;
import zpl.valve.plugin.finder.BannerFinder;
import zpl.valve.plugin.finder.TimelineFinder;
import zpl.valve.plugin.vo.BannerVo;
import zpl.valve.plugin.vo.TimelineVo;

import static org.springframework.data.domain.Sort.Order.asc;

@Finder("timelineFinder")
public class TimelineFinderImpl implements TimelineFinder {
    private final ReactiveExtensionClient client;

    public TimelineFinderImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Flux<TimelineVo> listAll() {
        return client.listAll(Timeline.class, new ListOptions(), defaultLinkSort())
            .map(TimelineVo::from);
    }

    static Sort defaultLinkSort() {
        return Sort.by(asc("spec.datetime"),
            asc("spec.priority"),
            asc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }
}
