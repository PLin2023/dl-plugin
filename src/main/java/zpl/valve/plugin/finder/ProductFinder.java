package zpl.valve.plugin.finder;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;
import zpl.valve.plugin.extension.Product;
import zpl.valve.plugin.vo.ProductCategoryVo;
import zpl.valve.plugin.vo.ProductVo;

public interface ProductFinder {
    Flux<ProductVo> listAll();

    Mono<ListResult<ProductVo>> listByCategory(String categoryMetadataName, Integer page,
        Integer size);
}