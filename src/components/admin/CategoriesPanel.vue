<template>
  <div class="categories-panel">
    <div class="pg-head">
      <div>
        <div class="pg-eyebrow">Content</div>
        <div class="pg-title">分类管理</div>
        <div class="pg-sub">共 {{ categories.length }} 个文化分类</div>
      </div>
      <div class="pg-acts"><button class="tb-btn tb-btn-v" @click="showAdd = !showAdd">+ 新增分类</button></div>
    </div>

    <div v-if="showAdd" class="card add-form">
      <input class="form-inp" v-model="newName" placeholder="分类名称" style="width:220px;margin:0;">
      <input class="form-inp" v-model.number="newSort" type="number" placeholder="排序" style="width:100px;margin:0;">
      <button class="tb-btn tb-btn-v" @click="addCategory">确认添加</button>
    </div>

    <div class="tbl-card">
      <div class="tbl-top"><span class="tbl-title">文化分类列表</span></div>
      <div v-if="!categories.length" class="empty-tip">暂无分类</div>
      <div v-for="(category, index) in categories" :key="category.categoryId" class="cat-row">
        <div :class="'cat-ico ig' + ((index % 5) + 1)">{{ index + 1 }}</div>
        <div class="cat-name">{{ category.categoryName }}</div>
        <div class="cat-cnt">{{ category.articleCount ?? 0 }} 篇文章</div>
        <div class="acts">
          <button class="act act-del" @click="removeCategory(category.categoryId)">删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '../../api/adminApi'
import '../../styles/admin-variables.css'

const categories = ref([])
const showAdd = ref(false)
const newName = ref('')
const newSort = ref(null)

const loadData = async () => {
  categories.value = await adminApi.getCategories()
}

const addCategory = async () => {
  if (!newName.value.trim()) return
  await adminApi.createCategory(newName.value.trim(), newSort.value || null)
  newName.value = ''
  newSort.value = null
  showAdd.value = false
  await loadData()
}

const removeCategory = async (id) => {
  if (!confirm('确定删除该分类吗？')) return
  try {
    await adminApi.deleteCategory(id)
    await loadData()
  } catch (e) {
    alert(e.message)
  }
}

onMounted(loadData)
</script>

<style scoped>
:root{--v:#C0392B;--v2:#E04B3A;--g:#D4A017;--dim:#6B5744;--dim5:rgba(107,87,68,.5);--cream:#FBF6EC;--white:#fff;--bd2:#EAD8BE;--r:3px;--r2:6px;}
.pg-head{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:28px;}
.pg-eyebrow{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:5px;}
.pg-title{font-family:'ZCOOL XiaoWei',serif;font-size:24px;letter-spacing:5px;}
.pg-sub{font-size:12px;color:var(--dim5);margin-top:4px;}
.pg-acts{display:flex;gap:8px;}
.card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);padding:16px 18px;margin-bottom:16px;}
.add-form{display:flex;gap:10px;align-items:center;flex-wrap:wrap;}
.form-inp{padding:9px 12px;border:1.5px solid var(--bd2);border-radius:var(--r);font-size:13px;}
.tbl-card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);overflow:hidden;}
.tbl-top{padding:14px 18px;border-bottom:1px solid var(--bd2);}
.tbl-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;}
.cat-row{display:flex;align-items:center;gap:12px;padding:12px 16px;border-bottom:1px solid var(--bd2);}
.cat-row:hover{background:var(--cream);}
.cat-ico{width:34px;height:34px;border-radius:var(--r);display:flex;align-items:center;justify-content:center;font-size:13px;color:#fff;flex-shrink:0;}
.cat-name{font-family:'ZCOOL XiaoWei',serif;font-size:13px;letter-spacing:2px;flex:1;}
.cat-cnt{font-size:11px;color:var(--dim5);margin-right:10px;}
.acts{display:flex;gap:5px;}
.act{font-size:11px;padding:3px 9px;border-radius:var(--r);border:1px solid var(--bd2);background:var(--white);cursor:pointer;color:var(--dim);}
.act-del:hover{background:rgba(192,57,43,.1);border-color:var(--v);color:var(--v);}
.tb-btn{padding:6px 14px;border:none;border-radius:var(--r);font-size:11px;cursor:pointer;}
.tb-btn-v{background:var(--v);color:#fff;}
.empty-tip{padding:40px;text-align:center;color:var(--dim5);}
.ig1{background:linear-gradient(135deg,#6B0F0F,#C0392B,#D4A017);}
.ig2{background:linear-gradient(135deg,#0E2A1A,#1B5A40);}
.ig3{background:linear-gradient(135deg,#2A1004,#7B3010);}
.ig4{background:linear-gradient(135deg,#0E1530,#1A3070);}
.ig5{background:linear-gradient(135deg,#1A0A20,#501A6A);}
</style>
