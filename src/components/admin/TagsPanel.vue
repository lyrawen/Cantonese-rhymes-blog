<template>
  <div class="tags-panel">
    <div class="pg-head">
      <div>
        <div class="pg-eyebrow">Content</div>
        <div class="pg-title">标签管理</div>
        <div class="pg-sub">共 {{ tags.length }} 个标签</div>
      </div>
    </div>

    <div class="card">
      <div class="add-row">
        <input class="form-inp" v-model="newTag" placeholder="输入新标签名称" style="width:220px;margin:0;">
        <button class="tb-btn tb-btn-v" @click="addTag">添加</button>
      </div>
      <div class="tag-cloud">
        <div v-for="tag in tags" :key="tag.tagId" class="tag-item">
          {{ tag.tagName }} ({{ tag.articleCount ?? 0 }})<span class="tag-x" @click="deleteTag(tag.tagId)">×</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '../../api/adminApi'
import '../../styles/admin-variables.css'

const newTag = ref('')
const tags = ref([])

const loadData = async () => {
  tags.value = await adminApi.getTags()
}

const addTag = async () => {
  if (!newTag.value.trim()) return
  await adminApi.createTag(newTag.value.trim())
  newTag.value = ''
  await loadData()
}

const deleteTag = async (id) => {
  if (!confirm('确定删除该标签吗？')) return
  await adminApi.deleteTag(id)
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
:root{--v:#C0392B;--v2:#E04B3A;--dim:#6B5744;--dim5:rgba(107,87,68,.5);--dim2:rgba(107,87,68,.18);--rice:#F5EDD6;--white:#fff;--bd2:#EAD8BE;--r:3px;--r2:6px;--sh:0 2px 12px rgba(26,16,8,.07);}
.pg-head{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:28px;}
.pg-eyebrow{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:5px;}
.pg-title{font-family:'ZCOOL XiaoWei',serif;font-size:24px;letter-spacing:5px;}
.pg-sub{font-size:12px;color:var(--dim5);margin-top:4px;}
.card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:20px 22px;box-shadow:var(--sh);}
.add-row{display:flex;gap:10px;margin-bottom:16px;}
.form-inp{padding:9px 12px;border:1.5px solid var(--bd2);border-radius:var(--r);font-size:13px;}
.tag-cloud{display:flex;flex-wrap:wrap;gap:8px;}
.tag-item{display:inline-flex;align-items:center;gap:5px;padding:5px 13px;border-radius:20px;border:1px solid var(--bd2);font-size:12px;color:var(--dim);background:var(--white);}
.tag-x{cursor:pointer;font-size:13px;opacity:.5;}
.tag-x:hover{opacity:1;color:var(--v);}
.tb-btn{padding:6px 14px;border:none;border-radius:var(--r);font-size:11px;cursor:pointer;}
.tb-btn-v{background:var(--v);color:#fff;}
</style>
