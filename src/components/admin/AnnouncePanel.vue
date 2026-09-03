<template>
  <div class="announce-panel">
    <div class="pg-head">
      <div>
        <div class="pg-eyebrow">System</div>
        <div class="pg-title">公告管理</div>
        <div class="pg-sub">共 {{ announcements.length }} 条公告 · {{ activeCount }} 条发布中</div>
      </div>
      <div class="pg-acts"><button class="tb-btn tb-btn-v" @click="showAdd = !showAdd">+ 发布公告</button></div>
    </div>

    <div v-if="showAdd" class="card add-form">
      <input class="form-inp" v-model="newTitle" placeholder="公告标题">
      <textarea class="form-inp" v-model="newContent" placeholder="公告内容" rows="4"></textarea>
      <button class="tb-btn tb-btn-v" @click="publish">确认发布</button>
    </div>

    <div v-for="item in announcements" :key="item.announcementId" class="ann-card" :style="{ opacity: item.status !== 1 ? 0.55 : 1 }">
      <div class="ann-head">
        <span :class="'badge ' + (item.status === 1 ? 'b-pub' : 'b-del')">{{ item.status === 1 ? '发布中' : '已下线' }}</span>
        <span class="ann-title">{{ item.title }}</span>
        <span class="ann-time">{{ formatDate(item.createTime) }}</span>
      </div>
      <div class="ann-body">{{ item.content }}</div>
      <div class="ann-foot">
        <button v-if="item.status === 1" class="act act-del" @click="toggleStatus(item.announcementId, 0)">下线</button>
        <button v-else class="act act-ok" @click="toggleStatus(item.announcementId, 1)">重新发布</button>
        <button class="act act-del" @click="remove(item.announcementId)">删除</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { adminApi } from '../../api/adminApi'
import '../../styles/admin-variables.css'

const announcements = ref([])
const showAdd = ref(false)
const newTitle = ref('')
const newContent = ref('')

const activeCount = computed(() => announcements.value.filter(a => a.status === 1).length)

const formatDate = (value) => {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString('zh-CN')
}

const loadData = async () => {
  announcements.value = await adminApi.getAnnouncements()
}

const publish = async () => {
  if (!newTitle.value.trim() || !newContent.value.trim()) return
  await adminApi.createAnnouncement(newTitle.value.trim(), newContent.value.trim())
  newTitle.value = ''
  newContent.value = ''
  showAdd.value = false
  await loadData()
}

const toggleStatus = async (id, status) => {
  await adminApi.updateAnnouncementStatus(id, status)
  await loadData()
}

const remove = async (id) => {
  if (!confirm('确定删除该公告吗？')) return
  await adminApi.deleteAnnouncement(id)
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
:root{--v:#C0392B;--jade:#2E7D6A;--jd:rgba(46,125,106,.10);--dim:#6B5744;--dim5:rgba(107,87,68,.5);--white:#fff;--bd2:#EAD8BE;--r:3px;--r2:6px;}
.pg-head{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:28px;}
.pg-eyebrow{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:5px;}
.pg-title{font-family:'ZCOOL XiaoWei',serif;font-size:24px;letter-spacing:5px;}
.pg-sub{font-size:12px;color:var(--dim5);margin-top:4px;}
.pg-acts{display:flex;gap:8px;}
.card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:16px 18px;margin-bottom:16px;}
.add-form{display:flex;flex-direction:column;gap:10px;}
.form-inp{padding:9px 12px;border:1.5px solid var(--bd2);border-radius:var(--r);font-size:13px;width:100%;}
.ann-card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:16px 20px;margin-bottom:14px;}
.ann-head{display:flex;align-items:center;gap:10px;margin-bottom:10px;}
.ann-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;flex:1;}
.ann-time{font-size:11px;color:var(--dim5);margin-left:auto;}
.ann-body{font-size:13px;color:var(--dim);line-height:1.8;margin-bottom:12px;}
.ann-foot{display:flex;gap:7px;}
.badge{font-size:10px;padding:3px 9px;border-radius:20px;}
.b-pub{background:var(--jd);color:var(--jade);}
.b-del{background:rgba(192,57,43,.1);color:var(--v);}
.act{font-size:11px;padding:3px 9px;border-radius:var(--r);border:1px solid var(--bd2);background:var(--white);cursor:pointer;color:var(--dim);}
.act-ok:hover{border-color:var(--jade);color:var(--jade);}
.act-del:hover{background:rgba(192,57,43,.1);border-color:var(--v);color:var(--v);}
.tb-btn{padding:6px 14px;border:none;border-radius:var(--r);font-size:11px;cursor:pointer;}
.tb-btn-v{background:var(--v);color:#fff;}
</style>
