<script setup lang="ts" xmlns="">
import {computed, ref, watch} from "vue";
import {useRouteQuery} from "@vueuse/router";
import type {ApplicationArea, Banner} from "@/types";
import {useBannerFetch} from "@/composables/use-banner";
import BannerEditModal from "@/components/banner-edit-modal.vue";
import {
  VPageHeader,
  IconImageAddLine,
  IconAddCircle,
  IconRefreshLine,
  VSpace,
  VButton,
  VCard,
  VLoading, VEmpty, VEntity, VEntityField, VAvatar, VPagination, Dialog, Toast, VStatusDot, VTag, VDropdownItem
} from "@halo-dev/components";
import {axiosInstance} from "@halo-dev/api-client";
import {formatDatetime} from "../utils/date";
import {useApplicationAreaFetch} from "@/composables/use-application-area";
import ApplicationAreaEditModal from "@/components/application-area-edit-modal.vue";

const checkedAll = ref(false);
const editingModal = ref<boolean>(false);
const creationModal = ref<boolean>(false);

const selectedApplicationArea = ref<ApplicationArea>()
const selectedApplicationAreaNames = ref<string[]>([])

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
  applicationAreas, total, refetch, isLoading
} = useApplicationAreaFetch(page, size, selectedSortValue)

watch(
  () => [applicationAreas.value],
  () => {
    console.log("applicationAreas: ", applicationAreas.value)
  }
)

function onEditingModalClose() {
  editingModal.value = false;
  selectedApplicationArea.value = undefined;
}

const handleCheckAllChange = (e: Event) => {
  const {checked} = e.target as HTMLInputElement;

  if (checked) {
    selectedApplicationAreaNames.value =
      applicationAreas.value
        ?.map((banner) => {
          return banner.metadata.name;
        }) || [];
  } else {
    selectedApplicationAreaNames.value.length = 0;
  }
};

const handleDeleteInBatch = async () => {
  Dialog.warning({
    title: '警告',
    description: '此操作风险极高，请明确你正在做什么，删除后所有数据均无法恢复！',
    confirmType: "danger",
    confirmText: '确认',
    cancelText: '取消',
    onConfirm: async () => {
      try {
        const userNamesToDelete = selectedApplicationAreaNames.value;
        console.log("deleting applicationAreas: ", userNamesToDelete);
        await Promise.all(
          userNamesToDelete.map((name) => {
            return axiosInstance.delete(`/apis/applicationArea.plugin.valve.zpl/v1alpha1/applicationAreas/${name}`)
          })
        );
        await refetch();
        selectedApplicationAreaNames.value.length = 0;
        Toast.success('删除成功');
      } catch (e) {
        Toast.error('删除失败');
        console.error(e);
      } finally {

      }
    },
  });
};


function checkSelection(applicationArea: ApplicationArea) {
  return (
    applicationArea.metadata.name === selectedApplicationArea.value?.metadata.name ||
    selectedApplicationAreaNames.value.includes((applicationArea.metadata.name))
  )
}

function handleEditBanner(applicationArea: ApplicationArea) {
  selectedApplicationArea.value = applicationArea;
  editingModal.value = true;
}

function handleDeleteBanner(applicationArea: ApplicationArea) {
  Dialog.warning({
    title: '确认删除',
    description: '此操作不可逆转，确认删除吗？',
    confirmText: '确认',
    cancelText: '取消',
    onConfirm: async () => {
      try {
        await axiosInstance.delete(`/apis/applicationArea.plugin.valve.zpl/v1alpha1/applicationAreas/${applicationArea.metadata.name}`)
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
  <ApplicationAreaEditModal
    :visible="editingModal"
    :application-area="selectedApplicationArea"
    @close="onEditingModalClose"
  />
  <VPageHeader title="应用领域">
    <template #icon>
      <IconImageAddLine class="mr-2 self-center"/>
    </template>
    <template #actions>
      <VSpace>
        <VButton
          type="secondary"
          @click="editingModal = true"
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
    <VCard :body-class="['!p-0']">
      <template #header>
        <div class="block w-full bg-gray-50 px-4 py-3">
          <div
            class="relative flex flex-col flex-wrap items-start gap-4 sm:flex-row sm:items-center"
          >
            <div
              class="hidden items-center sm:flex"
            >
              <input
                v-model="checkedAll"
                type="checkbox"
                @change="handleCheckAllChange"
              />
            </div>
            <div class="flex w-full flex-1 items-center sm:w-auto">
              <VSpace v-if="selectedApplicationAreaNames.length">
                <VButton type="danger" @click="handleDeleteInBatch">
                  删除所有
                </VButton>
              </VSpace>
            </div>
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
              <div class="flex flex-row gap-2">
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
            </VSpace>
          </div>
        </div>
      </template>

      <VLoading v-if="isLoading"/>

      <Transition v-else-if="!applicationAreas?.length" appear name="fade">
        <VEmpty
          message="目前不存在应用领域信息，请先创建"
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
                  <IconAddCircle class="h-full w-full"/>
                </template>
                新建
              </VButton>
            </VSpace>
          </template>
        </VEmpty>
      </Transition>

      <Transition v-else appear name="fade">
        <ul class="box-border h-full w-full divide-y divide-gray-100" role="list">
          <li v-for="(applicationArea, index) in applicationAreas" :key="index" class="hover:bg-gray-50">
            <VEntity :is-selected="checkSelection(applicationArea)" class="py-4">
              <template #checkbox>
                <input
                  v-model="selectedApplicationAreaNames"
                  :value="applicationArea.metadata.name"
                  name="post-checkbox"
                  type="checkbox"
                  class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                />
              </template>
              <template #start>
                <div class="flex items-center space-x-4 w-full">
                  <VEntityField class="w-12 flex-shrink-0">
                    <template #title>
                      <span class="font-medium ">{{ applicationArea.spec.priority }}</span>
                    </template>
                  </VEntityField>
                  <VEntityField class="w-[240px] flex-shrink-0">
                    <template v-if="applicationArea.spec.name" #title>
                      <span class="font-medium truncate">{{ applicationArea.spec.name }}</span>
                    </template>
                    <template v-if="applicationArea.spec.description" #description>
                      <span class="text-sm text-gray-600 truncate">{{ applicationArea.spec.description }}</span>
                    </template>
                  </VEntityField>
                  <VEntityField class="w-[120px] flex-shrink-0">
                    <template #description>
                      <img :src="applicationArea.spec.image" alt="PC" class="h-12 w-full object-cover rounded">
                    </template>
                  </VEntityField>
                </div>
              </template>
              <template #end>
                <div class="flex items-center space-x-4">
                  <VEntityField v-if="applicationArea.metadata.deletionTimestamp" class="w-[80px] flex-shrink-0">
                    <template #description>
                      <VStatusDot
                        v-tooltip="'已被删除'"
                        state="warning"
                        animate
                        class="mr-2"
                      />
                    </template>
                  </VEntityField>
                  <VEntityField class="w-[120px] flex-shrink-0">
                    <template #description>
                <span class="text-xs tabular-nums text-gray-500">
                  {{ formatDatetime(applicationArea.metadata.creationTimestamp) }}
                </span>
                    </template>
                  </VEntityField>
                </div>
              </template>
              <template #dropdownItems>
                <VDropdownItem @click="handleEditBanner(applicationArea)" class="text-blue-600 hover:bg-blue-50">
                  编辑
                </VDropdownItem>
                <VDropdownItem
                  type="danger"
                  @click="handleDeleteBanner(applicationArea)"
                  class="text-red-600 hover:bg-red-50"
                >
                  删除
                </VDropdownItem>
              </template>
            </VEntity>
          </li>
        </ul>
      </Transition>
      <template #footer>
        <VPagination
          v-model:page="page"
          v-model:size="size"
          :total="total"
          page-label="页"
          size-label="条/页"
          :total-label="
            `共 ${total} 项数据`
          "
          :size-options="[20, 30, 50, 100]"
        />
      </template>
    </VCard>
  </div>

</template>

<style scoped lang="scss">

</style>
