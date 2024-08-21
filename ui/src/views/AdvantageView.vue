<script setup lang="ts" xmlns="">
import {computed, ref, watch} from "vue";
import {useRouteQuery} from "@vueuse/router";
import type {Advantage, ApplicationArea, Banner} from "@/types";
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
import {axiosInstance, coreApiClient} from "@halo-dev/api-client";
import {formatDatetime} from "../utils/date";
import {useApplicationAreaFetch} from "@/composables/use-application-area";
import ApplicationAreaEditModal from "@/components/application-area-edit-modal.vue";
import {useAdvantageFetch} from "@/composables/use-advantage";
import AdvantageEditModal from "@/components/advantage-edit-modal.vue";
import {Icon} from "@iconify/vue";

const checkedAll = ref(false);
const editingModal = ref<boolean>(false);
const creationModal = ref<boolean>(false);

const selectedAdvantage = ref<Advantage>()
const selectedAdvantageNames = ref<string[]>([])

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
  advantages, total, refetch, isLoading
} = useAdvantageFetch(page, size, selectedSortValue)

watch(
  () => [advantages.value],
  () => {
    console.log("advantages: ", advantages.value)
  }
)

function onEditingModalClose() {
  editingModal.value = false;
  selectedAdvantage.value = undefined;
}

const handleCheckAllChange = (e: Event) => {
  const {checked} = e.target as HTMLInputElement;

  if (checked) {
    selectedAdvantageNames.value =
      advantages.value
        ?.map((advantage) => {
          return advantage.metadata.name;
        }) || [];
  } else {
    selectedAdvantageNames.value.length = 0;
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
        const userNamesToDelete = selectedAdvantageNames.value;
        console.log("deleting advantages: ", userNamesToDelete);
        await Promise.all(
          userNamesToDelete.map((name) => {
            return axiosInstance.delete(`/apis/advantage.plugin.valve.zpl/v1alpha1/advantages/${name}`)
          })
        );
        await refetch();
        selectedAdvantageNames.value.length = 0;
        Toast.success('删除成功');
      } catch (e) {
        Toast.error('删除失败');
        console.error(e);
      } finally {

      }
    },
  });
};


function checkSelection(advantage: Advantage) {
  return (
    advantage.metadata.name === selectedAdvantage.value?.metadata.name ||
    selectedAdvantageNames.value.includes((advantage.metadata.name))
  )
}

function handleEditBanner(advantage: Advantage) {
  selectedAdvantage.value = advantage;
  editingModal.value = true;
}

function handleDeleteBanner(advantage: Advantage) {
  Dialog.warning({
    title: '确认删除',
    description: '此操作不可逆转，确认删除吗？',
    confirmText: '确认',
    cancelText: '取消',
    onConfirm: async () => {
      try {
        await axiosInstance.delete(`/apis/advantage.plugin.valve.zpl/v1alpha1/advantages/${advantage.metadata.name}`)
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
  <AdvantageEditModal
    :visible="editingModal"
    :advantage="selectedAdvantage"
    @close="onEditingModalClose"
  />
  <VPageHeader title="核心优势">
    <template #icon>
      <Icon icon="gg:push-up" class="mr-2 self-center"/>
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
              <VSpace v-if="selectedAdvantageNames.length">
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

      <Transition v-else-if="!advantages?.length" appear name="fade">
        <VEmpty
          message="目前不存在核心优势信息，请先创建"
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
          <li v-for="(advantage, index) in advantages" :key="index" class="hover:bg-gray-50">
            <VEntity :is-selected="checkSelection(advantage)" class="py-4">
              <template #checkbox>
                <input
                  v-model="selectedAdvantageNames"
                  :value="advantage.metadata.name"
                  name="post-checkbox"
                  type="checkbox"
                  class="h-4 w-4 rounded border-gray-300 text-indigo-600 focus:ring-indigo-500"
                />
              </template>
              <template #start>
                <div class="flex items-center space-x-4 w-full">
                  <VEntityField class="w-12 flex-shrink-0">
                    <template #title>
                      <span class="font-medium ">{{ advantage.spec.priority }}</span>
                    </template>
                  </VEntityField>
                  <VEntityField class="w-[240px] flex-shrink-0">
                    <template v-if="advantage.spec.title" #title>
                      <span class="font-medium truncate">{{ advantage.spec.title }}</span>
                    </template>
                    <template v-if="advantage.spec.description" #description>
                      <span class="text-sm text-gray-600 truncate">{{ advantage.spec.description }}</span>
                    </template>
                  </VEntityField>
                  <VEntityField class="w-[120px] flex-shrink-0">
                    <template #description>
                      <img :src="advantage.spec.image" alt="PC" class="h-12 w-full object-cover rounded">
                    </template>
                  </VEntityField>
                  <VEntityField class="w-12 flex-shrink-0">
                    <template #title>
                      <span class="font-medium ">{{ advantage.spec.moreLink }}</span>
                    </template>
                  </VEntityField>
                </div>
              </template>
              <template #end>
                <div class="flex items-center space-x-4">
                  <VEntityField v-if="advantage.metadata.deletionTimestamp" class="w-[80px] flex-shrink-0">
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
                  {{ formatDatetime(advantage.metadata.creationTimestamp) }}
                </span>
                    </template>
                  </VEntityField>
                </div>
              </template>
              <template #dropdownItems>
                <VDropdownItem @click="handleEditBanner(advantage)" class="text-blue-600 hover:bg-blue-50">
                  编辑
                </VDropdownItem>
                <VDropdownItem
                  type="danger"
                  @click="handleDeleteBanner(advantage)"
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
