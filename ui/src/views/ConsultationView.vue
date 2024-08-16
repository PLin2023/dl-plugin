<script setup lang="ts">
import {ref, reactive, computed, onMounted} from 'vue';
import {VCard, VPageHeader, VSpace, VButton, VPagination, VEmpty, VLoading, Toast} from '@halo-dev/components';
import {IconRefreshLine, IconSearch} from '@halo-dev/components';
import type {Consultation, ConsultationList} from '@/types';
import {formatDatetime} from '@/utils/date';
import {useQuery} from "@tanstack/vue-query";
import {axiosInstance} from "@halo-dev/api-client";

const searchParams = reactive({
  keyword: '',
  status: '',
  startDate: '',
  endDate: '',
  page: 1,
  size: 20,
  sort: 'metadata.creationTimestamp,desc'
});

const showAdvancedSearch = ref(false);

const {
  data: consultationListData,
  isLoading,
  refetch
} = useQuery({
  queryKey: ['consultations', searchParams],
  queryFn: async (): Promise<ConsultationList> => {
    const {data} = await axiosInstance.get<ConsultationList>(
      "/apis/api.consultation.plugin.halo.run/v1alpha1/consultations",
      {
        params: searchParams,
      }
    );
    return data;
  },
});

const consultations = computed(() => consultationListData.value?.items || []);
const total = computed(() => consultationListData.value?.total || 0);

const handleSearch = () => {
  searchParams.page = 1;
  refetch();
};

const handleClearFilters = () => {
  Object.assign(searchParams, {
    keyword: '',
    status: '',
    startDate: '',
    endDate: '',
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

const handleStatusChange = async (consultation: Consultation, newStatus: string) => {
  try {
    await axiosInstance.put(`/apis/consultation.plugin.halo.run/v1alpha1/consultations/${consultation.metadata.name}`, {
      ...consultation,
      spec: {
        ...consultation.spec,
        status: newStatus,
      },
    });
    Toast.success('状态更新成功');
    refetch();
  } catch (e) {
    console.error(e);
    Toast.error('状态更新失败');
  }
};

const handleDeleteConsultation = async (consultation: Consultation) => {
  try {
    await axiosInstance.delete(`/apis/api.consultation.plugin.halo.run/v1alpha1/consultations/${consultation.metadata.name}`);
    Toast.success('删除成功');
    refetch();
  } catch (e) {
    console.error(e);
    Toast.error('删除失败');
  }
};

const getStatusText = (status: string) => {
  switch (status) {
    case 'Unread':
      return '未读';
    case 'Read':
      return '已读';
    case 'Responded':
      return '已回复';
    default:
      return status;
  }
};

onMounted(() => {
  refetch();
});
</script>

<template>
  <VPageHeader title="咨询管理">
    <template #icon>
      <IconListUnordered class="mr-2 self-center"/>
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
                placeholder="搜索咨询..."
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
            <div class="form-group">
              <label class="form-label">状态</label>
              <select v-model="searchParams.status" class="form-select">
                <option value="">所有状态</option>
                <option value="Unread">未读</option>
                <option value="Read">已读</option>
                <option value="Responded">已回复</option>
              </select>
            </div>
            <div class="form-group">
              <label class="form-label">创建时间</label>
              <div class="date-range">
                <input
                  v-model="searchParams.startDate"
                  type="date"
                  class="form-input"
                />
                <span class="date-separator">-</span>
                <input
                  v-model="searchParams.endDate"
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
          v-if="!consultations.length"
          title="暂无数据"
          message="目前不存在咨询信息"
        >
          <template #actions>
            <VSpace>
              <VButton @click="refetch()">
                刷新数据
              </VButton>
            </VSpace>
          </template>
        </VEmpty>

        <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div v-for="consultation in consultations" :key="consultation.metadata.name" class="flex flex-col h-full">
            <VCard class="flex flex-col h-full">
              <div class="p-4 flex flex-col flex-grow">
                <h3 class="text-lg font-semibold mb-2">{{ consultation.spec.name }}</h3>
                <p class="text-sm text-gray-600 mb-2">{{ consultation.spec.email }}</p>
                <p class="text-sm text-gray-700 mb-2 line-clamp-3 flex-grow">{{ consultation.spec.content }}</p>
                <div class="mt-auto">
                  <div class="flex items-center justify-start">
                    <span class="text-sm font-medium">状态：</span>
                    <select
                      :value="consultation.spec.status"
                      @change="(e) => handleStatusChange(consultation, (e.target as HTMLSelectElement).value)"
                      class="ml-2 px-2 py-1 text-sm border rounded"
                    >
                      <option value="Unread">未读</option>
                      <option value="Read">已读</option>
                      <option value="Responded">已回复</option>
                    </select>
                  </div>
                  <p class="text-xs text-gray-400 mt-1">创建时间:
                    {{ formatDatetime(consultation.metadata.creationTimestamp) }}</p>
                </div>
              </div>
              <div class="p-4 bg-gray-50 rounded-b">
                <VButton size="sm" type="danger" @click="handleDeleteConsultation(consultation)">删除</VButton>
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
  grid-template-columns: repeat(2, 1fr);
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

.form-select {
  width: 120px;
}

.date-range {
  display: flex;
  align-items: center;
}

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

/* 优化日期输入框的响应式布局 */
@media (max-width: 576px) {
  .date-range {
    flex-direction: column;
    align-items: stretch;
  }

  .date-separator {
    display: none;
  }

  .date-range .form-input {
    margin-bottom: 5px;
  }
}
</style>
