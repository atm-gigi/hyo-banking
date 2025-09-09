<script setup>
  import { useRoute, useRouter } from 'vue-router';
  import { banks } from '@/constants/bank.js';
  import { atmTransactionStore } from '@/stores/atmTransactionStore';

  const route = useRoute();
  const router = useRouter();
  const atmStore = atmTransactionStore();

  function getImg(path) {
    return new URL(`../assets/banks/${path}`, import.meta.url).href;
  }

  const bankCodeMapping = {
    국민은행: 'KB',

    신한은행: 'SHINHAN',

    우리은행: 'WOORI',

    하나은행: 'HANA',

    농협은행: 'NH',

    기업은행: 'IBK',

    수협은행: 'SH',

    제일은행: 'SC',

    부산은행: 'BNK',

    대구은행: 'DGB',

    광주은행: 'GWANGJU',

    새마을금고: 'MG',

    신협: 'SHINHYUP',
  };

  const handleClick = bank => {
    const bankCode = bankCodeMapping[bank.name];
    atmStore.setTargetBankCode(bankCode);
    console.log('선택한 은행:', bankCode);

    router.push({
      name: 'input-account',
      query: { ...route.query },
    });
  };
</script>
<template>
  <main class="flex flex-col h-screen p-10 bg-gray-100">
    <div class="flex-shrink-0 text-center mb-5">
      <p class="text-5xl text-center font-bold text-black">돈 받으실 분의 은행을 골라주세요</p>
    </div>
    <div class="flex-1 overflow-y-auto relative">
      <div class="grid grid-cols-3 gap-4">
        <button
          v-for="bank in banks"
          :key="bank.name"
          @click="handleClick(bank)"
          class="bg-white py-6 active:scale-95 active:bg-gray-200 rounded-lg shadow flex items-center justify-center gap-x-3 transition-all"
        >
          <img :src="getImg(bank.logo)" :alt="`${bank.name} 로고`" class="w-10 h-10" />
          <p class="font-semibold text-3xl">{{ bank.name }}</p>
        </button>
      </div>
    </div>
  </main>
</template>
