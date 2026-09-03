<template>
  <svg :width="width" :height="height" viewBox="0 0 800 400" xmlns="http://www.w3.org/2000/svg" ref="svgRoot">
    <defs>
      <linearGradient id="archFade" x1="0%" y1="0%" x2="0%" y2="100%">
        <stop offset="0%" :stop-color="color" stop-opacity="0.1" />
        <stop offset="60%" :stop-color="color" stop-opacity="0.5" />
        <stop offset="100%" :stop-color="color" stop-opacity="0.8" />
      </linearGradient>
    </defs>

    <!-- 远景层 - 远山/云 -->
    <g ref="layerFar" :style="{ opacity: 0 }">
      <path d="M0 280 Q80 240 160 270 Q240 230 320 260 Q400 220 480 250 Q560 230 640 260 Q720 240 800 270 L800 400 L0 400Z"
            :fill="color" opacity="0.08" />
      <path d="M0 300 Q100 270 200 290 Q300 260 400 280 Q500 255 600 275 Q700 260 800 280 L800 400 L0 400Z"
            :fill="color" opacity="0.12" />
    </g>

    <!-- 中景 - 镬耳屋群 -->
    <g ref="layerMid" :style="{ opacity: 0 }">
      <!-- 建筑 1 -->
      <g transform="translate(50, 100)">
        <path d="M0 200 L0 120 Q25 90 50 120 L50 200Z" :fill="color" opacity="0.25" />
        <path d="M25 120 Q25 80 50 90 Q75 80 75 120" :stroke="color" stroke-width="2" fill="none" opacity="0.4" />
        <path d="M15 160 L35 160" :stroke="color" stroke-width="1" fill="none" opacity="0.3" />
        <rect x="5" y="180" width="12" height="20" :fill="color" opacity="0.15" rx="1" />
        <rect x="33" y="180" width="12" height="20" :fill="color" opacity="0.15" rx="1" />
      </g>

      <!-- 建筑 2 - 主建筑 -->
      <g transform="translate(250, 60)">
        <path d="M0 260 L0 150 Q50 100 100 150 L100 260Z" :fill="color" opacity="0.35" />
        <path d="M50 150 Q50 90 100 100 Q150 90 150 150" :stroke="color" stroke-width="2.5" fill="none" opacity="0.5" />
        <!-- 锅耳山墙 -->
        <path d="M0 150 Q-10 110 10 100 Q20 90 15 110 L15 150" :stroke="color" stroke-width="2" fill="none" opacity="0.4" />
        <path d="M100 150 Q110 110 90 100 Q80 90 85 110 L85 150" :stroke="color" stroke-width="2" fill="none" opacity="0.4" />
        <!-- 窗户 -->
        <rect x="20" y="180" width="20" height="30" :fill="color" opacity="0.15" rx="2" />
        <rect x="60" y="180" width="20" height="30" :fill="color" opacity="0.15" rx="2" />
        <rect x="20" y="220" width="20" height="30" :fill="color" opacity="0.15" rx="2" />
        <rect x="60" y="220" width="20" height="30" :fill="color" opacity="0.15" rx="2" />
        <!-- 门 -->
        <rect x="35" y="230" width="30" height="30" :fill="color" opacity="0.12" rx="1" />
      </g>

      <!-- 建筑 3 -->
      <g transform="translate(500, 130)">
        <path d="M0 220 L0 130 Q30 105 60 130 L60 220Z" :fill="color" opacity="0.2" />
        <path d="M30 130 Q30 95 55 105 Q80 95 80 130" :stroke="color" stroke-width="2" fill="none" opacity="0.35" />
        <rect x="10" y="170" width="15" height="20" :fill="color" opacity="0.12" rx="1" />
        <rect x="35" y="170" width="15" height="20" :fill="color" opacity="0.12" rx="1" />
      </g>

      <!-- 建筑 4 - 塔 -->
      <g transform="translate(620, 150)">
        <path d="M30 200 L30 100 L20 105 L20 95 L30 85 L30 70 L28 72 L28 60 L32 60 L32 72 L30 70 L30 85 L40 95 L40 105 L30 100 L30 200Z"
              :fill="color" opacity="0.18" />
      </g>
    </g>

    <!-- 前景层 - 细节建筑 -->
    <g ref="layerFront" :style="{ opacity: 0 }">
      <g transform="translate(120, 180)">
        <rect x="0" y="160" width="80" height="40" :fill="color" opacity="0.3" rx="1" />
        <path d="M-5 160 L40 130 L85 160" :stroke="color" stroke-width="1.5" fill="none" opacity="0.35" />
        <rect x="10" y="170" width="15" height="25" :fill="color" opacity="0.15" rx="1" />
        <rect x="55" y="170" width="15" height="25" :fill="color" opacity="0.15" rx="1" />
      </g>

      <!-- 灯笼（前景装饰） -->
      <g transform="translate(370, 200)">
        <line x1="10" y1="0" x2="10" y2="15" :stroke="color" stroke-width="1" opacity="0.4" />
        <ellipse cx="10" cy="25" rx="7" ry="10" :fill="color" opacity="0.2" />
        <line x1="5" y1="25" x2="15" y2="25" :stroke="color" stroke-width="0.5" opacity="0.3" />
        <line x1="10" y1="35" x2="10" y2="40" :stroke="color" stroke-width="0.5" opacity="0.3" />
      </g>
    </g>
  </svg>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  width: { type: [String, Number], default: '100%' },
  height: { type: [String, Number], default: '100%' },
  color: { type: String, default: '#D4A017' }
})

const svgRoot = ref(null)
const layerFar = ref(null)
const layerMid = ref(null)
const layerFront = ref(null)

defineExpose({ svgRoot, layerFar, layerMid, layerFront })
</script>
