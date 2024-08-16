import {axiosInstance} from "@halo-dev/api-client";
import {useQuery} from "@tanstack/vue-query";
import {ref, type Ref} from "vue";
import type {TimelineList} from "@/types";

export function useTimelineFetch(page: Ref<number>, size: Ref<number>, sort: Ref<string | undefined>)  {
  const total = ref(0);

  const {
    data: timelines,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["timelines", page, size],
    queryFn: async () => {
      const {data} = await axiosInstance.get<TimelineList>(
        "/apis/timeline.plugin.valve.zpl/v1alpha1/timelines",
        {
          params: {
            page: page.value,
            size: size.value,
            sort: sort.value ?? "spec.priority,asc",
          },
        }
      );

      total.value = data.total;
      console.log("total:", total.value, "data:", data.items)
      return data.items;
    },
    refetchOnWindowFocus: false,
    refetchInterval(query) {
      const data = query.state.data;
      const deletingLinks = data?.filter((timeline) => !!timeline.metadata.deletionTimestamp);
      return deletingLinks?.length ? 1000 : false;
    },
  });

  return {
    timelines,
    isLoading,
    refetch,
    total,
  };
}
