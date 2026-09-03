<template>
  <div class="page active">
    <div class="nav-gap"></div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <div class="loading-text">加载中...</div>
    </div>
    
    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <div class="error-icon">⚠️</div>
      <div class="error-text">{{ error }}</div>
      <button class="btn btn-v" @click="fetchArticleDetail">重试</button>
    </div>
    
    <!-- 文章内容 -->
    <template v-else-if="article">
      <section class="art-detail-hero">
        <div class="art-detail-hero-bg"></div>
        <div class="art-detail-hero-img" :style="{ backgroundImage: `url('${fixImagePath(article.coverImage)}')` }"></div>
        <div class="wrap art-detail-hero-pos">
          <div class="art-breadcrumb">
            <a @click="$router.push('/')">首页</a>
            <span>/</span>
            <a>{{ article.category }}</a>
            <span>/</span>
            <span>{{ article.title }}</span>
          </div>
          <div class="art-detail-tags">
            <span class="art-detail-tag">{{ article.categoryName }}</span>
            <span class="art-detail-tag" v-if="article.isFeatured === 1">精选</span>
          </div>
          <h1 class="art-detail-h1">{{ article.title }}</h1>
          <div class="art-detail-desc">
            {{ article.summary }}
          </div>
          <div class="art-detail-meta">
            <div class="art-meta-avt" @click="goToUserProfile(article.authorId)" style="cursor: pointer;">
              <img 
                v-if="article.userAvatar"
                :src="fixAvatarPath(article.userAvatar)" 
                :alt="article.userNickname"
                class="art-meta-avt-img"
              />
              <div v-else class="art-meta-avt-img art-meta-avt-placeholder">
                {{ article.userNickname?.charAt(0) || '?' }}
              </div>
            </div>
            <div>
              <div class="art-meta-authr" @click="goToUserProfile(article.authorId)" style="cursor: pointer;">{{ article.userNickname }}</div>
              <div class="art-meta-date">{{ article.createTime.substring(0, 10) }}</div>
            </div>
            <div class="art-meta-div"></div>
            <div class="art-meta-stat"><img src="/icons/浏览.png" alt="浏览" class="stat-icon" /> {{ (article.viewCount / 1000).toFixed(1) }}K</div>
            <div class="art-meta-stat"><img src="/icons/喜欢2.png" alt="喜欢" class="stat-icon" /> {{ article.likeCount }}</div>
            <div class="art-meta-stat"><img src="/icons/评论 (3).png" alt="评论" class="stat-icon" /> {{ article.commentCount }}</div>
            <div class="art-meta-div"></div>
            <div 
              class="art-action-btn voice-btn" 
              :class="{ 'playing': isPlaying }"
              @click="toggleVoice"
            >
              <div v-if="isPlaying" class="voice-dot"></div>
              <img 
                v-else
                src="/icons/喇叭.png" 
                alt="粤语朗读" 
                class="voice-icon"
              />
              <div class="art-action-lbl">{{ isPlaying ? '朗读中...' : '粤语朗读' }}</div>
            </div>
          </div>
        </div>
      </section>

      <section class="wrap art-body-layout">
        <div class="art-body-main">
          <article class="art-content" :class="{ 'content-blur': !isLoggedIn }">
            <div v-html="renderedContent"></div>
          </article>

          <!-- 游客登录提示 -->
          <div class="login-mask" v-if="!isLoggedIn">
            <div class="login-mask-inner">
              <div class="login-mask-icon">🔒</div>
              <div class="login-mask-title">继续阅读完整内容</div>
              <div class="login-mask-desc">登录后可查看全文，还能参与评论互动</div>
              <button class="btn btn-v btn-lg" @click="$router.push('/login')">立即登录</button>
              <div class="login-mask-tip">还没有账号？<a @click="$router.push('/login')">立即注册</a></div>
            </div>
          </div>

          <div class="art-actions" v-if="isLoggedIn">
            <div class="art-action-btn" @click="toggleArticleLike">
              <img 
                :src="isLiked ? '/icons/点赞_块.png' : '/icons/点赞 (5).png'" 
                alt="点赞" 
                class="art-action-ico-img"
              />
              <div class="art-action-lbl">{{ article.likeCount }}</div>
            </div>
            <div class="art-action-btn">
              <img 
                src="/icons/评论 (4).png" 
                alt="评论" 
                class="art-action-ico-img"
              />
              <div class="art-action-lbl">{{ article.commentCount }}</div>
            </div>
            <div class="art-action-btn">
              <img 
                src="/icons/分享.png" 
                alt="分享" 
                class="art-action-ico-img"
              />
              <div class="art-action-lbl">分享</div>
            </div>
            <div class="art-action-btn" @click="toggleArticleFavorite">
              <img 
                :src="isFavorited ? '/icons/收藏 -已收藏-copy-copy.png' : '/icons/收藏 (1).png'" 
                alt="收藏" 
                class="art-action-ico-img"
              />
              <div class="art-action-lbl">{{ article.favoriteCount }}</div>
            </div>
          </div>

          <!-- 作者卡片 -->
          <div class="art-author-card" @click="goToUserProfile(article.authorId)">
            <div class="art-author-avt">
              <img 
                v-if="article.userAvatar"
                :src="fixAvatarPath(article.userAvatar)" 
                :alt="article.userNickname"
                class="art-author-avt-img"
              />
              <div v-else class="art-author-avt-placeholder">
                {{ article.userNickname?.charAt(0) || '?' }}
              </div>
            </div>
            <div class="art-author-info">
              <div class="art-author-name">{{ article.userNickname }}</div>
              <div class="art-author-badge">认证作者</div>
              <div class="art-author-bio">{{ article.userBio || '暂无简介' }}</div>
              <div class="art-author-stats">
                <span>已发布 {{ article.articleCount || 0 }} 篇文章</span>
                <span>粉丝 {{ article.followerCount || 0 }} 人</span>
              </div>
            </div>
            <div class="art-author-actions">
              <button class="btn btn-ol btn-sm" @click.stop="toggleFollow">
                {{ isFollowing ? '已关注' : '关注' }}
              </button>
              <button
                v-if="!isAuthorSelf"
                class="btn btn-ghost btn-sm"
                @click.stop="sendMessage"
              >
                私信
              </button>
            </div>
          </div>

          <div class="comments" v-if="isLoggedIn">
            <div class="comments-head">
              评论 <span class="comments-count">{{ article.commentCount }}</span>
            </div>
            <div class="comment-box">
              <textarea 
                class="comment-input" 
                v-model="commentContent"
                placeholder="写下你的评论..."
                :disabled="submittingComment"
              ></textarea>
              <div class="comment-box-foot">
                <button 
                  class="btn btn-v btn-sm" 
                  @click="submitComment"
                  :disabled="!commentContent.trim() || submittingComment"
                >
                  {{ submittingComment ? '提交中...' : '发表评论' }}
                </button>
              </div>
            </div>
            
            <!-- 评论列表 -->
            <div class="comment-item" v-for="(comment, index) in article.commentsList" :key="comment.id || index">
              <img 
                v-if="comment.userAvatar"
                :src="fixAvatarPath(comment.userAvatar)"
                :alt="comment.userNickname || comment.userName"
                class="cmt-avt-img"
                @click="goToUserProfile(comment.userId)"
                style="cursor: pointer;"
              />
              <div v-else class="cmt-avt-img cmt-avt-placeholder" @click="goToUserProfile(comment.userId)">
                {{ (comment.userNickname || comment.userName)?.charAt(0) || '?' }}
              </div>
              <div class="cmt-right">
                <div class="cmt-meta">
                  <div class="cmt-name" @click="goToUserProfile(comment.userId)" style="cursor: pointer;">{{ comment.userNickname || comment.userName }}</div>
                  <div class="cmt-date">{{ formatDate(comment.createTime) }}</div>
                </div>
                <div class="cmt-body">{{ comment.content }}</div>
                <div class="cmt-actions">
                  <div class="cmt-action" @click="toggleCommentLike(comment)">
                    <img 
                      :src="comment.isLiked ? '/icons/点赞_块.png' : '/icons/点赞 (5).png'" 
                      alt="点赞" 
                      class="like-icon" 
                      :class="{ 'liked': comment.isLiked }"
                    />
                    {{ comment.likeCount || 0 }}
                  </div>
                  <div class="cmt-action" @click="showReplyForm(comment)">回复</div>
                </div>
                
                <!-- 回复表单 -->
                <div v-if="replyingTo && replyingTo.id === comment.id" class="reply-form">
                  <div class="replying-to-info" v-if="replyingTo">
                    回复 @{{ replyingTo.userNickname || replyingTo.userName }}
                  </div>
                  <textarea 
                    class="reply-input" 
                    v-model="replyContent"
                    placeholder="写下你的回复..."
                    :disabled="submittingComment"
                  ></textarea>
                  <div class="reply-form-actions">
                    <button 
                      class="btn btn-ol btn-sm" 
                      @click="cancelReply"
                      :disabled="submittingComment"
                    >
                      取消
                    </button>
                    <button 
                      class="btn btn-v btn-sm" 
                      @click="submitReply"
                      :disabled="!replyContent.trim() || submittingComment"
                    >
                      {{ submittingComment ? '回复中...' : '回复' }}
                    </button>
                  </div>
                </div>
                
                <!-- 回复列表 -->
                <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
                  <div 
                    class="reply-item" 
                    v-for="(reply, replyIndex) in comment.replies" 
                    :key="reply.id || replyIndex"
                  >
                    <img 
                      v-if="reply.userAvatar"
                      :src="fixAvatarPath(reply.userAvatar)"
                      :alt="reply.userNickname || reply.userName"
                      class="reply-avt-img"
                      @click="goToUserProfile(reply.userId)"
                      style="cursor: pointer;"
                    />
                    <div v-else class="reply-avt-img reply-avt-placeholder" @click="goToUserProfile(reply.userId)">
                      {{ (reply.userNickname || reply.userName)?.charAt(0) || '?' }}
                    </div>
                    <div class="reply-right">
                      <div class="reply-meta">
                        <div class="reply-name" @click="goToUserProfile(reply.userId)" style="cursor: pointer;">{{ reply.userNickname || reply.userName }}</div>
                        <div class="reply-date">{{ formatDate(reply.createTime) }}</div>
                      </div>
                      <div class="reply-body">
                        <span v-if="reply.replyToUserName" class="reply-to-tag">回复 @{{ reply.replyToUserName }}</span>
                        {{ reply.content }}
                      </div>
                      <div class="reply-actions">
                          <div class="reply-action" @click="toggleCommentLike(reply)">
                            <img 
                              :src="reply.isLiked ? '/icons/点赞_块.png' : '/icons/点赞 (5).png'" 
                              alt="点赞" 
                              class="like-icon" 
                              :class="{ 'liked': reply.isLiked }"
                            />
                            {{ reply.likeCount || 0 }}
                          </div>
                          <div class="reply-action" @click="showReplyForm(reply)">回复</div>
                        </div>
                        
                        <!-- 回复表单 -->
                        <div v-if="replyingTo && replyingTo.id === reply.id" class="reply-form">
                          <div class="replying-to-info" v-if="replyingTo">
                            回复 @{{ replyingTo.userNickname || replyingTo.userName }}
                          </div>
                          <textarea 
                            class="reply-input" 
                            v-model="replyContent"
                            placeholder="写下你的回复..."
                            :disabled="submittingComment"
                          ></textarea>
                          <div class="reply-form-actions">
                            <button 
                              class="btn btn-ol btn-sm" 
                              @click="cancelReply"
                              :disabled="submittingComment"
                            >
                              取消
                            </button>
                            <button 
                              class="btn btn-v btn-sm" 
                              @click="submitReply"
                              :disabled="!replyContent.trim() || submittingComment"
                            >
                              {{ submittingComment ? '回复中...' : '回复' }}
                            </button>
                          </div>
                        </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 游客评论提示 -->
          <div class="comments-guest" v-else>
            <div class="guest-prompt">
              <div class="guest-icon">💬</div>
              <div class="guest-text">登录后参与评论互动</div>
              <button class="btn btn-ol btn-sm" @click="$router.push('/login')">去登录</button>
            </div>
          </div>
        </div>

        <aside class="art-sidebar">
          <div class="toc">
            <div class="toc-head">
              <div class="toc-head-title">目录</div>
            </div>
            <div class="toc-list">
              <!-- 动态生成目录 -->
              <a 
                v-for="item in toc" 
                :key="item.id"
                :class="['toc-item', `level-${item.level}`, { active: activeId === item.id }]"
                @click="scrollTo(item.id)"
              >
                {{ item.text }}
              </a>
            </div>
          </div>

          <div class="widget">
            <div class="widget-head">
              <div class="widget-head-dot"></div>
              <div class="widget-head-title">相关文章</div>
            </div>
            <div class="related-art">
              <div v-if="!article.relatedArticles || article.relatedArticles.length === 0" class="related-art-empty">
                <div class="related-art-empty-icon">📚</div>
                <div class="related-art-empty-text">暂无相关文章，敬请期待</div>
              </div>
              <div class="related-art-item" v-for="(related, index) in article.relatedArticles" :key="index" @click.stop.prevent="goToArticle(related.articleId)">
                <div class="related-art-img" :style="{ backgroundImage: `url('${fixImagePath(related.coverImage)}')` }"></div>
                <div class="related-art-title">{{ related.title }}</div>
              </div>
            </div>
          </div>
        </aside>
      </section>
    </template>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { marked } from 'marked'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

// 配置marked，给标题自动加id
const renderer = new marked.Renderer()
renderer.heading = function({ text, depth, raw }) {
  // 确保text是字符串
  const textStr = typeof text === 'string' ? text : String(text)
  const id = 'heading-' + textStr.replace(/\s+/g, '-')
  return `<h${depth} id="${id}">${textStr}</h${depth}>`
}

marked.use({ renderer })

// 配置marked选项
marked.setOptions({
  breaks: true,  // 启用换行符
  gfm: true,     // 启用GitHub风格Markdown
  headerIds: false // 不生成header id（因为我们自定义了renderer）
})

// 从原始Markdown内容中提取标题
const extractTOC = (markdownContent) => {
  const headings = []
  // 先处理转义的换行符
  const processedContent = markdownContent.replace(/\\n/g, '\n')
  const lines = processedContent.split('\n')
  
  lines.forEach(line => {
    // 匹配 ## 标题
    const match = line.match(/^(#{1,3})\s+(.+)/)
    if (match) {
      headings.push({
        level: match[1].length,   // 1=一级 2=二级 3=三级
        text: match[2],           // 标题文字
        id: 'heading-' + match[2].replace(/\s+/g, '-')  // 对应的锚点id
      })
    }
  })
  
  return headings
}

const activeId = ref('')

// 监听页面滚动，判断当前在哪个标题区域
const onScroll = () => {
  const headings = document.querySelectorAll('h1, h2, h3')
  
  for (let i = headings.length - 1; i >= 0; i--) {
    const heading = headings[i]
    // 标题距页面顶部的距离
    if (heading.getBoundingClientRect().top <= 100) {
      activeId.value = heading.id
      break
    }
  }
}

// 点击目录跳转
const scrollTo = (id) => {
  const el = document.getElementById(id)
  if (el) {
    const navHeight = 62 // 导航栏高度
    const offset = 20 // 额外的偏移量
    const elementPosition = el.getBoundingClientRect().top + window.pageYOffset
    const offsetPosition = elementPosition - navHeight - offset
    
    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    })
  }
}

// 打印完整的路由对象
console.log('完整路由对象:', route)

const isLoggedIn = computed(() => authStore.isLoggedIn)
const isAuthorSelf = computed(() => {
  if (!authStore.user || !article.value) return false
  return String(authStore.user.userId) === String(article.value.authorId)
})
const article = ref(null)
const loading = ref(true)
const error = ref(null)
const commentContent = ref('')
const submittingComment = ref(false)
const isPlaying = ref(false)
const speechSynthesis = ref(null)
const isFollowing = ref(false)
const isLiked = ref(false)
const isFavorited = ref(false)

// 生成目录
const toc = computed(() => {
  if (!article.value || !article.value.content) {
    return []
  }
  return extractTOC(article.value.content)
})

// 渲染Markdown内容
const renderedContent = computed(() => {
  if (!article.value || !article.value.content) {
    console.log('文章内容为空')
    return ''
  }
  
  // 处理转义的换行符
  let content = article.value.content
  console.log('原始内容类型:', typeof content)
  console.log('原始内容长度:', content.length)
  
  // 尝试多种方式处理换行符
  // 方法1：直接替换字面的\n
  content = content.replace(/\\n/g, '\n')
  // 方法2：处理连续的换行符
  content = content.replace(/\n\n/g, '\n\n')
  
  console.log('处理后的文章内容:', content.substring(0, 200))
  const parsed = marked.parse(content)
  console.log('解析后的内容:', parsed.substring(0, 200))
  console.log('解析后的HTML结构:', parsed.substring(0, 500))
  return parsed
})

const API_BASE_URL = '/api'

// 修复图片路径的辅助函数
const fixImagePath = (path) => {
  if (!path) return ''
  // 确保路径以/uploads开头
  let fixedPath = path.startsWith('/uploads') ? path : `/uploads${path}`
  // 移除路径中的空格
  fixedPath = fixedPath.replace(/\s+/g, '')
  // 将.jpg扩展名替换为.png
  fixedPath = fixedPath.replace(/\.jpg$/i, '.png')
  return fixedPath
}

// 修复头像路径的辅助函数
const fixAvatarPath = (path) => {
  if (!path) return ''
  let fixedPath = path
  if (!fixedPath.startsWith('/')) {
    fixedPath = '/' + fixedPath
  }
  fixedPath = fixedPath.replace(/\s+/g, '')
  return fixedPath
}

// 切换评论点赞状态
const toggleCommentLike = async (comment) => {
  if (!isLoggedIn.value) {
    alert('请先登录');
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE_URL}/comments/like?userId=${authStore.user.userId}&commentId=${comment.id}`);
    
    if (response.ok) {
      const isLiked = await response.json();
      comment.isLiked = isLiked;
      // 更新点赞数
      if (isLiked) {
        comment.likeCount = (comment.likeCount || 0) + 1;
      } else {
        comment.likeCount = Math.max(0, (comment.likeCount || 1) - 1);
      }
    } else {
      console.error('点赞失败:', response.status);
    }
  } catch (error) {
    console.error('点赞请求失败:', error);
  }
}

// 检查评论是否已点赞
const checkCommentLikes = async () => {
  if (!isLoggedIn.value || !article.value?.commentsList) return;
  
  // 检查主评论的点赞状态
  for (const comment of article.value.commentsList) {
    await checkSingleCommentLike(comment);
    // 检查回复的点赞状态
    if (comment.replies) {
      for (const reply of comment.replies) {
        await checkSingleCommentLike(reply);
      }
    }
  }
}

const checkSingleCommentLike = async (comment) => {
  try {
    const response = await fetch(`${API_BASE_URL}/comments/is-liked?userId=${authStore.user.userId}&commentId=${comment.id}`);
    if (response.ok) {
      comment.isLiked = await response.json();
    }
  } catch (error) {
    console.error('检查点赞状态失败:', error);
  }
}

// 格式化日期的辅助函数
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  if (isNaN(date.getTime())) return dateString
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 翻译文本为粤语
const translateToCantonese = async (text) => {
  try {
    console.log('开始翻译，原文:', text.substring(0, 100) + '...')
    const response = await fetch(`${API_BASE_URL}/translate?q=${encodeURIComponent(text)}`)
    console.log('翻译响应状态:', response.status)
    const data = await response.json()
    console.log('翻译响应数据:', data)
    if (data.trans_result && data.trans_result.length > 0) {
      console.log('翻译结果:', data.trans_result[0].dst.substring(0, 100) + '...')
      return data.trans_result[0].dst
    }
    return text
  } catch (error) {
    console.error('翻译失败:', error)
    return text
  }
}

// 播放粤语朗读
const playCantonese = async () => {
  if (!article.value) return
  
  isPlaying.value = true
  
  try {
    console.log('开始播放粤语朗读')
    // 准备要朗读的文本
    const textToRead = article.value.title + '。' + article.value.content.replace(/<[^>]+>/g, '')
    console.log('要朗读的文本总长度:', textToRead.length)
    
    // 分段处理文本，每段500字
    const segments = []
    const segmentSize = 500
    for (let i = 0; i < textToRead.length; i += segmentSize) {
      segments.push(textToRead.substring(i, i + segmentSize))
    }
    console.log('文本分段数:', segments.length)
    
    // 依次处理每个段落
    for (let i = 0; i < segments.length && isPlaying.value; i++) {
      console.log(`处理第 ${i + 1} 段文本`)
      const segment = segments[i]
      console.log('当前段落:', segment.substring(0, 100) + '...')
      
      // 翻译为粤语
      console.log('开始翻译文本')
      const cantoneseText = await translateToCantonese(segment)
      console.log('翻译结果:', cantoneseText.substring(0, 100) + '...')
      
      // 调用百度智能云语音API
      console.log('开始调用百度智能云语音API')
      const requestData = {
        text: cantoneseText,
        lang: 'zh'
      }
      console.log('请求数据:', requestData)
      
      const response = await fetch(`${API_BASE_URL}/speech/synthesize`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(requestData)
      })
      
      console.log('响应状态:', response.status)
      console.log('响应头:', Object.fromEntries(response.headers.entries()))
      
      if (response.ok) {
        const contentType = response.headers.get('content-type')
        console.log('内容类型:', contentType)
        
        const audioBlob = await response.blob()
        console.log('音频Blob类型:', audioBlob.type)
        console.log('音频Blob大小:', audioBlob.size)
        
        const audioUrl = URL.createObjectURL(audioBlob)
        console.log('创建的音频URL:', audioUrl)
        
        // 创建音频元素并播放
        const audio = new Audio(audioUrl)
        window.currentAudio = audio // 保存当前音频实例，用于停止播放
        
        // 播放结束时的回调
        audio.onended = () => {
          console.log(`第 ${i + 1} 段朗读结束`)
          window.currentAudio = null // 清除当前音频实例
          URL.revokeObjectURL(audioUrl) // 释放URL对象
          
          // 如果是最后一段，标记播放完成
          if (i === segments.length - 1) {
            console.log('全部朗读结束')
            isPlaying.value = false
          }
        }
        
        // 播放出错时的回调
        audio.onerror = (event) => {
          console.error('播放出错:', event)
          console.error('音频元素错误:', audio.error)
          window.currentAudio = null // 清除当前音频实例
          URL.revokeObjectURL(audioUrl) // 释放URL对象
          
          // 播放出错时停止整个朗读过程
          isPlaying.value = false
        }
        
        // 开始播放
        console.log('开始播放')
        try {
          await audio.play()
          console.log('播放成功启动')
          
          // 等待当前音频播放完成，再处理下一段
          await new Promise((resolve) => {
            audio.addEventListener('ended', resolve)
            audio.addEventListener('error', resolve)
          })
        } catch (playError) {
          console.error('播放启动失败:', playError)
          window.currentAudio = null
          URL.revokeObjectURL(audioUrl)
          isPlaying.value = false
          break
        }
      } else {
        console.error('语音合成失败:', response.status)
        console.error('响应文本:', await response.text())
        isPlaying.value = false
        alert('语音合成失败，请稍后再试')
        break
      }
    }
  } catch (error) {
    console.error('朗读失败:', error)
    isPlaying.value = false
    alert('朗读失败，请稍后再试')
  }
}

// 停止朗读
const stopPlaying = () => {
  // 停止百度智能云语音播放
  if (window.currentAudio) {
    window.currentAudio.pause()
    window.currentAudio = null
  }
  isPlaying.value = false
}

// 跳转到文章详情
const goToArticle = (articleId) => {
  console.log('点击相关文章，文章ID:', articleId)
  if (!articleId) {
    console.error('文章ID不存在')
    return
  }
  
  const targetUrl = `/article/${articleId}`
  console.log('跳转到:', targetUrl)
  console.log('当前路由:', route.path)
  
  if (route.path === targetUrl) {
    console.log('跳转到相同路由，强制刷新')
    window.location.href = targetUrl
  } else {
    console.log('跳转到不同路由，使用router.push')
    router.push(targetUrl)
  }
}

// 切换语音播放状态
const toggleVoice = () => {
  if (isPlaying.value) {
    stopPlaying()
  } else {
    playCantonese()
  }
}

const fetchArticleDetail = async () => {
  try {
    const articleId = route.params.id
    console.log('开始获取文章详情，ID:', articleId)
    
    // 检查文章ID是否存在
    if (!articleId) {
      console.error('文章ID不存在')
      error.value = '文章不存在'
      loading.value = false
      return
    }
    
    let articleResponse
    
    // 先尝试普通获取文章详情
    articleResponse = await fetch(`${API_BASE_URL}/articles/${articleId}`)
    
    // 如果普通获取失败，且用户已登录，尝试使用作者专用端点
    if (!articleResponse.ok && isLoggedIn.value) {
      console.log('普通获取失败，尝试使用作者专用端点')
      articleResponse = await fetch(`${API_BASE_URL}/articles/${articleId}/author`)
    }
    
    console.log('文章响应状态:', articleResponse.status)
    
    if (articleResponse.ok) {
      const data = await articleResponse.json()
      console.log('获取到的文章数据:', data)
      article.value = data
      
      // 检查点赞和收藏状态
      if (isLoggedIn.value) {
        await checkLikeAndFavoriteStatus()
        await checkFollowStatus()
      }
      
      // 并行获取评论数据
      const [commentsResponse, countResponse] = await Promise.all([
        fetch(`${API_BASE_URL}/comments/article/${articleId}/tree`),
        fetch(`${API_BASE_URL}/comments/count/${articleId}`)
      ])
      
      console.log('评论响应状态:', commentsResponse.status)
      console.log('评论数量响应状态:', countResponse.status)
      
      // 获取评论数据
      if (commentsResponse.ok) {
        const commentsData = await commentsResponse.json()
        console.log('获取到的评论数据:', commentsData)
        article.value.commentsList = commentsData || []
        
        // 检查每条一级评论的parent_id
        article.value.commentsList.forEach(comment => {
          console.log(`一级评论 ID: ${comment.id}, parent_id: ${comment.parentId}, content: ${comment.content}`)
        })
        
        // 检查评论点赞状态
        if (isLoggedIn.value) {
          await checkCommentLikes()
        }
        
        // 为每条一级评论获取回复
        for (const comment of article.value.commentsList) {
          await fetchReplies(comment.id, comment)
        }
      }
      
      // 获取评论数量
      if (countResponse.ok) {
        const countData = await countResponse.json()
        console.log('获取到的评论数量:', countData)
        article.value.commentCount = countData
      }
      
      // 获取相关文章
      try {
        const relatedResponse = await fetch(`${API_BASE_URL}/articles/${articleId}/related`)
        if (relatedResponse.ok) {
          const relatedData = await relatedResponse.json()
          console.log('获取到的相关文章数据:', relatedData)
          article.value.relatedArticles = relatedData || []
        } else {
          console.log('获取相关文章失败，状态码:', relatedResponse.status)
          article.value.relatedArticles = []
        }
      } catch (err) {
        console.error('获取相关文章失败:', err)
        article.value.relatedArticles = []
      }
    } else {
      console.error('响应失败:', articleResponse)
      error.value = '获取文章失败'
    }
  } catch (err) {
    console.error('获取文章详情失败:', err)
    error.value = '网络错误'
  } finally {
    loading.value = false
    // 加载完成后滚动到页面顶部
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// 获取评论的回复
const fetchReplies = async (parentId, comment) => {
  try {
    const response = await fetch(`${API_BASE_URL}/comments/replies/${parentId}`)
    if (response.ok) {
      const repliesData = await response.json()
      console.log(`获取到的回复数据 for ${parentId}:`, repliesData)
      
      // 检查每条回复的parent_id
      repliesData.forEach(reply => {
        console.log(`回复 ID: ${reply.id}, parent_id: ${reply.parentId}, 应该属于父评论: ${parentId}, content: ${reply.content}`)
      })
      
      comment.replies = repliesData || []
    }
  } catch (err) {
    console.error('获取回复失败:', err)
  }
}

// 监听路由变化，查看路由参数
watch(
  () => route.params,
  (newParams) => {
    console.log('路由参数变化:', newParams)
    if (newParams.id) {
      console.log('重新加载文章，ID:', newParams.id)
      fetchArticleDetail()
    }
  },
  { immediate: true }
)

const submitComment = async () => {
  if (!commentContent.value.trim()) return
  
  try {
    submittingComment.value = true
    
    const commentData = {
      articleId: article.value.articleId,
      userId: authStore.user.userId,
      content: commentContent.value.trim(),
      parentId: null // 一级评论，parentId为null
    }
    
    const response = await fetch(`${API_BASE_URL}/comments`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authStore.token}`
      },
      body: JSON.stringify(commentData)
    })
    
    if (response.ok) {
      const newComment = await response.json()
      
      // 添加到评论列表
      if (!article.value.commentsList) {
        article.value.commentsList = []
      }
      article.value.commentsList.unshift(newComment)
      newComment.replies = [] // 初始化回复数组
      
      // 更新评论数量
      article.value.commentCount++
      
      // 清空输入框
      commentContent.value = ''
      
      alert('评论发表成功！')
    } else {
      alert('评论发表失败，请重试')
    }
  } catch (err) {
    console.error('提交评论失败:', err)
    alert('评论发表失败，请重试')
  } finally {
    submittingComment.value = false
  }
}

// 回复评论
const replyContent = ref('')
const replyingTo = ref(null)

const showReplyForm = (comment) => {
  replyingTo.value = comment
  replyContent.value = ''
}

const cancelReply = () => {
  replyingTo.value = null
  replyContent.value = ''
}

// 跳转到用户个人主页
const goToUserProfile = (userId) => {
  if (userId) {
    router.push(`/author/${userId}`)
  }
}

// 切换关注状态
const toggleFollow = async () => {
  if (!isLoggedIn.value) {
    alert('请先登录')
    return
  }
  
  try {
    const authorId = article.value.authorId
    const response = await fetch(`${API_BASE_URL}/users/follow/${authorId}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      }
    })
    if (response.ok) {
      const data = await response.json()
      isFollowing.value = data.isFollowing
      if (article.value) {
        article.value.followerCount = data.followerCount
      }
      alert(data.isFollowing ? '关注成功' : '已取消关注')
    } else {
      const data = await response.json()
      alert(data.error || '操作失败')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    alert('操作失败，请重试')
  }
}

const checkFollowStatus = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/users/${authStore.user.userId}/following`)
    if (response.ok) {
      const followingList = await response.json()
      isFollowing.value = followingList.some(user => user.userId === article.value.authorId)
    }
  } catch (error) {
    console.error('检查关注状态失败:', error)
  }
}

// 发送私信
const sendMessage = () => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (isAuthorSelf.value) {
    alert('不能给自己发私信')
    return
  }
  router.push({ path: '/messages', query: { peerId: article.value.authorId } })
}

// 检查点赞和收藏状态
const checkLikeAndFavoriteStatus = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/articles/${article.value.articleId}/like-status?userId=${authStore.user.userId}`)
    if (response.ok) {
      const data = await response.json()
      isLiked.value = data.isLiked
      isFavorited.value = data.isFavorited
    }
  } catch (error) {
    console.error('检查点赞和收藏状态失败:', error)
  }
}

// 切换点赞状态
const toggleArticleLike = async () => {
  if (!isLoggedIn.value) {
    alert('请先登录')
    return
  }
  
  try {
    const response = await fetch(`${API_BASE_URL}/articles/${article.value.articleId}/toggle-like?userId=${authStore.user.userId}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      }
    })
    
    if (response.ok) {
      const data = await response.json()
      isLiked.value = data.isLiked
      article.value.likeCount = data.likeCount
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    alert('操作失败，请重试')
  }
}

// 切换收藏状态
const toggleArticleFavorite = async () => {
  if (!isLoggedIn.value) {
    alert('请先登录')
    return
  }
  
  try {
    const response = await fetch(`${API_BASE_URL}/articles/${article.value.articleId}/toggle-favorite?userId=${authStore.user.userId}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      }
    })
    
    if (response.ok) {
      const data = await response.json()
      isFavorited.value = data.isFavorited
      article.value.favoriteCount = data.favoriteCount
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    alert('操作失败，请重试')
  }
}

const submitReply = async () => {
  if (!replyContent.value.trim() || !replyingTo.value) return
  
  try {
    submittingComment.value = true
    
    const replyData = {
      articleId: article.value.articleId,
      userId: authStore.user.userId,
      content: replyContent.value.trim(),
      parentId: replyingTo.value.parentId ? replyingTo.value.parentId : replyingTo.value.id, // 如果是回复子评论，parentId指向父评论；如果是回复父评论，parentId指向该评论
      replyToUserId: replyingTo.value.userId // 记录被回复用户的ID
    }
    
    const response = await fetch(`${API_BASE_URL}/comments`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authStore.token}`
      },
      body: JSON.stringify(replyData)
    })
    
    if (response.ok) {
      const newReply = await response.json()
      
      // 添加到父评论的回复列表
      let parentComment = replyingTo.value.parentId 
        ? findCommentById(article.value.commentsList, replyingTo.value.parentId)
        : replyingTo.value
      
      if (parentComment) {
        if (!parentComment.replies) {
          parentComment.replies = []
        }
        parentComment.replies.push(newReply)
      }
      
      // 更新评论数量
      article.value.commentCount++
      
      // 清空输入框并关闭回复表单
      replyContent.value = ''
      replyingTo.value = null
      
      alert('回复发表成功！')
    } else {
      alert('回复发表失败，请重试')
    }
  } catch (err) {
    console.error('提交回复失败:', err)
    alert('回复发表失败，请重试')
  } finally {
    submittingComment.value = false
  }
}

// 辅助函数：根据ID查找评论
const findCommentById = (comments, id) => {
  for (let comment of comments) {
    if (comment.id === id) {
      return comment
    }
    if (comment.replies) {
      const found = comment.replies.find(reply => reply.id === id)
      if (found) return found
    }
  }
  return null
}

onMounted(() => {
  fetchArticleDetail()
  window.addEventListener('scroll', onScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll)
})
</script>

<style scoped>
.nav-gap { height: 62px; }

.wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 36px;
}

.loading-container,
.error-container {
  min-height: 80vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 40px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #E8DCC8;
  border-top: 4px solid #C0392B;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-text,
.error-text {
  font-size: 16px;
  color: #6B5744;
  margin-bottom: 16px;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.art-detail-hero {
  background: #1A1008;
  padding: 64px 0 56px;
  position: relative;
  overflow: hidden;
}

.art-detail-hero::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 80px;
  background: linear-gradient(to bottom, transparent, #FBF6EC);
}

.art-detail-hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1A1008 0%, #3D0E08 40%, #6B1A14 70%, #1A1008 100%);
  opacity: 0.85;
}

.art-detail-hero-img {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #3A0A06, #8B1A14, #C0392B, #D4A017);
  opacity: 0.35;
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
}

.art-detail-hero-pos {
  position: relative;
  z-index: 1;
}

.art-breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  margin-bottom: 24px;
  letter-spacing: 1px;
}

.art-breadcrumb span {
  color: rgba(255, 255, 255, 0.3);
}

.art-breadcrumb a {
  color: rgba(255, 255, 255, 0.55);
  cursor: pointer;
  transition: color 0.2s;
}

.art-breadcrumb a:hover {
  color: #D4A017;
}

.art-detail-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.art-detail-tag {
  background: rgba(212, 160, 23, 0.12);
  color: #D4A017;
  border: 1px solid rgba(212, 160, 23, 0.25);
  font-size: 11px;
  padding: 4px 14px;
  border-radius: 2px;
  letter-spacing: 1px;
}

.art-detail-h1 {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: clamp(28px, 4vw, 46px);
  color: #fff;
  letter-spacing: 5px;
  line-height: 1.3;
  margin-bottom: 16px;
}

.art-detail-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.55);
  line-height: 1.9;
  max-width: 620px;
  margin-bottom: 32px;
}

.art-detail-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.art-meta-avt {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid rgba(212, 160, 23, 0.35);
  flex-shrink: 0;
  overflow: hidden;
}

.art-meta-avt-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 50%;
}

.art-meta-avt-placeholder {
  background: linear-gradient(135deg, #C0392B, #D4A017);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-family: 'ZCOOL XiaoWei', serif;
}

.art-meta-avt:hover {
  opacity: 0.8;
}

.art-meta-authr:hover {
  text-decoration: underline;
}

.art-meta-authr {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.85);
}

.art-meta-date {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  margin-top: 3px;
}

.art-meta-div {
  width: 1px;
  height: 32px;
  background: rgba(255, 255, 255, 0.1);
}

.art-meta-stat {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);
  display: flex;
  align-items: center;
  gap: 5px;
}

.art-body-layout {
  display: grid;
  grid-template-columns: 1fr 260px;
  gap: 48px;
  padding: 56px 0 72px;
  align-items: start;
}

.art-body-main {
 position: relative;
}

.art-content {
  font-size: 15.5px;
  line-height: 2.0;
  color: #1A1008;
  background: #fff;
  padding: 40px 44px;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  box-shadow: 0 2px 16px rgba(26, 16, 8, 0.08);
  position: relative;
}

.art-content.content-blur {
  max-height: 500px;
  overflow: hidden;
}

.art-content h2 {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 22px;
  letter-spacing: 3px;
  color: #1A1008;
  margin: 36px 0 16px;
  padding-left: 14px;
  border-left: 3px solid #C0392B;
}

.art-content h3 {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 17px;
  letter-spacing: 2px;
  color: #1A1008;
  margin: 28px 0 12px;
}

.art-content p {
  margin-bottom: 22px;
  color: #6B5744;
}

.art-content p:first-child::first-letter {
  font-family: 'Ma Shan Zheng', serif;
  font-size: 52px;
  float: left;
  line-height: 0.85;
  margin-right: 10px;
  color: #C0392B;
  padding-top: 4px;
}

.art-content blockquote {
  border-left: 3px solid #D4A017;
  padding: 12px 20px;
  background: #F5EDD6;
  border-radius: 0 3px 3px 0;
  margin: 24px 0;
  color: #6B5744;
  font-style: italic;
}

.art-content hr {
  border: none;
  border-top: 1px solid #EAD8BE;
  margin: 32px 0;
}

.login-mask {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 280px;
  background: linear-gradient(to bottom, transparent 0%, #FBF6EC 40%);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 40px;
  z-index: 10;
}

.login-mask-inner {
  text-align: center;
  padding: 30px 40px;
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  box-shadow: 0 8px 36px rgba(26, 16, 8, 0.14);
}

.login-mask-icon {
  font-size: 40px;
  margin-bottom: 12px;
}

.login-mask-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 20px;
  letter-spacing: 3px;
  color: #1A1008;
  margin-bottom: 8px;
}

.login-mask-desc {
  font-size: 13px;
  color: #6B5744;
  margin-bottom: 20px;
}

.login-mask-tip {
  font-size: 12px;
  color: rgba(107, 87, 68, 0.5);
  margin-top: 12px;
}

.login-mask-tip a {
  color: #C0392B;
  cursor: pointer;
}

.login-mask-tip a:hover {
  text-decoration: underline;
}

.art-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 28px 0;
  margin-top: 8px;
}

.art-action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.art-action-ico {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  border: 1.5px solid #D4B896;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  background: #fff;
  transition: all 0.2s;
}

.art-action-ico-img {
  width: 24px;
  height: 24px;
  object-fit: contain;
  transition: all 0.2s;
}

.art-action-btn:hover .art-action-ico {
  border-color: #C0392B;
  color: #C0392B;
}

.art-action-btn:hover .art-action-ico-img {
  transform: scale(1.1);
}

.art-action-btn.liked .art-action-ico {
  background: #C0392B;
  border-color: #C0392B;
  color: #fff;
}

.art-action-lbl {
  font-size: 11px;
  color: #4A3500;
  letter-spacing: 1px;
}

/* 语音按钮样式 */
.voice-btn {
  flex-direction: row;
  gap: 8px;
  padding: 8px 16px;
  background: #C0392B;
  border: 1px solid #C0392B;
  border-radius: 20px;
  transition: all 0.3s;
}

.voice-btn:hover {
  background: #E04B3A;
  border-color: #E04B3A;
}

.voice-btn.playing {
  background: #C0392B;
  border-color: #D4A017;
  animation: pulse 1.5s infinite;
  box-shadow: 0 0 0 2px rgba(212, 160, 23, 0.3);
}

.voice-icon {
  width: 20px;
  height: 20px;
  transition: transform 0.3s;
  filter: brightness(0) invert(1); /* 使图标变为白色 */
}

.voice-btn.playing .voice-icon {
  animation: shake 0.5s infinite;
}

.voice-btn .art-action-lbl {
  color: #FFFFFF;
}

.voice-dot {
  width: 12px;
  height: 12px;
  background: #D4A017;
  border-radius: 50%;
  animation: dot-pulse 1.5s infinite;
}

@keyframes dot-pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.7;
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(212, 160, 23, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(212, 160, 23, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(212, 160, 23, 0);
  }
}

@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-2px);
  }
  75% {
    transform: translateX(2px);
  }
}

.comments {
  margin-top: 8px;
}

.comments-head {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 18px;
  letter-spacing: 3px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.comments-count {
  font-size: 12px;
  color: rgba(107, 87, 68, 0.5);
  letter-spacing: 1px;
  padding: 3px 10px;
  background: #EAD8BE;
  border-radius: 20px;
}

.comment-box {
  margin-bottom: 28px;
}

.comment-input {
  width: 100%;
  padding: 14px 18px;
  border: 1px solid #D4B896;
  border-radius: 3px;
  background: #fff;
  font-size: 13.5px;
  color: #1A1008;
  resize: vertical;
  min-height: 90px;
  outline: none;
  transition: border-color 0.2s;
}

.comment-input:focus {
  border-color: #C0392B;
}

.comment-input::placeholder {
  color: rgba(107, 87, 68, 0.5);
}

.comment-box-foot {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.comment-item {
  display: flex;
  gap: 14px;
  padding: 18px 0;
  border-bottom: 1px solid #EAD8BE;
}

.comment-item:last-child {
  border-bottom: none;
}

.cmt-avt-img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 2px;
  object-fit: cover;
  cursor: pointer;
  transition: opacity 0.2s;
}

.cmt-avt-img:hover {
  opacity: 0.8;
}

.cmt-avt-placeholder {
  background: linear-gradient(135deg, #C0392B, #D4A017);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  font-family: 'ZCOOL XiaoWei', serif;
}

.cmt-right {
  flex: 1;
}

.cmt-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.cmt-name {
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: color 0.2s;
}

.cmt-name:hover {
  color: #C0392B;
}

.cmt-date {
  font-size: 11px;
  color: rgba(107, 87, 68, 0.5);
}

.cmt-body {
  font-size: 14px;
  color: #6B5744;
  line-height: 1.85;
}

.cmt-actions {
  display: flex;
  gap: 14px;
  margin-top: 10px;
}

.cmt-action {
  font-size: 11px;
  color: rgba(107, 87, 68, 0.5);
  cursor: pointer;
  letter-spacing: 1px;
  transition: color 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.cmt-action:hover {
  color: #C0392B;
}

/* 回复表单样式 */
.reply-form {
  margin-top: 16px;
  padding: 16px;
  background: #FBF6EC;
  border-radius: 6px;
  border-left: 3px solid #C0392B;
}

.replying-to-info {
  font-size: 12px;
  color: #8B7355;
  margin-bottom: 8px;
  font-weight: 500;
}

.reply-input {
  width: 100%;
  min-height: 80px;
  padding: 12px 16px;
  border: 1px solid #D4B896;
  border-radius: 3px;
  resize: vertical;
  font-size: 13.5px;
  line-height: 1.5;
  margin-bottom: 12px;
  outline: none;
  transition: border-color 0.2s;
}

.reply-input:focus {
  border-color: #C0392B;
}

.reply-input::placeholder {
  color: rgba(107, 87, 68, 0.5);
}

.reply-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 点赞图标样式 */
.like-icon {
  width: 14px;
  height: 14px;
  vertical-align: middle;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.cmt-action:hover .like-icon {
  transform: scale(1.2);
  filter: brightness(1.2);
}

.cmt-action:active .like-icon {
  transform: scale(0.9);
}

/* 点赞动画 */
@keyframes like-pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.3);
  }
  100% {
    transform: scale(1);
  }
}

.like-icon.liked {
  animation: like-pulse 0.4s ease-in-out;
}

/* 点赞成功后的心跳效果 */
@keyframes heartbeat {
  0% {
    transform: scale(1);
  }
  25% {
    transform: scale(1.15);
  }
  50% {
    transform: scale(1);
  }
  75% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

.cmt-action:active .like-icon.liked {
  animation: heartbeat 0.6s ease-in-out;
}

/* 回复列表样式 */
.replies-list {
  margin-top: 16px;
  margin-left: 50px;
  border-left: 2px solid #EAD8BE;
  padding-left: 20px;
}

.reply-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #EAD8BE;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-avt-img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: contain;
  flex-shrink: 0;
  margin-top: 2px;
  margin-right: 12px;
  cursor: pointer;
}

.reply-avt-placeholder {
  background: linear-gradient(135deg, #C0392B, #D4A017);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 14px;
  font-family: 'ZCOOL XiaoWei', serif;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 2px;
  margin-right: 12px;
  border: 1px solid rgba(212, 160, 23, 0.3);
  cursor: pointer;
}

.reply-avt-img:hover,
.reply-avt-placeholder:hover {
  opacity: 0.8;
}

.reply-right {
  flex: 1;
}

.reply-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.reply-name {
  font-size: 12px;
  font-weight: 600;
  color: #1A1008;
}

.reply-date {
  font-size: 10px;
  color: rgba(107, 87, 68, 0.5);
}

.reply-body {
  font-size: 13px;
  line-height: 1.8;
  color: #6B5744;
  margin-bottom: 10px;
}

.reply-to-tag {
  color: #8B7355;
  font-weight: 500;
  margin-right: 4px;
}

.reply-actions {
  display: flex;
  gap: 14px;
}

.reply-action {
  font-size: 10px;
  color: rgba(107, 87, 68, 0.5);
  cursor: pointer;
  letter-spacing: 1px;
  transition: color 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.reply-action:hover {
  color: #C0392B;
}

.cmt-replies {
  margin-left: 50px;
  margin-top: 4px;
}

.cmt-reply {
  background: #FBF6EC;
  border-radius: 3px;
  padding: 12px 16px;
  margin-bottom: 8px;
}

.cmt-reply-meta {
  font-size: 11px;
  color: rgba(107, 87, 68, 0.5);
  margin-bottom: 6px;
}

.cmt-reply-body {
  font-size: 13px;
  color: #6B5744;
  line-height: 1.8;
}

.comments-guest {
  margin-top: 8px;
}

.guest-prompt {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 24px;
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
}

.guest-icon {
  font-size: 28px;
}

.guest-text {
  font-size: 14px;
  color: #6B5744;
}

.art-sidebar {
  position: sticky;
  top: calc(62px + 20px);
}

.toc {
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 20px;
  position: sticky;
  top: calc(62px + 20px);
}

.toc-head {
  padding: 14px 18px;
  border-bottom: 1px solid #EAD8BE;
  display: flex;
  align-items: center;
  gap: 8px;
}

.toc-head-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 14px;
  letter-spacing: 2px;
}

.toc-list {
  padding: 8px 0;
  max-height: 60vh;
  overflow-y: auto;
}

.toc-item {
  display: block;
  padding: 8px 18px;
  font-size: 13px;
  color: #6B5744;
  cursor: pointer;
  transition: all 0.2s;
  border-left: 2px solid transparent;
  line-height: 1.5;
}

.toc-item.level-2 {
  padding-left: 28px;
}

.toc-item.level-3 {
  padding-left: 40px;
  font-size: 12px;
}

.toc-item:hover,
.toc-item.active {
  color: #C0392B;
  border-left-color: #C0392B;
  background: rgba(192, 57, 43, 0.10);
}

.related-art {
  margin-top: 4px;
}

.related-art-empty {
  padding: 32px 16px;
  text-align: center;
}

.related-art-empty-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.related-art-empty-text {
  font-size: 13px;
  color: #6B5744;
}

.related-art-item {
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
  user-select: none;
}

.related-art-item:hover {
  background: #FBF6EC;
}

.related-art-item:active {
  background: #F5EDE0;
}

.related-art-img {
  width: 60px;
  height: 50px;
  border-radius: 3px;
  overflow: hidden;
  flex-shrink: 0;
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
  pointer-events: none;
}

.related-art-title {
  font-size: 13px;
  color: #1A1008;
  line-height: 1.5;
  flex: 1;
  transition: color 0.2s;
  pointer-events: none;
}

.related-art-item:hover .related-art-title {
  color: #C0392B;
}

.widget {
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  overflow: hidden;
}

.widget-head {
  padding: 14px 20px;
  border-bottom: 1px solid #EAD8BE;
  display: flex;
  align-items: center;
  gap: 8px;
}

.widget-head-dot {
  width: 6px;
  height: 6px;
  background: #C0392B;
  border-radius: 50%;
  flex-shrink: 0;
}

.widget-head-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 15px;
  letter-spacing: 2px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 24px;
  border: none;
  font-family: 'Noto Serif SC', serif;
  font-size: 13px;
  letter-spacing: 2px;
  border-radius: 3px;
  transition: all 0.2s ease;
  white-space: nowrap;
  cursor: pointer;
}

.btn-v {
  background: #C0392B;
  color: #fff;
}

.btn-v:hover {
  background: #E04B3A;
  box-shadow: 0 4px 18px rgba(192, 57, 43, 0.35);
  transform: translateY(-1px);
}

.btn-ol {
  background: transparent;
  color: #C0392B;
  border: 1.5px solid #C0392B;
}

.btn-ol:hover {
  background: rgba(192, 57, 43, 0.10);
}

.btn-sm {
  padding: 6px 16px;
  font-size: 12px;
  letter-spacing: 1px;
}

.btn-lg {
  padding: 14px 36px;
  font-size: 15px;
}

.av1 {
  background: linear-gradient(135deg, #8B1A14, #D4A017);
}

.av2 {
  background: linear-gradient(135deg, #1B4A40, #4AA08A);
}

.av3 {
  background: linear-gradient(135deg, #4A0A1A, #8B3A5A);
}

.av4 {
  background: linear-gradient(135deg, #1A1A4A, #2C5FBD);
}

.img-yuju {
  background: linear-gradient(135deg, #6B0F0F 0%, #C0392B 55%, #D4A017 100%);
}

.img-feiyi {
  background: linear-gradient(135deg, #2A0010 0%, #7A1040 50%, #C05080 100%);
}

.img-yueyue {
  background: linear-gradient(135deg, #051525 0%, #0A4070 50%, #2A80B0 100%);
}

.stat-icon {
  width: 16px;
  height: 16px;
  margin-right: 4px;
  vertical-align: middle;
}

/* 作者卡片样式 */
.art-author-card {
  background: #fff;
  border: 1px solid #D4B896;
  border-radius: 6px;
  padding: 24px;
  margin-top: 8px;
  display: flex;
  gap: 20px;
  cursor: pointer;
  box-shadow: 0 2px 16px rgba(26, 16, 8, 0.08);
  transition: all 0.25s ease;
}

.art-author-card:hover {
  box-shadow: 0 8px 36px rgba(26, 16, 8, 0.14);
  transform: translateY(-3px);
}

.art-author-avt {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  flex-shrink: 0;
  border: 3px solid #D4B896;
  overflow: hidden;
}

.art-author-avt-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.art-author-avt-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #8B1A14, #D4A017);
  color: #fff;
  font-size: 32px;
  font-family: 'ZCOOL XiaoWei', serif;
}

.art-author-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.art-author-name {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 18px;
  letter-spacing: 2px;
  color: #1A1008;
  margin-bottom: 4px;
}

.art-author-badge {
  font-size: 11px;
  letter-spacing: 2px;
  color: #D4A017;
  background: rgba(212, 160, 23, 0.14);
  display: inline-block;
  padding: 3px 10px;
  border-radius: 2px;
  margin-bottom: 10px;
  align-self: flex-start;
}

.art-author-bio {
  font-size: 13.5px;
  color: #6B5744;
  line-height: 1.8;
}

.art-author-stats {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: rgba(107, 87, 68, 0.7);
}

.art-author-actions {
  display: flex;
  gap: 8px;
  align-self: center;
}

.art-author-actions .btn {
  flex-shrink: 0;
}

/* Markdown内容样式 */
.art-content :deep(h2) {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 20px;
  letter-spacing: 3px;
  color: #1A1008;
  margin: 28px 0 14px;
  padding-left: 12px;
  border-left: 3px solid #C0392B;
}

.art-content :deep(h3) {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 18px;
  letter-spacing: 2px;
  color: #1A1008;
  margin: 24px 0 12px;
  padding-left: 10px;
  border-left: 2px solid #D4A017;
}

.art-content :deep(p) {
  font-size: 15px;
  line-height: 2.0;
  color: #6B5744;
  margin-bottom: 18px;
}

/* 首字下沉效果 - 只在第一个段落生效 */
.art-content :deep(div > p:first-of-type::first-letter) {
  font-family: 'Ma Shan Zheng', serif;
  font-size: 52px;
  float: left;
  line-height: .85;
  margin-right: 10px;
  color: #C0392B;
  padding-top: 4px;
  font-weight: bold;
}

.art-content :deep(blockquote) {
  border-left: 3px solid #D4A017;
  padding: 12px 20px;
  background: #F5F5DC;
  border-radius: 0 8px 8px 0;
  margin: 24px 0;
  color: #6B5744;
  font-style: italic;
}

.art-content strong {
  color: #C0392B;
  font-weight: 600;
}

.art-content ul,
.art-content ol {
  margin-bottom: 18px;
  padding-left: 20px;
  color: #6B5744;
}

.art-content li {
  font-size: 15px;
  line-height: 1.8;
  margin-bottom: 8px;
}

.art-content blockquote {
  border-left: 4px solid #D4A017;
  padding-left: 16px;
  margin: 20px 0;
  color: #8B7355;
  font-style: italic;
}

.art-content code {
  background-color: #f5f5f5;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

.art-content pre {
  background-color: #f5f5f5;
  padding: 16px;
  border-radius: 5px;
  overflow-x: auto;
  margin-bottom: 18px;
}

.art-content pre code {
  background-color: transparent;
  padding: 0;
}
</style>