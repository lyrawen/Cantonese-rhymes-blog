<template>
  <div class="users-panel">
    <div class="pg-head">
      <div>
        <div class="pg-eyebrow">Users</div>
        <div class="pg-title">用户管理</div>
        <div class="pg-sub">共 {{ users.length }} 位用户</div>
      </div>
    </div>

    <div class="tbl-card">
      <div class="tbl-top">
        <span class="tbl-title">用户列表</span>
        <input class="tbl-search" v-model="searchKeyword" placeholder="搜索用户名/邮箱">
        <select class="tbl-sel" v-model="roleFilter">
          <option value="">全部角色</option>
          <option value="admin">管理员</option>
          <option value="user">普通用户</option>
        </select>
        <select class="tbl-sel" v-model="statusFilter">
          <option value="">全部状态</option>
          <option value="1">正常</option>
          <option value="0">已禁用</option>
        </select>
      </div>
      <div class="tbl-head" style="grid-template-columns:2fr 1.5fr 1fr 1fr 1fr 1.2fr">
        <div class="th">用户信息</div>
        <div class="th">邮箱</div>
        <div class="th">角色</div>
        <div class="th">状态</div>
        <div class="th">注册时间</div>
        <div class="th">操作</div>
      </div>
      <div v-for="(user, index) in filteredUsers" :key="user.userId" class="tr" :style="{ gridTemplateColumns: '2fr 1.5fr 1fr 1fr 1fr 1.2fr', background: index % 2 === 1 ? 'var(--cream)' : '' }">
        <div class="td">
          <div :class="'avt-sm a' + ((index % 5) + 1)">{{ displayName(user).charAt(0) }}</div>
          <div>
            <div class="td-name">{{ displayName(user) }}</div>
            <div class="td-sub">{{ user.articleCount ?? 0 }} 篇文章</div>
          </div>
        </div>
        <div class="td" style="font-size:11px">{{ user.email || '—' }}</div>
        <div class="td"><span :class="'badge ' + (user.role === 'admin' ? 'b-admin' : 'b-user')">{{ user.role === 'admin' ? '管理员' : '用户' }}</span></div>
        <div class="td"><span :class="'badge ' + (user.status === 1 ? 'b-normal' : 'b-ban')">{{ user.status === 1 ? '正常' : '已禁用' }}</span></div>
        <div class="td" style="font-size:11px">{{ formatDate(user.createTime) }}</div>
        <div class="td">
          <div class="acts">
            <button v-if="user.role !== 'admin' && user.status === 1" class="act act-del" @click="toggleStatus(user.userId, 0)">禁用</button>
            <button v-if="user.role !== 'admin' && user.status !== 1" class="act act-ok" @click="toggleStatus(user.userId, 1)">解禁</button>
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

const users = ref([])
const searchKeyword = ref('')
const roleFilter = ref('')
const statusFilter = ref('')

const filteredUsers = computed(() => users.value.filter(user => {
  const name = displayName(user)
  const matchKeyword = !searchKeyword.value ||
    name.includes(searchKeyword.value) ||
    user.email?.includes(searchKeyword.value) ||
    user.username?.includes(searchKeyword.value)
  const matchRole = !roleFilter.value || user.role === roleFilter.value
  const matchStatus = statusFilter.value === '' || String(user.status) === statusFilter.value
  return matchKeyword && matchRole && matchStatus
}))

const displayName = (user) => user.nickname || user.username || '未知用户'
const formatDate = (value) => {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleDateString('zh-CN')
}

const loadData = async () => {
  users.value = await adminApi.getUsers()
}

const toggleStatus = async (id, status) => {
  await adminApi.updateUserStatus(id, status)
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
:root{--v:#C0392B;--g:#D4A017;--gd:#A67C00;--glow:rgba(212,160,23,.14);--ink:#1A1008;--ink1:rgba(26,16,8,.07);--jade:#2E7D6A;--jd:rgba(46,125,106,.10);--dim:#6B5744;--dim5:rgba(107,87,68,.5);--cream:#FBF6EC;--white:#fff;--bd2:#EAD8BE;--r:3px;--r2:6px;}
.pg-head{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:28px;}
.pg-eyebrow{font-size:10px;letter-spacing:3px;color:var(--v);text-transform:uppercase;margin-bottom:5px;}
.pg-title{font-family:'ZCOOL XiaoWei',serif;font-size:24px;letter-spacing:5px;}
.pg-sub{font-size:12px;color:var(--dim5);margin-top:4px;}
.tbl-card{background:var(--white);border:1px solid var(--bd2);border-radius:var(--r2);overflow:hidden;}
.tbl-top{display:flex;align-items:center;padding:14px 18px;border-bottom:1px solid var(--bd2);gap:10px;flex-wrap:wrap;}
.tbl-title{font-family:'ZCOOL XiaoWei',serif;font-size:14px;letter-spacing:2px;}
.tbl-search{padding:6px 12px;border:1px solid var(--bd2);border-radius:20px;font-size:12px;background:var(--cream);width:160px;}
.tbl-sel{padding:6px 11px;border:1px solid var(--bd2);border-radius:var(--r);font-size:12px;}
.tbl-head{background:var(--ink);display:grid;}
.th{padding:10px 16px;font-size:10px;letter-spacing:2px;color:var(--g);text-transform:uppercase;}
.tr{display:grid;border-bottom:1px solid var(--bd2);}
.td{padding:12px 16px;font-size:12.5px;color:var(--dim);display:flex;align-items:center;gap:7px;}
.td-name{font-size:13px;color:var(--ink);font-weight:500;}
.td-sub{font-size:10px;color:var(--dim5);}
.badge{font-size:10px;padding:3px 9px;border-radius:20px;}
.b-normal{background:var(--jd);color:var(--jade);}
.b-ban{background:rgba(192,57,43,.1);color:var(--v);}
.b-admin{background:var(--ink1);color:var(--ink);}
.b-user{background:var(--glow);color:var(--gd);}
.acts{display:flex;gap:5px;}
.act{font-size:11px;padding:3px 9px;border-radius:var(--r);border:1px solid var(--bd2);background:var(--white);cursor:pointer;color:var(--dim);}
.act-ok:hover{border-color:var(--jade);color:var(--jade);}
.act-del:hover{background:rgba(192,57,43,.1);border-color:var(--v);color:var(--v);}
.avt-sm{width:28px;height:28px;border-radius:50%;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:11px;color:#fff;}
.a1{background:linear-gradient(135deg,#8B1A14,#D4A017);}
.a2{background:linear-gradient(135deg,#1B4A40,#4AA08A);}
.a3{background:linear-gradient(135deg,#1A1A4A,#2C5FBD);}
.a4{background:linear-gradient(135deg,#4A0A1A,#8B3A5A);}
.a5{background:linear-gradient(135deg,#3D1F0A,#8B4F1A);}
</style>
