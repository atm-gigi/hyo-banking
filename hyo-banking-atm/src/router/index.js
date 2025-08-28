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
      path: '/ten-key',
      name: 'ten-key',
      component: () => import('../views/TenKeyView.vue'),
    },
  ],
});

export default router;
