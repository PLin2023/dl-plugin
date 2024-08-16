package zpl.valve.plugin.vo;

import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import zpl.valve.plugin.extension.HomeAbout;
import zpl.valve.plugin.extension.Timeline;

@Value
@Builder
public class HomeAboutVo implements ExtensionVoOperator {
    MetadataOperator metadata;
    HomeAbout.HomeAboutSpec spec;

    public static HomeAboutVo from(HomeAbout homeAbout) {
        return HomeAboutVo.builder()
            .metadata(homeAbout.getMetadata())
            .spec(homeAbout.getSpec())
            .build();
    }
}
