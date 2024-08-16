import { axiosInstance } from "@halo-dev/api-client";
import { useQuery } from "@tanstack/vue-query";
import { ref, type Ref } from "vue";
import type { ProductCategoryList } from "@/types";

export function useProductCategoryFetch(page: Ref<number>, size: Ref<number>, sort: Ref<string | undefined>) {
  const total = ref(0);

  const {
    data: productCategories,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["productCategories", page, size, sort],
    queryFn: async () => {
      const { data } = await axiosInstance.get<ProductCategoryList>(
        "/apis/productCategory.plugin.valve.zpl/v1alpha1/productCategorys",
        {
          params: {
            page: page.value,
            size: size.value,
            sort: sort.value ?? "spec.priority,asc",
          },
        }
      );

      total.value = data.total;
      console.log("total:", total.value, "data:", data.items);
      return data.items;
    },
    refetchOnWindowFocus: false,
    refetchInterval(query) {
      const data = query.state.data;
      const deletingCategories = data?.filter((category) => !!category.metadata.deletionTimestamp);
      return deletingCategories?.length ? 1000 : false;
    },
  });

  return {
    productCategories,
    isLoading,
    refetch,
    total,
  };
}
