# OAuth Login Frontend

Vue 3.5 기반의 OAuth 로그인 프론트엔드 애플리케이션입니다.

## 기능

- 홈페이지: 서버 정보 및 상태 확인
- 로그인 페이지: Google/Naver OAuth 로그인
- 대시보드: JWT 토큰 검증 및 사용자 정보 확인

## 시작하기

### 1. 의존성 설치

```bash
cd frontend
npm install
```

### 2. 개발 서버 실행

```bash
npm run dev
```

애플리케이션이 http://localhost:3000 에서 실행됩니다.

### 3. 빌드

```bash
npm run build
```

## 프로젝트 구조

```
src/
├── components/     # 재사용 가능한 컴포넌트
├── views/         # 페이지 컴포넌트
│   ├── Home.vue   # 홈페이지
│   ├── Login.vue  # 로그인 페이지
│   └── Dashboard.vue # 대시보드
├── services/      # API 서비스
│   └── api.js     # API 통신 로직
├── router/        # 라우터 설정
│   └── index.js
├── App.vue        # 루트 컴포넌트
└── main.js        # 애플리케이션 진입점
```

## API 연동

Spring Boot 백엔드 서버 (http://localhost:8080)와 연동됩니다.

### 사용되는 API 엔드포인트

- `GET /` - 서버 정보 조회
- `GET /health` - 서버 상태 확인
- `GET /auth/login/google` - Google 로그인 URL 조회
- `GET /auth/login/naver` - Naver 로그인 URL 조회
- `POST /auth/validate` - JWT 토큰 검증
- `POST /auth/logout` - 로그아웃

## 기술 스택

- Vue 3.5
- Vue Router 4
- Axios
- Vite