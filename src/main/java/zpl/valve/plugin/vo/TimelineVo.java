package zpl.valve.plugin.vo;

import lombok.Builder;
import lombok.Value;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.theme.finders.vo.ExtensionVoOperator;
import zpl.valve.plugin.extension.Banner;
import zpl.valve.plugin.extension.Timeline;

@Value
@Builder
public class TimelineVo implements ExtensionVoOperator {
    MetadataOperator metadata;
    Timeline.TimelineSpec spec;

    public static TimelineVo from(Timeline timeline) {
        return TimelineVo.builder()
            .metadata(timeline.getMetadata())
            .spec(timeline.getSpec())
            .build();
    }
}
