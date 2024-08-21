<script setup lang="ts" xmlns="">
import {computed, nextTick, ref, watch} from "vue";
import {useRouteQuery} from "@vueuse/router";
import type {HomeAbout, Timeline} from "@/types";
import {
  VPageHeader,
  IconImageAddLine,
  IconAddCircle,
  IconRefreshLine,
  VSpace,
  VButton,
  VCard,
  VLoading,
  VEmpty,
  VEntity,
  VEntityField,
  VAvatar,
  VPagination,
  Dialog,
  Toast,
  VStatusDot,
  VTag,
  VDropdownItem,
  IconTimerLine
} from "@halo-dev/components";

import {FcAbout} from "oh-vue-icons/icons";
import {initialHomeAboutFormState, useHomeAboutFetch} from "@/composables/use-home-about";
import cloneDeep from "lodash.clonedeep";
import {submitForm} from "@formkit/core";
import {axiosInstance} from "@halo-dev/api-client";
import {Icon} from "@iconify/vue";

const saving = ref<boolean>(false);

const {
  homeAbout, refetch, isLoading
} = useHomeAboutFetch();

const formState = ref<HomeAbout>(cloneDeep(initialHomeAboutFormState));

watch(() => homeAbout.value, (newHomeAbout) => {
  if (newHomeAbout) {
    formState.value = cloneDeep(newHomeAbout);
    console.log(newHomeAbout);
  }
})

const handleSaveLink = async () => {
  await nextTick();

  try {
    saving.value = true;
 
    await axiosInstance.put<Timeline>(
      `/apis/homeAbout.plugin.valve.zpl/v1alpha1/homeAbouts/${formState.value.metadata.name}`,
      formState.value
    );
    
    Toast.success("保存成功");
    refetch();
  } catch (e) {
    console.error(e);
  } finally {
    saving.value = false;
  }
};

</script>

<template>
  <VPageHeader title="首页关于">
    <template #icon>
      <Icon icon="cib:about-me" class="mr-2 self-center"/>
    </template>
  </VPageHeader>

  <div class="m-0 md:m-4">
    <VCard :body-class="['!p-0']">
      <template #header>
        <div class="block w-full bg-gray-50 px-4 py-3">
          <div
            class="relative flex flex-col flex-wrap items-end gap-4"
          >
            <VSpace spacing="lg" class="flex-wrap">
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
            </VSpace>
          </div>
        </div>
      </template>

      <VLoading v-if="isLoading"/>

      <Transition v-else appear name="fade">
        <FormKit
          id="homeAbout-form"
          v-model="formState.spec"
          name="homeAbout-form"
          type="form"
          :config="{ validationVisibility: 'submit' }"
          :disabled="isLoading"
          @submit="handleSaveLink"
        >
          <div class="">
            <div class="my-5 divide-y divide-gray-100 pl-5">
              <FormKit type="text" name="name" validation="required" label="企业名称"></FormKit>
              <FormKit type="text" name="description" validation="required" label="企业描述（简短）"></FormKit>
              <FormKit type="url" name="aboutUrl" label="查看更多（链接）" validation="required"></FormKit>
              <FormKit type="attachment" name="link1Image" label="图片1"></FormKit>
              <FormKit type="url" name="link1Url" label="图片1跳转链接"></FormKit>
              <FormKit type="text" name="link1Text" label="图片1文本"></FormKit>
              <FormKit type="attachment" name="link2Image" label="图片2"></FormKit>
              <FormKit type="url" name="link2Url" label="图片2跳转链接"></FormKit>
              <FormKit type="text" name="link2Text" label="图片2文本"></FormKit>
              <FormKit type="attachment" name="publicityImage" label="宣传大图" validation="required"></FormKit>
              <FormKit type="text" name="publicityImageText1" label="宣传大图文本1"></FormKit>
              <FormKit type="text" name="publicityImageText2" label="宣传大图文本2"></FormKit>
              <FormKit type="text" name="publicityImageText3" label="宣传大图文本3"></FormKit>
              <FormKit type="text" name="publicityImageText4" label="宣传大图文本4"></FormKit>
              <VButton :loading="saving" type="secondary" @click="submitForm('homeAbout-form')"> 提交 </VButton>
            </div>
          </div>
        </FormKit>
      </Transition>
    </VCard>
  </div>

</template>

<style scoped lang="scss">

</style>
