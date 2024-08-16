package zpl.valve.plugin.extension;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

@Data
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "ApplicationArea", group = "applicationArea.plugin.valve.zpl",
    version = "v1alpha1", singular = "applicationArea", plural = "applicationAreas")
public class ApplicationArea extends AbstractExtension {

    @Schema(requiredMode = REQUIRED)
    private ApplicationAreaSpec spec;

    @Data
    public static class ApplicationAreaSpec {
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

