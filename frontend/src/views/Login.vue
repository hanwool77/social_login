<template>
  <div class="login">
    <div class="login-container">
      <h2>로그인</h2>
      <p class="subtitle">소셜 계정으로 간편하게 로그인하세요</p>
      
      <div class="oauth-buttons">
        <button @click="loginWithGoogle" class="oauth-btn google-btn" :disabled="loading">
          <span class="btn-icon">G</span>
          Google로 로그인
        </button>
        
        <button @click="loginWithNaver" class="oauth-btn naver-btn" :disabled="loading">
          <span class="btn-icon">N</span>
          Naver로 로그인
        </button>
        
        <button @click="loginWithKakao" class="oauth-btn kakao-btn" :disabled="loading">
          <span class="btn-icon">K</span>
          Kakao로 로그인
        </button>
      </div>
      
      <div v-if="error" class="error-message">
        {{ error }}
      </div>
      
      <div v-if="loading" class="loading-message">
        로그인 URL을 가져오는 중...
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'Login',
  setup() {
    const loading = ref(false)
    const error = ref('')

    const loginWithGoogle = () => {
      // Spring Security OAuth2 엔드포인트로 직접 이동
      window.location.href = 'http://localhost:8080/oauth2/authorization/google'
    }

    const loginWithNaver = () => {
      // Spring Security OAuth2 엔드포인트로 직접 이동
      window.location.href = 'http://localhost:8080/oauth2/authorization/naver'
    }

    const loginWithKakao = () => {
      // Spring Security OAuth2 엔드포인트로 직접 이동
      window.location.href = 'http://localhost:8080/oauth2/authorization/kakao'
    }

    return {
      loading,
      error,
      loginWithGoogle,
      loginWithNaver,
      loginWithKakao
    }
  }
}
</script>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.login-container {
  background: white;
  border-radius: 12px;
  padding: 3rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  max-width: 400px;
  width: 100%;
  text-align: center;
}

h2 {
  color: #333;
  margin-bottom: 0.5rem;
  font-size: 2rem;
}

.subtitle {
  color: #666;
  margin-bottom: 2rem;
  font-size: 0.9rem;
}

.oauth-buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.oauth-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  padding: 1rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.oauth-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.google-btn {
  background-color: #fff;
  color: #333;
  border: 2px solid #ddd;
}

.google-btn .btn-icon {
  background-color: #4285f4;
  color: white;
}

.google-btn:hover:not(:disabled) {
  background-color: #f8f9fa;
  border-color: #4285f4;
}

.naver-btn {
  background-color: #03c75a;
  color: white;
}

.naver-btn .btn-icon {
  background-color: white;
  color: #03c75a;
}

.naver-btn:hover:not(:disabled) {
  background-color: #02b351;
}

.kakao-btn {
  background-color: #fee500;
  color: #3c1e1e;
}

.kakao-btn .btn-icon {
  background-color: #3c1e1e;
  color: #fee500;
}

.kakao-btn:hover:not(:disabled) {
  background-color: #fada0a;
}

.error-message {
  color: #dc3545;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 6px;
  padding: 1rem;
  margin-top: 1rem;
  font-size: 0.9rem;
}

.loading-message {
  color: #007bff;
  background-color: #d1ecf1;
  border: 1px solid #bee5eb;
  border-radius: 6px;
  padding: 1rem;
  margin-top: 1rem;
  font-size: 0.9rem;
}
</style>