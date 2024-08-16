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
@GVK(kind = "Banner", group = "banner.plugin.valve.zpl",
    version = "v1alpha1", singular = "banner", plural = "banners")
public class Banner extends AbstractExtension {

    @Schema(requiredMode = REQUIRED)
    private BannerSpec spec;

    @Data
    public static class BannerSpec {
        @Schema(minLength = 1, requiredMode = REQUIRED)
        private String name;

        @Schema(requiredMode = NOT_REQUIRED)
        private String title;

        @Schema(requiredMode = NOT_REQUIRED)
        private String subtitle;

        @Schema(requiredMode = REQUIRED)
        private String bannerImagePc;

        @Schema(requiredMode = REQUIRED)
        private String bannerImageMobile;

        @Schema(requiredMode = REQUIRED, defaultValue = "0")
        private Integer priority;
    }
}

