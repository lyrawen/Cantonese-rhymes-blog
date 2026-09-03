<template>
  <div class="ai-panel">
    <div class="pg-head">
      <div>
        <div class="pg-eyebrow">AI</div>
        <div class="pg-title">AI问答管理</div>
        <div class="pg-sub">共 {{ records.length }} 条记录</div>
      </div>
    </div>

    <div class="tbl-card" style="margin-bottom:20px;">
      <div class="tbl-top">
        <span class="tbl-title">提问记录</span>
        <input class="tbl-search" v-model="searchKeyword" placeholder="搜索提问内容">
      </div>
    </div>

    <div v-if="!filteredRecords.length" class="empty-tip">暂无 AI 问答记录</div>
    <div v-for="(item, index) in filteredRecords" :key="item.chatId" class="ai-item">
      <div class="ai-item-head">
        <div :class="'avt-sm a' + ((index % 5) + 1)">{{ (item.userName || '匿').charAt(0) }}</div>
        <span class="meta">{{ item.userName || '匿名用户' }} · {{ formatDate(item.createTime) }}</span>
      </div>
      <div class="ai-q">{{ item.question }}</div>
      <div class="ai-a">{{ item.answer }}</div>
      <div class="acts">
        <button class="act act-del" @click="remove(item.chatId)">删除记录</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminApi } from '../../api/adminApi'
import '../../styles/admin-variables.css'

const records = ref([])
const searchKeyword = ref('')

const filteredRecords = computed(() => records.value.filter(item =>
  !searchKeyword.value || item.question?.includes(searchKeyword.value) || item.answer?.includes(searchKeyword.value)
))

const formatDate = (value) => {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString('zh-CN')
}

const loadData = async () => {
  records.value = await adminApi.getAiChat()
}

const remove = async (id) => {
  if (!confirm('确定删除这条 AI 记录吗？')) return
  await adminApi.deleteAiChat(id)
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
:root{--v:#C0392B;--vd2:rgba(192,57,43,.06);--dim:#6B5744;--dim5:rgba(107,87,68,.5);--rice:#F5EDD6;--white:#fff;--ink:#1A1008;--bd2:#EAD8BE;--r:3px;--r2:6px;}
.pg-head{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:28px;}
.pg-eyebrow{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:5px;}
.pg-title{font-family:'ZCOOL XiaoWei',serif;font-size:24px;letter-spacing:5px;}
.pg-sub{font-size:12px;color:var(--dim5);margin-top:4px;}
.tbl-card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);overflow:hidden;}
.tbl-top{display:flex;align-items:center;padding:14px 18px;gap:10px;}
.tbl-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;}
.tbl-search{padding:6px 12px;border:1px solid var(--bd2);border-radius:20px;font-size:12px;background:var(--rice);width:180px;}
.ai-item{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:14px 18px;margin-bottom:12px;}
.ai-item-head{display:flex;align-items:center;gap:10px;margin-bottom:10px;}
.meta{font-size:11px;color:var(--dim5);}
.ai-q{font-size:13.5px;color:var(--ink);line-height:1.7;margin-bottom:8px;padding-left:12px;border-left:3px solid var(--v);}
.ai-a{font-size:12.5px;color:var(--dim);line-height:1.8;padding:10px 13px;background:var(--rice);border-radius:var(--r);margin-bottom:10px;}
.acts{display:flex;gap:5px;}
.act{font-size:11px;padding:3px 9px;border-radius:var(--r);border:1px solid var(--bd2);background:var(--white);cursor:pointer;color:var(--dim);}
.act-del:hover{background:var(--vd2);border-color:var(--v);color:var(--v);}
.avt-sm{width:28px;height:28px;border-radius:50%;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:11px;color:#fff;}
.a1{background:linear-gradient(135deg,#8B1A14,#D4A017);}
.a2{background:linear-gradient(135deg,#1B4A40,#4AA08A);}
.a3{background:linear-gradient(135deg,#1A1A4A,#2C5FBD);}
.a4{background:linear-gradient(135deg,#4A0A1A,#8B3A5A);}
.a5{background:linear-gradient(135deg,#3D1F0A,#8B4F1A);}
.empty-tip{padding:40px;text-align:center;color:var(--dim5);}
</style>
