<template>
  <div class="page active ai-page">
    <div class="nav-gap"></div>
    <div class="ai-hero">
      <div class="ai-hero-bg"></div>
      <div class="ai-hero-char">智</div>
      <div class="wrap ai-hero-pos">
        <h1>粤<span>AI</span> · 文化助手</h1>
        <p>探索岭南文化的智能问答系统</p>
      </div>
    </div>
    
    <div class="wrap">
      <div class="ai-layout">
        <div class="ai-sidebar">
          <div class="ai-sidebar-head">
            <span class="ai-sidebar-head-dot"></span>
            历史提问
          </div>
          <div class="ai-history-list">
            <div 
              class="ai-history-item" 
              v-for="(item, index) in chatHistory" 
              :key="item.id || index" 
              :class="{ on: item.id === currentChatId }"
              @click="loadChatHistory(item)"
            >
              <div class="ai-history-question">{{ item.title }}</div>
              <div class="ai-history-date">{{ formatDate(item.timestamp) }}</div>
            </div>
            <div class="ai-history-empty" v-if="chatHistory.length === 0">
              暂无历史对话
            </div>
          </div>
          <div class="ai-new-chat-btn" @click="startNewChat">
            <span v-if="isViewingHistory">←</span>
            <span v-if="isViewingHistory">返回当前对话</span>
            <span v-if="!isViewingHistory">+</span>
            <span v-if="!isViewingHistory">新建对话</span>
          </div>
        </div>
        <div class="ai-chat-main">
          <div class="ai-chat-header">
            <div class="ai-chat-avatar"><span>粤</span></div>
            <div class="ai-chat-info">
              <h3>粤AI · 岭南文化助手</h3>
              <p>基于粤韵志知识库</p>
            </div>
            <div class="ai-chat-badge">在线</div>
          </div>
          <div class="ai-messages" ref="messagesContainer">
            <div class="ai-message" v-if="messages.length === 0">
              <div class="ai-message-avatar bot"><span>粤</span></div>
              <div class="ai-bubble bot">
                你好！我是粤AI，专注于解答各类岭南文化问题。你可以问我关于粤剧戏曲、粤菜饮食、粤语方言等任何问题。
              </div>
            </div>
            <div v-for="(msg, index) in messages" :key="index" 
                 :class="['ai-message', msg.role === 'user' ? 'user' : '']">
              <div class="ai-message-avatar bot" v-if="msg.role === 'ai'">
                <span>粤</span>
              </div>
              <div :class="['ai-bubble', msg.role === 'ai' ? 'bot' : 'user']">
                <div v-html="formatMessage(msg.content)"></div>
                <div v-if="msg.relatedArticles && msg.relatedArticles.length > 0" class="ai-related-articles">
                  <div class="ai-related-title">📚 相关阅读：</div>
                  <div v-for="(article, idx) in msg.relatedArticles" :key="idx" class="ai-related-item">
                    {{ article }}
                  </div>
                </div>
                <div class="ai-bubble-time">{{ formatTime(msg.timestamp) }}</div>
              </div>
              <div class="ai-message-avatar user" v-if="msg.role === 'user'"></div>
            </div>
            <div class="ai-message" v-if="isThinking">
              <div class="ai-message-avatar bot"><span>粤</span></div>
              <div class="ai-bubble bot">
                <div class="message-loading">
                  <span></span><span></span><span></span>
                  <div class="loading-text">小粤正在思考中...</div>
                </div>
              </div>
            </div>

            <!-- 上翻离开底部后，右下角浮现的小箭头（Telegram 式：不占布局、不遮挡正文） -->
            <button class="ai-scroll-down" v-if="showScrollToBottom" @click="scrollToBottom" title="回到底部">↓</button>
          </div>
          <div class="ai-guest-mask" v-if="!isLoggedIn">
            请先 <a @click="$router.push('/login')">登录</a> 后使用 AI 文化问答功能
          </div>
          <div class="ai-input-area" v-else>
            <div class="ai-input-wrap">
              <textarea 
                class="ai-input" 
                v-model="inputMessage"
                placeholder="提问任何岭南文化问题……"
                @keydown.enter.exact.prevent="sendMessage"
                rows="1"
                ref="inputRef"
              ></textarea>
              <button class="ai-send" @click="sendMessage" :disabled="!inputMessage.trim() || isLoading">↑</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const messagesContainer = ref(null)
const inputRef = ref(null)

const isLoggedIn = computed(() => authStore.isLoggedIn)
const user = computed(() => authStore.user)

const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const chatHistory = ref([])
const showScrollToBottom = ref(false)
const currentChatId = ref(null)
const isViewingHistory = ref(false)
const stickToBottom = ref(true) // 是否自动跟随到最新内容（用户上翻会变 false）
const isThinking = ref(false)    // 是否处于"等待 AI 首字回复"阶段

const API_BASE_URL = '/api'

const aiResponses = {
  '岭南建筑有什么特点？': `岭南建筑是中国传统建筑的重要流派之一，具有以下鲜明特点：

**1. 骑楼结构**
岭南地区多雨炎热，骑楼建筑既可遮阳避雨，又方便商业经营，是岭南建筑的典型代表。

**2. 镬耳墙**
又称"锅耳墙"，形似古代官帽两边的耳，寓意"独占鳌头"，是岭南传统民居的标志性元素。

**3. 雕刻装饰**
岭南建筑注重装饰，木雕、石雕、砖雕工艺精湛，常见于祠堂、庙宇和富商宅第。

**4. 通风设计**
考虑到南方炎热潮湿的气候，岭南建筑多采用高挑空、大窗户、天井等设计，利于通风散热。

**5. 园林融合**
岭南园林将建筑与自然景观有机结合，如广州的余荫山房、佛山的清晖园等。`,

  '粤剧有哪些经典剧目？': `粤剧是岭南文化的瑰宝，拥有众多经典剧目：

**传统经典剧目：**
- 《帝女花》- 讲述明末长平公主的故事
- 《紫钗记》- 汤显祖名著改编
- 《牡丹亭惊梦》- 经典爱情故事
- 《双仙拜月亭》- 元代杂剧改编
- 《柳毅传书》- 神话爱情故事

**现代经典剧目：**
- 《搜书院》- 反封建爱情故事
- 《关汉卿》- 描写元代剧作家
- 《山乡风云》- 革命题材
- 《梦断香销四十年》- 陆游与唐婉的故事

**粤剧特色：**
粤剧融合了唱、做、念、打，以粤语演唱，音乐优美，表演细腻，是国家级非物质文化遗产。`,

  '粤菜的代表菜有哪些？': `粤菜作为中国八大菜系之一，以清淡鲜美著称，代表菜品丰富：

**经典名菜：**
- **白切鸡** - 皮爽肉滑，原汁原味
- **烧鹅/烧鸭** - 皮脆肉嫩，色泽金黄
- **清蒸石斑鱼** - 保持鱼肉鲜甜
- **蜜汁叉烧** - 甜香可口
- **干炒牛河** - 镬气十足

**点心类：**
- **虾饺** - 晶莹剔透，鲜虾饱满
- **烧卖** - 猪肉虾仁馅，顶部点缀蟹黄
- **肠粉** - 滑嫩爽口
- **叉烧包** - 松软香甜

**汤品类：**
- **老火靓汤** - 慢火熬制，滋补养生
- **冬瓜盅** - 清热解暑

粤菜讲究"清、鲜、嫩、滑、爽、香"，追求食材本味。`,

  '粤语有什么特色？': `粤语（广东话）是岭南文化的重要载体，具有独特魅力：

**语言特点：**
- 保留了大量古汉语特征
- 有九声六调，音韵丰富
- 词汇生动形象，表达细腻

**特色俚语：**
- "唔该" - 谢谢/麻烦你
- "犀利" - 厉害
- "好犀利" - 非常厉害
- "搞搞震" - 捣乱
- "扮嘢" - 装模作样
- "hea" - 懒散、无所事事

**文化价值：**
- 粤语承载着岭南地区的历史文化
- 粤剧、粤语流行歌曲传播广泛
- 是粤港澳地区的重要文化纽带

**使用范围：**
全球约有7000万人使用粤语，是影响力最大的汉语方言之一。`
}

const defaultResponses = [
  '这是一个很好的问题！岭南文化博大精深，让我为您详细解答...',
  '感谢您的提问！关于这个问题，我可以从几个方面来介绍...',
  '您问到了岭南文化的精髓所在，让我来为您一一讲解...'
]

const sendMessage = async () => {
  if (!inputMessage.value.trim() || isLoading.value) return

  const userMessage = inputMessage.value.trim()
  const timestamp = new Date()
  messages.value.push({ role: 'user', content: userMessage, timestamp })
  inputMessage.value = ''
  
  // 新建对话，清空当前对话ID
  currentChatId.value = null
  
  scrollToBottom()
  
  isLoading.value = true
  isThinking.value = true
  let aiAnswer = ''

  try {
    const response = await fetch('/api/ai/yue-culture/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        text: userMessage,
        category: ''
      })
    })

    if (response.ok) {
      aiAnswer = await response.text()
      // 拿到回复、开始流式输出后隐藏“思考中”占位
      isThinking.value = false
      // 实现打字机效果
      await typeWriterEffect(aiAnswer)
      
      // 保存AI聊天记录到数据库
      await saveChatHistory(userMessage, aiAnswer)
    } else {
      throw new Error('API调用失败')
    }
  } catch (error) {
    console.error('AI问答失败:', error)
    messages.value.push({ 
      role: 'ai', 
      content: '抱歉，AI服务暂时遇到问题。建议您直接浏览粤韵志网站的相关文章，或稍后再试。', 
      timestamp: new Date() 
    })
  } finally {
    isLoading.value = false
    isThinking.value = false
    // 回答结束时也不强制拉回，若用户上翻阅读则保留其位置
    await followIfAtBottom()
    isViewingHistory.value = false
  }
}

// 保存AI聊天记录到数据库
const saveChatHistory = async (question, answer) => {
  try {
    const response = await fetch(`${API_BASE_URL}/ai-chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authStore.token}`
      },
      body: JSON.stringify({
        userId: authStore.user.userId,
        question: question,
        answer: answer
      })
    })
    
    if (response.ok) {
      const savedChat = await response.json()
      // 更新历史列表
      chatHistory.value.unshift({
        id: savedChat.id,
        question: question,
        answer: answer,
        title: question.slice(0, 20) + (question.length > 20 ? '...' : ''),
        timestamp: new Date(savedChat.createTime)
      })
      // 限制历史记录数量
      if (chatHistory.value.length > 10) {
        chatHistory.value.pop()
      }
    }
  } catch (error) {
    console.error('保存聊天记录失败:', error)
  }
}

// 获取AI聊天历史
const fetchChatHistory = async () => {
  if (!isLoggedIn.value) return
  
  try {
    const response = await fetch(`${API_BASE_URL}/ai-chat/history/${authStore.user.userId}`, {
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      }
    })
    
    if (response.ok) {
      const historyData = await response.json()
      chatHistory.value = historyData.map(item => ({
        id: item.id,
        question: item.question,
        answer: item.answer,
        title: item.question.slice(0, 20) + (item.question.length > 20 ? '...' : ''),
        timestamp: new Date(item.createTime)
      }))
    }
  } catch (error) {
    console.error('获取聊天历史失败:', error)
  }
}

// 点击历史记录，加载对话内容
const loadChatHistory = async (historyItem) => {
  try {
    currentChatId.value = historyItem.id
    isViewingHistory.value = true
    
    // 清空当前消息
    messages.value = []
    
    // 添加用户问题
    messages.value.push({
      role: 'user',
      content: historyItem.question,
      timestamp: new Date(historyItem.timestamp)
    })
    
    // 添加AI回答（一次性显示，不使用打字机效果）
    messages.value.push({
      role: 'ai',
      content: historyItem.answer,
      timestamp: new Date(historyItem.timestamp),
      relatedArticles: []
    })
    
    // 滚动到顶部
    await scrollToTop()
  } catch (error) {
    console.error('加载历史对话失败:', error)
  }
}

// 新建对话
const startNewChat = () => {
  currentChatId.value = null
  messages.value = []
  inputMessage.value = ''
  if (inputRef.value) {
    inputRef.value.focus()
  }
}

// 打字机效果（按块输出 + 智能滚动，避免高频重绘导致的卡顿闪烁）
const typeWriterEffect = async (text) => {
  const timestamp = new Date()
  const messageIndex = messages.value.length

  // 先添加一个空消息
  messages.value.push({
    role: 'ai',
    content: '',
    timestamp,
    relatedArticles: []
  })

  const step = 4      // 每轮追加的字符数
  const typingSpeed = 18 // 每轮间隔（毫秒）
  let currentText = ''

  for (let i = 0; i < text.length; i += step) {
    currentText += text.slice(i, i + step)
    messages.value[messageIndex].content = currentText
    // 等待本轮渲染完成后再判断是否跟随滚动（避免闪烁）
    await followIfAtBottom()
    await sleep(typingSpeed)
  }

  // 处理相关文章推荐（这里简化处理，实际可以从API返回中获取）
  const relatedArticles = []
  if (text.includes('粤韵志')) {
    relatedArticles.push('粤韵志网站有更多相关文章')
  }
  messages.value[messageIndex].relatedArticles = relatedArticles
  // 相关内容追加后再次判断是否需滚动到底
  await followIfAtBottom()
}

const sleep = (ms) => new Promise(resolve => setTimeout(resolve, ms))

// AI 输出时的滚动策略：
// 用户处于跟随状态（stickToBottom=true）→ 始终贴底跟随，即使内容瞬间长高也不打扰；
// 用户上翻阅读 → 保留其位置并亮出“回到底部”按钮，等用户滚回底部即自动恢复跟随
const followIfAtBottom = async () => {
  await nextTick()
  const el = messagesContainer.value
  if (!el) return
  if (stickToBottom.value) {
    el.scrollTop = el.scrollHeight
    showScrollToBottom.value = false
  } else {
    showScrollToBottom.value = true
  }
}

const sendSuggestion = (text) => {
  if (!isLoggedIn.value) {
    return
  }
  inputMessage.value = text
  sendMessage()
}

const formatMessage = (content) => {
  return content
    .replace(/###/g, '') // 去掉###井号
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>')
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const today = new Date()
  const yesterday = new Date(today)
  yesterday.setDate(yesterday.getDate() - 1)
  
  if (d.toDateString() === today.toDateString()) {
    return '今天 ' + d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0')
  } else if (d.toDateString() === yesterday.toDateString()) {
    return '昨天 ' + d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0')
  } else {
    return d.getFullYear() + '-' + (d.getMonth() + 1).toString().padStart(2, '0') + '-' + d.getDate().toString().padStart(2, '0')
  }
}

const formatTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.getHours().toString().padStart(2, '0') + ':' + d.getMinutes().toString().padStart(2, '0')
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    stickToBottom.value = true
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    showScrollToBottom.value = false
  }
}

const scrollToTop = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = 0
  }
}

// 监听滚动事件：由用户的真实滚动位置维护"是否跟随"状态，并控制按钮显隐
const handleScroll = () => {
  if (!messagesContainer.value) return

  const { scrollTop, scrollHeight, clientHeight } = messagesContainer.value
  const atBottom = scrollHeight - scrollTop - clientHeight < 60

  stickToBottom.value = atBottom
  showScrollToBottom.value = !atBottom && !isViewingHistory.value
}

onMounted(() => {
  if (inputRef.value && isLoggedIn.value) {
    inputRef.value.focus()
  }
  
  // 添加滚动监听
  if (messagesContainer.value) {
    messagesContainer.value.addEventListener('scroll', handleScroll)
  }
  
  // 加载AI聊天历史
  fetchChatHistory()
})
</script>

<style scoped>
:root {
  --cream: #FBF6EC;
  --ink: #1A1008;
  --bd: #D4B896;
  --bd2: #EAD8BE;
  --v: #C0392B;
  --v2: #E04B3A;
  --r: 3px;
  --r2: 6px;
  --nav-h: 62px;
}

.nav-gap { height: 62px; }

.wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 36px;
}

.ai-page {
  min-height: calc(100vh - var(--nav-h));
  background: var(--cream);
}

.ai-hero {
  background: var(--ink);
  padding: 40px 0;
  position: relative;
  overflow: hidden;
}

.ai-hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1A0804 0%, #3A100A 40%, #6B1A14 70%, #1A0804 100%);
}

.ai-hero-char {
  position: absolute;
  right: 20px;
  bottom: 0;
  font-family: 'Ma Shan Zheng', serif;
  font-size: 180px;
  color: rgba(212, 160, 23, 0.08);
  line-height: 1;
  user-select: none;
}

.ai-hero-pos {
  position: relative;
  z-index: 1;
}

.ai-hero h1 {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 42px;
  color: #fff;
  letter-spacing: 6px;
  margin-bottom: 8px;
}

.ai-hero h1 em {
  color: var(--g);
  font-style: normal;
}

.ai-hero p {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
  letter-spacing: 2px;
}

.ai-layout {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 24px;
  padding: 40px 0 60px;
}

.ai-sidebar {
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  overflow: hidden;
  height: fit-content;
}

.ai-sidebar-head {
  padding: 18px 20px;
  border-bottom: 1px solid #EAD8BE;
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 16px;
  letter-spacing: 2px;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #1A1008;
}

.ai-sidebar-head-dot {
  width: 6px;
  height: 6px;
  background: #C0392B;
  border-radius: 50%;
}

.ai-history-list {
  max-height: 500px;
  overflow-y: auto;
}

.ai-history-item {
  padding: 16px 20px;
  border-bottom: 1px solid #EAD8BE;
  cursor: pointer;
  transition: background 0.2s;
}

.ai-history-item:hover {
  background: #FBF6EC;
}

.ai-history-item.on {
  background: rgba(192, 57, 43, 0.10);
  border-left: 3px solid #C0392B;
}

.ai-history-question {
  font-size: 13px;
  color: #1A1008;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ai-history-date {
  font-size: 11px;
  color: rgba(107, 87, 68, 0.5);
}

.ai-history-empty {
  padding: 40px 20px;
  text-align: center;
  font-size: 13px;
  color: var(--dim5);
}

.ai-new-chat-btn {
  margin: 16px;
  padding: 12px 20px;
  background: #C0392B;
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  transition: all 0.2s;
  border: none;
  width: calc(100% - 32px);
  justify-content: center;
}

.ai-new-chat-btn:hover {
  background: #E04B3A;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(192, 57, 43, 0.3);
}

.ai-new-chat-btn span:first-child {
  font-size: 18px;
  font-weight: bold;
}

.ai-history-actions {
  margin: 0 16px 16px;
}

.ai-history-action-btn {
  padding: 10px 16px;
  background: #C0392B;
  color: #fff;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  transition: all 0.2s;
  border: none;
  width: 100%;
  justify-content: center;
}

.ai-history-action-btn:hover {
  background: #E04B3A;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(192, 57, 43, 0.3);
}

.ai-history-action-btn span:first-child {
  font-size: 16px;
  font-weight: bold;
}

.ai-chat-main {
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.ai-chat-header {
  padding: 18px 24px;
  border-bottom: 1px solid #EAD8BE;
  display: flex;
  align-items: center;
  gap: 12px;
}

.ai-chat-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--r);
  background: #1A1008;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-chat-avatar span {
  font-family: 'Ma Shan Zheng', serif;
  font-size: 20px;
  color: #D4A017;
}

.ai-chat-info h3 {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 16px;
  letter-spacing: 2px;
  margin: 0;
  color: var(--ink);
}

.ai-chat-info p {
  font-size: 11px;
  color: #B5ABA2;
  margin: 2px 0 0 0;
}

.ai-chat-badge {
  margin-left: auto;
  background: rgba(46, 125, 106, 0.10);
  color: #2E7D6A;
  font-size: 10px;
  letter-spacing: 2px;
  padding: 4px 10px;
  border-radius: 12px;
  border: 1px solid rgba(46, 125, 106, 0.20);
}

.ai-messages {
  padding: 24px;
  min-height: 400px;
  max-height: 500px;
  overflow-y: auto;
  flex: 1;
  border-bottom: 1px solid #EAD8BE;
  position: relative;
}

/* “回到最新”小箭头（Telegram 式）：仅在用户上翻离开底部时浮现；绝对定位不占布局，
   且消息气泡最大宽 70%，右下角区域基本留空，不会遮挡正文 */
.ai-scroll-down {
  position: absolute;
  right: 16px;
  bottom: 16px;
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 50%;
  background: rgba(26, 16, 8, 0.75);
  color: #fff;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.25);
  transition: background 0.2s, transform 0.15s;
  z-index: 10;
}

.ai-scroll-down:hover {
  background: #C0392B;
  transform: translateY(-1px);
}

.ai-message {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.ai-message.user {
  /* 仿微信：自己的消息整体靠右，头像在最右侧、气泡在头像左边 */
  justify-content: flex-end;
}

.ai-message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
}

.ai-message-avatar.bot {
  background: #1A1008;
  border-radius: var(--r);
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-message-avatar.bot span {
  font-family: 'Ma Shan Zheng', serif;
  font-size: 18px;
  color: #D4A017;
}

.ai-message-avatar.user {
  background: var(--v);
  border-radius: 50%;
}

.ai-bubble {
  max-width: 70%;
  padding: 14px 18px;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.8;
}

.ai-bubble.bot {
  background: #FBF6EC;
  border: 1px solid #EAD8BE;
  border-radius: 0 6px 6px 6px;
  color: var(--dim);
}

.ai-bubble.user {
  background: #C0392B;
  color: #fff;
  border-radius: 6px 0 6px 6px;
}

.ai-bubble-time {
  font-size: 10px;
  color: var(--dim5);
  margin-top: 4px;
  text-align: right;
}

.ai-bubble.user .ai-bubble-time {
  color: rgba(255, 255, 255, 0.6);
}

.ai-related-articles {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(212, 184, 150, 0.3);
}

.ai-related-title {
  font-size: 12px;
  font-weight: bold;
  color: var(--ink);
  margin-bottom: 6px;
}

.ai-related-item {
  font-size: 12px;
  color: var(--v);
  margin-bottom: 4px;
  padding-left: 12px;
  position: relative;
}

.ai-related-item::before {
  content: '•';
  position: absolute;
  left: 0;
  color: var(--v);
}

.ai-input-area {
  padding: 24px;
  border-top: 1px solid #EAD8BE;
  background: #FBF6EC;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 10;
}

.ai-input-wrap {
  display: flex;
  gap: 10px;
  align-items: flex-end;
  background: #fff;
  border: 2px solid #D4B896;
  border-radius: 6px;
  padding: 10px 14px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.ai-input-wrap:focus-within {
  border-color: #C0392B;
}

.ai-input {
  flex: 1;
  background: none;
  border: none;
  outline: none;
  font-size: 14px;
  color: #1A1008;
  resize: none;
  min-height: 24px;
  line-height: 1.5;
  border-radius: 6px;
}

.ai-input::placeholder {
  color: rgba(107, 87, 68, 0.5);
}

.ai-send {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  background: #C0392B;
  border: none;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.ai-send:hover:not(:disabled) {
  background: #E04B3A;
}

.ai-send:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.ai-guest-mask {
  padding: 12px 20px;
  background: var(--vd);
  border: 1px solid rgba(192, 57, 43, 0.15);
  border-radius: var(--r);
  text-align: center;
  font-size: 13px;
  color: var(--dim);
}

.ai-guest-mask a {
  color: var(--v);
  cursor: pointer;
  text-decoration: underline;
}

.message-loading {
  display: flex;
  gap: 4px;
}

.message-loading span {
  width: 8px;
  height: 8px;
  background: var(--v);
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;
}

.message-loading span:nth-child(1) {
  animation-delay: -0.32s;
}

.message-loading span:nth-child(2) {
  animation-delay: -0.16s;
}

.loading-text {
  margin-left: 12px;
  font-size: 14px;
  color: var(--dim);
  align-self: center;
}

.message-loading {
  display: flex;
  align-items: center;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

@media (max-width: 900px) {
  .ai-layout {
    grid-template-columns: 1fr;
  }
  
  .ai-sidebar {
    display: none;
  }
}
</style>