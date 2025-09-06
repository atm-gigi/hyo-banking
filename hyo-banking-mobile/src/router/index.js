import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

// Add Transaction Routes
const addTransactionRoutes = [
  {
    path: '/add-transaction',
    component: () => import('../components/AddTransactionLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'task',
        name: 'select-task',
        component: () => import('../views/add-transaction/SelectTaskView.vue'),
        meta: { requiresAuth: true },
      },
      {
        path: 'payment',
        name: 'select-payment',
        component: () => import('../views/add-transaction/SelectPaymentView.vue'),
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
      name: 'home',
      component: () => import('../views/HomeView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/start',
      name: 'start',
      component: () => import('../views/StartView.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresAuth: false },
    },
    // Add Transaction Routes
    ...addTransactionRoutes,
    // {
    //   path: '/about',
    //   name: 'about',
    //   // route level code-splitting
    //   // this generates a separate chunk (About.[hash].js) for this route
    //   // which is lazy-loaded when the route is visited.
    //   component: () => import('../views/AboutView.vue'),
    // },
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
    if (authStore.isAuthenticated && authStore.checkTokenExpiry()) {
      // 로그인되어 있고 토큰이 유효한 경우
      next();
    } else {
      // 로그인되지 않았거나 토큰이 만료된 경우 StartView로 리다이렉트
      next('/start');
    }
  } else {
    // 인증이 필요하지 않은 페이지
    if (to.name === 'login' && authStore.isAuthenticated && authStore.checkTokenExpiry()) {
      // 이미 로그인되어 있는 경우 홈으로 리다이렉트
      next('/home');
    } else {
      next();
    }
  }
});

export default router;
