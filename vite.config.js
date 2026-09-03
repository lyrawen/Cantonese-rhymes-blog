import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 4000,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/uploads': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/avatars': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/profile_covers': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false
      },
      '/ws': {
        target: 'http://localhost:8081',
        ws: true,
        changeOrigin: true
      }
    }
  }
})