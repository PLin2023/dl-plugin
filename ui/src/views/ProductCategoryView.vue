<script setup lang="ts">
import { computed, ref, watch } from "vue";
import { useRouteQuery } from "@vueuse/router";
import type { ProductCategory } from "@/types";
import { useProductCategoryFetch } from "@/composables/use-product-category";
import ProductCategoryEditModal from "@/components/product-category-edit-modal.vue";
import {
  VPageHeader,
  IconAddCircle,
  IconRefreshLine,
  VSpace,
  VButton,
  VCard,
  VLoading,
  VEmpty,
  Dialog,
  Toast,
  VDropdownItem, VPagination
} from "@halo-dev/components";
import { axiosInstance } from "@halo-dev/api-client";
import { formatDatetime } from "../utils/date";
import {Icon} from "@iconify/vue";

const editingModal = ref<boolean>(false);
const selectedProductCategory = ref<ProductCategory>();

const page = useRouteQuery<number>("page", 1, {
  transform: Number,
});
const size = useRouteQuery<number>("size", 20, {
  transform: Number,
});

const selectedSortValue = useRouteQuery<string | undefined>("sort");

function handleClearFilters() {
  selectedSortValue.value = undefined;
}

const hasFilters = computed(() => {
  return selectedSortValue.value;
});

watch(
  () => [selectedSortValue.value],
  () => {
    page.value = 1;
  }
);

const {
  productCategories,
  total,
  refetch,
  isLoading
} = useProductCategoryFetch(page, size, selectedSortValue);

function onEditingModalClose() {
  editingModal.value = false;
  selectedProductCategory.value = undefined;
}

function handleEditCategory(category: ProductCategory) {
  selectedProductCategory.value = category;
  editingModal.value = true;
}

function handleDeleteCategory(category: ProductCategory) {
  Dialog.warning({
    title: '确认删除',
    description: '此操作不可逆转，确认删除吗？',
    confirmText: '确认',
    cancelText: '取消',
    onConfirm: async () => {
      try {
        await axiosInstance.delete(`/apis/productCategory.plugin.valve.zpl/v1alpha1/productCategorys/${category.metadata.name}`);
        await refetch();
        Toast.success('删除成功');
      } catch (e) {
        Toast.error('删除失败');
        console.error(e);
      }
    },
  });
}
</script>

<template>
  <ProductCategoryEditModal
    :visible="editingModal"
    :product-category="selectedProductCategory"
    @close="onEditingModalClose"
  />
  <VPageHeader title="产品分类">
    <template #icon>
      <Icon icon="material-symbols:category" class="mr-2 self-center"/>
    </template>
    <template #actions>
      <VSpace>
        <VButton
          type="secondary"
          @click="editingModal = true"
        >
          <template #icon>
            <IconAddCircle class="h-full w-full" />
          </template>
          新建
        </VButton>
      </VSpace>
    </template>
  </VPageHeader>

  <div class="m-0 md:m-4">
    <VCard :body-class="['!p-4']">
      <template #header>
        <div class="block w-full bg-gray-50 px-4 py-3">
          <div class="flex items-center justify-between">
            <VSpace spacing="lg" class="flex-wrap">
              <FilterCleanButton
                v-if="hasFilters"
                @click="handleClearFilters"
              />
              <FilterDropdown
                v-model="selectedSortValue"
                label="排序"
                :items="[
                  {
                    label: '默认',
                  },
                  {
                    label: '较近创建',
                    value: 'metadata.creationTimestamp,desc',
                  },
                  {
                    label: '较早创建',
                    value: 'metadata.creationTimestamp,asc',
                  },
                ]"
              />
            </VSpace>
            <div
              class="group cursor-pointer rounded p-1 hover:bg-gray-200"
              @click="refetch()"
            >
              <IconRefreshLine
                v-tooltip="'刷新数据'"
                :class="{
                  'animate-spin text-gray-900': isLoading,
                }"
                class="h-4 w-4 text-gray-600 group-hover:text-gray-900"
              />
            </div>
          </div>
        </div>
      </template>

      <VLoading v-if="isLoading" />

      <Transition v-else-if="!productCategories?.length" appear name="fade">
        <VEmpty
          message="目前不存在产品分类，请先创建"
          title="暂无数据"
        >
          <template #actions>
            <VSpace>
              <VButton @click="refetch()">
                刷新数据
              </VButton>
              <VButton
                type="secondary"
                @click="editingModal = true"
              >
                <template #icon>
                  <IconAddCircle class="h-full w-full" />
                </template>
                新建
              </VButton>
            </VSpace>
          </template>
        </VEmpty>
      </Transition>

      <Transition v-else appear name="fade">
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          <div v-for="category in productCategories" :key="category.metadata.name" class="flex flex-col h-full">
            <VCard class="h-full" :bodyClass="['flex','flex-col','h-full']">
              <img :src="category.spec.image" :alt="category.spec.name" class="w-full h-48 object-cover rounded-t">
              <div class="p-4 flex flex-col flex-grow">
                <h3 class="text-lg font-semibold mb-2">{{ category.spec.name }}</h3>
                <p class="text-sm text-gray-600 mb-2 line-clamp-3 flex-grow">{{ category.spec.description }}</p>
                <div class="mt-auto">
                  <p class="text-xs text-gray-400">优先级: {{ category.spec.priority }}</p>
                  <p class="text-xs text-gray-400">创建时间: {{ formatDatetime(category.metadata.creationTimestamp) }}</p>
                </div>
              </div>
              <div class="p-4 bg-gray-50 rounded-b">
                <VSpace>
                  <VButton size="sm" @click="handleEditCategory(category)">编辑</VButton>
                  <VButton size="sm" type="danger" @click="handleDeleteCategory(category)">删除</VButton>
                </VSpace>
              </div>
            </VCard>
          </div>
        </div>
      </Transition>

      <template #footer>
        <div class="flex justify-between items-center">
          <VPagination
            v-model:page="page"
            v-model:size="size"
            :total="total"
            :size-options="[20, 30, 50, 100]"
          />
        </div>
      </template>
    </VCard>
  </div>
</template>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
