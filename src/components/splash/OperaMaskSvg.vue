<template>
  <svg :width="width" :height="height" viewBox="0 0 400 500" xmlns="http://www.w3.org/2000/svg" ref="svgRoot">
    <defs>
      <radialGradient id="maskGlow" cx="50%" cy="45%" r="55%">
        <stop offset="0%" stop-color="#C0392B" stop-opacity="0.15" />
        <stop offset="100%" stop-color="#C0392B" stop-opacity="0" />
      </radialGradient>
      <linearGradient id="goldStroke" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="#D4A017" />
        <stop offset="50%" stop-color="#F0D060" />
        <stop offset="100%" stop-color="#D4A017" />
      </linearGradient>
    </defs>

    <!-- 发光背景 -->
    <ellipse cx="200" cy="220" rx="160" ry="190" fill="url(#maskGlow)" />

    <!-- 脸谱主体 - 红色底色 -->
    <g ref="maskGroup">
      <!-- 脸部轮廓 -->
      <ellipse cx="200" cy="230" rx="110" ry="150" fill="#C0392B" :stroke="strokeColor" stroke-width="2.5" />

      <!-- 额头纹样 - 金色 -->
      <path d="M160 130 Q200 100 240 130 Q200 145 160 130Z" :fill="goldColor" opacity="0.8" />
      <path d="M175 120 Q200 105 225 120" :stroke="goldColor" stroke-width="2" fill="none" opacity="0.6" />

      <!-- 眉毛 - 粗黑 -->
      <path d="M135 165 Q168 140 200 155" :stroke="darkColor" stroke-width="8" fill="none" stroke-linecap="round" />
      <path d="M265 165 Q232 140 200 155" :stroke="darkColor" stroke-width="8" fill="none" stroke-linecap="round" />

      <!-- 眼部白色区域 -->
      <ellipse cx="165" cy="195" rx="32" ry="22" fill="white" opacity="0.9" />
      <ellipse cx="235" cy="195" rx="32" ry="22" fill="white" opacity="0.9" />

      <!-- 眼珠 -->
      <ellipse cx="165" cy="195" rx="16" ry="16" :fill="darkColor" />
      <ellipse cx="235" cy="195" rx="16" ry="16" :fill="darkColor" />

      <!-- 眼珠高光 -->
      <ellipse cx="172" cy="188" rx="6" ry="5" fill="white" opacity="0.7" />
      <ellipse cx="242" cy="188" rx="6" ry="5" fill="white" opacity="0.7" />

      <!-- 眼角纹样 -->
      <path d="M135 195 Q120 190 110 200" :stroke="goldColor" stroke-width="2" fill="none" opacity="0.6" />
      <path d="M265 195 Q280 190 290 200" :stroke="goldColor" stroke-width="2" fill="none" opacity="0.6" />

      <!-- 鼻梁 -->
      <path d="M200 200 L200 270" :stroke="darkColor" stroke-width="3" fill="none" />

      <!-- 脸颊纹样 - 金色 -->
      <path d="M120 230 Q140 220 150 240 Q140 260 120 250" :fill="goldColor" opacity="0.5" />
      <path d="M280 230 Q260 220 250 240 Q260 260 280 250" :fill="goldColor" opacity="0.5" />

      <!-- 嘴部 -->
      <ellipse cx="200" cy="300" rx="30" ry="12" fill="#1A1008" />
      <path d="M175 298 Q200 290 225 298" :stroke="goldColor" stroke-width="1.5" fill="none" />

      <!-- 下巴纹样 -->
      <path d="M175 330 Q200 350 225 330" :stroke="goldColor" stroke-width="1.5" fill="none" opacity="0.5" />

      <!-- 胡须 -->
      <path d="M160 280 Q140 310 120 340" :stroke="darkColor" stroke-width="3" fill="none" opacity="0.4" />
      <path d="M240 280 Q260 310 280 340" :stroke="darkColor" stroke-width="3" fill="none" opacity="0.4" />

      <!-- 头冠装饰 -->
      <path d="M140 100 Q170 75 200 80 Q230 75 260 100" :stroke="goldColor" stroke-width="3" fill="none" />
      <circle cx="200" cy="80" r="8" :fill="goldColor" />
      <circle cx="170" cy="85" r="5" :fill="goldColor" opacity="0.7" />
      <circle cx="230" cy="85" r="5" :fill="goldColor" opacity="0.7" />
    </g>

    <!-- 金色粒子装饰 -->
    <g ref="particles">
      <circle v-for="(p, i) in particleData" :key="i" :cx="p.x" :cy="p.y" :r="p.r" :fill="goldColor" :opacity="0" />
    </g>
  </svg>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  width: { type: [String, Number], default: '100%' },
  height: { type: [String, Number], default: '100%' },
  goldColor: { type: String, default: '#D4A017' },
  strokeColor: { type: String, default: '#F0D060' },
  darkColor: { type: String, default: '#1A1008' }
})

const svgRoot = ref(null)
const maskGroup = ref(null)
const particles = ref(null)

// 生成粒子位置
const particleData = Array.from({ length: 20 }, () => ({
  x: 100 + Math.random() * 200,
  y: 60 + Math.random() * 320,
  r: 1 + Math.random() * 3
}))

defineExpose({ svgRoot, maskGroup, particles })
</script>
