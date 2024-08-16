package zpl.valve.plugin.vo;

import lombok.Builder;
import lombok.Value;
import org.checkerframework.checker.units.qual.A;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import zpl.valve.plugin.extension.Advantage;
import zpl.valve.plugin.extension.Banner;

@Value
@Builder
public class AdvantageVo implements ExtensionVoOperator {
    MetadataOperator metadata;
    Advantage.AdvantageSpec spec;

    public static AdvantageVo from(Advantage advantage) {
        return AdvantageVo.builder()
            .metadata(advantage.getMetadata())
            .spec(advantage.getSpec())
            .build();
    }
}
