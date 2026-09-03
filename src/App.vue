<template>
  <div id="app">
    <GlobalNav v-if="showNav" />
    <router-view />
    <SiteFooter v-if="showFooter" />
    <SplashScreen v-if="showSplash" @close="handleSplashClose" />
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { defineAsyncComponent } from 'vue'
import GlobalNav from './components/GlobalNav.vue'
import SiteFooter from './components/SiteFooter.vue'

const SplashScreen = defineAsyncComponent(() => import('./components/SplashScreen.vue'))

const route = useRoute()
const showNav = computed(() => route.name !== 'Login')
const showFooter = computed(() => route.name !== 'Login')

// === 出场动画控制 ===
const showSplash = ref(false)

// 监听路由变化，判断是否展示出场动画
watch(
  () => route.path,
  (path) => {
    if (path === '/' && !sessionStorage.getItem('splashPlayed')) {
      showSplash.value = true
    }
  },
  { immediate: true }
)

const handleSplashClose = () => {
  showSplash.value = false
  sessionStorage.setItem('splashPlayed', 'true')
}
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
</style>