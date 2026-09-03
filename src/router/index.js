import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Explore from '../views/Explore.vue'
import ArticleDetail from '../views/ArticleDetail.vue'
import AuthorProfile from '../views/AuthorProfile.vue'
import Login from '../views/Login.vue'
import AIChat from '../views/AIChat.vue'
import Ranking from '../views/Ranking.vue'
import Write from '../views/Write.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import Messages from '../views/Messages.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/explore',
    name: 'Explore',
    component: Explore
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: ArticleDetail
  },
  {
    path: '/author/:id',
    name: 'AuthorProfile',
    component: AuthorProfile
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/ai-chat',
    name: 'AIChat',
    component: AIChat
  },
  {
    path: '/ranking',
    name: 'Ranking',
    component: Ranking
  },
  {
    path: '/write',
    name: 'Write',
    component: Write
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/messages',
    name: 'Messages',
    component: Messages,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router