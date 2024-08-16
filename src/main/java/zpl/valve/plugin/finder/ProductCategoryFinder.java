package zpl.valve.plugin.finder;

import reactor.core.publisher.Flux;
import zpl.valve.plugin.vo.AdvantageVo;
import zpl.valve.plugin.vo.ProductCategoryVo;

public interface ProductCategoryFinder {
  Flux<ProductCategoryVo> listAll();
}