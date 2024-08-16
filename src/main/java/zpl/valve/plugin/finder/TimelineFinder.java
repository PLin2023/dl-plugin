package zpl.valve.plugin.finder;

import reactor.core.publisher.Flux;
import zpl.valve.plugin.vo.BannerVo;
import zpl.valve.plugin.vo.TimelineVo;

public interface TimelineFinder {
  Flux<TimelineVo> listAll();
}