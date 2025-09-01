<template>
  <div class="dashboard">
    <div v-if="!isAuthenticated" class="login-required">
      <h2>로그인이 필요합니다</h2>
      <p>대시보드를 사용하려면 먼저 로그인해주세요.</p>
      <router-link to="/login" class="btn btn-primary">로그인하기</router-link>
    </div>
    
    <div v-else class="dashboard-container">
      <div class="user-info">
        <h2>안녕하세요, {{ userName }}님!</h2>
        <p class="subtitle">로그인이 성공적으로 완료되었습니다.</p>
        <div class="user-details">
          <p><strong>이메일:</strong> {{ userEmail }}</p>
          <p><strong>이름:</strong> {{ userName }}</p>
        </div>
      </div>
      
      <div class="token-section">
        <h3>API 테스트</h3>
        <p>JWT 토큰을 사용한 다양한 API 호출을 테스트해보세요.</p>
        
        <div class="api-buttons">
          <button @click="validateCurrentToken" :disabled="loading" class="btn btn-primary">
            토큰 검증
          </button>
          <button @click="fetchUserInfo" :disabled="loading" class="btn btn-info">
            내 정보 조회
          </button>
          <button @click="fetchProtectedData" :disabled="loading" class="btn btn-success">
            보호된 데이터 조회
          </button>
        </div>
      </div>
      
      <div v-if="loading" class="loading-message">
        토큰을 검증하는 중...
      </div>
      
      <!-- 토큰 검증 결과 -->
      <div v-if="validationResult" class="result-section">
        <h3>토큰 검증 결과</h3>
        <div class="result-card" :class="{'valid': validationResult.valid, 'invalid': !validationResult.valid}">
          <div class="result-header">
            <span class="status-icon">{{ validationResult.valid ? '✓' : '✗' }}</span>
            <span class="status-text">{{ validationResult.valid ? '유효한 토큰' : '유효하지 않은 토큰' }}</span>
          </div>
          
          <div v-if="validationResult.valid" class="result-details">
            <p><strong>이메일:</strong> {{ validationResult.email }}</p>
            <p><strong>메시지:</strong> {{ validationResult.message }}</p>
          </div>
          
          <div v-if="validationResult.error" class="error-details">
            <p><strong>오류:</strong> {{ validationResult.error }}</p>
          </div>
        </div>
      </div>
      
      <!-- 사용자 정보 결과 -->
      <div v-if="userInfo" class="result-section">
        <h3>사용자 정보</h3>
        <div class="result-card valid">
          <div class="result-details">
            <p><strong>이메일:</strong> {{ userInfo.email }}</p>
            <p><strong>이름:</strong> {{ userInfo.name }}</p>
            <p><strong>공급자:</strong> {{ userInfo.provider }}</p>
            <p><strong>가입일:</strong> {{ formatDate(userInfo.createdAt) }}</p>
          </div>
        </div>
      </div>
      
      <!-- 보호된 데이터 결과 -->
      <div v-if="protectedData" class="result-section">
        <h3>보호된 데이터</h3>
        <div class="result-card valid">
          <div class="result-details">
            <p><strong>메시지:</strong> {{ protectedData.message }}</p>
            <p><strong>사용자:</strong> {{ protectedData.user }}</p>
            <p><strong>시간:</strong> {{ formatDate(protectedData.timestamp) }}</p>
            <p><strong>데이터:</strong> {{ protectedData.data }}</p>
          </div>
        </div>
      </div>
      
      <div class="actions">
        <button @click="handleLogout" class="btn btn-secondary">로그아웃</button>
        <router-link to="/" class="btn btn-outline">홈으로</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '../composables/useAuth'
import { authApi, protectedApi } from '../services/api'

export default {
  name: 'Dashboard',
  setup() {
    const router = useRouter()
    const { token, email, name, isAuthenticated, logout } = useAuth()
    const loading = ref(false)
    const validationResult = ref(null)
    const userInfo = ref(null)
    const protectedData = ref(null)

    const validateCurrentToken = async () => {
      try {
        loading.value = true
        validationResult.value = null
        
        const response = await authApi.validateToken()
        validationResult.value = response.data
      } catch (error) {
        console.error('Token validation failed:', error)
        validationResult.value = {
          valid: false,
          error: '토큰 검증 중 오류가 발생했습니다.'
        }
      } finally {
        loading.value = false
      }
    }
    
    const fetchUserInfo = async () => {
      try {
        const response = await authApi.getUserInfo()
        userInfo.value = response.data
      } catch (error) {
        console.error('Failed to fetch user info:', error)
      }
    }
    
    const fetchProtectedData = async () => {
      try {
        const response = await protectedApi.getProtectedData()
        protectedData.value = response.data
      } catch (error) {
        console.error('Failed to fetch protected data:', error)
      }
    }

    const handleLogout = async () => {
      try {
        await authApi.logout()
        logout()
        validationResult.value = null
        router.push('/')
      } catch (error) {
        console.error('Logout failed:', error)
      }
    }

    const formatDate = (dateString) => {
      if (!dateString) return 'N/A'
      return new Date(dateString).toLocaleString('ko-KR')
    }

    onMounted(() => {
      // 로그인되어 있으면 자동으로 토큰 검증 및 사용자 정보 로드
      if (isAuthenticated.value) {
        validateCurrentToken()
        fetchUserInfo()
      }
    })

    return {
      isAuthenticated,
      userEmail: email,
      userName: name,
      loading,
      validationResult,
      userInfo,
      protectedData,
      validateCurrentToken,
      fetchUserInfo,
      fetchProtectedData,
      handleLogout,
      formatDate
    }
  }
}
</script>

<style scoped>
.dashboard {
  max-width: 800px;
  margin: 0 auto;
}

.login-required {
  background: white;
  border-radius: 12px;
  padding: 3rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
  text-align: center;
}

.login-required h2 {
  color: #dc3545;
  margin-bottom: 1rem;
}

.login-required p {
  color: #666;
  margin-bottom: 2rem;
}

.user-info {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 2rem;
  margin-bottom: 2rem;
  text-align: center;
}

.user-details {
  background: white;
  border-radius: 6px;
  padding: 1rem;
  margin-top: 1rem;
}

.user-details p {
  margin: 0.5rem 0;
  color: #333;
}

.dashboard-container {
  background: white;
  border-radius: 12px;
  padding: 3rem;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

h2 {
  color: #333;
  margin-bottom: 0.5rem;
  font-size: 2rem;
  text-align: center;
}

.subtitle {
  color: #666;
  margin-bottom: 2rem;
  text-align: center;
  font-size: 0.9rem;
}

.token-section {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 2rem;
  margin-bottom: 2rem;
}

.token-section h3 {
  color: #333;
  margin-bottom: 1.5rem;
  border-bottom: 2px solid #007bff;
  padding-bottom: 0.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  resize: vertical;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0,123,255,0.25);
}

.btn {
  display: inline-block;
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s;
  margin-right: 1rem;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #545b62;
}

.btn-outline {
  background-color: transparent;
  color: #007bff;
  border: 2px solid #007bff;
}

.btn-outline:hover {
  background-color: #007bff;
  color: white;
}

.loading-message {
  color: #007bff;
  background-color: #d1ecf1;
  border: 1px solid #bee5eb;
  border-radius: 6px;
  padding: 1rem;
  margin-bottom: 1rem;
  text-align: center;
}

.result-section {
  margin-bottom: 2rem;
}

.result-section h3 {
  color: #333;
  margin-bottom: 1rem;
  border-bottom: 2px solid #007bff;
  padding-bottom: 0.5rem;
}

.result-card {
  border-radius: 8px;
  padding: 1.5rem;
  border: 2px solid;
}

.result-card.valid {
  background-color: #d4edda;
  border-color: #28a745;
}

.result-card.invalid {
  background-color: #f8d7da;
  border-color: #dc3545;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.status-icon {
  font-size: 1.5rem;
  font-weight: bold;
}

.result-card.valid .status-icon {
  color: #28a745;
}

.result-card.invalid .status-icon {
  color: #dc3545;
}

.status-text {
  font-size: 1.1rem;
  font-weight: 600;
}

.result-card.valid .status-text {
  color: #155724;
}

.result-card.invalid .status-text {
  color: #721c24;
}

.result-details p,
.error-details p {
  margin: 0.5rem 0;
  color: #333;
}

.actions {
  text-align: center;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.api-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.btn-info {
  background-color: #17a2b8;
  color: white;
}

.btn-info:hover {
  background-color: #138496;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-success:hover {
  background-color: #218838;
}
</style>