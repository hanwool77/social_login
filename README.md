# OAuth Login Application

Spring Boot 기반의 OAuth2 로그인 애플리케이션입니다.

## 지원하는 OAuth Provider
- Google
- Naver
- Kakao

## 설정 방법

### 1. 환경 변수 설정
프로젝트 루트에 `.env` 파일을 생성하고 다음 내용을 추가하세요:

```bash
# .env.example 파일을 복사하여 .env 파일 생성
cp .env.example .env
```

### 2. OAuth2 클라이언트 정보 설정
각 OAuth Provider에서 클라이언트 ID와 Secret을 발급받아 `.env` 파일에 설정하세요.

#### Google OAuth 설정
1. [Google Cloud Console](https://console.cloud.google.com/) 방문
2. 프로젝트 생성 후 OAuth 2.0 클라이언트 ID 생성
3. 리다이렉트 URI: `http://localhost:8080/login/oauth2/code/google`

#### Naver OAuth 설정
1. [네이버 개발자 센터](https://developers.naver.com/) 방문
2. 애플리케이션 등록
3. 리다이렉트 URI: `http://localhost:8080/login/oauth2/code/naver`

#### Kakao OAuth 설정
1. [카카오 개발자](https://developers.kakao.com/) 방문
2. 애플리케이션 생성 후 카카오 로그인 설정
3. 리다이렉트 URI: `http://localhost:8080/login/oauth2/code/kakao`

### 3. 애플리케이션 실행
```bash
./gradlew bootRun
```

## 주의사항
- `.env` 파일은 Git에 커밋하지 마세요 (민감한 정보 포함)
- 실제 배포 시에는 환경 변수나 외부 설정 서버를 사용하세요