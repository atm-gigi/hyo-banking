import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/choose-task',
      name: 'choose-task',
      component: () => import('../views/ChooseTaskView.vue'),
    },
    {
      path: '/payment-method',
      name: 'payment-method',
      component: () => import('../views/PaymentMethodView.vue'),
    },
    {
      path: '/put-payment',
      name: 'put-payment',
      component: () => import('../views/PutPaymentView.vue'),
    },
    {
      path: '/select-amount',
      name: 'select-amount',
      component: () => import('../views/SelectAmountView.vue'),
    },
    {
      path: '/ten-key',
      name: 'ten-key',
      component: () => import('../views/TenKeyView.vue'),
    },
    {
      path: '/get-cash',
      name: 'get-cash',
      component: () => import('../views/InputCashView.vue'),
    },
    {
      path: '/manual-input',
      name: 'manual-input',
      component: () => import('../views/ManualInputView.vue'),
    },
    {
      path: '/input-password',
      name: 'input-password',
      component: () => import('../views/InputPasswordView.vue'),
    },
    {
      path: '/loading',
      name: 'loading',
      component: () => import('../views/LoadingView.vue'),
    },
    {
      path: '/end-transaction',
      name: 'end-transaction',
      component: () => import('../views/EndTransactionView.vue'),
    },
    {
      path: '/undo-transaction',
      name: 'undo-transaction',
      component: () => import('../views/UndoTransactionView.vue'),
    },
    {
      path:'/suggest-door-phone',
      name:'suggest-door-phone',
      component: () => import('../views/SuggestDoorPhoneView.vue'),
    },
    {
      path:'/error',
      name:'error',
      component: () => import('../views/ErrorView.vue'),
    }
  ],
});

export default router;
