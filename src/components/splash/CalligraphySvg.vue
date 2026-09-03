<template>
  <svg :width="width" :height="height" viewBox="0 0 400 500" xmlns="http://www.w3.org/2000/svg" ref="svgRoot">
    <defs>
      <linearGradient id="inkGrad" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" :stop-color="goldColor" />
        <stop offset="50%" :stop-color="brightGold" />
        <stop offset="100%" :stop-color="goldColor" />
      </linearGradient>
      <filter id="glowFilter">
        <feGaussianBlur stdDeviation="3" result="blur" />
        <feMerge><feMergeNode in="blur" /><feMergeNode in="SourceGraphic" /></feMerge>
      </filter>
    </defs>

    <!-- 背景光晕 -->
    <circle cx="200" cy="250" r="120" :fill="goldColor" opacity="0.05" ref="glowBg" />
    <circle cx="200" cy="250" r="80" :fill="goldColor" opacity="0.08" ref="glowMid" />
    <circle cx="200" cy="250" r="40" :fill="brightGold" opacity="0.1" ref="glowCore" />

    <!-- 「粤」字书法 - 使用粗笔触风格 -->
    <g ref="calligraphyGroup" filter="url(#glowFilter)">
      <!-- 顶部撇点 - 起笔 -->
      <path
        d="M180 60 Q190 50 210 55 Q230 60 240 80 Q245 90 235 85 Q220 75 200 70 Q185 68 180 60Z"
        :fill="goldColor"
        class="yue-stroke"
        ref="stroke1"
      />

      <!-- 左上方折笔 -->
      <path
        d="M165 80 Q160 90 158 105 Q155 120 160 130 Q165 135 170 125 Q175 110 178 100 Q180 95 185 90 Q180 85 170 82Z"
        :fill="goldColor"
        class="yue-stroke"
        ref="stroke2"
      />

      <!-- 左撇 - 长锋 -->
      <path
        d="M185 95 Q175 120 155 160 Q140 195 130 220 Q125 235 132 238 Q140 235 148 215 Q160 185 175 155 Q185 135 190 115 Q192 105 188 98Z"
        :fill="goldColor"
        class="yue-stroke"
        ref="stroke3"
      />

      <!-- 中间横折 -->
      <path
        d="M160 145 Q170 140 200 135 Q230 130 250 140 Q258 145 255 155 Q248 160 230 158 Q210 155 190 155 Q170 158 162 155Z"
        :fill="goldColor"
        class="yue-stroke"
        ref="stroke4"
      />

      <!-- 中间竖 -->
      <path
        d="M205 135 Q210 160 215 195 Q218 220 215 250 Q212 265 205 270 Q198 265 195 250 Q192 220 195 195 Q198 165 200 145Z"
        :fill="goldColor"
        class="yue-stroke"
        ref="stroke5"
      />

      <!-- 下方横 -->
      <path
        d="M155 230 Q175 225 210 222 Q245 218 270 225 Q278 228 275 238 Q268 243 245 240 Q215 238 185 238 Q162 240 155 238Z"
        :fill="goldColor"
        class="yue-stroke"
        ref="stroke6"
      />

      <!-- 下方竖折 -->
      <path
        d="M215 235 Q218 260 222 290 Q225 315 228 340 Q230 360 235 375 Q245 395 260 400 Q275 398 280 385 Q282 375 275 370 Q260 365 250 350 Q245 335 240 310 Q238 290 235 265 Q233 248 228 238Z"
        :fill="goldColor"
        class="yue-stroke"
        ref="stroke7"
      />

      <!-- 底部钩 -->
      <path
        d="M160 360 Q180 355 210 350 Q240 345 260 350 Q268 352 265 342 Q258 335 240 338 Q215 342 185 348 Q168 352 160 360Z"
        :fill="goldColor"
        class="yue-stroke"
        ref="stroke8"
      />
    </g>

    <!-- 墨点粒子 -->
    <g ref="inkSplashes">
      <circle v-for="(s, i) in splashes" :key="'ink-'+i"
              :cx="s.x" :cy="s.y" :r="s.r" :fill="goldColor" :opacity="0" />
    </g>
  </svg>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  width: { type: [String, Number], default: '100%' },
  height: { type: [String, Number], default: '100%' },
  goldColor: { type: String, default: '#D4A017' },
  brightGold: { type: String, default: '#F0D060' }
})

const svgRoot = ref(null)
const calligraphyGroup = ref(null)
const glowBg = ref(null)
const glowMid = ref(null)
const glowCore = ref(null)
const inkSplashes = ref(null)
const stroke1 = ref(null)
const stroke2 = ref(null)
const stroke3 = ref(null)
const stroke4 = ref(null)
const stroke5 = ref(null)
const stroke6 = ref(null)
const stroke7 = ref(null)
const stroke8 = ref(null)

// 墨点位置
const splashes = Array.from({ length: 15 }, () => ({
  x: 160 + Math.random() * 80,
  y: 60 + Math.random() * 350,
  r: 2 + Math.random() * 5
}))

defineExpose({ svgRoot, calligraphyGroup, glowBg, glowMid, glowCore, inkSplashes,
  stroke1, stroke2, stroke3, stroke4, stroke5, stroke6, stroke7, stroke8 })
</script>
