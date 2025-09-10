<script setup>
  import { useAuthStore } from '../stores/auth';
  import { useRouter } from 'vue-router';
  import { maskSensitiveInfo } from '../utils/auth';
  import { formatDate } from '../utils/formatters';
  import PrimaryBtn from '@/components/buttons/PrimaryBtn.vue';
  import IconBtn from '@/components/buttons/IconBtn.vue';
  import { ref, onMounted } from 'vue';
  import { ICON_URLS } from '@/constants';
  import { getMacros } from '@/apis';

  const authStore = useAuthStore();
  const router = useRouter();
  const macros = ref([]);
  const isLoadingMacros = ref(false);

  const handleDepositeClick = () => {
    router.push({ name: 'create-deposit' });
  };

  const handleWithdrawClick = () => {
    router.push({ name: 'create-withdraw' });
  };

  const handleTransferClick = () => {
    router.push({ name: 'create-transfer' });
  };

  const loadMacros = async () => {
    if (!authStore.userId) return;

    try {
      isLoadingMacros.value = true;
      // DRAFT와 ACTIVE 상태 모두 조회
      const response = await getMacros(authStore.userId, null, 0, 10);
      macros.value = response.content || [];
    } catch (error) {
      console.error('매크로 로드 오류:', error);
      macros.value = [];
    } finally {
      isLoadingMacros.value = false;
    }
  };

  const handleMacroClick = macro => {
    // TransactionView로 이동
    router.push({
      name: 'transaction-detail',
      params: {
        id: macro.id,
      },
    });
  };

  const handleMacroManageClick = () => {
    // 매크로 관리 페이지로 이동 (추후 구현)
    router.push({ name: 'macro-list' });
  };

  onMounted(() => {
    loadMacros();
  });

  const menuItems = [
    // {
    //   id: 'qr',
    //   text: 'QR 찍기',
    //   iconSrc: ICON_URLS.QR_CODE,
    //   iconAlt: 'qr-code--v1',
    //   handler: handleQrClick,
    // },
    {
      id: 'withdraw',
      text: '돈 뽑기',
      iconSrc: ICON_URLS.MONEY,
      iconAlt: 'money',
      handler: handleWithdrawClick,
    },
    {
      id: 'deposit',
      text: '돈 넣기',
      iconSrc: ICON_URLS.MONEY_BOX,
      iconAlt: 'money-box',
      handler: handleDepositeClick,
    },
    {
      id: 'transfer',
      text: '돈 보내기',
      iconSrc: ICON_URLS.MONEY_TRANSFER,
      iconAlt: 'initiate-money-transfer',
      handler: handleTransferClick,
    },
  ];
</script>

<template>
  <main class="relative w-full min-h-screen pb-20">
    <!-- 메인 콘텐츠 -->
    <div class="max-w-md mx-auto p-4">
      <!-- 환영 메시지 -->
      <div class="bg-white rounded-2xl p-6 mb-6 shadow-sm">
        <h2 class="text-2xl font-bold text-kb-brown-200 mb-2">
          안녕하세요, {{ authStore.userInfo?.name || '사용자' }}님
        </h2>

        <!-- 계좌 정보 -->
        <div class="rounded-lg p-4">
          <div class="text-sm text-kb-brown-200 mb-1">주계좌</div>
          <div class="font-bold text-kb-brown-200">
            {{ maskSensitiveInfo(authStore.userInfo?.accountNumber, 'account') }}
          </div>
          <div class="text-sm text-kb-gray-100">
            {{ authStore.userInfo?.bankName || '국민은행' }}
          </div>
        </div>
        <div>
          <PrimaryBtn class="w-full py-2" text="주 계좌 변경하기" />
        </div>
      </div>

      <!-- 저장된 매크로 -->
      <div class="flex flex-col gap-3 bg-white p-6 rounded-2xl shadow-sm mb-6">
        <div class="flex items-center justify-between">
          <h2 class="font-bold text-lg">저장된 매크로</h2>
          <button
            @click="handleMacroManageClick"
            class="text-sm text-blue-600 hover:text-blue-800 font-medium"
          >
            관리
          </button>
        </div>
        <hr class="border-gray-200" />

        <div v-if="isLoadingMacros" class="text-center py-8 text-gray-500">
          <p>매크로를 불러오는 중...</p>
        </div>

        <div v-else-if="macros.length === 0" class="text-center py-8 text-gray-500">
          <p>저장된 매크로가 없습니다.</p>
          <p class="text-sm mt-1">거래 생성 시 매크로로 저장해보세요!</p>
        </div>

        <div v-else class="space-y-3">
          <div
            v-for="macro in macros.slice(0, 5)"
            :key="macro.id"
            @click="handleMacroClick(macro)"
            class="flex items-center justify-between py-3 cursor-pointer hover:bg-gray-50 rounded-lg p-3 transition-colors border border-gray-100"
          >
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                <span class="text-blue-600 font-bold text-lg">📱</span>
              </div>
              <div>
                <p class="font-medium text-gray-900">{{ macro.name }}</p>
                <p class="text-sm text-gray-500">
                  {{ formatDate(macro.createdAt) }}
                </p>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <span
                class="text-xs px-2 py-1 rounded-full"
                :class="{
                  'bg-green-100 text-green-700': macro.status === 'ACTIVE',
                  'bg-yellow-100 text-yellow-700': macro.status === 'DRAFT',
                  'bg-gray-100 text-gray-700': macro.status === 'INACTIVE',
                }"
              >
                {{
                  macro.status === 'DRAFT'
                    ? '작성중'
                    : macro.status === 'ACTIVE'
                      ? '활성'
                      : '비활성'
                }}
              </span>
              <img class="size-4" width="30" height="30" :src="ICON_URLS.FORWARD" alt="forward" />
            </div>
          </div>
        </div>
      </div>

      <!-- 메뉴 버튼들 -->
      <div class="flex flex-col gap-3 bg-white p-6 rounded-2xl shadow-sm mb-6">
        <h2 class="font-bold text-lg">ATM 거래 추가하기</h2>
        <hr class="border-gray-200" />
        <template v-for="(item, index) in menuItems" :key="item.id">
          <IconBtn
            :text="item.text"
            :icon-src="item.iconSrc"
            :icon-alt="item.iconAlt"
            @click="item.handler"
          />
          <hr v-if="index < menuItems.length - 1" class="border-gray-200" />
        </template>
      </div>

      <!-- QR 예제 버튼 -->
      <div class="bg-white rounded-2xl shadow-sm p-6">
        <h2 class="font-bold text-lg mb-4">개발자 도구</h2>
        <button
          @click="() => router.push({ name: 'qr-example' })"
          class="w-full py-3 px-4 bg-purple-600 text-white rounded-lg hover:bg-purple-700 transition-colors font-medium flex items-center justify-center gap-2"
        >
          <span>📱</span>
          QR 코드 생성/스캔 예제
        </button>
      </div>
    </div>
  </main>
</template>
