package zpl.valve.plugin.finder;

import reactor.core.publisher.Flux;
import zpl.valve.plugin.vo.ApplicationAreaVo;
import zpl.valve.plugin.vo.BannerVo;

public interface ApplicationAreaFinder {
  Flux<ApplicationAreaVo> listAll();
}