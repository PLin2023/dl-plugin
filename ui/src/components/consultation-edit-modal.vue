<script lang="ts" setup>
import { ref, watch } from 'vue';
import { VModal, VSpace, VButton, Toast } from '@halo-dev/components';
import type { Consultation } from '@/types';
import { axiosInstance } from "@halo-dev/api-client";

const props = defineProps<{
  visible: boolean;
  consultation?: Consultation;
}>();

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void;
  (e: 'close'): void;
}>();

const formState = ref<Consultation['spec']>({
  name: '',
  email: '',
  content: '',
  status: 'Unread',
});

watch(() => props.consultation, (newVal) => {
  if (newVal) {
    formState.value = { ...newVal.spec };
  }
}, { immediate: true });

const handleSave = async () => {
  try {
    if (props.consultation) {
      await axiosInstance.put(`/apis/consultation.plugin.halo.run/v1alpha1/consultations/${props.consultation.metadata.name}`, {
        ...props.consultation,
        spec: formState.value,
      });
      Toast.success('保存成功');
      emit('close');
    }
  } catch (e) {
    console.error(e);
    Toast.error('保存失败');
  }
};
</script>

<template>
  <VModal :visible="visible" @update:visible="(v) => emit('update:visible', v)" @close="emit('close')">
    <template #title>
      查看/编辑咨询信息
    </template>
    <div class="space-y-4">
      <div>
        <label class="block mb-1 font-medium text-gray-700">姓名</label>
        <input v-model="formState.name" class="w-full px-3 py-2 border rounded-md" readonly />
      </div>
      <div>
        <label class="block mb-1 font-medium text-gray-700">邮箱</label>
        <input v-model="formState.email" class="w-full px-3 py-2 border rounded-md" readonly />
      </div>
      <div>
        <label class="block mb-1 font-medium text-gray-700">内容</label>
        <textarea v-model="formState.content" class="w-full px-3 py-2 border rounded-md" rows="4" readonly></textarea>
      </div>
      <div>
        <label class="block mb-1 font-medium text-gray-700">状态</label>
        <select v-model="formState.status" class="w-full px-3 py-2 border rounded-md">
          <option value="Unread">未读</option>
          <option value="Read">已读</option>
          <option value="Responded">已回复</option>
        </select>
      </div>
    </div>
    <template #footer>
      <VSpace>
        <VButton type="secondary" @click="handleSave">保存</VButton>
        <VButton @click="emit('close')">取消</VButton>
      </VSpace>
    </template>
  </VModal>
</template>
