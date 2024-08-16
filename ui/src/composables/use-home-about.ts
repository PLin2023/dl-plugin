import {axiosInstance} from "@halo-dev/api-client";
import {useQuery} from "@tanstack/vue-query";
import {ref, type Ref} from "vue";
import type {HomeAbout, HomeAboutList, Timeline, TimelineList} from "@/types";


export const initialHomeAboutFormState: HomeAbout = {
  metadata: {
    name: "",
    generateName: "homeAbout-",
  },
  spec: {
    'name': "",
    'description': '',
    'aboutUrl': '',
    'link1Text': '',
    'link1Url': '',
    'link1Image': '',
    'link2Text': '',
    'link2Url': '',
    'link2Image': '',
    'publicityImage': '',
    'publicityImageText1': '',
    'publicityImageText2': '',
    'publicityImageText3': '',
    'publicityImageText4': '',
  },
  kind: "HomeAbout",
  apiVersion: "homeAbout.plugin.valve.zpl/v1alpha1",
};


export function useHomeAboutFetch() {

  const {
    data: homeAbout,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["homeAbouts"],
    queryFn: async () => {
      const {data} = await axiosInstance.get<HomeAboutList>(
        "/apis/homeAbout.plugin.valve.zpl/v1alpha1/homeAbouts",
        {
          params: {
            page: 1,
            size: 1,
          },
        }
      );

      console.log("data:", data.items)
      if (data.items.length > 0)
        return data.items[0];
      else {
        const res = await axiosInstance.post<HomeAbout>(`/apis/homeAbout.plugin.valve.zpl/v1alpha1/homeAbouts`, initialHomeAboutFormState);
        console.log("created homeAbout:", res.data);
        return res.data;
      }
    },
    refetchOnWindowFocus: false,
    refetchInterval(query) {
      const data = query.state.data;
      const deletingLinks = !!data?.metadata.deletionTimestamp;
      return deletingLinks ? 1000 : false;
    },
  });

  return {
    homeAbout,
    isLoading,
    refetch,
  };
}
