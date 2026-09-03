<template>
  <nav id="global-nav">
    <div class="nav-inner">
      <div class="nav-logo" @click="$router.push('/')">
        <div class="nav-logo-seal">
          <span>粤</span>
        </div>
        <div class="nav-logo-name">
          <span style="color: #C0392B;">粤</span>韵<em>志</em>
        </div>
      </div>
      <div class="nav-links">
        <button class="nav-link" :class="{ on: currentRoute === 'Home' }" @click="$router.push('/')">首页</button>
        <button class="nav-link" :class="{ on: currentRoute === 'Explore' }" @click="$router.push('/explore')">探索</button>
        <button class="nav-link" :class="{ on: currentRoute === 'AIChat' }" @click="$router.push('/ai-chat')">AI问答</button>
        <button class="nav-link" :class="{ on: currentRoute === 'Ranking' }" @click="$router.push('/ranking')">排行榜</button>
        <button v-if="isAdmin" class="nav-link nav-admin" :class="{ on: currentRoute === 'AdminDashboard' }" @click="$router.push('/admin')">管理后台</button>
      </div>
      <div class="nav-right">
        <div class="nav-search-box">
          <img src="/icons/search.png" alt="搜索" class="nav-search-img" />
          <input 
            type="text" 
            v-model="searchKeyword" 
            placeholder="搜索粤文化..." 
            @keyup.enter="handleSearch"
            @input="handleSearchInput"
          />
          <div v-if="showSearchResults" class="search-results">
            <div 
              v-for="result in searchResults" 
              :key="result.articleId" 
              class="search-result-item"
              @click="goToArticleDetail(result.articleId)"
            >
              <div class="search-result-title" v-html="highlightKeyword(result.title, searchKeyword)"></div>
              <div class="search-result-summary">{{ result.summary }}</div>
            </div>
            <div v-if="searchResults.length === 0" class="search-no-result">
              未找到相关文章
            </div>
          </div>
        </div>
        <div
          v-if="isLoggedIn"
          class="nav-bell"
          title="私信"
          @click="goMessages"
        >
          <img src="/icons/message.png" alt="消息" class="nav-bell-img" />
          <span v-if="unreadTotal > 0" class="bell-dot">{{ unreadTotal > 99 ? '99+' : unreadTotal }}</span>
        </div>
        <div v-if="isLoggedIn" class="nav-user">
          <div class="nav-avt" @click="toggleUserMenu">
            <img 
              v-if="user?.avatar && !avatarError" 
              :src="fixAvatarPath(user.avatar)" 
              :alt="user.nickname || user.username" 
              class="nav-avt-img"
              @load="avatarError = false"
              @error="handleAvatarError"
            />
            <div v-else class="nav-avt-placeholder" :class="'av' + (user?.userId % 5 + 1)">
              {{ user?.nickname?.charAt(0) || user?.username?.charAt(0) || '粤' }}
            </div>
          </div>
          <div class="user-menu" v-if="showUserMenu">
            <div class="user-menu-item" @click="goToProfile">个人主页</div>
            <div class="user-menu-item" @click="logout">退出登录</div>
          </div>
        </div>
        <button v-if="isLoggedIn" class="btn btn-ink btn-sm" @click="$router.push('/write')">写文章</button>
        <button v-else class="btn btn-v btn-sm" @click="$router.push('/login')">登录</button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed, ref, watch, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { fetchUnreadTotal } from '../api/chatApi'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const showUserMenu = ref(false)
const avatarError = ref(false)
const unreadTotal = ref(0)

const currentRoute = computed(() => route.name)
const isLoggedIn = computed(() => authStore.isLoggedIn)
const user = computed(() => authStore.user)
const isAdmin = computed(() => authStore.user?.role === 'admin')

// 修复头像路径
const fixAvatarPath = (path) => {
  if (!path) return ''
  let fixedPath = path
  if (!fixedPath.startsWith('/')) {
    fixedPath = '/' + fixedPath
  }
  fixedPath = fixedPath.replace(/\s+/g, '')
  return fixedPath
}

// 处理头像加载错误
const handleAvatarError = () => {
  avatarError.value = true
}

// 监听用户数据变化，重置头像错误状态
watch(user, () => {
  avatarError.value = false
}, { deep: true })

const toggleUserMenu = () => {
  showUserMenu.value = !showUserMenu.value
}

const goToProfile = () => {
  showUserMenu.value = false
  router.push(`/author/${user.value?.userId || 1}`)
}

const logout = () => {
  showUserMenu.value = false
  authStore.logout()
  unreadTotal.value = 0
  router.push('/login')
}

const loadUnread = async () => {
  if (!authStore.token) {
    unreadTotal.value = 0
    return
  }
  try {
    const data = await fetchUnreadTotal(authStore.token)
    unreadTotal.value = data.unreadTotal || 0
  } catch {
    unreadTotal.value = 0
  }
}

const goMessages = () => {
  router.push('/messages')
}

const onUnreadRefresh = () => loadUnread()

// 搜索相关
const searchKeyword = ref('')
const searchResults = ref([])
const showSearchResults = ref(false)
let searchTimeout = null

const API_BASE_URL = '/api'

const handleSearchInput = () => {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  
  if (searchKeyword.value.trim() === '') {
    showSearchResults.value = false
    searchResults.value = []
    return
  }
  
  searchTimeout = setTimeout(() => {
    performSearch()
  }, 300)
}

const handleSearch = () => {
  if (searchKeyword.value.trim() !== '') {
    performSearch()
  }
}

const performSearch = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/articles/search?keyword=${encodeURIComponent(searchKeyword.value)}`)
    if (response.ok) {
      const data = await response.json()
      searchResults.value = data
      showSearchResults.value = true
    } else {
      searchResults.value = []
      showSearchResults.value = true
    }
  } catch (error) {
    console.error('搜索失败:', error)
    searchResults.value = []
    showSearchResults.value = true
  }
}

const highlightKeyword = (text, keyword) => {
  if (!keyword || !text) return text
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<span class="highlight">$1</span>')
}

const goToArticleDetail = (articleId) => {
  showSearchResults.value = false
  searchKeyword.value = ''
  router.push(`/article/${articleId}`)
}

// 点击外部关闭搜索结果
const handleClickOutside = (event) => {
  const userMenu = document.querySelector('.user-menu')
  const navUser = document.querySelector('.nav-user')
  const searchBox = document.querySelector('.nav-search-box')
  if (userMenu && navUser && !navUser.contains(event.target)) {
    showUserMenu.value = false
  }
  if (searchBox && !searchBox.contains(event.target)) {
    showSearchResults.value = false
  }
}

// 添加点击事件监听器
window.addEventListener('click', handleClickOutside)

onUnmounted(() => {
  window.removeEventListener('click', handleClickOutside)
  window.removeEventListener('chat-unread-refresh', onUnreadRefresh)
})

watch(isLoggedIn, (loggedIn) => {
  if (loggedIn) loadUnread()
  else unreadTotal.value = 0
})

// 组件挂载时初始化用户信息
onMounted(() => {
  authStore.initAuth()
  if (authStore.isLoggedIn) loadUnread()
  window.addEventListener('chat-unread-refresh', onUnreadRefresh)
})
</script>

<style scoped>
  #global-nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 900;
  height: 62px;
  background: rgba(251, 246, 236, 0.94);
  border-bottom: 1px solid #EAD8BE;
  backdrop-filter: blur(14px);
}

.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 36px;
  height: 100%;
  display: flex;
  align-items: center;
}

.nav-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-right: 36px;
  cursor: pointer;
  flex-shrink: 0;
}

.nav-logo-seal {
  width: 36px;
  height: 36px;
  background: #1A1008;  /* 黑色背景 */
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.nav-logo-seal span {
  font-family: 'Ma Shan Zheng', serif;
  font-size: 18px;
  color: #D4A017;
  line-height: 1;
}

.nav-logo-name {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 20px;
  letter-spacing: 3px;
  color: var(--ink);
}

.nav-logo-name em {
  color: var(--v);
  font-style: normal;
}

.nav-links {
  display: flex;
  gap: 2px;
}

.nav-link {
  padding: 8px 16px;
  font-size: 13px;
  letter-spacing: 2px;
  color: var(--dim);
  border: none;
  background: none;
  border-radius: var(--r);
  transition: all .2s;
}

.nav-link:hover, .nav-link.on {
  color: #C0392B;
  background: rgba(192, 57, 43, 0.10);
}

.nav-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}

.nav-search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 20px;
  padding: 8px 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.nav-search-box input {
  background: none;
  border: none;
  outline: none;
  font-size: 13px;
  color: var(--ink);
  width: 200px;
}

.nav-search-box input::placeholder {
  color: var(--dim5);
}

.nav-search-ico {
  color: var(--dim);
  font-size: 13px;
}

.nav-search-img {
  width: 14px;
  height: 14px;
  object-fit: contain;
}

.nav-bell {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--rice);
  border: 1px solid var(--bd);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  font-size: 15px;
  transition: all .2s;
}

.nav-bell-img {
  width: 16px;
  height: 16px;
  object-fit: contain;
}

.nav-bell:hover {
  border-color: var(--v);
  background: var(--vd);
}

.bell-dot {
  position: absolute;
  top: -2px;
  right: -2px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  background: var(--v);
  border-radius: 8px;
  border: 2px solid var(--cream);
  color: #fff;
  font-size: 9px;
  line-height: 12px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-user {
  position: relative;
}

.nav-avt {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  font-family: 'ZCOOL XiaoWei', serif;
  border: 2px solid var(--bd);
  cursor: pointer;
  transition: all .2s;
  overflow: hidden;
}

.nav-avt-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.nav-avt-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  font-family: 'ZCOOL XiaoWei', serif;
}

.nav-avt:hover {
  border-color: var(--v);
  transform: scale(1.05);
}

.user-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 8px;
  background: #fff;
  border: 1px solid var(--bd2);
  border-radius: var(--r);
  box-shadow: var(--sh2);
  min-width: 120px;
  z-index: 1000;
}

.user-menu-item {
  padding: 10px 16px;
  font-size: 13px;
  color: var(--ink);
  cursor: pointer;
  transition: background .2s;
  border-bottom: 1px solid var(--bd2);
}

.user-menu-item:last-child {
  border-bottom: none;
  color: var(--v);
}

.user-menu-item:hover {
  background: var(--cream);
}

/* 头像渐变类 */
.av1 { background: linear-gradient(135deg, #8B1A14, #D4A017); }
.av2 { background: linear-gradient(135deg, #1B4A40, #4AA08A); }
.av3 { background: linear-gradient(135deg, #4A0A1A, #8B3A5A); }
.av4 { background: linear-gradient(135deg, #1A1A4A, #2C5FBD); }
.av5 { background: linear-gradient(135deg, #3D1F0A, #8B4F1A); }

/* 按钮样式 - 统一配色风格 */
.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border: none;
  border-radius: 3px;
  font-size: 12px;
  font-family: 'Noto Serif SC', serif;
  letter-spacing: 2px;
  cursor: pointer;
  transition: all 0.2s;
  z-index: 1000;
  position: relative;
}

.btn-v {
  background: #C0392B !important;
  color: #fff !important;
}

.btn-v:hover {
  background: #E04B3A !important;
  box-shadow: 0 4px 12px rgba(192, 57, 43, 0.3);
  transform: translateY(-1px);
}

/* 写文章按钮样式 - 金字配墨底 */
.btn-ink {
  background: #1A1008 !important;
  color: #D4A017 !important;
  border: 1px solid #D4A017;
}

.btn-ink:hover {
  background: #2A1A10 !important;
  box-shadow: 0 4px 12px rgba(212, 160, 23, 0.3);
  transform: translateY(-1px);
}

.btn-sm {
  padding: 8px 20px;
  font-size: 11px;
}

@media (max-width: 768px) {
  .nav-inner {
    padding: 0 20px;
  }
  
  .nav-links {
    display: none;
  }
  
  .nav-search-box {
    display: none;
  }
}

/* 搜索结果样式 */
.nav-search-box {
  position: relative;
}

.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: #fff;
  border: 1px solid var(--bd2);
  border-radius: var(--r);
  box-shadow: var(--sh2);
  max-height: 400px;
  overflow-y: auto;
  z-index: 1000;
  margin-top: 8px;
}

.search-result-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: background .2s;
  border-bottom: 1px solid var(--bd2);
}

.search-result-item:hover {
  background: var(--cream);
}

.search-result-item:last-child {
  border-bottom: none;
}

.search-result-title {
  font-size: 14px;
  color: var(--ink);
  margin-bottom: 4px;
  font-weight: 500;
}

.search-result-summary {
  font-size: 12px;
  color: var(--dim);
  line-height: 1.5;
}

.search-no-result {
  padding: 16px;
  text-align: center;
  color: var(--dim);
  font-size: 13px;
}

</style>

<style>
/* 全局样式 - 用于 v-html 渲染的内容 */
.highlight {
  background: rgba(192, 57, 43, 0.2) !important;
  color: #C0392B !important;
  padding: 2px 4px !important;
  border-radius: 2px !important;
  font-weight: bold !important;
}
</style>