<template>
  <div class="comments-panel">
    <div class="pg-head">
      <div>
        <div class="pg-eyebrow">Interaction</div>
        <div class="pg-title">评论管理</div>
        <div class="pg-sub">共 {{ comments.length }} 条评论 · {{ blockedCount }} 条已屏蔽</div>
      </div>
    </div>

    <div class="tbl-card">
      <div class="tbl-top">
        <span class="tbl-title">全部评论</span>
        <input class="tbl-search" v-model="searchKeyword" placeholder="搜索评论内容">
        <select class="tbl-sel" v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="1">正常</option>
          <option value="0">已屏蔽</option>
        </select>
      </div>
      <div class="tbl-head" style="grid-template-columns:3fr 1fr 1fr 80px 100px">
        <div class="th">评论内容</div>
        <div class="th">用户</div>
        <div class="th">所属文章</div>
        <div class="th">状态</div>
        <div class="th">操作</div>
      </div>
      <div v-if="!filteredComments.length" class="empty-tip">暂无评论数据</div>
      <div v-for="(comment, index) in filteredComments" :key="comment.commentId" class="tr" :style="{ gridTemplateColumns: '3fr 1fr 1fr 80px 100px', background: comment.status === 0 ? 'var(--vd2)' : index % 2 === 1 ? 'var(--cream)' : '' }">
        <div class="td" style="font-size:12.5px;" :style="{ color: comment.status === 0 ? 'var(--v)' : '' }">
          {{ comment.content }}
        </div>
        <div class="td">
          <div :class="'avt-sm a' + ((index % 5) + 1)">{{ (comment.userName || '匿').charAt(0) }}</div>
          {{ comment.userName || '匿名用户' }}
        </div>
        <div class="td" style="font-size:11px;color:var(--dim)">{{ comment.articleTitle || '—' }}</div>
        <div class="td"><span :class="'badge ' + (comment.status === 1 ? 'b-normal' : 'b-del')">{{ comment.status === 1 ? '正常' : '已屏蔽' }}</span></div>
        <div class="td">
          <div class="acts">
            <button v-if="comment.status === 0" class="act act-ok" @click="restoreComment(comment.commentId)">恢复</button>
            <button v-else class="act act-del" @click="blockComment(comment.commentId)">屏蔽</button>
            <button class="act act-del" @click="removeComment(comment.commentId)">删除</button>
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

const comments = ref([])
const searchKeyword = ref('')
const statusFilter = ref('')

const blockedCount = computed(() => comments.value.filter(c => c.status === 0).length)

const filteredComments = computed(() => comments.value.filter(comment => {
  const matchKeyword = !searchKeyword.value || comment.content?.includes(searchKeyword.value)
  const matchStatus = statusFilter.value === '' || String(comment.status) === statusFilter.value
  return matchKeyword && matchStatus
}))

const loadData = async () => {
  comments.value = await adminApi.getComments()
}

const removeComment = async (id) => {
  if (!confirm('确定删除这条评论吗？')) return
  await adminApi.deleteComment(id)
  await loadData()
}

const blockComment = async (id) => {
  await adminApi.updateCommentStatus(id, 0)
  await loadData()
}

const restoreComment = async (id) => {
  await adminApi.updateCommentStatus(id, 1)
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
:root {
  --v:#C0392B;--v2:#E04B3A;--vd:rgba(192,57,43,.10);--vd2:rgba(192,57,43,.06);
  --g:#D4A017;--gd:#A67C00;--glow:rgba(212,160,23,.14);
  --ink:#1A1008;--jade:#2E7D6A;--jd:rgba(46,125,106,.10);
  --dim:#6B5744;--dim5:rgba(107,87,68,.5);
  --rice:#F5EDD6;--cream:#FBF6EC;--white:#fff;
  --bd2:#EAD8BE;--r:3px;--r2:6px;
}
.pg-head{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:28px;}
.pg-eyebrow{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:5px;}
.pg-title{font-family:'ZCOOL XiaoWei',serif;font-size:24px;letter-spacing:5px;}
.pg-sub{font-size:12px;color:var(--dim5);margin-top:4px;}
.tbl-card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);overflow:hidden;margin-bottom:20px;}
.tbl-top{display:flex;align-items:center;padding:14px 18px;border-bottom:1px solid var(--bd2);gap:10px;flex-wrap:wrap;}
.tbl-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;}
.tbl-search{padding:6px 12px;border:1px solid var(--bd2);border-radius:20px;font-size:12px;background:var(--rice);width:160px;}
.tbl-sel{padding:6px 11px;border:1px solid var(--bd2);border-radius:var(--r);font-size:12px;}
.tbl-head{background:var(--ink);display:grid;}
.th{padding:10px 16px;font-size:10px;letter-spacing:2px;color:var(--g);text-transform:uppercase;}
.tr{display:grid;border-bottom:1px solid var(--bd2);}
.td{padding:12px 16px;font-size:12.5px;color:var(--dim);display:flex;align-items:center;gap:7px;}
.badge{font-size:10px;padding:3px 9px;border-radius:20px;}
.b-normal{background:var(--jd);color:var(--jade);}
.b-del{background:var(--vd);color:var(--v);}
.acts{display:flex;gap:5px;}
.act{font-size:11px;padding:3px 9px;border-radius:var(--r);border:1px solid var(--bd2);background:var(--white);cursor:pointer;color:var(--dim);}
.act-ok:hover{border-color:var(--jade);color:var(--jade);}
.act-del:hover{background:var(--vd);border-color:var(--v);color:var(--v);}
.avt-sm{width:22px;height:22px;border-radius:50%;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:10px;color:#fff;}
.a1{background:linear-gradient(135deg,#8B1A14,#D4A017);}
.a2{background:linear-gradient(135deg,#1B4A40,#4AA08A);}
.a3{background:linear-gradient(135deg,#1A1A4A,#2C5FBD);}
.a4{background:linear-gradient(135deg,#4A0A1A,#8B3A5A);}
.a5{background:linear-gradient(135deg,#3D1F0A,#8B4F1A);}
.empty-tip{padding:40px;text-align:center;color:var(--dim5);font-size:13px;}
</style>
