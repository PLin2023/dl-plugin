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
@GVK(kind = "HomeAbout", group = "homeAbout.plugin.valve.zpl",
    version = "v1alpha1", singular = "homeAbout", plural = "homeAbouts")
public class HomeAbout extends AbstractExtension {

    @Schema(requiredMode = REQUIRED)
    private HomeAboutSpec spec;

    @Data
    public static class HomeAboutSpec {
        @Schema(requiredMode = REQUIRED)
        private String name;

        @Schema(requiredMode = REQUIRED)
        private String description;

        @Schema(requiredMode = REQUIRED)
        private String aboutUrl;

        @Schema(requiredMode = NOT_REQUIRED)
        private String link1Text;

        @Schema(requiredMode = NOT_REQUIRED)
        private String link1Url;

        @Schema(requiredMode = REQUIRED)
        private String link1Image;

        @Schema(requiredMode = NOT_REQUIRED)
        private String link2Text;

        @Schema(requiredMode = NOT_REQUIRED)
        private String link2Url;

        @Schema(requiredMode = REQUIRED)
        private String link2Image;

        @Schema(requiredMode = REQUIRED)
        private String publicityImage;

        @Schema(requiredMode = NOT_REQUIRED)
        private String publicityImageText1;

        @Schema(requiredMode = NOT_REQUIRED)
        private String publicityImageText2;

        @Schema(requiredMode = NOT_REQUIRED)
        private String publicityImageText3;

        @Schema(requiredMode = NOT_REQUIRED)
        private String publicityImageText4;

    }
}

