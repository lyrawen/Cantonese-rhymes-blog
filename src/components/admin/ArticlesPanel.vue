<template>
  <div class="articles-panel">
    <div class="pg-head">
      <div>
        <div class="pg-eyebrow">Content</div>
        <div class="pg-title">文章管理</div>
        <div class="pg-sub">共 {{ allArticles.length }} 篇 · {{ pendingArticles.length }} 篇待审核</div>
      </div>
    </div>

    <div class="card" v-if="pendingArticles.length">
      <div class="card-title">
        待审核文章 <span class="count-badge">{{ pendingArticles.length }}</span>
      </div>
      <div id="auditList">
        <div v-for="(article, index) in pendingArticles" :key="article.articleId" class="audit-item" :class="{ 'with-border': index < pendingArticles.length - 1 }">
          <div class="cover-box">
            <img v-if="article.coverImage" :src="article.coverImage" class="cover-img" />
            <div v-else :class="'ig' + ((index % 5) + 1)" class="cover-placeholder"></div>
          </div>
          <div class="audit-content">
            <div class="audit-title">{{ article.title }}</div>
            <div class="audit-meta">作者 {{ article.authorName || '未知' }} · {{ article.categoryName || '未分类' }} · {{ formatDate(article.createTime) }}</div>
            <div class="audit-summary">{{ article.summary || stripHtml(article.content).slice(0, 120) }}...</div>
            <div class="acts">
              <button class="act act-j" @click="auditPass(article.articleId)">通过发布</button>
              <button class="act act-del" @click="auditReject(article.articleId)">退回修改</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="tbl-card">
      <div class="tbl-top">
        <span class="tbl-title">全部文章</span>
        <input class="tbl-search" v-model="searchKeyword" placeholder="搜索标题">
        <select class="tbl-sel" v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="1">已发布</option>
          <option value="0">待审核</option>
          <option value="2">已拒绝</option>
        </select>
      </div>
      <div class="tbl-head grid-row">
        <div class="th">标题</div>
        <div class="th">作者/分类</div>
        <div class="th">状态</div>
        <div class="th">阅读</div>
        <div class="th">点赞</div>
        <div class="th">发布时间</div>
        <div class="th">操作</div>
      </div>
      <div v-for="(article, index) in filteredArticles" :key="article.articleId" class="tr grid-row" :class="{ alt: index % 2 === 1 }">
        <div class="td"><span class="td-name">{{ article.title }}</span></div>
        <div class="td">
          <div>
            <div class="author-name">{{ article.authorName || '未知' }}</div>
            <div class="td-sub">{{ article.categoryName || '未分类' }}</div>
          </div>
        </div>
        <div class="td"><span :class="'badge ' + statusClass(article.status)">{{ statusText(article.status) }}</span></div>
        <div class="td">{{ article.viewCount ?? '—' }}</div>
        <div class="td">{{ article.likeCount ?? '—' }}</div>
        <div class="td date-cell">{{ formatDate(article.publishTime || article.createTime) }}</div>
        <div class="td">
          <div class="acts">
            <button v-if="article.status === 0" class="act act-j" @click="auditPass(article.articleId)">通过</button>
            <button class="act act-del" @click="removeArticle(article.articleId)">删除</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminApi } from '../../api/adminApi'
import '../../styles/admin-variables.css'

const pendingArticles = ref([])
const allArticles = ref([])
const searchKeyword = ref('')
const statusFilter = ref('')

const filteredArticles = computed(() => allArticles.value.filter(article => {
  const matchKeyword = !searchKeyword.value || article.title?.includes(searchKeyword.value)
  const matchStatus = statusFilter.value === '' || String(article.status) === statusFilter.value
  return matchKeyword && matchStatus
}))

const loadData = async () => {
  const [pending, all] = await Promise.all([
    adminApi.getPendingArticles(),
    adminApi.getAllArticles()
  ])
  pendingArticles.value = pending
  allArticles.value = all
}

const auditPass = async (id) => {
  await adminApi.approveArticle(id)
  await loadData()
}

const auditReject = async (id) => {
  await adminApi.rejectArticle(id)
  await loadData()
}

const removeArticle = async (id) => {
  if (!confirm('确定删除这篇文章吗？')) return
  await adminApi.deleteArticle(id)
  await loadData()
}

const statusText = (status) => ({ 0: '待审核', 1: '已发布', 2: '已拒绝' }[status] || '未知')
const statusClass = (status) => ({ 0: 'b-pend', 1: 'b-pub', 2: 'b-del' }[status] || 'b-pend')
const formatDate = (value) => {
  if (!value) return '—'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return `${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}
const stripHtml = (html = '') => html.replace(/<[^>]+>/g, '')

onMounted(loadData)
</script>

<style scoped>
:root {
  --v:#C0392B;--v2:#E04B3A;--vd:rgba(192,57,43,.10);--vd2:rgba(192,57,43,.06);
  --g:#D4A017;--gd:#A67C00;--glow:rgba(212,160,23,.14);
  --ink:#1A1008;--jade:#2E7D6A;--jd:rgba(46,125,106,.10);
  --dim:#6B5744;--dim5:rgba(107,87,68,.5);
  --cream:#FBF6EC;--white:#fff;--bd2:#EAD8BE;--rice:#F5EDD6;
  --r:3px;--r2:6px;--sh:0 2px 12px rgba(26,16,8,.07);
}
.pg-head{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:28px;}
.pg-eyebrow{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:5px;}
.pg-title{font-family:'ZCOOL XiaoWei',serif;font-size:24px;letter-spacing:5px;}
.pg-sub{font-size:12px;color:var(--dim5);margin-top:4px;}
.card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:20px 22px;box-shadow:var(--sh);margin-bottom:16px;}
.card-title{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:14px;display:flex;align-items:center;gap:8px;}
.count-badge{background:var(--v);color:#fff;font-size:9px;padding:2px 7px;border-radius:20px;}
.audit-item{display:flex;gap:12px;align-items:flex-start;padding:14px 0;}
.audit-item.with-border{border-bottom:1px solid var(--bd2);}
.cover-box{width:72px;height:60px;border-radius:var(--r);overflow:hidden;flex-shrink:0;}
.cover-img,.cover-placeholder{width:100%;height:100%;object-fit:cover;}
.audit-content{flex:1;}
.audit-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;margin-bottom:5px;}
.audit-meta{font-size:11px;color:var(--dim5);margin-bottom:8px;}
.audit-summary{font-size:12.5px;color:var(--dim);line-height:1.7;margin-bottom:10px;}
.tbl-card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);overflow:hidden;margin-bottom:20px;}
.tbl-top{display:flex;align-items:center;padding:14px 18px;border-bottom:1px solid var(--bd2);gap:10px;flex-wrap:wrap;}
.tbl-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;}
.tbl-search{padding:6px 12px;border:1px solid var(--bd2);border-radius:20px;font-size:12px;background:var(--rice);width:160px;}
.tbl-sel{padding:6px 11px;border:1px solid var(--bd2);border-radius:var(--r);font-size:12px;}
.grid-row{display:grid;grid-template-columns:2.5fr 1fr 80px 70px 70px 80px 110px;}
.tbl-head{background:var(--ink);}
.th{padding:10px 16px;font-size:10px;letter-spacing:2px;color:var(--g);text-transform:uppercase;}
.tr{border-bottom:1px solid var(--bd2);}
.tr.alt{background:var(--cream);}
.td{padding:12px 16px;font-size:12.5px;color:var(--dim);display:flex;align-items:center;}
.td-name,.author-name{font-size:13px;color:var(--ink);font-weight:500;}
.td-sub{font-size:10px;color:var(--dim5);}
.date-cell{font-size:11px;}
.badge{font-size:10px;padding:3px 9px;border-radius:20px;}
.b-pub{background:var(--jd);color:var(--jade);}
.b-pend{background:var(--glow);color:var(--gd);}
.b-del{background:var(--vd);color:var(--v);}
.acts{display:flex;gap:5px;}
.act{font-size:11px;padding:3px 9px;border-radius:var(--r);border:1px solid var(--bd2);background:var(--white);cursor:pointer;color:var(--dim);}
.act-j{background:var(--jade);color:#fff;border-color:var(--jade);}
.act-del:hover{background:var(--vd);border-color:var(--v);color:var(--v);}
.ig1{background:linear-gradient(135deg,#6B0F0F,#C0392B,#D4A017);}
.ig2{background:linear-gradient(135deg,#0E2A1A,#1B5A40);}
.ig3{background:linear-gradient(135deg,#2A1004,#7B3010);}
.ig4{background:linear-gradient(135deg,#0E1530,#1A3070);}
.ig5{background:linear-gradient(135deg,#1A0A20,#501A6A);}
</style>
