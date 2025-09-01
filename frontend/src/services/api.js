import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080'

// Axios 인스턴스 생성
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
})

// 요청 인터셉터: 모든 API 요청에 JWT 토큰 자동 추가
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`  // JWT를 Authorization Header에 추가
      console.log('Adding JWT token to request:', config.url)
    }
    return config
  },
  (error) => {
    console.error('Request interceptor error:', error)
    return Promise.reject(error)
  }
)

// 응답 인터셉터: 401 에러 시 자동 로그아웃
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      console.log('401 Unauthorized - clearing tokens and redirecting to login')
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_email')
      localStorage.removeItem('auth_name')
      
      // 현재 페이지가 로그인 페이지가 아니면 리다이렉트
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export const authApi = {
  getGoogleLoginUrl: () => apiClient.get('/auth/login/google'),
  getNaverLoginUrl: () => apiClient.get('/auth/login/naver'),
  getKakaoLoginUrl: () => apiClient.get('/auth/login/kakao'),
  validateToken: async () => {
    // 토큰은 Header에 자동으로 포함되므로 별도 파라미터 불필요
    return await apiClient.post('/auth/validate')
  },
  getUserInfo: async () => {
    return await apiClient.get('/auth/me')
  },
  logout: async () => {
    return await apiClient.post('/auth/logout')
  }
}

export const protectedApi = {
  getProtectedData: async () => {
    return await apiClient.get('/api/protected-data')
  }
}

export const homeApi = {
  getServerInfo: () => apiClient.get('/'),
  getHealthStatus: () => apiClient.get('/health')
}

// 기본 export
export default apiClient