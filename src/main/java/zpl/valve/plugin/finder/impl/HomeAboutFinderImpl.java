package zpl.valve.plugin.finder.impl;

import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.theme.finders.Finder;
import zpl.valve.plugin.extension.HomeAbout;
import zpl.valve.plugin.extension.Timeline;
import zpl.valve.plugin.finder.HomeAboutFinder;
import zpl.valve.plugin.finder.TimelineFinder;
import zpl.valve.plugin.vo.HomeAboutVo;
import zpl.valve.plugin.vo.TimelineVo;

import static org.springframework.data.domain.Sort.Order.asc;

@Finder("homeAboutFinder")
public class HomeAboutFinderImpl implements HomeAboutFinder {
    private final ReactiveExtensionClient client;

    public HomeAboutFinderImpl(ReactiveExtensionClient client) {
        this.client = client;
    }

    @Override
    public Mono<HomeAboutVo> get() {
        return client.listAll(HomeAbout.class, new ListOptions(), defaultLinkSort())
            .map(HomeAboutVo::from)
            .next();
    }

    static Sort defaultLinkSort() {
        return Sort.by(
            asc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }
}