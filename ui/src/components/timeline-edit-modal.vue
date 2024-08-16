<script lang="ts" setup>
import {axiosInstance} from "@halo-dev/api-client";
import {Toast, VButton, VLoading, VModal, VSpace} from "@halo-dev/components";
import cloneDeep from "lodash.clonedeep";
import {computed, inject, nextTick, ref, watch, type Ref} from "vue";
import type {Timeline} from "@/types";
import {submitForm} from "@formkit/core";

const props = withDefaults(
  defineProps<{
    visible: boolean;
    timeline?: Timeline;
  }>(),
  {
    visible: false,
    timeline: undefined,
  }
);

const emit = defineEmits<{
  (event: "update:visible", value: boolean): void;
  (event: "close"): void;
}>();

const initialFormState: Timeline = {
  metadata: {
    name: "",
    generateName: "timeline-",
  },
  spec: {
    datetime: "",
    title: "",
    priority: 0,
  },
  kind: "Timeline",
  apiVersion: "timeline.plugin.valve.zpl/v1alpha1",
};

const formState = ref<Timeline>(cloneDeep(initialFormState));
const saving = ref<boolean>(false);
const formVisible = ref(false);

const isUpdateMode = computed(() => {
  return !!formState.value.metadata.creationTimestamp;
});

const modalTitle = computed(() => {
  return isUpdateMode.value ? "编辑时间线节点" : "新建时间线节点";
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

// 如果传入 timeline，则更新当前表单内容为 timeline 中的内容
watch(
  () => props.timeline,
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
      await axiosInstance.put<Timeline>(
        `/apis/timeline.plugin.valve.zpl/v1alpha1/timelines/${formState.value.metadata.name}`,
        formState.value
      );
    } else {
      await axiosInstance.post<Timeline>(`/apis/timeline.plugin.valve.zpl/v1alpha1/timelines`, formState.value);
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
      id="timeline-form"
      v-model="formState.spec"
      name="timeline-form"
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
          <FormKit type="month" name="datetime" validation="required" label="时间"></FormKit>
          <FormKit type="text" name="title" label="标题" validation="required"></FormKit>
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
          kind="Timeline"
          group="timeline.plugin.valve.zpl"
        />
      </div>
    </div>

    <template #footer>
      <VSpace>
        <VButton :loading="saving" type="secondary" @click="submitForm('timeline-form')"> 提交</VButton>
        <VButton @click="onVisibleChange(false)">取消</VButton>
      </VSpace>
    </template>
  </VModal>
</template>
