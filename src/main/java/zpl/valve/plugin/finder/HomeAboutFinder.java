package zpl.valve.plugin.finder;

import reactor.core.publisher.Mono;
import zpl.valve.plugin.vo.HomeAboutVo;
import zpl.valve.plugin.vo.TimelineVo;

public interface HomeAboutFinder {
  Mono<HomeAboutVo> get();
}