<script setup lang="ts">
import {ref, reactive, computed, onMounted, watch} from 'vue';
import {useQuery} from '@tanstack/vue-query';
import {axiosInstance} from "@halo-dev/api-client";
import type {Product, ProductCategory} from "@/types";
import {
  VCard,
  VPageHeader,
  VSpace,
  VButton,
  VPagination,
  VLoading,
  VEmpty,
  Toast,
  Dialog,
} from "@halo-dev/components";
import {IconAddCircle, IconRefreshLine} from "@halo-dev/components";
import {formatDatetime} from "@/utils/date";
import ProductEditModal from "@/components/product-edit-modal.vue";
import {Icon} from "@iconify/vue";

const searchParams = reactive({
  keyword: '',
  category: '',
  minPrice: '',
  maxPrice: '',
  createdAfter: '',
  createdBefore: '',
  page: 1,
  size: 20,
  sort: 'metadata.creationTimestamp,desc'
});

const showAdvancedSearch = ref(false);
const categories = ref<ProductCategory[]>([]);

const fetchCategories = async () => {
  try {
    const {data} = await axiosInstance.get<{ items: ProductCategory[] }>(
      "/apis/productCategory.plugin.valve.zpl/v1alpha1/productCategorys"
    );
    categories.value = data.items;
  } catch (e) {
    console.error("Failed to fetch categories", e);
    Toast.error("Failed to fetch categories");
  }
};

const {
  data: productListData,
  isLoading,
  refetch
} = useQuery({
  queryKey: ['products', searchParams],
  queryFn: async () => {
    const {data} = await axiosInstance.get("/apis/console.api.product.plugin.valve.zpl/v1alpha1/products", {
      params: searchParams
    });
    return data;
  },
});

const products = computed(() => productListData.value?.items || []);
const total = computed(() => productListData.value?.total || 0);

const handleSearch = () => {
  searchParams.page = 1;
  refetch();
};

const handleClearFilters = () => {
  Object.assign(searchParams, {
    keyword: '',
    category: '',
    minPrice: '',
    maxPrice: '',
    createdAfter: '',
    createdBefore: '',
    page: 1,
    sort: 'metadata.creationTimestamp,desc'
  });
  refetch();
};

const handlePageChange = (value: { page: number; size: number }) => {
  searchParams.page = value.page;
  searchParams.size = value.size;
  refetch();
};

const handleSortChange = (event: Event) => {
  const target = event.target as HTMLSelectElement;
  searchParams.sort = target.value;
  searchParams.page = 1;
  refetch();
};

const handleDeleteProduct = (product: Product) => {
  Dialog.warning({
    title: '确认删除',
    description: '此操作不可逆转，确认删除吗？',
    confirmText: '确认',
    cancelText: '取消',
    onConfirm: async () => {
      try {
        await axiosInstance.delete(`/apis/product.plugin.valve.zpl/v1alpha1/products/${product.metadata.name}`);
        Toast.success('删除成功');
        refetch();
      } catch (e) {
        console.error(e);
        Toast.error('删除失败');
      }
    },
  });
};

const editingModal = ref(false);
const selectedProduct = ref<Product | null>(null);

const handleEditProduct = (product: Product) => {
  selectedProduct.value = product;
  editingModal.value = true;
};

const handleCreateProduct = () => {
  selectedProduct.value = null;
  editingModal.value = true;
};

const onEditingModalClose = () => {
  editingModal.value = false;
  selectedProduct.value = null;
  refetch();
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('zh-CN', {style: 'currency', currency: 'CNY'}).format(price);
};

onMounted(() => {
  fetchCategories();
});

watch(() => searchParams, () => {
  refetch();
}, {deep: true});
</script>

<template>
  <VPageHeader title="产品列表">
    <template #icon>
    </template>
    <template #actions>
      <VSpace>
        <VButton
          type="secondary"
          @click="handleCreateProduct"
        >
          <template #icon>
            <IconAddCircle class="h-full w-full"/>
          </template>
          新建
        </VButton>
      </VSpace>
    </template>
  </VPageHeader>

  <div class="m-0 md:m-4">
    <VCard :body-class="['!p-4']">
      <template #header>
        <div class="search-form-container">
          <div class="search-form">
            <div class="search-bar">
              <input
                v-model="searchParams.keyword"
                placeholder="搜索产品..."
                class="search-input"
                @keyup.enter="handleSearch"
              />
              <button @click="handleSearch" class="search-button">搜索</button>
            </div>
            <button @click="showAdvancedSearch = !showAdvancedSearch" class="toggle-button">
              {{ showAdvancedSearch ? '隐藏筛选' : '显示筛选' }}
            </button>
          </div>

          <div v-if="showAdvancedSearch" class="advanced-search">
            <div class="form-group category-group">
              <label class="form-label">分类</label>
              <select
                v-model="searchParams.category"
                class="form-select"
              >
                <option value="">所有分类</option>
                <option v-for="category in categories" :key="category.metadata.name" :value="category.metadata.name">
                  {{ category.spec.name }}
                </option>
              </select>
            </div>
            <div class="form-group price-group">
              <label class="form-label">价格范围</label>
              <div class="price-range">
                <input
                  v-model="searchParams.minPrice"
                  placeholder="最低价格"
                  type="number"
                  class="form-input"
                />
                <span class="price-separator">-</span>
                <input
                  v-model="searchParams.maxPrice"
                  placeholder="最高价格"
                  type="number"
                  class="form-input"
                />
              </div>
            </div>
            <div class="form-group date-group">
              <label class="form-label">创建时间</label>
              <div class="date-range">
                <input
                  v-model="searchParams.createdAfter"
                  type="date"
                  class="form-input"
                />
                <span class="date-separator">-</span>
                <input
                  v-model="searchParams.createdBefore"
                  type="date"
                  class="form-input"
                />
              </div>
            </div>
          </div>

          <div class="form-actions">
            <div class="sort-section">
              <label class="sort-label">排序：</label>
              <select
                v-model="searchParams.sort"
                @change="handleSortChange"
                class="form-select"
              >
                <option value="metadata.creationTimestamp,desc">最新创建</option>
                <option value="metadata.creationTimestamp,asc">最早创建</option>
                <option value="spec.price,asc">价格从低到高</option>
                <option value="spec.price,desc">价格从高到低</option>
              </select>
            </div>
            <div class="action-buttons">
              <button @click="handleClearFilters" class="clear-button">清除筛选</button>
              <button @click="refetch()" class="refresh-button">
                刷新
              </button>
            </div>
          </div>
        </div>
      </template>

      <VLoading v-if="isLoading"/>

      <template v-else>
        <VEmpty
          v-if="!products.length"
          title="暂无数据"
          message="目前不存在产品，请先创建"
        >
          <template #actions>
            <VSpace>
              <VButton @click="refetch()">
                刷新数据
              </VButton>
              <VButton
                type="secondary"
                @click="handleCreateProduct"
              >
                <template #icon>
                  <IconAddCircle class="h-full w-full"/>
                </template>
                新建
              </VButton>
            </VSpace>
          </template>
        </VEmpty>

        <div v-else class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
          <div v-for="product in products" :key="product.metadata.name" class="flex flex-col h-full">
            <VCard class="flex flex-col h-full">
              <img v-if="product.spec.images.length" :src="product.spec.images[0].url" :alt="product.spec.name"
                   class="w-full h-48 object-cover rounded-t">
              <div class="p-4 flex flex-col flex-grow">
                <h3 class="text-lg font-semibold mb-2">{{ product.spec.name }}</h3>
                <p class="text-sm text-gray-600 mb-2 line-clamp-3 flex-grow">{{ product.spec.description }}</p>
                <div class="mt-auto">
                  <p v-if="product.spec.sku" class="text-xs text-gray-500 mb-1">SKU: {{ product.spec.sku }}</p>
                  <p v-if="product.spec.price !== undefined" class="text-xs text-gray-500 mb-1">价格:
                    {{ formatPrice(product.spec.price) }}</p>
                  <p class="text-xs text-gray-400">优先级: {{ product.spec.priority }}</p>
                  <p class="text-xs text-gray-400">创建时间: {{
                      formatDatetime(product.metadata.creationTimestamp)
                    }}</p>
                </div>
              </div>
              <div class="p-4 bg-gray-50 rounded-b">
                <VSpace>
                  <VButton size="sm" @click="handleEditProduct(product)">编辑</VButton>
                  <VButton size="sm" type="danger" @click="handleDeleteProduct(product)">删除</VButton>
                </VSpace>
              </div>
            </VCard>
          </div>
        </div>

        <div class="mt-4 flex justify-between items-center">
          <span class="text-sm text-gray-500">共 {{ total }} 项数据</span>
          <VPagination
            v-model:page="searchParams.page"
            v-model:size="searchParams.size"
            :total="total"
            :size-options="[20, 50, 100]"
            @change="handlePageChange"
          />
        </div>
      </template>
    </VCard>
  </div>

  <ProductEditModal
    :visible="editingModal"
    :product="selectedProduct || undefined"
    @close="onEditingModalClose"
  />
</template>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.search-form-container {
  padding: 20px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  width: 100%;
}

.search-form {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.search-bar {
  display: flex;
  flex-grow: 1;
  margin-right: 10px;
}

.search-input {
  flex-grow: 1;
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 4px 0 0 4px;
  font-size: 14px;
}

.search-button {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

.toggle-button {
  padding: 8px 16px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.advanced-search {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-top: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-label {
  margin-bottom: 5px;
  font-size: 14px;
  font-weight: 500;
}

.form-input,
.form-select {
  padding: 8px 12px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
}

.price-range,
.date-range {
  display: flex;
  align-items: center;
}

.price-separator,
.date-separator {
  margin: 0 10px;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.sort-section {
  display: flex;
  align-items: center;
}

.sort-label {
  margin-right: 10px;
  font-size: 14px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.clear-button,
.refresh-button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.clear-button {
  background-color: #6c757d;
  color: white;
}

.refresh-button {
  background-color: #28a745;
  color: white;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .advanced-search {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 992px) {
  .advanced-search {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .search-form {
    flex-direction: column;
    align-items: stretch;
  }

  .search-bar {
    margin-right: 0;
    margin-bottom: 10px;
  }

  .toggle-button {
    width: 100%;
  }

  .form-actions {
    flex-direction: column;
    align-items: stretch;
  }

  .sort-section {
    margin-bottom: 10px;
  }

  .action-buttons {
    justify-content: space-between;
  }
}

/* 优化价格和日期输入框的响应式布局 */
@media (max-width: 576px) {
  .price-range,
  .date-range {
    flex-direction: column;
    align-items: stretch;
  }

  .price-separator,
  .date-separator {
    display: none;
  }

  .price-range .form-input,
  .date-range .form-input {
    margin-bottom: 5px;
  }
}
</style>
