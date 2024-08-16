package zpl.valve.plugin.vo;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import zpl.valve.plugin.extension.Banner;

@Value
@Builder
public class BannerVo implements ExtensionVoOperator {
    MetadataOperator metadata;
    Banner.BannerSpec spec;

    public static BannerVo from(Banner banner) {
        return BannerVo.builder()
            .metadata(banner.getMetadata())
            .spec(banner.getSpec())
            .build();
    }
}
