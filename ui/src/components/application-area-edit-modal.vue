<script lang="ts" setup>
import {axiosInstance} from "@halo-dev/api-client";
import {Toast, VButton, VLoading, VModal, VSpace} from "@halo-dev/components";
import cloneDeep from "lodash.clonedeep";
import {computed, inject, nextTick, ref, watch, type Ref} from "vue";
import type {ApplicationArea, Banner} from "@/types";
import {submitForm} from "@formkit/core";

const props = withDefaults(
  defineProps<{
    visible: boolean;
    applicationArea?: ApplicationArea;
  }>(),
  {
    visible: false,
    advantage: undefined,
  }
);

const emit = defineEmits<{
  (event: "update:visible", value: boolean): void;
  (event: "close"): void;
}>();

const initialFormState: ApplicationArea = {
  metadata: {
    name: "",
    generateName: "applicationArea-",
  },
  spec: {
    name: "",
    description: "",
    image: "",
    priority: 0,
  },
  kind: "ApplicationArea",
  apiVersion: "applicationArea.plugin.valve.zpl/v1alpha1",
};

const formState = ref<ApplicationArea>(cloneDeep(initialFormState));
const saving = ref<boolean>(false);
const formVisible = ref(false);

const isUpdateMode = computed(() => {
  return !!formState.value.metadata.creationTimestamp;
});

const modalTitle = computed(() => {
  return isUpdateMode.value ? "编辑应用领域" : "新建应用领域";
});

const onVisibleChange = (visible: boolean) => {
  emit("update:visible", visible);
  if (!visible) {
    emit("close");
  }
};

const handleResetForm = () => {
  formState.value = cloneDeep(initialFormState);
};

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      formVisible.value = true;
    } else {
      setTimeout(() => {
        formVisible.value = false;
        handleResetForm();
      }, 200);
    }
  }
);

// 如果传入 applicationArea，则更新当前表单内容为 applicationArea 中的内容
watch(
  () => props.applicationArea,
  (link) => {
    if (link) {
      formState.value = cloneDeep(link);
    }
  }
);

const annotationsFormRef = ref();

const handleSaveLink = async () => {
  annotationsFormRef.value?.handleSubmit();
  await nextTick();

  const {customAnnotations, annotations, customFormInvalid, specFormInvalid} = annotationsFormRef.value || {};
  if (customFormInvalid || specFormInvalid) {
    return;
  }

  formState.value.metadata.annotations = {
    ...annotations,
    ...customAnnotations,
  };

  try {
    saving.value = true;
    if (isUpdateMode.value) {
      await axiosInstance.put<ApplicationArea>(
        `/apis/applicationArea.plugin.valve.zpl/v1alpha1/applicationAreas/${formState.value.metadata.name}`,
        formState.value
      );
    } else {
      await axiosInstance.post<ApplicationArea>(`/apis/applicationArea.plugin.valve.zpl/v1alpha1/applicationAreas`, formState.value);
    }

    Toast.success("保存成功");

    onVisibleChange(false);
  } catch (e) {
    console.error(e);
  } finally {
    saving.value = false;
  }
};

const loading = ref(false);

</script>
<template>
  <VModal :title="modalTitle" :visible="visible" :width="650" @update:visible="onVisibleChange">
    <template #actions>
      <slot name="append-actions"/>
    </template>

    <FormKit
      v-if="formVisible"
      id="application-area-form"
      v-model="formState.spec"
      name="application-area-form"
      type="form"
      :config="{ validationVisibility: 'submit' }"
      :disabled="loading"
      @submit="handleSaveLink"
    >
      <div class="md:grid md:grid-cols-4 md:gap-6">
        <div class="md:col-span-1">
          <div class="sticky top-0">
            <span class="text-base text-gray-900 font-medium"> 常规 </span>
          </div>
        </div>
        <div class="mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
          <FormKit type="number" name="priority" validation="required" label="优先级（从小到大）"></FormKit>
          <FormKit type="text" name="name" validation="required" label="行业名称"></FormKit>
          <FormKit type="text" name="description" label="行业描述" validation="required"></FormKit>
          <FormKit type="attachment" name="image" validation="required" label="图片"></FormKit>
        </div>
      </div>
    </FormKit>

    <div class="py-5">
      <div class="border-t border-gray-200"></div>
    </div>

    <div class="md:grid md:grid-cols-4 md:gap-6">
      <div class="md:col-span-1">
        <div class="sticky top-0">
          <span class="text-base text-gray-900 font-medium"> 元数据 </span>
        </div>
      </div>
      <div class="mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
        <AnnotationsForm
          v-if="visible"
          :key="formState.metadata.name"
          ref="annotationsFormRef"
          :value="formState.metadata.annotations"
          kind="ApplicationArea"
          group="applicationArea.plugin.valve.zpl"
        />
      </div>
    </div>

    <template #footer>
      <VSpace>
        <VButton :loading="saving" type="secondary" @click="submitForm('application-area-form')"> 提交</VButton>
        <VButton @click="onVisibleChange(false)">取消</VButton>
      </VSpace>
    </template>
  </VModal>
</template>
