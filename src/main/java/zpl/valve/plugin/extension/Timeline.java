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
@GVK(kind = "Timeline", group = "timeline.plugin.valve.zpl",
    version = "v1alpha1", singular = "timeline", plural = "timelines")
public class Timeline extends AbstractExtension {

    @Schema(requiredMode = REQUIRED)
    private TimelineSpec spec;

    @Data
    public static class TimelineSpec {
        @Schema(requiredMode = REQUIRED)
        private String datetime;

        @Schema(requiredMode = REQUIRED)
        private String title;

        @Schema(requiredMode = REQUIRED)
        private Integer priority;
    }
}

