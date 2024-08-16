<script lang="ts" setup>
import {axiosInstance} from "@halo-dev/api-client";
import {Toast, VButton, VLoading, VModal, VSpace} from "@halo-dev/components";
import cloneDeep from "lodash.clonedeep";
import {computed, inject, nextTick, ref, watch, type Ref} from "vue";
import type {Banner} from "@/types";
import {submitForm} from "@formkit/core";

const props = withDefaults(
  defineProps<{
    visible: boolean;
    banner?: Banner;
  }>(),
  {
    visible: false,
    banner: undefined,
  }
);

const emit = defineEmits<{
  (event: "update:visible", value: boolean): void;
  (event: "close"): void;
}>();

const initialFormState: Banner = {
  metadata: {
    name: "",
    generateName: "banner-",
  },
  spec: {
    name: "",
    title: "",
    subtitle: "",
    bannerImagePc: "",
    bannerImageMobile: "",
    priority: 0,
  },
  kind: "Banner",
  apiVersion: "banner.plugin.valve.zpl/v1alpha1",
};

const formState = ref<Banner>(cloneDeep(initialFormState));
const saving = ref<boolean>(false);
const formVisible = ref(false);

const isUpdateMode = computed(() => {
  return !!formState.value.metadata.creationTimestamp;
});

const modalTitle = computed(() => {
  return isUpdateMode.value ? "编辑轮播图" : "新建轮播图";
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

// 如果传入 banner，则更新当前表单内容为 banner 中的内容
watch(
  () => props.banner,
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
      await axiosInstance.put<Banner>(
        `/apis/banner.plugin.valve.zpl/v1alpha1/banners/${formState.value.metadata.name}`,
        formState.value
      );
    } else {
      await axiosInstance.post<Banner>(`/apis/banner.plugin.valve.zpl/v1alpha1/banners`, formState.value);
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
      id="banner-form"
      v-model="formState.spec"
      name="banner-form"
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
          <FormKit type="text" name="name" validation="required" label="名称"></FormKit>
          <FormKit type="text" name="title" label="标题"></FormKit>
          <FormKit type="text" name="subtitle" label="副标题"></FormKit>
          <FormKit type="attachment" name="bannerImagePc" validation="required" label="PC端图片"></FormKit>
          <FormKit type="attachment" name="bannerImageMobile" validation="required" label="手机端图片"></FormKit>
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
          kind="Banner"
          group="banner.plugin.valve.zpl"
        />
      </div>
    </div>

    <template #footer>
      <VSpace>
        <VButton :loading="saving" type="secondary" @click="submitForm('banner-form')"> 提交</VButton>
        <VButton @click="onVisibleChange(false)">取消</VButton>
      </VSpace>
    </template>
  </VModal>
</template>
