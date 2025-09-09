<template>
  <main
    class="min-h-screen w-full h-full flex bg-gray-100 overflow-hidden justify-center items-center"
  >
    <div
      :class="[
        'mx-auto max-w-4xl px-4 py-10 transition-all duration-500 ease-out',
        keypadOpen
          ? 'pr-[22rem] md:pr-[26rem] translate-x-[-8px]' // 오른쪽 여백 + 살짝 왼쪽 이동
          : '',
      ]"
    >
      <div class="w-full flex flex-col items-center justify-center">
        <div
          id="qr-region"
          class="w-[400px] h-[400px] rounded-xl border-4 border-kb-brown-200 shadow-lg overflow-hidden"
        ></div>
        <img src="" alt="QR 찍는 캐릭터 사진" />
        <p class="text-center text-5xl leading-relaxed font-bold whitespace-nowrap">
          안전한 QR 거래. <br />
          ATM의 <span class="text-kb-yellow-200">QR 스캐너</span>에<br class="hidden md:block" />
          휴대폰의 <span class="text-kb-yellow-200">QR</span>을 보여주세요.
        </p>
      </div>

      <div
        class="mt-10 flex flex-col sm:flex-row gap-3 w-full max-w-2xl mx-auto justify-center items-center"
      >
        <transition name="fade" mode="out-in">
          <button
            v-if="!editing"
            key="btn"
            class="flex-1 py-6 rounded-2xl bg-kb-brown-100 text-4xl text-white font-semibold shadow-sm hover:bg-kb-brown-300 focus:outline-none focus:ring-4 focus:ring-gray-200 transition-all duration-300"
            @click="startManual"
          >
            스캔이 안 될 때 (코드 입력)
          </button>

          <div
            v-else
            key="input"
            class="flex-1 flex items-center gap-2 transition-all duration-300"
          >
            <input
              type="text"
              v-model="code"
              placeholder="코드를 입력하세요"
              class="flex-1 px-4 py-4 rounded-2xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-blue-300 text-5xl"
            />
          </div>
        </transition>
      </div>
    </div>

    <!--오른쪽 슬라이드 키패드 (드로어) -->
    <aside
      :class="[
        'fixed top-1/2 -translate-y-1/2 right-4 w-[22rem] md:w-[26rem]  bg-gray-100',
        'transition-transform duration-500 ease-out flex flex-col',
        keypadOpen ? 'translate-x-0' : 'translate-x-full',
      ]"
      aria-label="수동 코드 입력 키패드"
    >
      <div class="p-4 overflow-auto flex justify-center items-center">
        <KeyPadBase16 v-if="editing" />
      </div>
    </aside>
  </main>
</template>

<script setup>
  import KeyPadBase16 from '@/components/KeyPadBase16.vue';
  import { Html5QrcodeScanner } from 'html5-qrcode';
  import { onBeforeUnmount, onMounted, ref } from 'vue';

  const scanner = ref(null);
  const editing = ref(false);
  const keypadOpen = ref(false);
  const code = ref('');

  const submitCode = () => {
    console.log('입력된 코드:', code.value);
    editing.value = false; // 입력 후 다시 버튼으로 돌아가도록
    keypadOpen.value = false;
  };

  const startManual = () => {
    editing.value = true;
    keypadOpen.value = true;
  };

  onMounted(() => {
    scanner.value = new Html5QrcodeScanner('qr-region', { fps: 10, qrbox: 250 }, false);
    scanner.value.render(
      decoded => {
        console.log('결과:', decoded);
        scanner.value.clear();
      },
      err => {
        /* 스캔 실패 로그 묵살 가능 */
      }
    );
  });
  onBeforeUnmount(async () => {
    if (scanner.value) await scanner.value.clear();
  });
</script>
<style>
  .fade-enter-active,
  .fade-leave-active {
    transition: all 0.3s ease;
  }
  .fade-enter-from,
  .fade-leave-to {
    opacity: 0;
    transform: scale(0.95);
  }
</style>
