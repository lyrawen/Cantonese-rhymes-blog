import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

const API_BASE_URL = '/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || null)

  const isLoggedIn = computed(() => !!token.value)

  const login = async (usernameOrEmail, password) => {
    try {
      const response = await fetch(`${API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          username: usernameOrEmail,
          password: password
        })
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || '登录失败')
      }

      token.value = data.token
      user.value = data.user

      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))

      return { token: data.token, user: data.user }
    } catch (error) {
      throw new Error(error.message || '网络错误，请稍后重试')
    }
  }

  const register = async (email, password, username) => {
    try {
      const response = await fetch(`${API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email,
          password,
          username
        })
      })

      const data = await response.json()

      if (!response.ok) {
        throw new Error(data.error || '注册失败')
      }

      token.value = data.token
      user.value = data.user

      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))

      return { token: data.token, user: data.user }
    } catch (error) {
      throw new Error(error.message || '网络错误，请稍后重试')
    }
  }

  const clearLocalAuth = () => {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const logout = async () => {
    const currentToken = token.value
    try {
      if (currentToken) {
        await fetch(`${API_BASE_URL}/auth/logout`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${currentToken}`
          }
        })
      }
    } catch (error) {
      console.error('退出登录请求失败:', error)
    } finally {
      clearLocalAuth()
      // 清除出场动画标记，以便重新登录后再次播放
      sessionStorage.removeItem('splashPlayed')
    }
  }

  const fetchCurrentUser = async () => {
    if (!token.value) return

    try {
      const response = await fetch(`${API_BASE_URL}/auth/me`, {
        headers: {
          'Authorization': `Bearer ${token.value}`
        }
      })

      if (response.ok) {
        const data = await response.json()
        user.value = data
        localStorage.setItem('user', JSON.stringify(data))
      } else {
        clearLocalAuth()
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }

  const initAuth = () => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    if (savedToken && savedUser) {
      token.value = savedToken
      user.value = JSON.parse(savedUser)
      fetchCurrentUser()
    }
  }

  return {
    user,
    token,
    isLoggedIn,
    login,
    register,
    logout,
    initAuth,
    fetchCurrentUser
  }
})