<template>
  <svg :width="width" :height="height" viewBox="0 0 600 400" xmlns="http://www.w3.org/2000/svg" ref="svgRoot">
    <defs>
      <linearGradient id="steamGrad" x1="0%" y1="100%" x2="0%" y2="0%">
        <stop offset="0%" :stop-color="goldColor" stop-opacity="0" />
        <stop offset="50%" :stop-color="goldColor" stop-opacity="0.15" />
        <stop offset="100%" :stop-color="goldColor" stop-opacity="0" />
      </linearGradient>
    </defs>

    <!-- 蒸笼组 -->
    <g ref="steamerGroup" :style="{ opacity: 0 }">
      <!-- 蒸笼主体 -->
      <ellipse cx="200" cy="260" rx="80" ry="20" :fill="brownColor" opacity="0.5" />
      <rect x="120" y="200" width="160" height="60" :fill="brownColor" opacity="0.4" rx="2" />
      <ellipse cx="200" cy="200" rx="80" ry="20" :fill="brownColor" opacity="0.55" />
      <ellipse cx="200" cy="200" rx="65" ry="14" :fill="brownColor" opacity="0.3" />

      <!-- 蒸笼纹理 -->
      <line v-for="i in 6" :key="'sl1-'+i"
            :x1="130 + i * 24" y1="200" :x2="130 + i * 24" y2="260"
            :stroke="brownColor" stroke-width="0.5" opacity="0.15" />

      <!-- 蒸笼盖 -->
      <ellipse cx="200" cy="195" rx="70" ry="16" :fill="brownColor" opacity="0.45" />
      <ellipse cx="200" cy="190" rx="45" ry="10" :fill="brownColor" opacity="0.35" />
      <ellipse cx="200" cy="188" rx="10" ry="4" :fill="goldColor" opacity="0.3" />

      <!-- 蒸汽 -->
      <g ref="steamGroup">
        <path v-for="(s, i) in steamPaths" :key="'steam-'+i"
              :d="s.d" :stroke="goldColor" stroke-width="1.5" fill="none" :opacity="0" />
      </g>
    </g>

    <!-- 茶壶 -->
    <g ref="teapotGroup" :style="{ opacity: 0 }">
      <!-- 壶身 -->
      <ellipse cx="420" cy="260" rx="55" ry="15" :fill="brownColor" opacity="0.4" />
      <path d="M375 200 Q365 260 375 260 L465 260 Q475 260 465 200Z" :fill="brownColor" opacity="0.35" />
      <ellipse cx="420" cy="200" rx="45" ry="12" :fill="brownColor" opacity="0.45" />
      <ellipse cx="420" cy="195" rx="30" ry="7" :fill="brownColor" opacity="0.3" />

      <!-- 壶盖 -->
      <ellipse cx="420" cy="195" rx="25" ry="6" :fill="brownColor" opacity="0.4" />
      <ellipse cx="420" cy="192" rx="8" ry="3" :fill="goldColor" opacity="0.25" />

      <!-- 壶嘴 -->
      <path d="M465 230 Q490 220 495 200" :stroke="brownColor" stroke-width="4" fill="none" opacity="0.35" stroke-linecap="round" />

      <!-- 壶把 -->
      <path d="M375 210 Q350 210 350 240 Q350 265 375 265" :stroke="brownColor" stroke-width="3.5" fill="none" opacity="0.35" stroke-linecap="round" />

      <!-- 茶壶热气 -->
      <path d="M420 185 Q415 170 420 160 Q425 150 420 140" :stroke="goldColor" stroke-width="1.5" fill="none" opacity="0" ref="teaSteam1" />
    </g>

    <!-- 点心组 -->
    <g ref="dimsumGroup" :style="{ opacity: 0 }">
      <!-- 虾饺 1 -->
      <ellipse cx="150" cy="300" rx="18" ry="8" :fill="goldColor" opacity="0.2" />
      <path d="M135 298 Q142 285 150 288 Q158 285 165 298" :fill="goldColor" opacity="0.15" />

      <!-- 烧卖 2 -->
      <ellipse cx="220" cy="310" rx="14" ry="6" :fill="goldColor" opacity="0.2" />
      <rect x="208" y="298" width="24" height="12" :fill="goldColor" opacity="0.12" rx="2" />
      <circle cx="220" cy="296" r="4" :fill="goldColor" opacity="0.15" />

      <!-- 肠粉 3 -->
      <rect x="260" y="305" width="40" height="10" :fill="goldColor" opacity="0.15" rx="3" />
      <rect x="265" y="307" width="30" height="6" :fill="goldColor" opacity="0.1" rx="2" />
    </g>
  </svg>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  width: { type: [String, Number], default: '100%' },
  height: { type: [String, Number], default: '100%' },
  goldColor: { type: String, default: '#D4A017' },
  brownColor: { type: String, default: '#8B5A2B' }
})

const svgRoot = ref(null)
const steamerGroup = ref(null)
const steamGroup = ref(null)
const teapotGroup = ref(null)
const dimsumGroup = ref(null)
const teaSteam1 = ref(null)

// 蒸汽路径 - 多个弯曲上升的线条
const steamPaths = Array.from({ length: 5 }, (_, i) => ({
  d: `M${180 + i * 10} 185 Q${175 + i * 8} 165 ${185 + i * 6} 150 Q${190 + i * 5} 135 ${180 + i * 4} 120`
}))

defineExpose({ svgRoot, steamerGroup, steamGroup, teapotGroup, dimsumGroup, teaSteam1 })
</script>
