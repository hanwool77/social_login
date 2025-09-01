import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/auth/login': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/auth/validate': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/auth/logout': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/oauth2': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/health': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})