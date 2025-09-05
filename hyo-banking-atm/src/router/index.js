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
      path: '/select-task',
      name: 'select-task',
      component: () => import('../views/SelectTaskView.vue'),
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
      path: '/check-transfer',
      name: 'check-transfer',
      component: () => import('../views/CheckTransferView.vue'),
    },
    {
      path: '/check-account',
      name: 'check-account',
      component: () => import('../views/CheckAccountView.vue'),
    },
    {
      path: '/input-account',
      name: 'input-account',
      component: () => import('../views/InputAccountView.vue'),
    },
    {
      path: '/check-amount',
      name: 'check-amount',
      component: () => import('../views/CheckAmountView.vue'),
    },
    {
      path: '/select-bills',
      name: 'select-bills',
      component: () => import('../views/SelectBillsView.vue'),
    },
    {
      path: '/select-bank',
      name: 'select-bank',
      component: () => import('../views/SelectBankView.vue'),
    },
    {
      path: '/input-cash',
      name: 'input-cash',
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
      path: '/suggest-door-phone',
      name: 'suggest-door-phone',
      component: () => import('../views/SuggestDoorPhoneView.vue'),
    },
    {
      path: '/error',
      name: 'error',
      component: () => import('../views/ErrorView.vue'),
    },
    {
      path: '/check-no',
      name: 'check-no',
      component: () => import('../views/CheckNoView.vue'),
    },
  ],
});

export default router;
