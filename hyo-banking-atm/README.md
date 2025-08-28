# 🏧 Hyo Banking ATM

Vue 3 + Vite + Tailwind CSS로 구축된 현대적인 ATM 애플리케이션입니다.

## 🚀 기술 스택

- **Frontend**: Vue 3 + Composition API
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **Code Quality**: ESLint + Prettier
- **Node.js**: 22.17.0 LTS

## 🛠️ 개발 환경 설정

### 권장 IDE

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (Vetur 비활성화)

## 📦 프로젝트 설정

```sh
npm install
```

### 개발 서버 실행 (핫 리로드)

```sh
npm run dev
```

### 프로덕션 빌드

```sh
npm run build
```

### ESLint 코드 검사

```sh
npm run lint
```

## 🎨 프론트엔드 개발 환경 설정

### Prettier 설정

이 프로젝트는 Prettier를 사용하여 일관된 코드 스타일을 유지합니다.

**Prettier 설정 확인**

```bash
# 프로젝트 루트에서 Prettier 설정 확인
cat .prettierrc.json

# src 디렉토리 코드 포맷팅
npx prettier --write "src/**/*.{js,vue,css}"
```

**VS Code 확장 프로그램 권장**

- Prettier - Code formatter
- Vue Language Features (Volar)
- Tailwind CSS IntelliSense
- ESLint

**자동 포맷팅 설정**

- 저장 시 자동 포맷팅 활성화
- Prettier를 기본 포맷터로 설정

## 📚 추가 문서

- [Vite 설정 참조](https://vite.dev/config/)
- [Vue 3 공식 문서](https://vuejs.org/)
- [Tailwind CSS 공식 문서](https://tailwindcss.com/)
