<template>
  <div class="admin-dashboard">
    <div class="sidebar">
      <div class="sb-logo">
        <div class="sb-seal"><span>粤</span></div>
        <div>
          <div class="sb-title">粤韵志</div>
          <div class="sb-subtitle">管理后台 · ADMIN</div>
        </div>
      </div>
      <div class="sb-admin">
        <div class="sb-avt">{{ adminInitial }}</div>
        <div>
          <div class="sb-admin-name">{{ adminName }}</div>
          <div class="sb-admin-role">✦ SUPER ADMIN</div>
        </div>
      </div>
      <nav class="sb-nav">
        <div class="sb-group-lbl">数据概览</div>
        <button class="sb-item" :class="{ on: currentPanel === 'overview' }" @click="switchPanel('overview')">
          <img class="sb-item-ico" src="/icons/运营概览.png" alt="">运营概览
        </button>

        <div class="sb-group-lbl">内容管理</div>
        <button class="sb-item" :class="{ on: currentPanel === 'articles' }" @click="switchPanel('articles')">
          <img class="sb-item-ico" src="/icons/文章管理.png" alt="">文章管理<span v-if="stats.pendingArticleCount" class="sb-badge">{{ stats.pendingArticleCount }}待审</span>
        </button>
        <button class="sb-item" :class="{ on: currentPanel === 'comments' }" @click="switchPanel('comments')">
          <img class="sb-item-ico" src="/icons/评论管理.png" alt="">评论管理<span v-if="stats.blockedCommentCount" class="sb-badge">{{ stats.blockedCommentCount }}屏蔽</span>
        </button>
        <button class="sb-item" :class="{ on: currentPanel === 'categories' }" @click="switchPanel('categories')">
          <img class="sb-item-ico" src="/icons/分类管理.png" alt="">分类管理
        </button>
        <button class="sb-item" :class="{ on: currentPanel === 'tags' }" @click="switchPanel('tags')">
          <img class="sb-item-ico" src="/icons/标签管理.png" alt="">标签管理
        </button>
        <button class="sb-item" :class="{ on: currentPanel === 'announce' }" @click="switchPanel('announce')">
          <img class="sb-item-ico" src="/icons/公告管理.png" alt="">公告管理
        </button>

        <div class="sb-group-lbl">用户管理</div>
        <button class="sb-item" :class="{ on: currentPanel === 'users' }" @click="switchPanel('users')">
          <img class="sb-item-ico" src="/icons/用户列表.png" alt="">用户列表
        </button>

        <div class="sb-group-lbl">数据 & AI</div>
        <button class="sb-item" :class="{ on: currentPanel === 'stats' }" @click="switchPanel('stats')">
          <img class="sb-item-ico" src="/icons/平台-数据统计.png" alt="">平台数据统计
        </button>
        <button class="sb-item" :class="{ on: currentPanel === 'ai' }" @click="switchPanel('ai')">
          <img class="sb-item-ico" src="/icons/机器人.png" alt="">AI问答管理
        </button>
        <button class="sb-item" :class="{ on: currentPanel === 'rank' }" @click="switchPanel('rank')">
          <img class="sb-item-ico" src="/icons/排行榜-管理中心.png" alt="">排行榜管理
        </button>
      </nav>
      <div class="sb-footer">
        <button class="sb-footer-btn" @click="logout"><img class="sb-footer-ico" src="/icons/退出登录.png" alt="">退出登录</button>
      </div>
    </div>

    <div class="main">
      <div class="topbar">
        <div class="tb-breadcrumb">后台 <span>›</span> <em>{{ panelLabels[currentPanel] }}</em></div>
        <div class="tb-right">
          <div class="tb-search"><span style="font-size:13px;color:var(--dim5)">🔍</span><input placeholder="搜索用户、文章…"></div>
          <div class="tb-notif"><img class="tb-notif-ico" src="/icons/喇叭.png" alt=""><span class="notif-dot"></span></div>
        </div>
      </div>

      <div class="content">
        <OverviewPanel v-if="currentPanel === 'overview'" />
        <ArticlesPanel v-if="currentPanel === 'articles'" />
        <CommentsPanel v-if="currentPanel === 'comments'" />
        <CategoriesPanel v-if="currentPanel === 'categories'" />
        <TagsPanel v-if="currentPanel === 'tags'" />
        <AnnouncePanel v-if="currentPanel === 'announce'" />
        <UsersPanel v-if="currentPanel === 'users'" />
        <StatsPanel v-if="currentPanel === 'stats'" />
        <AIPanel v-if="currentPanel === 'ai'" />
        <RankPanel v-if="currentPanel === 'rank'" />
      </div>
    </div>

    <div v-if="toast.show" class="toast" :style="toastStyle">{{ toast.message }}</div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { adminApi } from '../api/adminApi'
import '../styles/admin-variables.css'
import OverviewPanel from '../components/admin/OverviewPanel.vue'
import ArticlesPanel from '../components/admin/ArticlesPanel.vue'
import CommentsPanel from '../components/admin/CommentsPanel.vue'
import CategoriesPanel from '../components/admin/CategoriesPanel.vue'
import TagsPanel from '../components/admin/TagsPanel.vue'
import AnnouncePanel from '../components/admin/AnnouncePanel.vue'
import UsersPanel from '../components/admin/UsersPanel.vue'
import StatsPanel from '../components/admin/StatsPanel.vue'
import AIPanel from '../components/admin/AIPanel.vue'
import RankPanel from '../components/admin/RankPanel.vue'

const router = useRouter()
const authStore = useAuthStore()

const currentPanel = ref('overview')
const stats = ref({ pendingArticleCount: 0, blockedCommentCount: 0 })
const toast = ref({ show: false, message: '', type: 'ok' })
const toastTimeout = ref(null)

const panelLabels = {
  overview: '运营概览',
  articles: '文章管理',
  comments: '评论管理',
  categories: '分类管理',
  tags: '标签管理',
  announce: '公告管理',
  users: '用户管理',
  stats: '平台数据统计',
  ai: 'AI问答管理',
  rank: '排行榜管理'
}

const toastStyle = computed(() => {
  const styles = {
    ok: 'background:#1B5A40;color:#E1F4EF;',
    warn: 'background:#6B1A14;color:#F5C8C4;'
  }
  return styles[toast.value.type] || styles.ok
})

const adminName = computed(() => authStore.user?.nickname || authStore.user?.username || '管理员')
const adminInitial = computed(() => adminName.value.charAt(0))

const loadStats = async () => {
  try {
    stats.value = await adminApi.getStats()
  } catch {
    // ignore when token expired
  }
}

const switchPanel = (panel) => {
  currentPanel.value = panel
  loadStats()
}

const showToast = (message, type = 'ok') => {
  toast.value = { show: true, message, type }
  if (toastTimeout.value) {
    clearTimeout(toastTimeout.value)
  }
  toastTimeout.value = setTimeout(() => {
    toast.value.show = false
  }, 2200)
}

const logout = () => {
  authStore.logout()
  router.push('/login')
}

onMounted(() => {
  if (!authStore.isLoggedIn || authStore.user?.role !== 'admin') {
    router.push('/login')
    return
  }
  loadStats()
})

defineExpose({
  showToast
})
</script>

<style scoped>
*,*::before,*::after{margin:0;padding:0;box-sizing:border-box;}
button{cursor:pointer;font-family:inherit;}
input,textarea,select{font-family:inherit;}

.admin-dashboard {
  display: flex;
  height: 100vh;
  overflow: hidden;
  font-family: 'Noto Serif SC', serif;
  background: var(--cream);
  color: var(--ink);
}

.sidebar{
  width:var(--sb);flex-shrink:0;background:#120A04;
  display:flex;flex-direction:column;height:100vh;overflow-y:auto;
}
.sidebar::-webkit-scrollbar{width:0;}
.sb-logo{padding:20px 18px;border-bottom:1px solid rgba(255,255,255,.06);display:flex;align-items:center;gap:9px;}
.sb-seal{width:30px;height:30px;background:rgba(212,160,23,.08);border:1px solid rgba(212,160,23,.2);border-radius:2px;display:flex;align-items:center;justify-content:center;flex-shrink:0;}
.sb-seal span{font-family:'Ma Shan Zheng',serif;font-size:15px;color:var(--g);}
.sb-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;color:var(--g);}
.sb-subtitle{font-size:9px;letter-spacing:2px;color:rgba(255,255,255,.2);margin-top:1px;}
.sb-admin{padding:16px 18px;border-bottom:1px solid rgba(255,255,255,.05);display:flex;align-items:center;gap:9px;}
.sb-avt{width:34px;height:34px;border-radius:50%;background:linear-gradient(135deg,#8B1A14,#D4A017);display:flex;align-items:center;justify-content:center;color:#fff;font-size:13px;flex-shrink:0;}
.sb-admin-name{font-size:12px;color:rgba(255,255,255,.7);}
.sb-admin-role{font-size:10px;color:var(--g);letter-spacing:1px;margin-top:2px;}
.sb-nav{flex:1;padding:8px 0;}
.sb-group-lbl{font-size:9px;letter-spacing:3px;color:rgba(255,255,255,.18);padding:12px 18px 4px;text-transform:uppercase;}
.sb-item{display:flex;align-items:center;gap:9px;padding:10px 18px;font-size:12px;letter-spacing:1px;color:rgba(255,255,255,.42);cursor:pointer;transition:all .18s;border:none;background:none;width:100%;text-align:left;position:relative;}
.sb-item:hover{color:rgba(255,255,255,.8);background:rgba(255,255,255,.04);}
.sb-item.on{color:var(--g);background:rgba(212,160,23,.06);border-right:3px solid var(--g);}
.sb-item-ico{width:16px;height:16px;object-fit:contain;flex-shrink:0;display:block;}
.sb-badge{margin-left:auto;background:var(--v);color:#fff;font-size:9px;padding:2px 6px;border-radius:20px;}
.sb-badge-g{background:var(--jade);}
.sb-footer{padding:14px 18px;border-top:1px solid rgba(255,255,255,.05);}
.sb-footer-btn{width:100%;padding:8px;background:rgba(255,255,255,.04);border:1px solid rgba(255,255,255,.08);border-radius:var(--r);color:rgba(255,255,255,.35);font-size:11px;letter-spacing:2px;cursor:pointer;transition:all .2s;display:flex;align-items:center;justify-content:center;gap:6px;}
.sb-footer-ico{width:14px;height:14px;object-fit:contain;display:block;opacity:.7;}
.sb-footer-btn:hover{background:rgba(192,57,43,.15);border-color:rgba(192,57,43,.3);color:rgba(255,255,255,.6);}

.main{flex:1;display:flex;flex-direction:column;overflow:hidden;}
.topbar{height:54px;background:var(--white);border-bottom:1px solid var(--bd2);display:flex;align-items:center;padding:0 26px;gap:14px;flex-shrink:0;}
.tb-breadcrumb{font-size:11px;letter-spacing:2px;color:var(--dim5);display:flex;align-items:center;gap:6px;}
.tb-breadcrumb em{color:var(--v);font-style:normal;}
.tb-right{margin-left:auto;display:flex;gap:8px;align-items:center;}
.tb-search{display:flex;align-items:center;gap:7px;background:var(--rice);border:1px solid var(--bd2);border-radius:20px;padding:5px 13px;}
.tb-search input{background:none;border:none;outline:none;font-size:12px;color:var(--ink);width:130px;}
.tb-search input::placeholder{color:var(--dim2);}
.tb-btn{display:inline-flex;align-items:center;gap:5px;padding:6px 14px;border:none;border-radius:var(--r);font-size:11px;letter-spacing:1px;cursor:pointer;transition:all .18s;font-family:inherit;}
.tb-btn-v{background:var(--v);color:#fff;}.tb-btn-v:hover{background:var(--v2);}
.tb-btn-g{background:var(--white);color:var(--dim);border:1px solid var(--bd2);}.tb-btn-g:hover{border-color:var(--v);color:var(--v);}
.tb-notif{width:32px;height:32px;border-radius:50%;background:var(--rice);border:1px solid var(--bd2);display:flex;align-items:center;justify-content:center;cursor:pointer;position:relative;}
.tb-notif-ico{width:16px;height:16px;object-fit:contain;display:block;}
.notif-dot{position:absolute;top:6px;right:6px;width:6px;height:6px;background:var(--v);border-radius:50%;border:2px solid var(--white);}
.content{flex:1;overflow-y:auto;padding:28px 28px 60px;}
.content::-webkit-scrollbar{width:5px;}
.content::-webkit-scrollbar-thumb{background:var(--bd2);border-radius:3px;}

.toast {
  position: fixed;
  bottom: 28px;
  left: 50%;
  transform: translateX(-50%);
  padding: 10px 22px;
  border-radius: 3px;
  font-size: 12px;
  letter-spacing: 1px;
  z-index: 9999;
  transition: all 0.25s;
  font-family: 'Noto Serif SC', serif;
  white-space: nowrap;
  opacity: 1;
}
</style>
