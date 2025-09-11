<template>
  <Transition
    enter-active-class="transition-transform duration-300 ease-out"
    enter-from-class="translate-y-full"
    enter-to-class="translate-y-0"
    leave-active-class="transition-transform duration-300 ease-in"
    leave-from-class="translate-y-0"
    leave-to-class="translate-y-full"
  >
    <div
      v-if="isVisible"
      class="fixed inset-0 z-50 flex items-end justify-center"
      @click="handleBackdropClick"
    >
      <!-- Backdrop -->
      <div class="absolute inset-0 duration-300"></div>

      <!-- Bank Selector -->
      <div
        v-if="isVisible"
        class="relative bg-gray-100 rounded-t-3xl w-full max-w-md max-h-[70vh]"
        @click.stop
      >
        <!-- Handle bar -->
        <div class="flex justify-center pt-3 pb-2">
          <div class="w-12 h-1 bg-gray-300 rounded-full"></div>
        </div>

        <!-- Header -->
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-bold text-center text-gray-900">은행 선택</h3>
        </div>

        <!-- Bank List -->
        <div class="px-6 py-4 max-h-96 overflow-y-auto">
          <div class="grid grid-cols-3 gap-3">
            <button
              v-for="(bank, i) in banks"
              :key="bank.code"
              @click="selectBank(bank)"
              :class="[
                'w-full pl-2 py-2 rounded-xl transition',
                selectedBank?.code === bank.code
                  ? 'bg-kb-yellow-200 text-kb-brown-200'
                  : 'bg-white hover:bg-kb-yellow-100 active:brightness-95 active:scale-95',
              ]"
            >
              <div class="flex items-center justify-start">
                <div
                  :class="[
                    'w-8 h-8 rounded-full flex items-center justify-center text-white font-bold text-xs mr-2',
                    getBankGradient(i),
                  ]"
                >
                  {{ bank.name.charAt(0) }}
                </div>
                <div class="font-semibold text-sm">{{ bank.name }}</div>
              </div>
            </button>
          </div>
        </div>

        <!-- Action buttons -->
        <div class="flex gap-4 p-6 pt-4">
          <button
            @click="cancel"
            class="flex-1 h-14 bg-gray-200 hover:bg-gray-300 active:bg-gray-400 rounded-xl font-semibold text-lg transition-colors duration-150"
          >
            취소
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import { BANKS } from '@/constants';

  const props = defineProps({
    isVisible: { type: Boolean, default: false },
    selectedBank: { type: Object, default: null },
  });

  const emit = defineEmits(['confirm', 'cancel', 'update:selectedBank']);

  const banks = ref(BANKS);

  const selectedBank = ref(props.selectedBank);

  watch(
    () => props.selectedBank,
    newValue => {
      selectedBank.value = newValue;
    }
  );

  const getBankGradient = bankCode => {
    const gradients = [
      'bg-gradient-to-br from-kb-yellow-100 to-kb-yellow-200',
      'bg-gradient-to-br from-green-500 to-green-600',
      'bg-gradient-to-br from-purple-500 to-purple-600',
      'bg-gradient-to-br from-red-500 to-red-600',
      'bg-gradient-to-br from-yellow-500 to-yellow-600',
      'bg-gradient-to-br from-pink-500 to-pink-600',
      'bg-gradient-to-br from-indigo-500 to-indigo-600',
      'bg-gradient-to-br from-teal-500 to-teal-600',
      'bg-gradient-to-br from-orange-500 to-orange-600',
      'bg-gradient-to-br from-cyan-500 to-cyan-600',
      'bg-gradient-to-br from-emerald-500 to-emerald-600',
      'bg-gradient-to-br from-violet-500 to-violet-600',
      'bg-gradient-to-br from-rose-500 to-rose-600',
      'bg-gradient-to-br from-amber-500 to-amber-600',
      'bg-gradient-to-br from-lime-500 to-lime-600',
      'bg-gradient-to-br from-sky-500 to-sky-600',
      'bg-gradient-to-br from-fuchsia-500 to-fuchsia-600',
      'bg-gradient-to-br from-slate-500 to-slate-600',
      'bg-gradient-to-br from-zinc-500 to-zinc-600',
      'bg-gradient-to-br from-stone-500 to-stone-600',
      'bg-gradient-to-br from-gray-500 to-gray-600',
      'bg-gradient-to-br from-neutral-500 to-neutral-600',
      'bg-gradient-to-br from-slate-400 to-slate-500',
      'bg-gradient-to-br from-gray-400 to-gray-500',
      'bg-gradient-to-br from-zinc-400 to-zinc-500',
      'bg-gradient-to-br from-neutral-400 to-neutral-500',
      'bg-gradient-to-br from-stone-400 to-stone-500',
      'bg-gradient-to-br from-red-400 to-red-500',
      'bg-gradient-to-br from-orange-400 to-orange-500',
      'bg-gradient-to-br from-amber-400 to-amber-500',
    ];

    const index = parseInt(bankCode) % gradients.length;
    return gradients[index];
  };

  const selectBank = bank => {
    selectedBank.value = bank;
    emit('update:selectedBank', bank);
    emit('confirm', bank);
  };

  const cancel = () => {
    emit('cancel');
  };

  const handleBackdropClick = () => {
    cancel();
  };
</script>
