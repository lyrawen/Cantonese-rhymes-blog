<template>
  <div class="page active">
    <div class="nav-gap"></div>
    
    <!-- 页面头部 -->
    <div class="rank-hero">
      <div class="rank-hero-bg"></div>
      <div class="rank-hero-pos wrap">
        <div class="rank-eyebrow">CULTURE RANKINGS</div>
        <h1 class="rank-h1">文化<span>排行榜</span></h1>
        <p class="rank-sub">基于阅读量、点赞量、评论量实时更新</p>
      </div>
    </div>
    
    <!-- 内容区域 -->
    <div class="ranking-body wrap">
      <!-- 切换标签 -->
      <div class="rank-tabs">
        <button 
          v-for="tab in tabs" 
          :key="tab.key"
          :class="['rank-tab', { on: activeTab === tab.key }]"
          @click="activeTab = tab.key"
        >
          {{ tab.label }}
        </button>
      </div>
      
      <div class="ranking-content">
        <!-- 左侧图表区域 -->
        <div class="ranking-chart-section">
          <div class="chart-card">
            <div class="chart-title">
              <span class="chart-title-dot"></span>
              分类阅读量对比
            </div>
            <div class="chart-container" ref="chartRef"></div>
          </div>
        </div>
        
        <!-- 右侧列表区域 -->
        <div class="ranking-list-section">
          <div class="chart-card">
            <div class="chart-title">
              <span class="chart-title-dot"></span>
              TOP 10 {{ currentTabLabel }}排行榜
            </div>
            
            <div class="rank-list">
              <div v-if="loading" class="loading-container">
                <div class="loading-spinner"></div>
                <div class="loading-text">加载中...</div>
              </div>
              <div v-else-if="rankingList.length === 0" class="empty-container">
                <div class="empty-icon">📊</div>
                <div class="empty-text">暂无排行榜数据</div>
              </div>
              <div 
                v-for="(item, index) in rankingList" 
                :key="item.id"
                class="rank-item"
                @click="goToArticle(item.id)"
              >
                <div class="rank-num" :class="getRankClass(index)">
                  {{ index + 1 }}
                </div>
                <div class="rank-img">
                  <img v-if="item.coverImage" :src="fixImagePath(item.coverImage)" :alt="item.title" />
                  <div v-else class="rank-img-placeholder"></div>
                </div>
                <div class="rank-info">
                  <div class="rank-info-title">{{ item.title }}</div>
                  <div class="rank-info-meta">{{ item.author }} · {{ item.category }}</div>
                </div>
                <div class="rank-value-container">
                  <div class="rank-value">{{ formatNumber(getItemValue(item)) }}</div>
                  <div class="rank-value-lbl">{{ getStatLabel() }}量</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const chartRef = ref(null)
const activeTab = ref('views')
const loading = ref(false)
const tabs = [
  { key: 'views', label: '阅读排行', icon: '👁' },
  { key: 'likes', label: '点赞排行', icon: '❤️' },
  { key: 'comments', label: '评论排行', icon: '💬' }
]

// 修复图片路径的辅助函数
const fixImagePath = (path) => {
  if (!path) return ''
  // 确保路径以/uploads开头
  let fixedPath = path.startsWith('/uploads') ? path : `/uploads${path}`
  // 移除路径中的空格
  fixedPath = fixedPath.replace(/\s+/g, '')
  // 将.jpg扩展名替换为.png
  fixedPath = fixedPath.replace(/\.jpg$/i, '.png')
  return fixedPath
}

const currentTabLabel = computed(() => {
  const tab = tabs.find(t => t.key === activeTab.value)
  return tab ? tab.label : ''
})

const rankingData = ref({
  views: [],
  likes: [],
  comments: []
})

const categoryStats = ref([])

const rankingList = computed(() => {
  return rankingData.value[activeTab.value] || []
})

const getRankClass = (index) => {
  if (index === 0) return 'rn1'
  if (index === 1) return 'rn2'
  if (index === 2) return 'rn3'
  return 'rnn'
}

const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num ? num.toLocaleString() : '0'
}

const getItemValue = (item) => {
  switch (activeTab.value) {
    case 'views':
      return item.views
    case 'likes':
      return item.likes
    case 'comments':
      return item.comments
    default:
      return item.views
  }
}

const getStatLabel = () => {
  switch (activeTab.value) {
    case 'views':
      return '阅读'
    case 'likes':
      return '点赞'
    case 'comments':
      return '评论'
    default:
      return '阅读'
  }
}

const goToArticle = (id) => {
  router.push(`/article/${id}`)
}

const fetchRankingData = async (type) => {
  try {
    loading.value = true
    console.log(`开始获取${type}排行榜数据...`)
    const response = await fetch(`/api/articles/ranking/${type}`)
    console.log(`${type}排行榜响应状态:`, response.status, response.ok)
    
    if (response.ok) {
      const data = await response.json()
      console.log(`获取${type}排行榜原始数据:`, data)
      console.log(`获取${type}排行榜数据类型:`, Array.isArray(data) ? '数组' : typeof data)
      
      if (Array.isArray(data)) {
        rankingData.value[type] = data.map(article => ({
          id: article.articleId,
          title: article.title,
          author: article.userNickname || article.authorName || '未知作者',
          category: article.categoryName || '未分类',
          views: article.viewCount || 0,
          likes: article.likeCount || 0,
          comments: article.commentCount || 0,
          coverImage: article.coverImage
        }))
        console.log(`处理后的${type}排行榜数据:`, rankingData.value[type])
      } else {
        console.error(`${type}排行榜数据不是数组:`, data)
        rankingData.value[type] = []
      }
    } else {
      console.error(`获取${type}排行榜失败，状态码:`, response.status)
      const errorText = await response.text()
      console.error(`错误响应:`, errorText)
    }
  } catch (error) {
    console.error('获取排行榜数据失败:', error)
    rankingData.value[type] = []
  } finally {
    loading.value = false
  }
}

const fetchCategoryStats = async () => {
  try {
    const response = await fetch('/api/articles/ranking/category-stats')
    if (response.ok) {
      const data = await response.json()
      categoryStats.value = data.map(item => ({
        name: item[0],
        value: item[1]
      }))
    }
  } catch (error) {
    console.error('获取分类统计失败:', error)
  }
}

const loadAllRankingData = async () => {
  console.log('开始加载所有排行榜数据...')
  try {
    await Promise.all([
      fetchRankingData('views'),
      fetchRankingData('likes'),
      fetchRankingData('comments')
    ])
    console.log('所有排行榜数据加载完成:', rankingData.value)
    await fetchCategoryStats()
    console.log('分类统计数据加载完成:', categoryStats.value)
  } catch (error) {
    console.error('加载排行榜数据失败:', error)
  }
}

let chartInstance = null

const initChart = () => {
  if (!chartRef.value) return
  
  if (typeof window.echarts === 'undefined') {
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js'
    script.onload = () => renderChart()
    document.head.appendChild(script)
  } else {
    renderChart()
  }
}

const renderChart = () => {
  if (!chartRef.value || typeof window.echarts === 'undefined') return
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = window.echarts.init(chartRef.value)
  
  const option = {
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: categoryStats.value.map(item => item.name),
      axisLine: {
        lineStyle: {
          color: '#E0E0E0'
        }
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        show: false
      },
      axisTick: {
        show: false
      },
      splitLine: {
        lineStyle: {
          color: '#F0F0F0'
        }
      },
      axisLabel: {
        color: '#666',
        fontSize: 12
      }
    },
    series: [
      {
        name: '阅读量',
        type: 'bar',
        data: categoryStats.value.map(item => item.value),
        itemStyle: {
          color: new window.echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#C0392B' },
            { offset: 1, color: '#8B0000' }
          ])
        },
        barWidth: '40%'
      }
    ]
  }
  
  chartInstance.setOption(option)
}

onMounted(() => {
  loadAllRankingData().then(() => {
    initChart()
  })
})

watch(activeTab, () => {
  nextTick(() => {
    initChart()
  })
})
</script>

<style scoped>
.nav-gap { height: 62px; }

.wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 36px;
}

.rank-hero {
  background: #1A1008;
  padding: 56px 0;
  position: relative;
  overflow: hidden;
}

.rank-hero-bg {
  position: absolute;
  inset: 0;
  background-image: repeating-linear-gradient(45deg, transparent, transparent 20px, rgba(212,160,23,.03) 20px, rgba(212,160,23,.03) 21px);
}

.rank-hero-pos {
  position: relative;
}

.rank-eyebrow {
  font-size: 11px;
  letter-spacing: 4px;
  color: #D4A017;
  text-transform: uppercase;
  margin-bottom: 12px;
}

.rank-h1 {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 50px;
  color: #fff;
  letter-spacing: 8px;
  margin-bottom: 8px;
}

.rank-h1 span {
  color: #D4A017;
}

.rank-sub {
  font-size: 12px;
  letter-spacing: 4px;
  color: rgba(255,255,255,.4);
}

.ranking-body {
  padding: 40px 36px 80px;
  background: #FBF6EC;
  min-height: calc(100vh - 62px - 200px);
}

.rank-tabs {
  display: flex;
  gap: 0;
  border-bottom: 2px solid #EAD8BE;
  margin-bottom: 32px;
}

.rank-tab {
  padding: 12px 24px;
  font-size: 13px;
  letter-spacing: 2px;
  color: #6B5744;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all .2s;
  background: none;
  border-left: none;
  border-right: none;
  border-top: none;
  font-family: 'Noto Serif SC', serif;
}

.rank-tab:hover {
  color: #C0392B;
}

.rank-tab.on {
  color: #C0392B;
  border-bottom-color: #C0392B;
}

.ranking-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 48px;
}

.chart-card {
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  padding: 24px;
  box-shadow: 0 2px 16px rgba(26,16,8,0.08);
}

.chart-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 18px;
  letter-spacing: 2px;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-title-dot {
  width: 6px;
  height: 6px;
  background: #C0392B;
  border-radius: 50%;
}

.chart-container {
  height: 320px;
}

.rank-list {
  margin-top: 16px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 14px 0;
  border-bottom: 1px solid #EAD8BE;
  cursor: pointer;
  transition: background .2s;
}

.rank-item:last-child {
  border-bottom: none;
}

.rank-item:hover {
  background: #FBF6EC;
  margin: 0 -16px;
  padding: 14px 16px;
}

.rank-num {
  width: 32px;
  height: 32px;
  border-radius: 3px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 16px;
}

.rn1 {
  background: #C0392B;
  color: #fff;
}

.rn2 {
  background: #D4A017;
  color: #1A1008;
}

.rn3 {
  background: #6B5744;
  color: #fff;
}

.rnn {
  background: #EAD8BE;
  color: #6B5744;
}

.rank-img {
  width: 56px;
  height: 48px;
  border-radius: 3px;
  overflow: hidden;
  flex-shrink: 0;
}

.rank-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.rank-img-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #6B0F0F 0%, #C0392B 55%, #D4A017 100%);
}

.loading-container,
.empty-container {
  min-height: 200px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 20px;
}

.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #E8DCC8;
  border-top: 3px solid #C0392B;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text,
.empty-text {
  font-size: 14px;
  color: #6B5744;
  margin-bottom: 8px;
}

.empty-icon {
  font-size: 40px;
  margin-bottom: 12px;
}

.rank-info {
  flex: 1;
}

.rank-info-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 15px;
  letter-spacing: 1px;
  margin-bottom: 4px;
}

.rank-info-meta {
  font-size: 11px;
  color: rgba(107,87,68,0.5);
}

.rank-value-container {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  flex-shrink: 0;
}

.rank-value {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 20px;
  color: #C0392B;
  text-align: right;
}

.rank-value-lbl {
  font-size: 10px;
  color: rgba(107,87,68,0.5);
  text-align: right;
}
</style>
