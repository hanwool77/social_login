<template>
  <div class="auth-callback">
    <div class="callback-container">
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>OAuth 인증을 처리하는 중...</p>
      </div>
      
      <div v-else-if="error" class="error">
        <div class="error-icon">⚠️</div>
        <h3>인증 실패</h3>
        <p>{{ error }}</p>
        <router-link to="/login" class="btn btn-primary">다시 로그인</router-link>
      </div>
      
      <div v-else class="success">
        <div class="success-icon">✅</div>
        <h3>인증 성공</h3>
        <p>잠시 후 대시보드로 이동합니다...</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth'

export default {
  name: 'AuthCallback',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const { login } = useAuth()
    const loading = ref(true)
    const error = ref('')

    onMounted(() => {
      setTimeout(() => {
        // URL 파라미터 확인
        if (route.query.error) {
          // 인증 실패
          error.value = route.query.message || 'OAuth 인증에 실패했습니다.'
          loading.value = false
        } else if (route.query.token && route.query.email) {
          // 인증 성공 - 토큰을 저장하고 대시보드로 이동
          login(route.query.token, route.query.email, route.query.name)
          loading.value = false
          setTimeout(() => {
            router.push('/dashboard')
          }, 1500)
        } else {
          // 파라미터가 없는 경우
          error.value = '인증 정보를 찾을 수 없습니다.'
          loading.value = false
        }
      }, 1000) // 1초 딜레이로 로딩 상태 표시
    })

    return {
      loading,
      error
    }
  }
}
</script>

<style scoped>
.auth-callback {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.callback-container {
  background: white;
  border-radius: 12px;
  padding: 3rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  max-width: 400px;
  width: 100%;
  text-align: center;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading p {
  color: #666;
  margin: 0;
}

.error, .success {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.error-icon, .success-icon {
  font-size: 3rem;
}

.error h3 {
  color: #dc3545;
  margin: 0;
}

.success h3 {
  color: #28a745;
  margin: 0;
}

.error p, .success p {
  color: #666;
  margin: 0;
  line-height: 1.5;
}

.btn {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s;
  margin-top: 1rem;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}
</style>