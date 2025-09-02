# Router 작성 규칙

이 디렉토리는 Vue Router 설정과 관련된 파일들을 포함합니다.

## 파일 구조

```
src/router/
├── index.js          # 메인 라우터 설정 파일
└── README.md         # 이 파일 (작성 규칙)
```

## 작성 규칙

### 1. 라우터 설정 파일 (`index.js`)

#### 1.1 Import 규칙

- Vue Router 관련 import를 최상단에 배치
- View 컴포넌트는 상대 경로로 import
- 메인 페이지는 즉시 import, 나머지는 지연 로딩 사용

```javascript
import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 정적 라우트 (즉시 import)
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    // 동적 라우트 (지연 로딩)
    {
      path: '/choose-task',
      name: 'choose-task',
      component: () => import('../views/ChooseTaskView.vue'),
    },
  ],
});
```

#### 1.2 Path 명명 규칙

- **kebab-case** 사용 (예: `/payment-method`, `/put-payment`)
- URL은 소문자와 하이픈(-)으로 구성
- 의미가 명확한 단어 사용

#### 1.3 Name 명명 규칙

- **kebab-case** 사용 (예: `payment-method`, `put-payment`)
- **파일명 기반 변환 규칙**: `SelectAmountView.vue` → `'select-amount'`
- PascalCase 파일명을 kebab-case로 변환하여 name 작성
- Path와 동일하게 작성하여 일관성 유지
- URL과 name을 일치시켜 유지보수성 향상

#### 1.4 Component 로딩 규칙

- **메인 페이지**: 즉시 import (예: `HomeView`)
- **기능별 페이지**: 지연 로딩 (예: `() => import('../views/ViewName.vue')`)
- 지연 로딩을 통해 초기 번들 크기 최적화

#### 1.5 라우트 구조 예시

```javascript
{
  path: '/payment-method',
  name: 'payment-method',
  component: () => import('../views/PaymentMethodView.vue'),
}
```

### 2. 라우트 추가 시 체크리스트

- [ ] Path가 kebab-case로 작성되었는가?
- [ ] Name이 파일명을 기반으로 kebab-case로 변환되었는가? (예: `SelectAmountView.vue` → `'select-amount'`)
- [ ] Component가 올바른 경로로 import되었는가?
- [ ] 지연 로딩이 적절히 적용되었는가?
