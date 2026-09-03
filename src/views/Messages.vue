<template>
  <div class="messages-page">
    <div class="nav-gap"></div>
    <div class="wrap messages-wrap">
      <div class="messages-panel">
        <aside class="conv-list">
          <div class="conv-list-hdr">
            <h1>私信</h1>
            <span v-if="totalUnread > 0" class="unread-badge">{{ totalUnread > 99 ? '99+' : totalUnread }}</span>
          </div>
          <div v-if="loadingList" class="conv-loading">加载中...</div>
          <div v-else-if="conversations.length === 0" class="conv-empty">暂无对话，互关好友会显示在这里</div>
          <div
            v-for="item in conversations"
            :key="item.peerId"
            :class="['conv-item', { on: activePeerId === item.peerId, 'is-new': item.type === 'new_mutual' }]"
            @click="selectConversation(item)"
          >
            <div class="conv-avt" :class="'av' + (item.peerId % 5 + 1)">
              <img v-if="item.peerAvatar" :src="fixAvatar(item.peerAvatar)" :alt="item.peerNickname" />
              <span v-else>{{ item.peerNickname?.charAt(0) || '友' }}</span>
            </div>
            <div class="conv-body">
              <div class="conv-top">
                <span class="conv-name">{{ item.peerNickname }}</span>
                <span v-if="item.lastMsgTime" class="conv-time">{{ formatTime(item.lastMsgTime) }}</span>
              </div>
              <p v-if="item.type === 'new_mutual'" class="conv-hint">{{ item.hint }}</p>
              <p v-else class="conv-preview">{{ item.lastMsg || '暂无消息' }}</p>
            </div>
            <span v-if="item.unreadCount > 0" class="conv-unread">{{ item.unreadCount }}</span>
          </div>
        </aside>

        <section class="chat-main" v-if="activePeer">
          <header class="chat-hdr">
            <div class="chat-peer">
              <div class="conv-avt sm" :class="'av' + (activePeer.peerId % 5 + 1)">
                <img v-if="activePeer.peerAvatar" :src="fixAvatar(activePeer.peerAvatar)" :alt="activePeer.peerNickname" />
                <span v-else>{{ activePeer.peerNickname?.charAt(0) }}</span>
              </div>
              <span>{{ activePeer.peerNickname }}</span>
            </div>
            <button class="btn-del" @click="confirmDelete" title="从列表移除会话">删除对话</button>
          </header>

          <div v-if="!chatStatus.canSend" class="chat-readonly">
            {{ chatStatus.reason || '当前无法发送新消息，可查看历史记录' }}
          </div>

          <div class="chat-messages" ref="messagesEl">
            <div v-if="loadingMessages" class="conv-loading">加载消息...</div>
            <div
              v-for="msg in messages"
              :key="msg.messageId || msg._localId"
              :class="['chat-bubble-row', msg.mine ? 'mine' : '']"
            >
              <div class="chat-bubble">{{ msg.content }}</div>
              <div class="chat-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>

          <footer class="chat-input-bar">
            <input
              v-model="draft"
              type="text"
              :placeholder="chatStatus.canSend ? '输入消息...' : '已取消互关，无法发送'"
              :disabled="!chatStatus.canSend"
              maxlength="1000"
              @keyup.enter="sendMessage"
            />
            <button class="btn btn-v btn-sm" :disabled="!chatStatus.canSend || !draft.trim()" @click="sendMessage">发送</button>
          </footer>
        </section>

        <section v-else class="chat-placeholder">
          <p>选择左侧会话开始聊天</p>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  fetchConversations,
  fetchUnreadTotal,
  fetchMessages,
  fetchChatStatus,
  deleteConversation
} from '../api/chatApi'
import { useChatWebSocket } from '../composables/useChatWebSocket'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const conversations = ref([])
const messages = ref([])
const activePeerId = ref(null)
const chatStatus = ref({ canSend: false, mutualFollow: false })
const totalUnread = ref(0)
const loadingList = ref(false)
const loadingMessages = ref(false)
const draft = ref('')
const messagesEl = ref(null)

const activePeer = computed(() =>
  conversations.value.find((c) => c.peerId === activePeerId.value)
)

const fixAvatar = (path) => {
  if (!path) return ''
  return path.startsWith('/') ? path : `/${path}`
}

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  if (Number.isNaN(d.getTime())) return ''
  const now = new Date()
  const isToday = d.toDateString() === now.toDateString()
  if (isToday) {
    return d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return d.toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' })
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesEl.value) {
    messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  }
}

const refreshUnread = async () => {
  if (!authStore.token) return
  try {
    const data = await fetchUnreadTotal(authStore.token)
    totalUnread.value = data.unreadTotal || 0
  } catch (e) {
    console.error(e)
  }
}

const loadConversations = async () => {
  loadingList.value = true
  try {
    conversations.value = await fetchConversations(authStore.token)
    await refreshUnread()
    window.dispatchEvent(new Event('chat-unread-refresh'))
  } catch (e) {
    alert(e.message)
  } finally {
    loadingList.value = false
  }
}

const loadMessages = async (peerId) => {
  loadingMessages.value = true
  try {
    const data = await fetchMessages(authStore.token, peerId)
    messages.value = data.content || []
    chatStatus.value = data.status || { canSend: false }
    const conv = conversations.value.find((c) => c.peerId === peerId)
    if (conv) conv.unreadCount = 0
    await refreshUnread()
    window.dispatchEvent(new Event('chat-unread-refresh'))
    await scrollToBottom()
  } catch (e) {
    alert(e.message)
  } finally {
    loadingMessages.value = false
  }
}

const selectConversation = async (item) => {
  activePeerId.value = item.peerId
  router.replace({ path: '/messages', query: { peerId: item.peerId } })
  await loadMessages(item.peerId)
}

const handleWsPayload = async (payload) => {
  if (payload.type === 'error') {
    alert(payload.error || '发送失败')
    return
  }
  if (payload.type === 'sent' && payload.message) {
    const exists = messages.value.some((m) => m.messageId === payload.message.messageId)
    if (!exists) {
      messages.value.push({ ...payload.message, mine: true })
    }
    await loadConversations()
    await scrollToBottom()
    return
  }
  if (payload.type === 'receive' && payload.message) {
    if (activePeerId.value === payload.peerId) {
      messages.value.push({ ...payload.message, mine: false })
      await scrollToBottom()
    }
    await loadConversations()
    await refreshUnread()
    window.dispatchEvent(new Event('chat-unread-refresh'))
  }
}

const { connect, disconnect, sendText } = useChatWebSocket(
  () => authStore.token,
  { onMessage: handleWsPayload }
)

watch(
  () => authStore.token,
  (t) => {
    if (t) connect()
    else disconnect()
  },
  { immediate: true }
)

const sendMessage = async () => {
  const text = draft.value.trim()
  if (!text || !activePeerId.value || !chatStatus.value.canSend) return
  try {
    sendText(activePeerId.value, text)
    draft.value = ''
  } catch (e) {
    alert(e.message)
  }
}

const confirmDelete = async () => {
  if (!activePeerId.value) return
  if (!confirm('确定从列表中移除此对话？历史消息仍保留在服务器，互关后重新打开可再次查看。')) return
  try {
    await deleteConversation(authStore.token, activePeerId.value)
    activePeerId.value = null
    messages.value = []
    router.replace({ path: '/messages' })
    await loadConversations()
  } catch (e) {
    alert(e.message)
  }
}

onMounted(async () => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  await loadConversations()
  const peerId = route.query.peerId
  if (peerId) {
    const id = Number(peerId)
    let item = conversations.value.find((c) => c.peerId === id)
    if (!item) {
      try {
        const status = await fetchChatStatus(authStore.token, id)
        if (!status.mutualFollow) {
          alert(status.reason || '需要互相关注才能私信')
          return
        }
        let nickname = `用户 ${id}`
        let avatar = null
        const userRes = await fetch(`/api/users/${id}`)
        if (userRes.ok) {
          const u = await userRes.json()
          nickname = u.nickname || u.username || nickname
          avatar = u.avatar
        }
        item = {
          peerId: id,
          peerNickname: nickname,
          peerAvatar: avatar,
          type: 'new_mutual',
          hint: '你们已经相互关注啦，一起来交流粤文化吧',
          unreadCount: 0,
          canSend: true
        }
        conversations.value.unshift(item)
      } catch (e) {
        alert(e.message)
        return
      }
    }
    if (item) await selectConversation(item)
  }
})

onUnmounted(() => {
  disconnect()
  window.removeEventListener('chat-unread-refresh', refreshUnread)
})

window.addEventListener('chat-unread-refresh', refreshUnread)
</script>

<style scoped>
.messages-page { min-height: 100vh; background: var(--cream, #fbf6ec); }
.nav-gap { height: 62px; }
.messages-wrap { max-width: 1100px; margin: 0 auto; padding: 24px 20px 48px; }
.messages-panel {
  display: grid;
  grid-template-columns: 320px 1fr;
  min-height: calc(100vh - 140px);
  background: #fff;
  border: 1px solid #ead8be;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,.06);
}
.conv-list { border-right: 1px solid #ead8be; display: flex; flex-direction: column; }
.conv-list-hdr {
  padding: 16px 18px;
  border-bottom: 1px solid #ead8be;
  display: flex;
  align-items: center;
  gap: 8px;
}
.conv-list-hdr h1 { font-size: 18px; font-family: 'ZCOOL XiaoWei', serif; margin: 0; }
.unread-badge {
  background: #c0392b;
  color: #fff;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
}
.conv-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #f5efe3;
  position: relative;
}
.conv-item:hover, .conv-item.on { background: rgba(192,57,43,.08); }
.conv-item.is-new .conv-hint { color: #1b4a40; font-style: italic; }
.conv-avt {
  width: 42px; height: 42px; border-radius: 50%; overflow: hidden;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 14px; flex-shrink: 0;
}
.conv-avt.sm { width: 36px; height: 36px; }
.conv-avt img { width: 100%; height: 100%; object-fit: cover; }
.conv-body { flex: 1; min-width: 0; }
.conv-top { display: flex; justify-content: space-between; gap: 8px; }
.conv-name { font-size: 14px; font-weight: 600; }
.conv-time { font-size: 11px; color: #999; flex-shrink: 0; }
.conv-preview, .conv-hint {
  font-size: 12px; color: #666; margin: 4px 0 0;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.conv-unread {
  position: absolute; right: 12px; bottom: 12px;
  background: #c0392b; color: #fff; font-size: 10px;
  min-width: 18px; height: 18px; border-radius: 9px;
  display: flex; align-items: center; justify-content: center;
}
.conv-loading, .conv-empty { padding: 24px; text-align: center; color: #999; font-size: 13px; }
.chat-main { display: flex; flex-direction: column; min-height: 480px; }
.chat-hdr {
  padding: 14px 18px; border-bottom: 1px solid #ead8be;
  display: flex; align-items: center; justify-content: space-between;
}
.chat-peer { display: flex; align-items: center; gap: 10px; font-weight: 600; }
.btn-del {
  border: none; background: none; color: #999; font-size: 12px; cursor: pointer;
}
.btn-del:hover { color: #c0392b; }
.chat-readonly {
  background: #fff8e6; color: #8b6914; font-size: 12px;
  padding: 8px 16px; text-align: center;
}
.chat-messages {
  flex: 1; overflow-y: auto; padding: 16px 18px;
  display: flex; flex-direction: column; gap: 12px;
  background: #faf7f2;
}
.chat-bubble-row { display: flex; flex-direction: column; align-items: flex-start; max-width: 75%; }
.chat-bubble-row.mine { align-self: flex-end; align-items: flex-end; }
.chat-bubble {
  background: #fff; border: 1px solid #ead8be;
  padding: 10px 14px; border-radius: 12px 12px 12px 2px;
  font-size: 14px; line-height: 1.5; word-break: break-word;
}
.chat-bubble-row.mine .chat-bubble {
  background: #c0392b; color: #fff; border-color: #c0392b;
  border-radius: 12px 12px 2px 12px;
}
.chat-time { font-size: 10px; color: #aaa; margin-top: 4px; }
.chat-input-bar {
  display: flex; gap: 10px; padding: 12px 16px;
  border-top: 1px solid #ead8be; background: #fff;
}
.chat-input-bar input {
  flex: 1; border: 1px solid #ead8be; border-radius: 20px;
  padding: 10px 16px; font-size: 14px; outline: none;
}
.chat-placeholder {
  display: flex; align-items: center; justify-content: center;
  color: #999; font-size: 14px;
}
.av1 { background: linear-gradient(135deg, #8B1A14, #D4A017); }
.av2 { background: linear-gradient(135deg, #1B4A40, #4AA08A); }
.av3 { background: linear-gradient(135deg, #4A0A1A, #8B3A5A); }
.av4 { background: linear-gradient(135deg, #1A1A4A, #2C5FBD); }
.av5 { background: linear-gradient(135deg, #3D1F0A, #8B4F1A); }
@media (max-width: 768px) {
  .messages-panel { grid-template-columns: 1fr; }
  .conv-list { max-height: 200px; overflow-y: auto; }
}
</style>
