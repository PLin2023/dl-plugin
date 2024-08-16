package zpl.valve.plugin.vo;

import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import zpl.valve.plugin.extension.ApplicationArea;
import zpl.valve.plugin.extension.Banner;

@Value
@Builder
public class ApplicationAreaVo implements ExtensionVoOperator {
    MetadataOperator metadata;
    ApplicationArea.ApplicationAreaSpec spec;

    public static ApplicationAreaVo from(ApplicationArea applicationArea) {
        return ApplicationAreaVo.builder()
            .metadata(applicationArea.getMetadata())
            .spec(applicationArea.getSpec())
            .build();
    }
}
