import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://24.199.80.133', // La URL de tu backend
        changeOrigin: true, // Esto engaña al backend para que no sepa que eres un localhost
      }
    }
  }
})