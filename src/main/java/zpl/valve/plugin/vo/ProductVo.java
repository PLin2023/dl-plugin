package zpl.valve.plugin.vo;

import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import zpl.valve.plugin.extension.Product;
import zpl.valve.plugin.extension.ProductCategory;

@Value
@Builder
public class ProductVo implements ExtensionVoOperator {
    MetadataOperator metadata;
    Product.ProductSpec spec;

    public static ProductVo from(Product product) {
        return ProductVo.builder()
            .metadata(product.getMetadata())
            .spec(product.getSpec())
            .build();
    }
}
