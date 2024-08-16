package zpl.valve.plugin.extension;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@EqualsAndHashCode(callSuper = true)
@GVK(group = "consultation.plugin.halo.run", version = "v1alpha1", kind = "Consultation", plural
        = "consultations", singular = "consultation")
public class Consultation extends AbstractExtension {

    @Schema(requiredMode = REQUIRED)
    private ConsultationSpec spec;

    @Data
    public static class ConsultationSpec {
        @Schema(requiredMode = REQUIRED)
        private String name;

        @Schema(requiredMode = REQUIRED)
        private String email;

        @Schema(requiredMode = REQUIRED)
        private String content;

        @Schema(requiredMode = REQUIRED)
        private String status = "Unread"; // 可以是 "Unread", "Read", "Responded"
    }
}