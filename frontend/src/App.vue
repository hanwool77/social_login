<template>
  <div id="app">
    <header>
      <nav>
        <h1>OAuth Login Demo</h1>
        <div>
          <router-link to="/" class="nav-link">Home</router-link>
          <template v-if="!isAuthenticated">
            <router-link to="/login" class="nav-link">Login</router-link>
          </template>
          <template v-else>
            <router-link to="/dashboard" class="nav-link">Dashboard</router-link>
            <button @click="handleLogout" class="nav-link logout-btn">로그아웃</button>
          </template>
        </div>
      </nav>
    </header>
    <main>
      <router-view />
    </main>
  </div>
</template>

<script>
import { useAuth } from './composables/useAuth'
import { useRouter } from 'vue-router'
import { authApi } from './services/api'

export default {
  name: 'App',
  setup() {
    const { isAuthenticated, logout } = useAuth()
    const router = useRouter()

    const handleLogout = async () => {
      try {
        await authApi.logout()
        logout()
        router.push('/')
      } catch (error) {
        console.error('Logout failed:', error)
      }
    }

    return {
      isAuthenticated,
      handleLogout
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: Arial, sans-serif;
  background-color: #f5f5f5;
}

#app {
  min-height: 100vh;
}

header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 1rem 2rem;
}

nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  color: #333;
  font-size: 1.5rem;
}

.nav-link {
  text-decoration: none;
  color: #666;
  margin-left: 1rem;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-link:hover {
  background-color: #f0f0f0;
}

.nav-link.router-link-active {
  background-color: #007bff;
  color: white;
}

.logout-btn {
  background: none;
  border: none;
  font-size: inherit;
  font-family: inherit;
}

.logout-btn:hover {
  background-color: #dc3545;
  color: white;
}

main {
  max-width: 1200px;
  margin: 2rem auto;
  padding: 0 2rem;
}
</style>