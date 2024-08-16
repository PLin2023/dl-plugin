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
@GVK(kind = "Advantage", group = "advantage.plugin.valve.zpl",
    version = "v1alpha1", singular = "advantage", plural = "advantages")
public class Advantage extends AbstractExtension {

    @Schema(requiredMode = REQUIRED)
    private AdvantageSpec spec;

    @Data
    public static class AdvantageSpec {
        @Schema(requiredMode = REQUIRED)
        private String title;

        @Schema(requiredMode = REQUIRED)
        private String description;

        @Schema(requiredMode = NOT_REQUIRED)
        private String moreLink;

        @Schema(requiredMode = REQUIRED)
        private String image;

        @Schema(requiredMode = REQUIRED, defaultValue = "0")
        private Integer priority;
    }
}

