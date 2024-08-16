package zpl.valve.plugin.vo;

import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import zpl.valve.plugin.extension.Advantage;
import zpl.valve.plugin.extension.ProductCategory;

@Value
@Builder
public class ProductCategoryVo implements ExtensionVoOperator {
    MetadataOperator metadata;
    ProductCategory.ProductCategorySpec spec;

    public static ProductCategoryVo from(ProductCategory productCategory) {
        return ProductCategoryVo.builder()
            .metadata(productCategory.getMetadata())
            .spec(productCategory.getSpec())
            .build();
    }
}
