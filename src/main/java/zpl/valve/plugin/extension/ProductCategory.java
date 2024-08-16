package zpl.valve.plugin.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "ProductCategory", group = "productCategory.plugin.valve.zpl",
    version = "v1alpha1", singular = "productCategory", plural = "productCategorys")
public class ProductCategory extends AbstractExtension {

    @Schema(requiredMode = REQUIRED)
    private ProductCategorySpec spec;

    @Data
    public static class ProductCategorySpec {
        @Schema(requiredMode = REQUIRED)
        private String name;

        @Schema(requiredMode = REQUIRED)
        private String description;

        @Schema(requiredMode = REQUIRED)
        private String image;

        @Schema(requiredMode = REQUIRED, defaultValue = "0")
        private Integer priority;
    }
}

