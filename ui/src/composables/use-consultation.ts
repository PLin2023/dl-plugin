import { ref, type Ref } from 'vue';
import { useQuery } from '@tanstack/vue-query';
import { axiosInstance } from "@halo-dev/api-client";
import type { ConsultationList, Consultation } from "@/types";

export function useConsultationFetch(page: Ref<number>, size: Ref<number>, keyword: Ref<string>, startDate: Ref<string>, endDate: Ref<string>, sort: Ref<string>) {
  const {
    data: consultationList,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ['consultations', page, size, keyword, startDate, endDate, sort],
    queryFn: async (): Promise<ConsultationList> => {
      const { data } = await axiosInstance.get<ConsultationList>(
        "/apis/api.consultation.plugin.halo.run/v1alpha1/consultations",
        {
          params: {
            page: page.value,
            size: size.value,
            keyword: keyword.value,
            startDate: startDate.value,
            endDate: endDate.value,
            sort: sort.value,
          },
        }
      );
      return data;
    },
    refetchOnWindowFocus: false,
  });

  return {
    consultationList,
    isLoading,
    refetch,
  };
}
