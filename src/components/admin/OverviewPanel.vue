<template>
  <div class="overview-panel">
    <div class="pg-head">
      <div>
        <div class="pg-eyebrow">Dashboard</div>
        <div class="pg-title">运营概览</div>
        <div class="pg-sub">数据库实时统计</div>
      </div>
      <div class="pg-acts">
        <button class="tb-btn tb-btn-v" @click="loadStats">刷新数据</button>
      </div>
    </div>

    <div class="stat-cards">
      <div class="sc sc-v">
        <div class="sc-top"><div class="sc-ico si-v">👥</div></div>
        <div class="sc-n">{{ formatNum(stats.userCount) }}</div>
        <div class="sc-l">注册用户</div>
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

    <div class="chart-grid chart-grid-2">
      <div class="chart-card">
        <div class="chart-head">
          <div class="chart-title"><span class="chart-dot"></span>分类文章数量</div>
        </div>
        <div ref="categoryChart" style="height:220px"></div>
      </div>
      <div class="chart-card">
        <div class="chart-head">
          <div class="chart-title"><span class="chart-dot"></span>内容分类占比</div>
        </div>
        <div ref="pieChart" style="height:220px"></div>
      </div>
    </div>

    <div class="chart-grid" style="grid-template-columns:1fr 1fr;gap:18px;">
      <div class="chart-card">
        <div class="chart-head">
          <div class="chart-title"><span class="chart-dot"></span>待处理事项</div>
        </div>
        <div class="todo-list">
          <div class="todo-item todo-v">
            <span>待审核文章</span>
            <div class="todo-right">
              <span class="todo-num">{{ stats.pendingArticleCount || 0 }}</span>
            </div>
          </div>
          <div class="todo-item todo-g">
            <span>已屏蔽评论</span>
            <div class="todo-right">
              <span class="todo-num">{{ stats.blockedCommentCount || 0 }}</span>
            </div>
          </div>
          <div class="todo-item todo-j">
            <span>AI 问答记录</span>
            <div class="todo-right">
              <span class="todo-num">{{ stats.aiChatCount || 0 }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="chart-card">
        <div class="chart-head">
          <div class="chart-title"><span class="chart-dot"></span>文章状态分布</div>
        </div>
        <div ref="barChart" style="height:180px"></div>
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
  pendingArticleCount: 0,
  blockedCommentCount: 0,
  aiChatCount: 0,
  categoryStats: [],
  articleStatusStats: []
})

const categoryChart = ref(null)
const pieChart = ref(null)
const barChart = ref(null)
let categoryChartInstance = null
let pieChartInstance = null
let barChartInstance = null

const C = ['#C0392B', '#D4A017', '#2E7D6A', '#6B5744', '#E04B3A', '#1B5A40']

const formatNum = (value) => Number(value || 0).toLocaleString()

const initCharts = () => {
  const categories = stats.value.categoryStats || []
  const categoryNames = categories.map(item => item.name)
  const categoryValues = categories.map(item => Number(item.value || 0))

  if (categoryChart.value) {
    categoryChartInstance?.dispose()
    categoryChartInstance = echarts.init(categoryChart.value)
    categoryChartInstance.setOption({
      color: [C[0]],
      tooltip: { trigger: 'axis' },
      grid: { left: 10, right: 10, top: 10, bottom: 20, containLabel: true },
      xAxis: {
        type: 'category',
        data: categoryNames,
        axisLabel: { fontSize: 10, color: '#6B5744', rotate: categoryNames.length > 4 ? 20 : 0 }
      },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#EAD8BE' } } },
      series: [{ type: 'bar', data: categoryValues, barMaxWidth: 36, itemStyle: { borderRadius: [3, 3, 0, 0] } }]
    })
  }

  if (pieChart.value) {
    pieChartInstance?.dispose()
    pieChartInstance = echarts.init(pieChart.value)
    pieChartInstance.setOption({
      color: C,
      tooltip: { trigger: 'item' },
      legend: { bottom: 0, textStyle: { fontSize: 10, color: '#6B5744' } },
      series: [{
        type: 'pie',
        radius: ['38%', '65%'],
        center: ['50%', '42%'],
        data: categories.map(item => ({ name: item.name, value: Number(item.value || 0) })),
        label: { fontSize: 9 },
        itemStyle: { borderRadius: 3 }
      }]
    })
  }

  if (barChart.value) {
    barChartInstance?.dispose()
    barChartInstance = echarts.init(barChart.value)
    const statusStats = stats.value.articleStatusStats || []
    barChartInstance.setOption({
      color: [C[2], C[1], C[0]],
      tooltip: { trigger: 'axis' },
      grid: { left: 10, right: 10, top: 10, bottom: 20, containLabel: true },
      xAxis: {
        type: 'category',
        data: statusStats.map(item => item.name),
        axisLabel: { fontSize: 11, color: '#6B5744' }
      },
      yAxis: { type: 'value', splitLine: { lineStyle: { color: '#EAD8BE' } } },
      series: [{
        type: 'bar',
        data: statusStats.map(item => Number(item.value || 0)),
        barWidth: 36,
        itemStyle: { borderRadius: [3, 3, 0, 0] },
        label: { show: true, position: 'top', fontSize: 11 }
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
  pieChartInstance?.dispose()
  barChartInstance?.dispose()
})
</script>

<style scoped>
:root{--v:#C0392B;--v2:#E04B3A;--vd:rgba(192,57,43,.10);--vd2:rgba(192,57,43,.06);--g:#D4A017;--gd:#A67C00;--glow:rgba(212,160,23,.14);--ink:#1A1008;--jade:#2E7D6A;--jd:rgba(46,125,106,.10);--dim:#6B5744;--dim5:rgba(107,87,68,.5);--white:#fff;--bd2:#EAD8BE;--sh:0 2px 12px rgba(26,16,8,.07);--r:3px;--r2:6px;}
.pg-head{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:28px;}
.pg-eyebrow{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:5px;}
.pg-title{font-family:'ZCOOL XiaoWei',serif;font-size:24px;letter-spacing:5px;}
.pg-sub{font-size:12px;color:var(--dim5);margin-top:4px;}
.pg-acts{display:flex;gap:8px;}
.stat-cards{display:grid;grid-template-columns:repeat(4,1fr);gap:16px;margin-bottom:26px;}
.sc{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:18px 20px;position:relative;overflow:hidden;}
.sc::before{content:'';position:absolute;top:0;left:0;width:3px;height:100%;}
.sc-v::before{background:var(--v);}.sc-g::before{background:var(--g);}.sc-j::before{background:var(--jade);}.sc-d::before{background:var(--dim);}
.sc-top{display:flex;align-items:center;justify-content:space-between;margin-bottom:12px;}
.sc-ico{width:36px;height:36px;border-radius:var(--r);display:flex;align-items:center;justify-content:center;font-size:16px;}
.si-v{background:var(--vd);}.si-g{background:var(--glow);}.si-j{background:var(--jd);}.si-d{background:rgba(26,16,8,.07);}
.sc-n{font-family:'ZCOOL XiaoWei',serif;font-size:30px;letter-spacing:2px;line-height:1;margin-bottom:4px;}
.sc-l{font-size:11px;color:var(--dim5);}
.chart-grid{display:grid;gap:18px;margin-bottom:20px;}
.chart-grid-2{grid-template-columns:3fr 2fr;}
.chart-card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:20px 22px;box-shadow:var(--sh);}
.chart-head{display:flex;align-items:center;justify-content:space-between;margin-bottom:16px;}
.chart-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;display:flex;align-items:center;gap:7px;}
.chart-dot{width:7px;height:7px;background:var(--v);border-radius:50%;}
.todo-list{display:flex;flex-direction:column;gap:10px;}
.todo-item{display:flex;align-items:center;justify-content:space-between;padding:10px 14px;border-radius:var(--r);}
.todo-v{background:var(--vd2);border-left:3px solid var(--v);}
.todo-g{background:var(--glow);border-left:3px solid var(--g);}
.todo-j{background:var(--jd);border-left:3px solid var(--jade);}
.todo-right{display:flex;align-items:center;gap:8px;}
.todo-num{font-family:'ZCOOL XiaoWei',serif;font-size:20px;color:var(--v);}
.todo-g .todo-num{color:var(--gd);}
.todo-j .todo-num{color:var(--jade);}
.tb-btn{padding:6px 14px;border:none;border-radius:var(--r);font-size:11px;cursor:pointer;}
.tb-btn-v{background:var(--v);color:#fff;}
</style>
