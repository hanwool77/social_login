<template>
  <div class="home">
    <h2>OAuth Login Demo</h2>
    <div class="info-cards">
      <div class="card">
        <h3>서버 정보</h3>
        <div v-if="serverInfo">
          <p><strong>메시지:</strong> {{ serverInfo.message }}</p>
          <p><strong>버전:</strong> {{ serverInfo.version }}</p>
        </div>
        <div v-else-if="loading">로딩 중...</div>
        <div v-else>서버 정보를 가져올 수 없습니다.</div>
      </div>
      
      <div class="card">
        <h3>서버 상태</h3>
        <div v-if="healthStatus">
          <p><strong>상태:</strong> 
            <span :class="{'status-up': healthStatus.status === 'UP', 'status-down': healthStatus.status !== 'UP'}">
              {{ healthStatus.status }}
            </span>
          </p>
        </div>
        <div v-else-if="loading">로딩 중...</div>
        <div v-else>상태를 확인할 수 없습니다.</div>
      </div>
    </div>
    
    <div class="actions">
      <router-link to="/login" class="btn btn-primary">로그인하기</router-link>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { homeApi } from '../services/api'

export default {
  name: 'Home',
  setup() {
    const serverInfo = ref(null)
    const healthStatus = ref(null)
    const loading = ref(true)

    const fetchData = async () => {
      try {
        const [serverResponse, healthResponse] = await Promise.all([
          homeApi.getServerInfo(),
          homeApi.getHealthStatus()
        ])
        
        serverInfo.value = serverResponse.data
        healthStatus.value = healthResponse.data
      } catch (error) {
        console.error('Failed to fetch data:', error)
      } finally {
        loading.value = false
      }
    }

    onMounted(fetchData)

    return {
      serverInfo,
      healthStatus,
      loading
    }
  }
}
</script>

<style scoped>
.home {
  text-align: center;
}

h2 {
  color: #333;
  margin-bottom: 2rem;
  font-size: 2rem;
}

.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.card {
  background: white;
  border-radius: 8px;
  padding: 2rem;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  text-align: left;
}

.card h3 {
  color: #333;
  margin-bottom: 1rem;
  border-bottom: 2px solid #007bff;
  padding-bottom: 0.5rem;
}

.card p {
  margin: 0.5rem 0;
  color: #666;
}

.status-up {
  color: #28a745;
  font-weight: bold;
}

.status-down {
  color: #dc3545;
  font-weight: bold;
}

.actions {
  margin-top: 2rem;
}

.btn {
  display: inline-block;
  padding: 1rem 2rem;
  text-decoration: none;
  border-radius: 6px;
  font-weight: bold;
  transition: all 0.3s;
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