<template>
  <main class="w-full h-full flex overflow-hidden justify-center items-center">
    <div
      :class="[
        'mx-auto max-w-4xl px-4 py-10 transition-all duration-500 ease-out',
        keypadOpen
          ? 'pr-[28rem] md:pr-[28rem] translate-x-[-8px]' // 오른쪽 여백 + 살짝 왼쪽 이동
          : '',
      ]"
    >
      <div class="w-full flex flex-col items-center justify-center">
        <div
          id="qr-region"
          class="w-[400px] h-[400px] rounded-xl border-4 border-kb-brown-200 shadow-lg overflow-hidden"
        ></div>
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
              ref="codeRef"
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
        'fixed top-1/2 -translate-y-1/2 right-4 w-[22rem] md:w-[26rem]',
        'transition-transform duration-500 ease-out flex flex-col',
        keypadOpen ? 'translate-x-0' : 'translate-x-full',
      ]"
      aria-label="수동 코드 입력 키패드"
    >
      <div class="p-4 overflow-auto flex justify-center items-center">
        <KeyPadBase16 v-if="editing" @key-click="keyClick" />
      </div>
    </aside>
  </main>
</template>

<script setup>
  import KeyPadBase16 from '@/components/KeyPadBase16.vue';
  import { Html5QrcodeScanner } from 'html5-qrcode';
  import { nextTick, onBeforeUnmount, onMounted, ref, computed } from 'vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const scanner = ref(null);
  const editing = ref(false);
  const code = ref('');
  const codeRef = ref(null);

  const keypadOpen = computed(() => {
    return editing.value;
  });

  const keyClick = key => {
    codeRef.value?.focus();
    if (key === '정정') {
      code.value = code.value.slice(0, -1);
    } else if (key === '결정') {
      if (code.value.length === 32) toMacroSteps(code.value);
      else alert('유효한 코드가 아닙니다.');
    } else if (key === '지움') {
      code.value = '';
    } else if (key === '닫기') {
      editing.value = false;
    } else if (code.value.length <= 36) {
      code.value += key;
    }

    nextTick(() => {
      const el = codeRef.value;
      if (!el) return;
      const pos = el.value.length;
      el.focus();
      el.setSelectionRange(pos, pos); // 커서를 끝으로 이동
      el.scrollLeft = el.scrollWidth;

      // 필요하면 화면도 따라오게
      el.scrollIntoView({ block: 'nearest', inline: 'nearest', behavior: 'smooth' });
    });
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
        const cleanToken = decoded.replace(/"/g, '');
        toMacroSteps(cleanToken);
        scanner.value.clear();
      },
      err => {}
    );
  });
  onBeforeUnmount(async () => {
    if (scanner.value) await scanner.value.clear();
  });

  const toMacroSteps = code => {
    // GET /api/qr-tokens/{code} 유효성 검증
    // get macro id
    // GET /api/macro/{id}/steps
    // 매크로에서 사용되는 총 필요한 현금
    // + 일경우 돈입금부터 -일경우 마지막에 출금
    // 매크로에서 사용되는 통장이나 카드 확인
    // 최종적으로 POST /api/executions { "macroId": macro id,  "qrToken": code } 실행후 id값 받아서
    // GET /api/executions/{macroExecutionId} 로 처리 상태 확인(폴링방식)
    router.push({ name: 'input-total-cash', query: { code } });
  };
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
