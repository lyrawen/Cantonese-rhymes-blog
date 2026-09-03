<template>
  <div class="rank-panel">
    <div class="pg-head">
      <div>
        <div class="pg-eyebrow">Ranking</div>
        <div class="pg-title">排行榜管理</div>
        <div class="pg-sub">TOP 10 文章排行 · 数据库实时数据</div>
      </div>
      <div class="pg-acts">
        <button class="tb-btn tb-btn-v" @click="loadRankings">刷新</button>
      </div>
    </div>

    <p v-if="loading" class="loading-tip">加载中...</p>

    <div class="chart-grid chart-grid-2">
      <div class="chart-card">
        <div class="chart-head"><div class="chart-title"><span class="chart-dot"></span>阅读量排行</div></div>
        <div ref="viewChart" style="height:320px"></div>
      </div>
      <div class="chart-card">
        <div class="chart-head"><div class="chart-title"><span class="chart-dot"></span>点赞量排行</div></div>
        <div ref="likeChart" style="height:320px"></div>
      </div>
    </div>

    <div class="chart-grid chart-grid-2">
      <div class="chart-card">
        <div class="chart-head"><div class="chart-title"><span class="chart-dot"></span>评论量排行</div></div>
        <div ref="commentChart" style="height:320px"></div>
      </div>
      <div class="chart-card">
        <div class="chart-head"><div class="chart-title"><span class="chart-dot"></span>收藏量排行</div></div>
        <div ref="favoriteChart" style="height:320px"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { adminApi } from '../../api/adminApi'
import '../../styles/admin-variables.css'

const viewChart = ref(null)
const likeChart = ref(null)
const commentChart = ref(null)
const favoriteChart = ref(null)
const loading = ref(false)

let viewChartInstance = null
let likeChartInstance = null
let commentChartInstance = null
let favoriteChartInstance = null

const C = ['#C0392B', '#D4A017', '#2E7D6A', '#6B5744']

const buildBarOption = (items, valueKey, color) => {
  const titles = items.map(item => item.title).reverse()
  const values = items.map(item => Number(item[valueKey] || 0)).reverse()
  return {
    color: [color],
    tooltip: { trigger: 'axis' },
    grid: { left: 10, right: 40, top: 10, bottom: 10, containLabel: true },
    xAxis: { type: 'value', splitLine: { lineStyle: { color: '#EAD8BE' } }, axisLabel: { fontSize: 9, color: '#6B5744' } },
    yAxis: { type: 'category', data: titles, axisLabel: { fontSize: 10, color: '#6B5744' } },
    series: [{ type: 'bar', data: values, barMaxWidth: 14, label: { show: true, position: 'right', fontSize: 9 } }]
  }
}

const renderChart = (el, instanceRef, items, valueKey, color) => {
  if (!el) return null
  instanceRef?.dispose()
  const chart = echarts.init(el)
  chart.setOption(buildBarOption(items, valueKey, color))
  return chart
}

const loadRankings = async () => {
  loading.value = true
  try {
    const [views, likes, comments, favorites] = await Promise.all([
      adminApi.getRanking('views'),
      adminApi.getRanking('likes'),
      adminApi.getRanking('comments'),
      adminApi.getRanking('favorites')
    ])
    await nextTick()
    viewChartInstance = renderChart(viewChart.value, viewChartInstance, views, 'viewCount', C[0])
    likeChartInstance = renderChart(likeChart.value, likeChartInstance, likes, 'likeCount', C[1])
    commentChartInstance = renderChart(commentChart.value, commentChartInstance, comments, 'commentCount', C[2])
    favoriteChartInstance = renderChart(favoriteChart.value, favoriteChartInstance, favorites, 'favoriteCount', C[3])
  } finally {
    loading.value = false
  }
}

onMounted(loadRankings)

onUnmounted(() => {
  viewChartInstance?.dispose()
  likeChartInstance?.dispose()
  commentChartInstance?.dispose()
  favoriteChartInstance?.dispose()
})
</script>

<style scoped>
:root{--v:#C0392B;--dim5:rgba(107,87,68,.5);--white:#fff;--bd2:#EAD8BE;--sh:0 2px 12px rgba(26,16,8,.07);--r:3px;--r2:6px;}
.pg-head{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:28px;}
.pg-eyebrow{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:5px;}
.pg-title{font-family:'ZCOOL XiaoWei',serif;font-size:24px;letter-spacing:5px;}
.pg-sub{font-size:12px;color:var(--dim5);margin-top:4px;}
.pg-acts{display:flex;gap:8px;}
.chart-grid{display:grid;gap:18px;margin-bottom:20px;}
.chart-grid-2{grid-template-columns:1fr 1fr;}
.chart-card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:20px 22px;box-shadow:var(--sh);}
.chart-head{margin-bottom:16px;}
.chart-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;display:flex;align-items:center;gap:7px;}
.chart-dot{width:7px;height:7px;background:var(--v);border-radius:50%;}
.tb-btn{padding:6px 14px;border:none;border-radius:var(--r);font-size:11px;cursor:pointer;background:var(--v);color:#fff;}
.loading-tip{color:var(--dim5);margin-bottom:16px;font-size:13px;}
</style>
