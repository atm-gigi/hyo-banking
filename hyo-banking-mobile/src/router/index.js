import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

// Transaction Routes
const addTransactionRoutes = [
  {
    path: '/transaction',
    component: () => import('../components/layouts/TransactionLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'deposit',
        name: 'create-deposit',
        component: () => import('../views/transaction/CreateDepositView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'withdraw',
        name: 'create-withdraw',
        component: () => import('../views/transaction/CreateWithdrawView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'transfer',
        name: 'create-transfer',
        component: () => import('../views/transaction/CreateTransferView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'transfer-amount',
        name: 'transfer-amount',
        component: () => import('../views/transaction/TransferAmountView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'detail/:id',
        name: 'transaction-detail',
        component: () => import('../views/transaction/TransactionView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'qr/:id',
        name: 'show-qr',
        component: () => import('../views/transaction/ShowQrView.vue'),
        meta: { requiresAuth: true },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: () => import('../components/layouts/DefaultLayout.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/HomeView.vue'),
          meta: { requiresAuth: true },
        },
        {
          path: 'start',
          name: 'start',
          component: () => import('../views/StartView.vue'),
          meta: { requiresAuth: false },
        },
        {
          path: 'login',
          name: 'login',
          component: () => import('../views/LoginView.vue'),
          meta: { requiresAuth: false },
        },
        {
          path: 'join',
          name: 'join',
          component: () => import('../views/JoinView.vue'),
          meta: { requiresAuth: false },
        },
      ],
    },

    // Transaction Routes
    ...addTransactionRoutes,
  ],
});

// 라우터 가드
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  // 로그인 상태 초기화 (페이지 새로고침 시)
  if (!authStore.isLoggedIn) {
    authStore.initializeAuth();
  }

  // 인증이 필요한 페이지인지 확인
  if (to.meta.requiresAuth) {
    if (authStore.isAuthenticated) {
      // 로그인되어 있는 경우
      next();
    } else {
      // 로그인되지 않은 경우 StartView로 리다이렉트
      next('/start');
    }
  } else {
    // 인증이 필요하지 않은 페이지
    const publicPages = ['start', 'login', 'join'];

    if (publicPages.includes(to.name) && authStore.isAuthenticated) {
      // 이미 로그인되어 있는 경우 홈으로 리다이렉트
      next('/');
    } else {
      next();
    }
  }
});

export default router;
