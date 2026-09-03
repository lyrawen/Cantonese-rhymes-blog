<template>
  <div class="stats-panel">
    <div class="pg-head">
      <div>
        <div class="pg-eyebrow">Analytics</div>
        <div class="pg-title">平台数据统计</div>
        <div class="pg-sub">ECharts 可视化 · 数据库实时数据</div>
      </div>
      <div class="pg-acts">
        <button class="tb-btn tb-btn-v" @click="loadStats">刷新</button>
      </div>
    </div>

    <div class="stat-cards">
      <div class="sc sc-v">
        <div class="sc-top"><div class="sc-ico si-v">👥</div></div>
        <div class="sc-n">{{ formatNum(stats.userCount) }}</div>
        <div class="sc-l">用户总数</div>
      </div>
      <div class="sc sc-g">
        <div class="sc-top"><div class="sc-ico si-g">📝</div></div>
        <div class="sc-n">{{ formatNum(stats.publishedArticleCount) }}</div>
        <div class="sc-l">已发布文章</div>
      </div>
      <div class="sc sc-j">
        <div class="sc-top"><div class="sc-ico si-j">💬</div></div>
        <div class="sc-n">{{ formatNum(stats.commentCount) }}</div>
        <div class="sc-l">评论总数</div>
      </div>
      <div class="sc sc-d">
        <div class="sc-top"><div class="sc-ico si-d">👁</div></div>
        <div class="sc-n">{{ formatNum(stats.totalViewCount) }}</div>
        <div class="sc-l">总阅读量</div>
      </div>
    </div>

    <div class="chart-grid chart-grid-2" style="margin-bottom:18px;">
      <div class="chart-card">
        <div class="chart-head">
          <div class="chart-title"><span class="chart-dot"></span>分类文章分布</div>
        </div>
        <div ref="categoryChart" style="height:220px"></div>
      </div>
      <div class="chart-card">
        <div class="chart-head">
          <div class="chart-title"><span class="chart-dot"></span>文章状态分布</div>
        </div>
        <div ref="statusChart" style="height:220px"></div>
      </div>
    </div>

    <div class="chart-grid chart-grid-2">
      <div class="chart-card">
        <div class="chart-head">
          <div class="chart-title"><span class="chart-dot"></span>标签与公告</div>
        </div>
        <div ref="metaChart" style="height:200px"></div>
      </div>
      <div class="chart-card">
        <div class="chart-head">
          <div class="chart-title"><span class="chart-dot"></span>内容运营概览</div>
        </div>
        <div ref="summaryChart" style="height:200px"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { adminApi } from '../../api/adminApi'
import '../../styles/admin-variables.css'

const stats = ref({
  userCount: 0,
  publishedArticleCount: 0,
  commentCount: 0,
  totalViewCount: 0,
  tagCount: 0,
  announcementCount: 0,
  pendingArticleCount: 0,
  rejectedArticleCount: 0,
  categoryStats: [],
  articleStatusStats: []
})

const categoryChart = ref(null)
const statusChart = ref(null)
const metaChart = ref(null)
const summaryChart = ref(null)
let categoryChartInstance = null
let statusChartInstance = null
let metaChartInstance = null
let summaryChartInstance = null

const C = ['#C0392B', '#D4A017', '#2E7D6A', '#6B5744', '#E04B3A', '#1B5A40']
const formatNum = (value) => Number(value || 0).toLocaleString()

const initCharts = () => {
  const categories = stats.value.categoryStats || []
  const statusStats = stats.value.articleStatusStats || []

  if (categoryChart.value) {
    categoryChartInstance?.dispose()
    categoryChartInstance = echarts.init(categoryChart.value)
    categoryChartInstance.setOption({
      color: [C[0]],
      tooltip: { trigger: 'axis' },
      grid: { left: 10, right: 10, top: 10, bottom: 20, containLabel: true },
      xAxis: { type: 'category', data: categories.map(item => item.name), axisLabel: { fontSize: 10, color: '#6B5744' } },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#EAD8BE' } } },
      series: [{ type: 'bar', data: categories.map(item => Number(item.value || 0)), barMaxWidth: 30, itemStyle: { borderRadius: [2, 2, 0, 0] } }]
    })
  }

  if (statusChart.value) {
    statusChartInstance?.dispose()
    statusChartInstance = echarts.init(statusChart.value)
    statusChartInstance.setOption({
      color: C,
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['35%', '65%'],
        data: statusStats.map(item => ({ name: item.name, value: Number(item.value || 0) })),
        label: { fontSize: 10 }
      }]
    })
  }

  if (metaChart.value) {
    metaChartInstance?.dispose()
    metaChartInstance = echarts.init(metaChart.value)
    metaChartInstance.setOption({
      color: [C[1], C[2]],
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['标签数', '公告数'], axisLabel: { fontSize: 11, color: '#6B5744' } },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#EAD8BE' } } },
      series: [{ type: 'bar', data: [Number(stats.value.tagCount || 0), Number(stats.value.announcementCount || 0)], barWidth: 40 }]
    })
  }

  if (summaryChart.value) {
    summaryChartInstance?.dispose()
    summaryChartInstance = echarts.init(summaryChart.value)
    summaryChartInstance.setOption({
      color: [C[0], C[1], C[2]],
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: ['用户', '文章', '评论', '阅读'], axisLabel: { fontSize: 10, color: '#6B5744' } },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#EAD8BE' } } },
      series: [{
        type: 'bar',
        data: [
          Number(stats.value.userCount || 0),
          Number(stats.value.publishedArticleCount || 0),
          Number(stats.value.commentCount || 0),
          Number(stats.value.totalViewCount || 0)
        ],
        barMaxWidth: 36
      }]
    })
  }
}

const loadStats = async () => {
  stats.value = await adminApi.getStats()
  await nextTick()
  initCharts()
}

onMounted(loadStats)

onUnmounted(() => {
  categoryChartInstance?.dispose()
  statusChartInstance?.dispose()
  metaChartInstance?.dispose()
  summaryChartInstance?.dispose()
})
</script>

<style scoped>
:root{--v:#C0392B;--g:#D4A017;--jade:#2E7D6A;--dim:#6B5744;--dim5:rgba(107,87,68,.5);--white:#fff;--bd2:#EAD8BE;--sh:0 2px 12px rgba(26,16,8,.07);--r:3px;--r2:6px;}
.pg-head{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:28px;}
.pg-eyebrow{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:5px;}
.pg-title{font-family:'ZCOOL XiaoWei',serif;font-size:24px;letter-spacing:5px;}
.pg-sub{font-size:12px;color:var(--dim5);margin-top:4px;}
.pg-acts{display:flex;gap:8px;}
.stat-cards{display:grid;grid-template-columns:repeat(4,1fr);gap:16px;margin-bottom:26px;}
.sc{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:18px 20px;position:relative;overflow:hidden;}
.sc::before{content:'';position:absolute;top:0;left:0;width:3px;height:100%;}
.sc-v::before{background:var(--v);}.sc-g::before{background:var(--g);}.sc-j::before{background:var(--jade);}.sc-d::before{background:var(--dim);}
.sc-top{margin-bottom:12px;}
.sc-ico{width:36px;height:36px;border-radius:var(--r);display:flex;align-items:center;justify-content:center;font-size:16px;}
.si-v{background:rgba(192,57,43,.1);}.si-g{background:rgba(212,160,23,.14);}.si-j{background:rgba(46,125,106,.1);}.si-d{background:rgba(26,16,8,.07);}
.sc-n{font-family:'ZCOOL XiaoWei',serif;font-size:30px;letter-spacing:2px;line-height:1;margin-bottom:4px;}
.sc-l{font-size:11px;color:var(--dim5);}
.chart-grid{display:grid;gap:18px;margin-bottom:20px;}
.chart-grid-2{grid-template-columns:1fr 1fr;}
.chart-card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:20px 22px;box-shadow:var(--sh);}
.chart-head{margin-bottom:16px;}
.chart-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;display:flex;align-items:center;gap:7px;}
.chart-dot{width:7px;height:7px;background:var(--v);border-radius:50%;}
.tb-btn{padding:6px 14px;border:none;border-radius:var(--r);font-size:11px;cursor:pointer;background:var(--v);color:#fff;}
</style>
