import {axiosInstance} from "@halo-dev/api-client";
import {useQuery} from "@tanstack/vue-query";
import {ref, type Ref} from "vue";
import type {BannerList} from "@/types";

export function useBannerFetch(page: Ref<number>, size: Ref<number>, sort: Ref<string | undefined> ) {
  const total = ref(0);

  const {
    data: banners,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["banners", page, size],
    queryFn: async () => {
      const {data} = await axiosInstance.get<BannerList>(
        "/apis/banner.plugin.valve.zpl/v1alpha1/banners",
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
      const deletingLinks = data?.filter((banner) => !!banner.metadata.deletionTimestamp);
      return deletingLinks?.length ? 1000 : false;
    },
  });

  return {
    banners,
    isLoading,
    refetch,
    total,
  };
}
