package zpl.valve.plugin.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import java.time.Instant;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "Product", group = "product.plugin.valve.zpl",
    version = "v1alpha1", singular = "product", plural = "products")
public class Product extends AbstractExtension {

    @Schema(requiredMode = REQUIRED)
    private ProductSpec spec;

    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ProductStatus status;

    @Data
    public static class ProductSpec {
        @Schema(requiredMode = REQUIRED)
        private String name;

        @Schema(requiredMode = REQUIRED)
        private String categoryMetadataName;

        @Schema(requiredMode = REQUIRED)
        private String description;

        @Schema(requiredMode = REQUIRED)
        private List<Standard> standards;

        @Schema(requiredMode = REQUIRED)
        private List<ProductImage> images;

        @Schema(requiredMode = REQUIRED, defaultValue = "0")
        private Integer priority;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private String sku;

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private Double price;

        @Data
        public static class Standard {
            @Schema(requiredMode = REQUIRED)
            private String name;

            @Schema(requiredMode = REQUIRED)
            private String value;
        }

        @Data
        public static class ProductImage {
            @Schema(requiredMode = REQUIRED)
            private String url;

            @Schema(requiredMode = REQUIRED)
            private Integer priority;
        }
    }

    @Data
    public static class ProductStatus {
        private Instant lastUpdateTime;
    }
}