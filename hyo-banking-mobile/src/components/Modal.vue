<template>
  <Transition
    enter-active-class="transition-all duration-300 ease-out"
    enter-from-class="opacity-0"
    enter-to-class="opacity-100"
    leave-active-class="transition-all duration-300 ease-in"
    leave-from-class="opacity-100"
    leave-to-class="opacity-0"
  >
    <div
      v-if="isVisible"
      class="fixed inset-0 z-50 flex items-center justify-center p-4"
      @click="handleBackdropClick"
    >
      <!-- Backdrop -->
      <div class="absolute inset-0 bg-gray-300 opacity-25"></div>

      <!-- Modal Content -->
      <Transition
        enter-active-class="transition-transform duration-300 ease-out"
        enter-from-class="scale-95 opacity-0"
        enter-to-class="scale-100 opacity-100"
        leave-active-class="transition-transform duration-300 ease-in"
        leave-from-class="scale-100 opacity-100"
        leave-to-class="scale-95 opacity-0"
      >
        <div
          v-if="isVisible"
          class="relative bg-white rounded-2xl w-full max-w-sm mx-auto shadow-xl"
          @click.stop
        >
          <!-- Header -->
          <div class="px-6 py-4 border-b border-gray-200">
            <h3 class="text-lg font-semibold text-gray-900 text-center">
              {{ title }}
            </h3>
          </div>

          <!-- Body -->
          <div class="px-6 py-4">
            <p class="text-gray-600 text-center">{{ message }}</p>
          </div>

          <!-- Footer -->
          <div class="px-6 py-4 border-t border-gray-200 flex gap-3">
            <button
              v-if="showCancel"
              @click="handleCancel"
              class="flex-1 py-2 px-4 bg-gray-200 hover:bg-gray-300 active:bg-gray-400 rounded-xl font-semibold text-gray-700 transition-colors duration-150"
            >
              {{ cancelText }}
            </button>
            <button
              @click="handleConfirm"
              class="flex-1 py-2 px-4 bg-kb-yellow-200 hover:bg-kb-yellow-100 active:bg-kb-yellow-300 rounded-xl font-semibold text-kb-brown-200 transition-colors duration-150"
            >
              {{ confirmText }}
            </button>
          </div>
        </div>
      </Transition>
    </div>
  </Transition>
</template>

<script setup>
  defineProps({
    isVisible: {
      type: Boolean,
      default: false,
    },
    title: {
      type: String,
      default: '알림',
    },
    message: {
      type: String,
      required: true,
    },
    confirmText: {
      type: String,
      default: '확인',
    },
    cancelText: {
      type: String,
      default: '취소',
    },
    showCancel: {
      type: Boolean,
      default: false,
    },
  });

  const emit = defineEmits(['confirm', 'cancel']);

  const handleConfirm = () => {
    emit('confirm');
  };

  const handleCancel = () => {
    emit('cancel');
  };

  const handleBackdropClick = () => {
    if (props.showCancel) {
      handleCancel();
    }
  };
</script>
