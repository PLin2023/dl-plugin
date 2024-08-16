<script lang="ts" setup>
import {axiosInstance} from "@halo-dev/api-client";
import {Toast, VButton, VLoading, VModal, VSpace} from "@halo-dev/components";
import cloneDeep from "lodash.clonedeep";
import {computed, inject, nextTick, ref, watch, type Ref} from "vue";
import type {Product, ProductCategory} from "@/types";
import {submitForm} from "@formkit/core";

const props = withDefaults(
  defineProps<{
    visible: boolean;
    product?: Product;
  }>(),
  {
    visible: false,
    product: undefined,
  }
);

const emit = defineEmits<{
  (event: "update:visible", value: boolean): void;
  (event: "close"): void;
}>();

const initialFormState: Product = {
  metadata: {
    name: "",
    generateName: "product-",
  },
  spec: {
    name: "",
    categoryMetadataName: "",
    description: "",
    standards: [],
    images: [],
    priority: 0,
  },
  kind: "Product",
  apiVersion: "product.plugin.valve.zpl/v1alpha1",
};

const formState = ref<Product>(cloneDeep(initialFormState));
const saving = ref<boolean>(false);
const formVisible = ref(false);
const categories = ref<ProductCategory[]>([]);

const isUpdateMode = computed(() => {
  return !!formState.value.metadata.creationTimestamp;
});

const modalTitle = computed(() => {
  return isUpdateMode.value ? "编辑产品" : "新建产品";
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
      fetchCategories();
    } else {
      setTimeout(() => {
        formVisible.value = false;
        handleResetForm();
      }, 200);
    }
  }
);

watch(
  () => props.product,
  (product) => {
    if (product) {
      formState.value = cloneDeep(product);
    }
  }
);

const fetchCategories = async () => {
  try {
    const {data} = await axiosInstance.get<{ items: ProductCategory[] }>(
      "/apis/productCategory.plugin.valve.zpl/v1alpha1/productCategorys"
    );
    categories.value = data.items;
  } catch (e) {
    console.error("Failed to fetch categories", e);
  }
};

const annotationsFormRef = ref();

const handleSaveProduct = async () => {
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
      await axiosInstance.put<Product>(
        `/apis/product.plugin.valve.zpl/v1alpha1/products/${formState.value.metadata.name}`,
        formState.value
      );
    } else {
      await axiosInstance.post<Product>(`/apis/product.plugin.valve.zpl/v1alpha1/products`, formState.value);
    }

    Toast.success("保存成功");

    onVisibleChange(false);
  } catch (e) {
    console.error(e);
  } finally {
    saving.value = false;
  }
};

const addStandard = () => {
  formState.value.spec.standards.push({name: "", value: ""});
};

const removeStandard = (index: number) => {
  formState.value.spec.standards.splice(index, 1);
};

const addImage = () => {
  formState.value.spec.images.push({url: "", priority: formState.value.spec.images.length});
};

// const updateImageUrl = (index: number, url: string) => {
//   formState.value.spec.images[index].url = url;
// };

const removeImage = (index: number) => {
  formState.value.spec.images.splice(index, 1);
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
      id="product-form"
      v-model="formState.spec"
      name="product-form"
      type="form"
      :config="{ validationVisibility: 'submit' }"
      :disabled="loading"
      @submit="handleSaveProduct"
    >
      <div class="md:grid md:grid-cols-4 md:gap-6">
        <div class="md:col-span-1">
          <div class="sticky top-0">
            <span class="text-base text-gray-900 font-medium">基本信息</span>
          </div>
        </div>
        <div class="mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
          <FormKit type="text" name="name" validation="required" label="名称"></FormKit>
          <FormKit type="select" name="categoryMetadataName" validation="required" label="产品分类">
            <option v-for="category in categories" :key="category.metadata.name" :value="category.metadata.name">
              {{ category.spec.name }}
            </option>
          </FormKit>
          <FormKit type="textarea" name="description" validation="required" label="描述"></FormKit>
          <FormKit type="number" name="priority" validation="required" label="优先级（从小到大）"></FormKit>
          <FormKit type="text" name="sku" label="SKU"></FormKit>
          <FormKit type="number" name="price" label="价格"></FormKit>
        </div>
      </div>

      <div class="md:grid md:grid-cols-4 md:gap-6 mt-8">
        <div class="md:col-span-1">
          <div class="sticky top-0">
            <span class="text-base text-gray-900 font-medium">产品标准</span>
          </div>
        </div>
        <div class="mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
          <div v-for="(standard, index) in formState.spec.standards" :key="index" class="flex items-center gap-4 py-2">
            <FormKit type="text" v-model="standard.name" :name="`standards[${index}].name`" label="标准名称"></FormKit>
            <FormKit type="text" outer-class="pt-0" v-model="standard.value" :name="`standards[${index}].value`" label="标准值"></FormKit>
            <VButton @click="removeStandard(index)" type="danger">删除</VButton>
          </div>
          <VButton @click="addStandard" type="secondary" class="mt-4">添加标准</VButton>
        </div>
      </div>

      <div class="md:grid md:grid-cols-4 md:gap-6 mt-8">
        <div class="md:col-span-1">
          <div class="sticky top-0">
            <span class="text-base text-gray-900 font-medium">产品图片</span>
          </div>
        </div>
        <div class="mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
          <div v-for="(image, index) in formState.spec.images" :key="index" class="flex items-center gap-4 py-2">
            <FormKit
              type="attachment"
              :name="`images[${index}].url`"
              label="图片"
              validation="required"
              v-model="image.url"
            />
            <FormKit type="number" outer-class="pt-0" v-model="image.priority" :name="`images[${index}].priority`" label="优先级"></FormKit>
            <VButton @click="removeImage(index)" type="danger">删除</VButton>
          </div>
          <VButton @click="addImage" type="secondary" class="mt-4">添加图片</VButton>
        </div>
      </div>
    </FormKit>

    <div class="py-5">
      <div class="border-t border-gray-200"></div>
    </div>

    <div class="md:grid md:grid-cols-4 md:gap-6">
      <div class="md:col-span-1">
        <div class="sticky top-0">
          <span class="text-base text-gray-900 font-medium">元数据</span>
        </div>
      </div>
      <div class="mt-5 md:col-span-3 md:mt-0 divide-y divide-gray-100">
        <AnnotationsForm
          v-if="visible"
          :key="formState.metadata.name"
          ref="annotationsFormRef"
          :value="formState.metadata.annotations"
          kind="Product"
          group="product.plugin.valve.zpl"
        />
      </div>
    </div>

    <template #footer>
      <VSpace>
        <VButton :loading="saving" type="secondary" @click="submitForm('product-form')">提交</VButton>
        <VButton @click="onVisibleChange(false)">取消</VButton>
      </VSpace>
    </template>
  </VModal>
</template>

<style scoped>
/* Add any component-specific styles here if needed */
</style>
