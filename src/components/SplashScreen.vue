<template>
  <div class="splash-overlay" ref="rootRef">
    <!-- 调试指示器 - 先确保有东西会动 -->
    <div class="test-box" ref="boxRef">粤韵志</div>
    <div class="test-box2" ref="box2Ref">CANTONESE CULTURE</div>
    <div class="decor" ref="decorRef"></div>

    <button class="close-btn" ref="closeRef" @click="closeSplash">✕ 跳过</button>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { gsap } from 'gsap'

const emit = defineEmits(['close'])

const rootRef = ref(null)
const boxRef = ref(null)
const box2Ref = ref(null)
const decorRef = ref(null)
const closeRef = ref(null)

let ctx = null

const closeSplash = () => {
  console.log('Splash: close requested')
  ctx?.revert()
  emit('close')
}

onMounted(() => {
  console.log('Splash: mounted, starting animation...')
  nextTick(() => {
    console.log('Splash: nextTick fired')
    startAnim()
  })
})

onUnmounted(() => {
  console.log('Splash: unmounted')
  ctx?.revert()
})

function startAnim() {
  // 安全检查
  if (!rootRef.value) { console.error('Splash: rootRef is null'); return }
  if (!boxRef.value) { console.error('Splash: boxRef is null'); return }

  console.log('Splash: DOM elements found, creating GSAP context')
  console.log('Splash: rootRef:', rootRef.value)
  console.log('Splash: boxRef:', boxRef.value)

  ctx = gsap.context(() => {
    console.log('Splash: GSAP context callback running')

    // 最简单的移动动画 —— 让 box 从左边飞到右边
    const tl = gsap.timeline({
      defaults: { duration: 1, ease: 'power2.out' },
      onComplete: () => {
        console.log('Splash: timeline complete, fading out')
        gsap.to(rootRef.value, {
          autoAlpha: 0,
          duration: 1,
          ease: 'power2.inOut',
          onComplete: closeSplash
        })
      }
    })

    // === 最简单的测试：让 boxRef 动起来 ===
    console.log('Splash: adding animations to timeline')

    // 初始状态
    gsap.set(boxRef.value, { x: -100, autoAlpha: 0 })
    gsap.set(box2Ref.value, { autoAlpha: 0, y: 30 })
    gsap.set(decorRef.value, { scale: 0, autoAlpha: 0 })
    gsap.set(closeRef.value, { autoAlpha: 0 })

    // 1. box 从左侧飞入
    tl.to(boxRef.value, {
      x: 0,
      autoAlpha: 1,
      duration: 1.5,
      ease: 'back.out(1.7)'
    })
    .to(boxRef.value, {
      scale: 1.1,
      duration: 0.5,
      ease: 'sine.inOut',
      yoyo: true,
      repeat: 1
    }, '+=0.5')
    // 2. 副标题淡入
    tl.to(box2Ref.value, {
      autoAlpha: 1,
      y: 0,
      duration: 1.2,
      ease: 'power2.out'
    }, '+=0.3')
    // 3. 装饰圆扩散
    tl.to(decorRef.value, {
      scale: 3,
      autoAlpha: 0.1,
      duration: 2,
      ease: 'power2.out'
    }, '-=0.8')
    // 4. 关闭按钮
    tl.to(closeRef.value, {
      autoAlpha: 1,
      duration: 0.8
    }, '+=0.5')
    // 5. 结束
    tl.to(rootRef.value, {
      autoAlpha: 0,
      duration: 1.5,
      ease: 'power2.inOut',
      onComplete: closeSplash
    }, '+=3')

    console.log('Splash: timeline created successfully')

  }, rootRef.value)

  console.log('Splash: GSAP context created')
}
</script>

<style scoped>
.splash-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: #080604;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
  overflow: hidden;
}

.test-box {
  font-family: 'Ma Shan Zheng', serif;
  font-size: 80px;
  color: #D4A017;
  text-shadow: 0 0 30px rgba(212,160,23,0.3);
  will-change: transform, opacity;
}

.test-box2 {
  font-family: 'Noto Serif SC', serif;
  font-size: 14px;
  letter-spacing: 8px;
  color: rgba(212,160,23,0.4);
  will-change: transform, opacity;
}

.decor {
  position: absolute;
  width: 100px;
  height: 100px;
  border: 1px solid rgba(212,160,23,0.15);
  border-radius: 50%;
  pointer-events: none;
  will-change: transform, opacity;
}

.close-btn {
  position: fixed;
  top: 20px;
  right: 20px;
  background: none;
  border: 1px solid rgba(212,160,23,0.15);
  color: rgba(212,160,23,0.4);
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-family: 'Noto Serif SC', serif;
  font-size: 12px;
  letter-spacing: 2px;
  z-index: 10;
}

.close-btn:hover {
  background: rgba(212,160,23,0.1);
  color: rgba(212,160,23,0.7);
}
</style>
