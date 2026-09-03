<template>
  <svg :width="width" :height="height" viewBox="0 0 400 200" xmlns="http://www.w3.org/2000/svg" ref="svgRoot">
    <!-- 祥云 1 - 左 -->
    <g ref="cloud1" :style="{ opacity: 0 }">
      <path
        d="M20 140 Q10 120 30 110 Q25 90 50 85 Q55 60 85 65 Q95 45 120 55 Q130 35 155 50 Q175 35 195 55 Q220 45 225 70 L230 70 Q245 55 260 70 Q275 60 290 75 Q310 65 320 85 Q340 80 350 100 L350 100 Q365 95 370 110 Q380 105 385 120 L385 120 Q390 135 380 145 Z"
        :stroke="color"
        stroke-width="1.5"
        fill="none"
        :opacity="0.3"
      />
      <path
        d="M40 130 Q35 115 50 108 Q48 92 68 88 Q72 68 92 73 Q100 58 120 65 Q128 50 148 60 Q162 48 178 62 Q198 55 210 72 Q228 60 240 75 Q252 66 268 78 Q282 70 295 82 Q310 75 320 90 Q335 85 342 98 L342 98"
        :stroke="color"
        stroke-width="1"
        fill="none"
        :opacity="0.15"
      />
    </g>
    <!-- 祥云 2 - 右 -->
    <g ref="cloud2" :style="{ opacity: 0 }">
      <path
        d="M380 60 Q390 40 370 30 Q378 15 355 10 Q360 -5 335 0 Q325 -15 305 -5 Q290 -20 270 -5 Q255 -15 240 0 Q225 -10 215 5 Q205 -5 195 10 Q185 0 175 15 Q168 5 160 20 L160 20"
        :stroke="color"
        stroke-width="1.5"
        fill="none"
        :opacity="0.25"
      />
      <path
        d="M370 50 Q375 35 358 28 Q362 16 342 12 Q345 0 325 5 Q318 -8 302 0 Q292 -10 278 0 Q265 -8 252 3 Q240 -5 228 8 Q218 0 208 12"
        :stroke="color"
        stroke-width="1"
        fill="none"
        :opacity="0.12"
      />
    </g>
    <!-- 祥云 3 - 中 -->
    <g ref="cloud3" :style="{ opacity: 0 }">
      <path
        d="M120 170 Q115 155 130 148 Q125 132 145 128 Q140 112 162 108 Q175 95 195 105 Q208 90 225 100 Q240 88 258 98 Q275 88 290 100 Q308 92 320 108 Q338 100 348 118 Q362 112 370 130 L370 130"
        :stroke="color"
        stroke-width="1.2"
        fill="none"
        :opacity="0.2"
      />
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
const cloud1 = ref(null)
const cloud2 = ref(null)
const cloud3 = ref(null)

defineExpose({ svgRoot, cloud1, cloud2, cloud3 })
</script>
