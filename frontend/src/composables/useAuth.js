import { ref, computed } from 'vue'

// 전역 상태
const token = ref(localStorage.getItem('auth_token') || '')
const email = ref(localStorage.getItem('auth_email') || '')
const name = ref(localStorage.getItem('auth_name') || '')

export function useAuth() {
  const isAuthenticated = computed(() => !!token.value)

  const login = (tokenValue, emailValue, nameValue) => {
    token.value = tokenValue
    email.value = emailValue
    name.value = nameValue || 'Unknown'
    
    localStorage.setItem('auth_token', tokenValue)
    localStorage.setItem('auth_email', emailValue)
    localStorage.setItem('auth_name', nameValue || 'Unknown')
  }

  const logout = () => {
    token.value = ''
    email.value = ''
    name.value = ''
    
    localStorage.removeItem('auth_token')
    localStorage.removeItem('auth_email')
    localStorage.removeItem('auth_name')
  }

  return {
    token: computed(() => token.value),
    email: computed(() => email.value),
    name: computed(() => name.value),
    isAuthenticated,
    login,
    logout
  }
}