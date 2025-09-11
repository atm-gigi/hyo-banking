<template>
  <main class="min-h-screen w-full flex items-center justify-center">
    <div class="fixed top-4 left-4 z-50">
      <label class="relative inline-flex items-center cursor-pointer select-none">
        <input type="checkbox" v-model="voiceGuide" class="sr-only peer" />
        <div
          class="w-16 h-9 rounded-full bg-gray-300 transition-colors peer-checked:bg-kb-brown-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-gray-200"
        ></div>
        <span
          class="absolute top-1 left-1 w-7 h-7 bg-white rounded-full shadow transform transition-transform peer-checked:translate-x-7"
        ></span>
        <span class="ml-3 text-xl">음성 안내</span>
      </label>
    </div>

    <div class="w-full max-w-5xl px-6 py-10 text-center">
      <!-- 헤더 -->
      <h1 class="text-5xl font-extrabold tracking-tight mb-4">
        간편한 ATM 거래를 위한 <span class="text-kb-yellow-200">QR 안내</span>
      </h1>
      <p class="text-2xl text-gray-700 mb-8">
        아래 <span class="font-bold">3가지</span>만 준비해 주세요.
      </p>

      <!-- 3단계 카드 -->
      <ol class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <!-- 2. 스캐너에 천천히 대기 -->

        <li class="bg-white rounded-2xl shadow p-6 text-left">
          <div class="flex items-center gap-3 mb-4 justify-center">
            <svg class="w-10 h-10" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
              <path d="M4 5h16v2H4V5zm2 4h12v8H6V9zm-2 10h16v2H4v-2z" />
            </svg>
            <h2 class="text-4xl font-bold mb-2">
              스캐너에 <span class="text-kb-yellow-200">천천히</span> 갖다 대기
            </h2>
          </div>
          <div class="flex justify-center">
            <img src="@/assets/qr/qr_guide_image.png" class="w-70 h-70 object-contain scale-150" />
          </div>
        </li>

        <!-- 3. 1~2초 정지 / 성공 신호 -->
        <li class="bg-white rounded-2xl shadow p-6 text-left">
          <div class="flex items-center gap-3 mb-4 justify-center">
            <svg class="w-10 h-10" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
              <path
                d="M12 22c5.523 0 10-4.477 10-10S17.523 2 12 2 2 6.477 2 12s4.477 10 10 10zM10.5 16l-3-3 1.414-1.414L10.5 13.172l5.586-5.586L17.5 9l-7 7z"
              />
            </svg>
            <h2 class="text-4xl font-bold">
              잠시 <span class="text-kb-yellow-200">멈추면</span> 자동 인식
            </h2>
          </div>

          <div class="flex justify-center">
            <img src="@/assets/qr/qr.png" class="w-70 h-70 object-contain" />
          </div>
        </li>
      </ol>

      <!-- 옵션 & 문제해결 -->
      <div class="mt-3 flex flex-col gap-4 items-center">
        <details class="w-full max-w-3xl bg-white rounded-2xl shadow p-6">
          <summary class="text-left text-xl font-semibold cursor-pointer">
            스캔이 잘 안 되나요?
          </summary>
          <ul class="list-disc text-left pl-6 mt-3 text-lg leading-relaxed">
            <li>화면 밝기를 더 올려 보세요. (자동 밝기 꺼두면 더 좋아요)</li>
            <li>화면에 <span class="font-semibold">QR이 화면 가득</span> 차게 확대해 보세요.</li>
            <li>거리를 <span class="font-semibold">가까이→멀리</span> 천천히 바꿔 보세요.</li>
            <li>액정 보호필름/지문·물기/빛 반사가 있으면 닦아 주세요.</li>
          </ul>
        </details>
      </div>

      <!-- 시작하기 버튼 -->
      <div class="mt-3">
        <button
          @click="goScan"
          class="w-full sm:w-auto px-10 py-6 text-3xl rounded-2xl bg-kb-brown-100 text-white font-bold shadow hover:bg-kb-brown-300 focus:outline-none focus:ring-4 focus:ring-gray-200 transition"
        >
          시작하기
        </button>
      </div>
    </div>
  </main>
</template>

<script setup>
  import { ref, watch } from 'vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const voiceGuide = ref(false);

  const SPEECH_TEXT =
    '간편한 ATM 거래를 위한 QR 안내입니다. 첫째, 휴대폰 밝기를 최대로 하고 QR이 크게 보이게 해 주세요. ' +
    '둘째, ATM의 QR 스캐너에 약 10에서 15센티미터 거리로 천천히 갖다 대 주세요. ' +
    '셋째, 1에서 2초만 가만히 있으면 자동으로 인식되고 소리 또는 초록불이 보입니다. 준비가 되면 시작하기 버튼을 눌러 주세요.';

  function speak(text) {
    // 사용자 조작 없이 자동 재생이 막힐 수 있으므로, 토글/버튼 이벤트 직후에만 호출 권장
    try {
      const utter = new SpeechSynthesisUtterance(text);
      utter.lang = 'ko-KR';
      window.speechSynthesis.cancel();
      window.speechSynthesis.speak(utter);
    } catch {}
  }

  watch(voiceGuide, v => {
    if (v) speak(SPEECH_TEXT);
    else window.speechSynthesis?.cancel();
  });

  function goScan() {
    // 음성 중이면 정리
    window.speechSynthesis?.cancel();
    router.push({ name: 'use-qr-token' }); // 실제 스캐너 라우트 이름으로 변경
  }
</script>
