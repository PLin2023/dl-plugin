import { axiosInstance } from "@halo-dev/api-client";
import { useQuery } from "@tanstack/vue-query";
import { ref, type Ref } from "vue";
import type { ProductList } from "@/types";

export function useProductFetch(page: Ref<number>, size: Ref<number>, sort: Ref<string | undefined>) {
  const total = ref(0);

  const {
    data: products,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ["products", page, size, sort],
    queryFn: async () => {
      const { data } = await axiosInstance.get<ProductList>(
        "/apis/product.plugin.valve.zpl/v1alpha1/products",
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
      const deletingProducts = data?.filter((product) => !!product.metadata.deletionTimestamp);
      return deletingProducts?.length ? 1000 : false;
    },
  });

  return {
    products,
    isLoading,
    refetch,
    total,
  };
}
