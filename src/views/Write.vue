<template>
  <div class="page active">
    <div class="nav-gap"></div>
    
    <!-- 顶部工具栏 -->
    <div class="write-toolbar">
      <div class="wrap toolbar-inner">
        <div class="toolbar-left">
          <button class="btn btn-ghost btn-sm" @click="$router.push('/')">
            ← 返回首页
          </button>
          <div class="save-status" :class="{ saved: saveStatus === 'saved', saving: saveStatus === 'saving' }">
            <div class="save-dot"></div>
            <span>{{ saveText }}</span>
          </div>
        </div>
        <div class="toolbar-right">
          <span class="word-count">{{ wordCount }} 字</span>
          <button class="btn btn-ghost btn-sm" @click="preview">预览</button>
          <button class="btn btn-v btn-sm" @click="saveDraft">存草稿</button>
          <button class="btn btn-v btn-sm" @click="submitReview">提交审核</button>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="wrap write-container">
      <!-- 左侧输入区域 -->
      <div class="write-main">
        <!-- 标题输入 -->
        <div class="write-section">
          <textarea 
            class="write-title" 
            v-model="article.title"
            placeholder="输入标题"
            maxlength="200"
            rows="1"
            @input="onEdit"
          ></textarea>
        </div>

        <!-- 摘要输入 -->
        <div class="write-section">
          <textarea 
            class="write-summary" 
            v-model="article.summary"
            placeholder="添加摘要，帮助读者快速了解内容（选填）..."
            maxlength="500"
            rows="2"
            @input="onEdit"
          ></textarea>
        </div>

        <!-- 富文本工具栏 -->
        <div class="write-section">
          <div class="write-tools">
            <select class="tool-select" @change="formatBlock">
              <option value="" disabled selected>段落样式</option>
              <option value="h2">标题一</option>
              <option value="h3">标题二</option>
              <option value="p">正文</option>
              <option value="blockquote">引用</option>
            </select>
            <div class="tool-divider"></div>
            <button class="tool-btn" @click="execCommand('bold')" title="加粗">
              <strong>B</strong>
            </button>
            <button class="tool-btn" @click="execCommand('italic')" title="斜体">
              <em>I</em>
            </button>
            <button class="tool-btn" @click="execCommand('underline')" title="下划线">
              <u>U</u>
            </button>
            <button class="tool-btn" @click="execCommand('strikeThrough')" title="删除线">
              <s>S</s>
            </button>
            <div class="tool-divider"></div>
            <button class="tool-btn" @click="execCommand('insertUnorderedList')" title="无序列表">
              ≡
            </button>
            <button class="tool-btn" @click="execCommand('insertOrderedList')" title="有序列表">
              1.
            </button>
            <button class="tool-btn" @click="execCommand('formatBlock', 'blockquote')" title="引用">
              ❝
            </button>
            <div class="tool-divider"></div>
            <button class="tool-btn" @click="insertLink" title="插入链接">
              <img src="/icons/分享.png" alt="分享" class="tool-icon">
            </button>
            <button class="tool-btn" @click="insertImage" title="插入图片">
              <img src="/icons/上传图片.png" alt="上传图片" class="tool-icon">
            </button>
            <div class="tool-divider"></div>
            <button class="tool-btn" @click="execCommand('undo')" title="撤销">
              ↺
            </button>
            <button class="tool-btn" @click="execCommand('redo')" title="重做">
              ↻
            </button>
          </div>
        </div>

        <!-- 内容编辑区 -->
        <div class="write-section">
          <div 
            class="write-content" 
            contenteditable="true" 
            @input="onContentEdit"
            data-placeholder="开始写作……

在这里，你可以分享对岭南文化的所思所感。
无论是粤剧的一声水袖，早茶桌上的人情冷暖，
还是那些正在消逝的老手艺与旧时光——

用文字留住它们。"
          ></div>
        </div>

        <!-- 底部信息 -->
        <div class="write-footer">
          <span>{{ wordCount }} 字</span>
          <span>· 预计阅读 {{ readTime }} 分钟</span>
        </div>
      </div>

      <!-- 右侧设置区域 -->
      <div class="write-sidebar">
        <!-- 封面上传 -->
        <div class="sidebar-card">
          <div class="sidebar-header">
            <h3 class="sidebar-title">封面图</h3>
          </div>
          <div class="sidebar-body">
            <div class="cover-upload" :class="{ filled: article.coverImage }" @click="triggerCoverUpload">
              <div class="cover-placeholder">
                <span class="cover-icon">📷</span>
                <span class="cover-text">点击上传封面</span>
                <span class="cover-hint">16:9，JPG/PNG</span>
              </div>
              <img v-if="article.coverImage" :src="article.coverImage" alt="封面图片">
              <div class="cover-actions" v-if="article.coverImage">
                <button @click.stop="triggerCoverUpload">更换</button>
                <button @click.stop="removeCover">删除</button>
              </div>
            </div>
            <input type="file" ref="coverFile" accept="image/*" style="display:none" @change="handleCoverUpload">
          </div>
        </div>

        <!-- 分类选择 -->
        <div class="sidebar-card">
          <div class="sidebar-header">
            <h3 class="sidebar-title">文章分类</h3>
          </div>
          <div class="sidebar-body">
            <select class="form-select" v-model="article.categoryId" @change="onEdit">
              <option value="">请选择分类（必填）</option>
              <option v-for="category in categories" :key="category.categoryId" :value="category.categoryId">
                {{ category.categoryName }}
              </option>
            </select>
          </div>
        </div>

        <!-- 标签输入 -->
        <div class="sidebar-card">
          <div class="sidebar-header">
            <h3 class="sidebar-title">标签 <span class="tag-hint">（最多8个）</span></h3>
          </div>
          <div class="sidebar-body">
            <div class="tags-input">
              <input 
                type="text" 
                class="tag-input" 
                v-model="tagInput"
                placeholder="输入标签，按回车添加"
                @keydown="handleTagInput"
              >
              <button class="btn btn-ghost btn-sm" @click="addTag">添加</button>
            </div>
            <div class="tags-list">
              <span v-for="tag in tags" :key="tag" class="tag-item">
                {{ tag }}
                <span class="tag-remove" @click="removeTag(tag)">×</span>
              </span>
            </div>
            <div class="tags-suggestions">
              <span v-for="sug in suggestedTags" :key="sug" class="tag-suggestion" @click="addSuggestedTag(sug)">
                {{ sug }}
              </span>
            </div>
          </div>
        </div>



        <!-- 状态说明和操作按钮 -->
        <div class="sidebar-card">
          <div class="sidebar-header">
            <h3 class="sidebar-title">发布设置</h3>
          </div>
          <div class="sidebar-body">
            <div class="status-info">
              <div class="status-dot"></div>
              <span>投稿后由管理员审核，审核通过后立即发布。草稿仅自己可见。</span>
            </div>
            <div class="action-buttons">
              <button class="btn btn-v btn-lg w-full" @click="submitReview">
                🚀 提交审核
              </button>
              <button class="btn btn-ghost btn-lg w-full" @click="saveDraft">
                存为草稿
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const article = ref({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  categoryId: ''
})

const categories = ref([])
const tags = ref([])
const suggestedTags = ref([])
const tagInput = ref('')
const saveStatus = ref('saved')
const saveText = ref('已保存')
const wordCount = ref(0)
const saveTimer = ref(null)
const coverFile = ref(null)

const readTime = computed(() => Math.max(1, Math.ceil(wordCount.value / 300)))

const fetchCategories = async () => {
  try {
    const response = await fetch('/api/categories')
    if (response.ok) {
      categories.value = await response.json()
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchTags = async () => {
  try {
    const response = await fetch('/api/tags')
    if (response.ok) {
      suggestedTags.value = await response.json()
    }
  } catch (error) {
    console.error('获取标签失败:', error)
  }
}

const onEdit = () => {
  autoSave()
  updateWordCount()
}

const onContentEdit = (e) => {
  // 只在需要保存时更新数据，避免频繁更新导致光标丢失
  const content = e.target.innerHTML
  
  // 立即更新字数，确保用户看到实时反馈
  updateWordCount(content)
  
  // 使用防抖机制，避免每次输入都更新Vue响应式数据
  clearTimeout(saveTimer.value)
  saveTimer.value = setTimeout(() => {
    article.value.content = content
    autoSave()
  }, 100)
}

const updateWordCount = (content = null) => {
  const text = (content || article.value.content || '').replace(/<[^>]+>/g, '').replace(/\s+/g, '')
  wordCount.value = text.length
}

const autoSave = () => {
  saveStatus.value = 'saving'
  saveText.value = '保存中…'
  clearTimeout(saveTimer.value)
  saveTimer.value = setTimeout(() => {
    saveStatus.value = 'saved'
    const t = new Date()
    saveText.value = `已保存 ${t.getHours()}:${String(t.getMinutes()).padStart(2, '0')}`
  }, 1000)
}

const execCommand = (command, value = null) => {
  document.querySelector('.write-content').focus()
  document.execCommand(command, false, value)
}

const formatBlock = (event) => {
  const value = event.target.value
  if (value) {
    execCommand('formatBlock', value)
  }
  event.target.selectedIndex = 0
}

const insertLink = () => {
  const url = prompt('链接地址：', 'https://')
  if (url) {
    execCommand('createLink', url)
  }
}

const insertImage = () => {
  const url = prompt('图片地址：', 'https://')
  if (url) {
    execCommand('insertImage', url)
  }
}

const triggerCoverUpload = () => {
  coverFile.value?.click()
}

const handleCoverUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  const token = localStorage.getItem('token')
  if (!token) {
    alert('请先登录')
    return
  }
  
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    const response = await fetch('/api/upload/article-cover', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData
    })
    
    if (response.ok) {
      const data = await response.json()
      article.value.coverImage = data.coverUrl
      alert('封面上传成功')
    } else {
      const error = await response.json()
      alert('上传失败：' + error.error)
    }
  } catch (error) {
    console.error('上传失败:', error)
    alert('上传失败，请重试')
  }
}

const removeCover = () => {
  article.value.coverImage = ''
  if (coverFile.value) {
    coverFile.value.value = ''
  }
}

const handleTagInput = (event) => {
  if (event.key === 'Enter') {
    addTag()
    event.preventDefault()
  }
}

const addTag = () => {
  const value = tagInput.value.trim()
  if (!value || tags.value.includes(value) || tags.value.length >= 8) return
  
  tags.value.push(value)
  tagInput.value = ''
}

const addSuggestedTag = (tag) => {
  if (tags.value.includes(tag) || tags.value.length >= 8) return
  tags.value.push(tag)
}

const removeTag = (tag) => {
  tags.value = tags.value.filter(t => t !== tag)
}



const syncContentFromEditor = () => {
  const editor = document.querySelector('.write-content')
  if (editor) {
    article.value.content = editor.innerHTML
    updateWordCount(article.value.content)
  }
}

const buildArticlePayload = (status) => {
  syncContentFromEditor()
  return {
    ...article.value,
    categoryId: article.value.categoryId ? Number(article.value.categoryId) : null,
    tags: tags.value.join(','),
    status,
    authorId: JSON.parse(localStorage.getItem('user'))?.userId
  }
}

const preview = () => {
  alert('预览功能开发中...')
}

const saveDraft = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    alert('请先登录')
    return
  }
  
  try {
    const response = await fetch('/api/articles/draft', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(buildArticlePayload(0))
    })
    
    if (response.ok) {
      alert('草稿保存成功！')
    } else {
      alert('草稿保存失败，请重试')
    }
  } catch (error) {
    console.error('保存草稿失败:', error)
    alert('保存草稿失败，请重试')
  }
}

const submitReview = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    alert('请先登录')
    return
  }
  
  if (!article.value.title || !article.value.categoryId) {
    alert('请填写标题和选择分类')
    return
  }

  syncContentFromEditor()
  if (!article.value.content || article.value.content.replace(/<[^>]+>/g, '').trim() === '') {
    alert('请填写文章内容')
    return
  }
  
  try {
    const response = await fetch('/api/articles', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(buildArticlePayload(0))
    })
    
    if (response.ok) {
      alert('文章提交成功，等待审核！')
      router.push('/explore')
    } else {
      alert('文章提交失败，请重试')
    }
  } catch (error) {
    console.error('提交文章失败:', error)
    alert('提交文章失败，请重试')
  }
}

onMounted(() => {
  fetchCategories()
  fetchTags()
})

onUnmounted(() => {
  clearTimeout(saveTimer.value)
})
</script>

<style scoped>
/* 顶部工具栏 */
.write-toolbar {
  position: fixed;
  top: 62px;
  left: 0;
  right: 0;
  background: rgba(251, 246, 236, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #EAD8BE;
  z-index: 100;
}

.toolbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.save-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  padding: 6px 14px;
  border-radius: 24px;
  transition: all 0.3s;
  font-weight: 500;
}

.save-status.saved {
  background: rgba(46, 125, 106, 0.12);
  color: #2E7D6A;
}

.save-status.saving {
  background: rgba(212, 160, 23, 0.16);
  color: #A67C00;
}

.save-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.saved .save-dot {
  background: #2E7D6A;
}

.saving .save-dot {
  background: #D4A017;
  animation: blink 0.7s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.25; }
}

.word-count {
  font-size: 12px;
  color: #6B5744;
  font-weight: 500;
  padding: 6px 12px;
  background: #F5EDD6;
  border-radius: 4px;
}

/* 主要内容区域 */
/* 主要内容区域 */
.write-container {
  display: flex;
  gap: 40px;
  padding: 80px 0 60px;
  align-items: flex-start;
  max-width: 1200px;
  margin: 100px auto 0 auto;
}


.write-main {
  flex: 1;
  max-width: 720px;
  min-width: 0;
}

.write-sidebar {
  width: 320px;
  flex-shrink: 0;
  position: sticky;
  top: 100px;
}

/* 写作区块 */
.write-section {
  margin-bottom: 32px;
}

/* ── 标题输入（修复文字被截断的问题）── */
.write-title {
  width: 100%;
  font-size: 28px;
  font-weight: 600;
  font-family: 'ZCOOL XiaoWei', serif;
  letter-spacing: 1px;
  color: #1A1008 !important;
  -webkit-text-fill-color: #1A1008 !important;
  opacity: 1 !important;
  border: 2px solid #EAD8BE;
  outline: none;
  background: #ffffff !important;
  -webkit-appearance: none;
  -moz-appearance: none;
  appearance: none;
  /* 调整 padding，保证留给字体的空间充足 */
  padding: 16px 24px;
  border-radius: 8px;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
  /* 设置标准的行高，并让高度自适应 */
  line-height: 1.5;
  height: auto;
  display: block;
}

.write-title:focus {
  border-color: #C0392B;
  box-shadow: 0 0 0 3px rgba(192, 57, 43, 0.08);
  color: #1A1008 !important;
  -webkit-text-fill-color: #1A1008 !important;
}

.write-title::placeholder {
  color: rgba(107, 87, 68, 0.6) !important;
  font-size: 28px;
  letter-spacing: 1px;
  opacity: 1 !important;
  text-align: left;
}

/* 摘要输入 */
.write-summary {
  width: 100%;
  font-size: 15px;
  color: #6B5744;
  -webkit-text-fill-color: #6B5744;
  line-height: 1.8;
  border: none;
  outline: none;
  background: white;
  resize: vertical;
  padding: 16px 24px;
  border-radius: 8px;
  border: 1px solid #EAD8BE;
  transition: all 0.2s;
  box-sizing: border-box;
}

.write-summary:focus {
  border-color: #C0392B;
  box-shadow: 0 0 0 3px rgba(192, 57, 43, 0.08);
}

.write-summary::placeholder {
  color: rgba(107, 87, 68, 0.4);
  -webkit-text-fill-color: rgba(107, 87, 68, 0.4);
}

/* 侧边栏卡片 */
.sidebar-card {
  background: white;
  border: 1px solid #EAD8BE;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(26, 16, 8, 0.06);
  overflow: hidden;
}

.sidebar-header {
  padding: 20px 24px;
  border-bottom: 1px solid #EAD8BE;
  background: #F5EDD6;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: #1A1008;
  margin: 0;
  letter-spacing: 1px;
}

.sidebar-body {
  padding: 24px;
}

/* 封面上传 */
.cover-upload {
  width: 100%;
  height: 180px;
  border: 2px dashed #D4B896;
  border-radius: 8px;
  background: #F5EDD6;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  overflow: hidden;
  position: relative;
}

.cover-upload:hover {
  border-color: #C0392B;
  background: #F0E8D6;
}

.cover-upload.filled {
  border-style: solid;
  border-color: #D4B896;
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  text-align: center;
  padding: 0 20px;
}

.cover-icon {
  font-size: 40px;
}

.cover-text {
  font-size: 16px;
  color: #6B5744;
  font-weight: 500;
}

.cover-hint {
  font-size: 12px;
  color: rgba(107, 87, 68, 0.6);
}

.cover-upload img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: none;
}

.cover-upload.filled img {
  display: block;
}

.cover-upload.filled .cover-placeholder {
  display: none;
}

.cover-actions {
  position: absolute;
  inset: 0;
  background: rgba(26, 16, 8, 0.75);
  display: none;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.cover-upload.filled:hover .cover-actions {
  display: flex;
}

.cover-actions button {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.25);
  color: white;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
  backdrop-filter: blur(4px);
}

.cover-actions button:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

/* 表单元素 */
.tag-hint {
  font-size: 12px;
  color: rgba(107, 87, 68, 0.6);
  font-weight: 400;
}

.form-select {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #EAD8BE;
  border-radius: 8px;
  background: white;
  color: #1A1008;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
  cursor: pointer;
  box-sizing: border-box;
}

.form-select:focus {
  border-color: #C0392B;
  box-shadow: 0 0 0 3px rgba(192, 57, 43, 0.08);
}

/* 标签 */
.tags-input {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  align-items: center;
}

.tag-input {
  flex: 1;
  min-width: 0;  /* 允许输入框在极端情况下收缩 */
  padding: 12px 16px;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  background: white;
  color: #1A1008;
  -webkit-text-fill-color: #1A1008;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
  box-sizing: border-box;
}

/* 只添加这一条新规则 - 给添加按钮固定宽度 */
.tags-input .btn {
  flex-shrink: 0;  /* 防止按钮被压缩 */
  width: 64px;     /* 固定宽度，可以根据实际需要调整 */
  padding: 12px 0; /* 左右内边距改为0，由宽度控制 */
}

.tag-input:focus {
  border-color: #C0392B;
  box-shadow: 0 0 0 3px rgba(192, 57, 43, 0.08);
}

.tag-input::placeholder {
  color: rgba(107, 87, 68, 0.4);
  -webkit-text-fill-color: rgba(107, 87, 68, 0.4);
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: rgba(192, 57, 43, 0.08);
  color: #C0392B;
  border-radius: 20px;
  font-size: 13px;
  border: 1px solid rgba(192, 57, 43, 0.15);
  font-weight: 500;
  transition: all 0.2s;
}

.tag-item:hover {
  background: rgba(192, 57, 43, 0.12);
  transform: translateY(-1px);
}

.tag-remove {
  cursor: pointer;
  opacity: 0.7;
  transition: opacity 0.2s;
  font-size: 16px;
  line-height: 1;
}

.tag-remove:hover {
  opacity: 1;
}

.tags-suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-suggestion {
  padding: 6px 12px;
  background: #F5EDD6;
  color: #6B5744;
  border-radius: 16px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.tag-suggestion:hover {
  background: rgba(192, 57, 43, 0.08);
  color: #C0392B;
  transform: translateY(-1px);
}

/* 富文本工具栏 */
.write-tools {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  background: white;
  border: 1px solid #EAD8BE;
  border-radius: 8px;
  flex-wrap: wrap;
  box-shadow: 0 2px 8px rgba(26, 16, 8, 0.04);
}

.tool-select {
  padding: 8px 12px;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  background: white;
  color: #6B5744;
  font-size: 13px;
  outline: none;
  cursor: pointer;
  transition: all 0.2s;
}

.tool-select:hover {
  border-color: #C0392B;
}

.tool-divider {
  width: 1px;
  height: 24px;
  background: #EAD8BE;
  margin: 0 6px;
}

.tool-btn {
  width: 36px;
  height: 32px;
  border: none;
  background: none;
  color: #6B5744;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
}

.tool-btn:hover {
  background: #F5EDD6;
  color: #1A1008;
  transform: translateY(-1px);
}

.tool-icon {
  width: 16px;
  height: 16px;
  vertical-align: middle;
}

/* 内容编辑区 */
.write-content {
  min-height: 500px;
  padding: 20px 24px;
  background: white;
  border: 1px solid #EAD8BE;
  border-radius: 8px;
  font-size: 16px;
  line-height: 1.8;
  color: #6B5744;
  outline: none;
  transition: all 0.2s;
  box-sizing: border-box;
}

.write-content:focus {
  border-color: #C0392B;
  box-shadow: 0 0 0 3px rgba(192, 57, 43, 0.08);
}

.write-content:empty:not(:focus)::before {
  content: attr(data-placeholder);
  color: rgba(107, 87, 68, 0.4);
  font-style: italic;
  white-space: pre-line;
  font-size: 16px;
  pointer-events: none;
}

/* 底部信息 */
.write-footer {
  margin-top: 24px;
  padding: 16px 24px;
  background: #F5EDD6;
  border-radius: 8px;
  font-size: 14px;
  color: rgba(107, 87, 68, 0.7);
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 文章设置 */
.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #EAD8BE;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info {
  flex: 1;
}

.setting-label {
  font-size: 14px;
  color: #1A1008;
  margin-bottom: 4px;
  font-weight: 500;
}

.setting-desc {
  font-size: 12px;
  color: rgba(107, 87, 68, 0.6);
}

.toggle-switch {
  width: 48px;
  height: 24px;
  background: #D4B896;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  transition: all 0.2s;
  flex-shrink: 0;
}

.toggle-switch.active {
  background: #C0392B;
}

.toggle-slider {
  width: 20px;
  height: 20px;
  background: white;
  border-radius: 50%;
  position: absolute;
  top: 2px;
  left: 2px;
  transition: all 0.2s;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.toggle-switch.active .toggle-slider {
  transform: translateX(24px);
}

/* 操作区域 */
.status-info {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  color: #6B5744;
  line-height: 1.6;
  padding: 16px;
  background: #F5EDD6;
  border-radius: 6px;
  border-left: 3px solid #D4A017;
  margin-bottom: 20px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #D4A017;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 3px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 按钮样式 */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-family: 'Noto Serif SC', serif;
  letter-spacing: 2px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
  box-sizing: border-box;
}

.btn-v {
  background: #C0392B;
  color: white;
  box-shadow: 0 2px 12px rgba(192, 57, 43, 0.25);
}

.btn-v:hover {
  background: #E04B3A;
  box-shadow: 0 4px 20px rgba(192, 57, 43, 0.35);
  transform: translateY(-2px);
}

.btn-ghost {
  background: transparent;
  color: #C0392B;
  border: 1.5px solid #C0392B;
}

.btn-ghost:hover {
  background: rgba(192, 57, 43, 0.08);
  transform: translateY(-2px);
}

.btn-sm {
  padding: 8px 16px;
  font-size: 12px;
}

.btn-lg {
  padding: 14px 28px;
  font-size: 15px;
}

.w-full {
  width: 100%;
}

/* 响应式调整 */
@media (max-width: 1024px) {
  .write-container {
    flex-direction: column;
  }
  
  .write-sidebar {
    width: 100%;
    position: static;
  }
  
  .write-title {
    font-size: 28px;
  }
  
  .write-title::placeholder {
    font-size: 28px;
  }
}

@media (max-width: 768px) {
  .write-title {
    font-size: 24px;
    padding: 12px 20px; /* 稍微减小移动端的内边距 */
  }
  
  .write-title::placeholder {
    font-size: 24px;
  }
  
  .write-content {
    min-height: 400px;
    padding: 16px 20px;
  }
}
</style>