<template>
  <div class="page active">
    <!-- 作者头部 -->
    <div class="author-hero">
      <!-- 封面照片 -->
      <div 
        class="author-cover" 
        :class="{ 'expanded': isCoverExpanded }"
        :style="coverStyle"
        @click="toggleCoverExpand"
      >
        <div class="author-cover-overlay"></div>
        <div class="author-cover-char">粤</div>
        <div class="cover-expand-btn">
          {{ isCoverExpanded ? '收起' : '展开' }}
        </div>
        <!-- 更换封面按钮（仅当前用户可见） -->
        <div v-if="isCurrentUser" class="cover-upload-btn" @click.stop="triggerCoverUpload">
          <input 
            type="file" 
            ref="coverInput" 
            style="display: none" 
            accept="image/*"
            @change="handleCoverUpload"
          />
          <span>📷 更换封面</span>
        </div>
      </div>
      <div class="author-hero-bottom">
        <div class="wrap">
          <div class="author-avt-wrap">
            <div class="author-avt-big" :style="avatarStyle" @click="isCurrentUser && triggerAvatarUpload()">
              <img v-if="user?.avatar && !avatarError" :src="user.avatar" @error="avatarError = true" />
              <span v-else>{{ user?.nickname?.charAt(0) || user?.username?.charAt(0) || '?' }}</span>
              <!-- 更换头像遮罩（仅当前用户可见） -->
              <div v-if="isCurrentUser" class="avatar-upload-overlay">
                <span>📷</span>
              </div>
              <input 
                type="file" 
                ref="avatarInput" 
                style="display: none" 
                accept="image/*"
                @change="handleAvatarUpload"
              />
            </div>
          </div>
          <div class="author-info">
            <div>
              <h1 class="author-name">{{ user?.nickname || user?.username || '未知用户' }}</h1>
              <div class="author-title-badge">
                <span>{{ user?.role === 'admin' ? '管理员' : '文化创作者' }}</span>
              </div>
              <p class="author-bio">{{ user?.bio || '暂无个人简介' }}</p>
              <div class="author-stats">
                <div>
                  <div class="author-stat-n">{{ articles.length }}</div>
                  <div class="author-stat-l">文章</div>
                </div>
                <div>
                  <div class="author-stat-n">{{ formatNumber(totalLikes) }}</div>
                  <div class="author-stat-l">获赞</div>
                </div>
                <div class="stat-item" @click="toggleFollowers">
                  <div class="author-stat-n">{{ formatNumber(user?.followerCount || 0) }}</div>
                  <div class="author-stat-l">粉丝</div>
                  <div v-if="showFollowers" class="stat-popup">
                    <div class="popup-title">粉丝</div>
                    <div class="popup-list">
                      <div v-if="followers.length === 0" class="empty-popup">暂无粉丝</div>
                      <div v-for="follower in followers" :key="follower.userId" class="popup-item" @click="goToUserProfile(follower.userId)">
                        <img :src="follower.avatar" :alt="follower.nickname" class="popup-avatar" />
                        <span class="popup-name">{{ follower.nickname || follower.username }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="stat-item" @click="toggleFollowing">
                  <div class="author-stat-n">{{ formatNumber(user?.followingCount || 0) }}</div>
                  <div class="author-stat-l">关注</div>
                  <div v-if="showFollowing" class="stat-popup">
                    <div class="popup-title">关注</div>
                    <div class="popup-list">
                      <div v-if="following.length === 0" class="empty-popup">暂无关注</div>
                      <div v-for="item in following" :key="item.userId" class="popup-item" @click="goToUserProfile(item.userId)">
                        <img :src="item.avatar" :alt="item.nickname" class="popup-avatar" />
                        <span class="popup-name">{{ item.nickname || item.username }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="author-actions">
              <button class="btn btn-v" @click="toggleFollow">
                {{ isFollowing ? '已关注' : '关注' }}
              </button>
              <button v-if="!isCurrentUser" class="btn btn-ghost" @click="sendMessage">私信</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 作者内容区 -->
    <div class="author-body">
      <div class="wrap">
        <!-- 标签页 -->
        <div class="author-tabs">
          <button :class="['author-tab', { on: activeTab === 'articles' }]" @click="activeTab = 'articles'">文章</button>
          <button :class="['author-tab', { on: activeTab === 'collections' }]" @click="activeTab = 'collections'">收藏</button>
          <button :class="['author-tab', { on: activeTab === 'about' }]" @click="activeTab = 'about'">关于</button>
        </div>

        <!-- 文章列表 -->
        <div v-if="activeTab === 'articles'">
          <div v-if="articles.length > 0">
            <div class="list-art" v-for="article in articles" :key="article.articleId || article.id" @click="goToArticle(article.articleId || article.id)">
              <div class="list-art-img">
                <img v-if="article.coverImage" :src="fixImagePath(article.coverImage)" :alt="article.title" class="list-art-img-in" />
                <div v-else class="list-art-img-in" :class="article.imgClass || 'img-yuju'"></div>
              </div>
              <div class="list-art-body">
                <div class="list-art-hdr">
                  <span class="tag tag-v">{{ article.category }}</span>
                </div>
                <h3 class="list-art-title">{{ article.title }}</h3>
                <p class="list-art-exc">{{ article.excerpt }}</p>
                <div class="list-art-foot">
                  <div class="list-art-avt" :style="avatarStyle"></div>
                  <div class="list-art-authr">{{ user?.nickname || user?.username }}</div>
                  <div class="list-art-stats">
                    <span class="list-art-stat">📅 {{ article.date }}</span>
                    <span class="list-art-stat">👁️ {{ article.views }}</span>
                    <span class="list-art-stat">❤️ {{ article.likes }}</span>
                    <span class="list-art-stat">💬 {{ article.comments }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 分页 -->
            <div class="pagination" v-if="totalPages > 1">
              <button class="pg-btn" :disabled="currentPage === 1" @click="currentPage--">←</button>
              <button 
                v-for="page in totalPages" 
                :key="page"
                :class="['pg-btn', { on: currentPage === page }]"
                @click="currentPage = page"
              >{{ page }}</button>
              <button class="pg-btn" :disabled="currentPage === totalPages" @click="currentPage++">→</button>
            </div>
          </div>
          <div v-else class="empty-state">
            <div class="empty-icon">📝</div>
            <div class="empty-title">暂无文章</div>
            <div class="empty-desc">该用户还没有发布任何文章</div>
          </div>
        </div>

        <!-- 收藏页面 -->
        <div v-else-if="activeTab === 'collections'" class="empty-state">
          <div class="empty-icon">📚</div>
          <div class="empty-title">暂无收藏</div>
          <div class="empty-desc">收藏的文章会显示在这里</div>
        </div>

        <!-- 关于页面 -->
        <div v-else-if="activeTab === 'about'" class="about-content">
          <div class="card" style="padding: 32px;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;">
              <h3 class="sec-title" style="margin: 0;">关于作者</h3>
              <button v-if="authStore.user && route.params.id && (String(authStore.user.userId) === String(route.params.id) || String(authStore.user.id) === String(route.params.id))" class="btn btn-ghost" @click="toggleEditMode">
                {{ isEditing ? '取消' : '编辑资料' }}
              </button>
            </div>
            
            <!-- 编辑模式 -->
            <div v-if="isEditing" class="about-info">
              <div class="about-item">
                <span class="about-label">用户名</span>
                <input type="text" v-model="editForm.username" class="form-input" placeholder="请输入用户名" />
              </div>
              <div class="about-item">
                <span class="about-label">昵称</span>
                <input type="text" v-model="editForm.nickname" class="form-input" placeholder="请输入昵称" />
              </div>
              <div class="about-item">
                <span class="about-label">个人简介</span>
                <textarea v-model="editForm.bio" class="form-input" rows="3" placeholder="请输入个人简介"></textarea>
              </div>
              <div class="about-item" style="border: none; margin-top: 16px;">
                <button class="btn btn-v" @click="saveProfile">保存修改</button>
              </div>
            </div>
            
            <!-- 查看模式 -->
            <div v-else class="about-info">
              <div class="about-item">
                <span class="about-label">用户名</span>
                <span class="about-value">{{ user?.username }}</span>
              </div>
              <div class="about-item">
                <span class="about-label">昵称</span>
                <span class="about-value">{{ user?.nickname || '未设置' }}</span>
              </div>
              <div class="about-item">
                <span class="about-label">加入时间</span>
                <span class="about-value">{{ formatDate(user?.createTime) }}</span>
              </div>
              <div class="about-item">
                <span class="about-label">个人简介</span>
                <span class="about-value">{{ user?.bio || '暂无简介' }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const API_BASE_URL = '/api'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const activeTab = ref('articles')
const currentPage = ref(1)
const isFollowing = ref(false)
const avatarError = ref(false)
const user = ref(null)
const articles = ref([])
const pageSize = 5
const isCoverExpanded = ref(false)
const showFollowers = ref(false)
const showFollowing = ref(false)
const followers = ref([])
const following = ref([])

const userId = computed(() => route.params.id)

// 封面照片样式 - 使用fixCoverPath处理路径
const coverStyle = computed(() => {
  if (user.value?.coverPhoto) {
    const coverPhotoUrl = fixCoverPath(user.value.coverPhoto)
    return {
      backgroundImage: `url(${coverPhotoUrl})`
    }
  }
  // 默认渐变背景
  return {
    background: 'linear-gradient(135deg, #1A0A04 0%, #4A1010 30%, #8B1A14 60%, #C0392B 80%, #D4A017 100%)'
  }
})

// 头像样式
const avatarStyle = computed(() => {
  if (user.value?.avatar && !avatarError.value) {
    return {
      backgroundImage: `url(${user.value.avatar})`,
      backgroundSize: 'cover',
      backgroundPosition: 'center'
    }
  }
  // 默认渐变背景
  return {
    background: 'linear-gradient(135deg, #C0392B, #D4A017)'
  }
})

// 统计数据
const totalViews = computed(() => {
  return articles.value.reduce((sum, article) => sum + (article.views || 0), 0)
})

const totalLikes = computed(() => {
  return articles.value.reduce((sum, article) => sum + (article.likes || 0), 0)
})

const totalPages = computed(() => {
  return Math.ceil(articles.value.length / pageSize)
})

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toLocaleString()
}

// 修复封面照片路径的辅助函数 - 仿照文章封面的fixImagePath
const fixCoverPath = (path) => {
  if (!path) return ''
  // 移除路径中的空格
  let fixedPath = path.replace(/\s+/g, '')
  // 已经是完整路径，直接返回
  if (fixedPath.startsWith('/uploads/')) {
    return fixedPath
  }
  // 确保路径以/profile_covers开头
  if (!fixedPath.startsWith('/profile_covers')) {
    fixedPath = `/profile_covers/${fixedPath}`
  }
  // 将.jpg扩展名替换为.png
  fixedPath = fixedPath.replace(/\.jpg$/i, '.png')
  return fixedPath
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '未知'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

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

// 获取用户数据
const fetchUserData = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/users/${userId.value}`)
    if (response.ok) {
      user.value = await response.json()
      if (authStore.token && authStore.user?.userId !== Number(userId.value)) {
        await checkFollowStatus()
      }
    } else {
      // 使用默认数据
      user.value = getDefaultUser(userId.value)
    }
  } catch (error) {
    console.error('获取用户数据失败:', error)
    user.value = getDefaultUser(userId.value)
  }
}

const checkFollowStatus = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/users/${authStore.user.userId}/following`, {
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      }
    })
    if (response.ok) {
      const followingList = await response.json()
      isFollowing.value = followingList.some(item => item.userId === Number(userId.value))
    }
  } catch (error) {
    console.error('检查关注状态失败:', error)
  }
}

// 获取用户文章
const fetchUserArticles = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/articles/user/${userId.value}`)
    if (response.ok) {
      const data = await response.json()
      // 处理分页数据
      articles.value = data.content || data
    } else {
      articles.value = getDefaultArticles()
    }
  } catch (error) {
    console.error('获取文章失败:', error)
    articles.value = getDefaultArticles()
  }
}

// 默认用户数据
const getDefaultUser = (id) => {
  const users = [
    { id: 1, username: '管理员', nickname: '系统管理员', bio: '粤韵志管理员', avatar: null, coverPhoto: '/profile_covers/user1_cover.png', role: 'admin', createTime: '2024-01-01' },
    { id: 2, username: 'chenzhiyuan', nickname: '陈志远', bio: '粤剧文化研究者，岭南文化学者。致力于传承和弘扬广东传统文化，研究领域包括粤剧、粤菜、粤语等。', avatar: '/avatars/av1.png', coverPhoto: '/profile_covers/user2_cover.png', role: 'user', createTime: '2024-01-15' },
    { id: 3, username: 'linshao', nickname: '林少华', bio: '岭南文化学者，青年粤剧编剧', avatar: '/avatars/av2.png', coverPhoto: '/profile_covers/user3_cover.png', role: 'user', createTime: '2024-02-01' },
    { id: 4, username: 'zhangming', nickname: '张明', bio: '美食文化评论家', avatar: '/avatars/av3.png', coverPhoto: '/profile_covers/user4_cover.png', role: 'user', createTime: '2024-02-15' },
    { id: 5, username: 'wangfang', nickname: '王芳', bio: '粤语文化研究者', avatar: '/avatars/av4.png', coverPhoto: '/profile_covers/user5_cover.png', role: 'user', createTime: '2024-03-01' },
    { id: 6, username: 'lihua', nickname: '李华', bio: '民俗文化学者', avatar: '/avatars/av5.png', coverPhoto: '/profile_covers/user6_cover.png', role: 'user', createTime: '2024-03-15' }
  ]
  // 确保id是数字类型
  const numericId = Number(id)
  // 如果id有效，返回对应的用户，否则返回一个随机用户
  return users.find(u => u.id === numericId) || users[Math.floor(Math.random() * users.length)]
}

// 默认文章数据
const getDefaultArticles = () => {
  return [
    {
      id: 1,
      title: '粤剧艺术的传承与创新',
      excerpt: '粤剧作为岭南文化的瑰宝，如何在现代社会中保持活力并创新发展？本文从历史、现状和未来三个维度进行探讨...',
      category: '粤剧艺术',
      date: '2024-03-10',
      views: 1280,
      likes: 156,
      comments: 23,
      imgClass: 'img-yuju'
    },
    {
      id: 2,
      title: '粤菜的文化内涵与制作技艺',
      excerpt: '粤菜以其精致的制作工艺和独特的口味闻名于世，背后蕴含着深厚的文化底蕴...',
      category: '粤菜美食',
      date: '2024-03-05',
      views: 956,
      likes: 128,
      comments: 18,
      imgClass: 'img-yuecai'
    },
    {
      id: 3,
      title: '粤语的历史演变与文化价值',
      excerpt: '粤语不仅是一种语言，更是岭南文化的重要载体，本文追溯粤语的历史演变过程...',
      category: '粤语文化',
      date: '2024-02-28',
      views: 1520,
      likes: 234,
      comments: 31,
      imgClass: 'img-yueyue'
    },
    {
      id: 4,
      title: '岭南民俗的现代传承',
      excerpt: '从传统节日到民间工艺，岭南民俗如何在现代生活中焕发新的生命力...',
      category: '民俗传统',
      date: '2024-02-20',
      views: 892,
      likes: 98,
      comments: 15,
      imgClass: 'img-minshu'
    }
  ]
}

// 关注/取消关注
const toggleFollow = async () => {
  if (!authStore.token) {
    alert('请先登录');
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE_URL}/users/follow/${userId.value}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      }
    });
    
    if (response.ok) {
      const data = await response.json();
      isFollowing.value = data.isFollowing;
      // 更新粉丝数量
      if (user.value) {
        user.value.followerCount = data.followerCount;
      }
    } else {
      const data = await response.json();
      alert(data.error || '操作失败');
    }
  } catch (error) {
    console.error('关注操作失败:', error);
    alert('操作失败，请稍后重试');
  }
}

// 获取粉丝列表
const fetchFollowers = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/users/${userId.value}/followers`);
    if (response.ok) {
      followers.value = await response.json();
    }
  } catch (error) {
    console.error('获取粉丝列表失败:', error);
  }
}

// 获取关注列表
const fetchFollowing = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/users/${userId.value}/following`);
    if (response.ok) {
      following.value = await response.json();
    }
  } catch (error) {
    console.error('获取关注列表失败:', error);
  }
}

// 切换粉丝列表显示
const toggleFollowers = async () => {
  showFollowers.value = !showFollowers.value;
  if (showFollowers.value) {
    await fetchFollowers();
  }
}

// 切换关注列表显示
const toggleFollowing = async () => {
  showFollowing.value = !showFollowing.value;
  if (showFollowing.value) {
    await fetchFollowing();
  }
}

// 跳转到用户个人中心页面
const goToUserProfile = (userId) => {
  // 先关闭弹窗
  showFollowers.value = false;
  showFollowing.value = false;
  // 跳转到用户个人中心页面
  router.push(`/author/${userId}`);
}

// 切换封面展开状态
const toggleCoverExpand = () => {
  isCoverExpanded.value = !isCoverExpanded.value
}

// 发送私信（需互相关注，在消息页校验）
const sendMessage = () => {
  if (!authStore.isLoggedIn) {
    router.push('/login')
    return
  }
  if (isCurrentUser.value) {
    alert('不能给自己发私信')
    return
  }
  router.push({ path: '/messages', query: { peerId: route.params.id } })
}

// 跳转到文章详情
const goToArticle = (id) => {
  // 跳转到文章详情页
  router.push(`/article/${id}`)
}

// 判断是否当前用户
const isCurrentUser = computed(() => {
  if (!authStore.user || !route.params.id) return false
  return String(authStore.user.userId) === String(route.params.id)
})

// 文件输入引用
const avatarInput = ref(null)
const coverInput = ref(null)

// 触发头像上传
const triggerAvatarUpload = () => {
  avatarInput.value?.click()
}

// 触发封面上传
const triggerCoverUpload = () => {
  coverInput.value?.click()
}

// 处理头像上传
const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await fetch(`${API_BASE_URL}/upload/avatar`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      },
      body: formData
    })

    const data = await response.json()

    if (data.success) {
      // 更新本地用户数据
      user.value.avatar = data.avatarUrl
      // 更新store中的用户信息
      authStore.user.avatar = data.avatarUrl
      localStorage.setItem('user', JSON.stringify(authStore.user))
      alert('头像上传成功！')
    } else {
      alert(data.error || '上传失败')
    }
  } catch (error) {
    console.error('上传头像失败:', error)
    alert('上传失败，请稍后重试')
  }
}

// 处理封面上传
const handleCoverUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  const formData = new FormData()
  formData.append('file', file)

  try {
    const response = await fetch(`${API_BASE_URL}/upload/cover`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${authStore.token}`
      },
      body: formData
    })

    const data = await response.json()

    if (data.success) {
      // 更新本地用户数据
      user.value.coverPhoto = data.coverUrl
      // 更新store中的用户信息
      authStore.user.coverPhoto = data.coverUrl
      localStorage.setItem('user', JSON.stringify(authStore.user))
      alert('封面上传成功！')
    } else {
      alert(data.error || '上传失败')
    }
  } catch (error) {
    console.error('上传封面失败:', error)
    alert('上传失败，请稍后重试')
  }
}

// 编辑模式相关
const isEditing = ref(false)
const editForm = ref({
  username: '',
  nickname: '',
  bio: ''
})

// 切换编辑模式
const toggleEditMode = () => {
  if (isEditing.value) {
    // 取消编辑，重置表单
    isEditing.value = false
  } else {
    // 进入编辑模式，初始化表单
    editForm.value = {
      username: user.value?.username || '',
      nickname: user.value?.nickname || '',
      bio: user.value?.bio || ''
    }
    isEditing.value = true
  }
}

// 保存用户资料
const saveProfile = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/users/${userId.value}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authStore.token}`
      },
      body: JSON.stringify({
        username: editForm.value.username,
        nickname: editForm.value.nickname,
        bio: editForm.value.bio
      })
    })

    const data = await response.json()

    if (response.ok) {
      // 更新本地用户数据
      user.value.username = data.username
      user.value.nickname = data.nickname
      user.value.bio = data.bio
      // 更新store中的用户信息
      authStore.user.username = data.username
      authStore.user.nickname = data.nickname
      authStore.user.bio = data.bio
      localStorage.setItem('user', JSON.stringify(authStore.user))
      isEditing.value = false
      alert('资料更新成功！')
    } else {
      alert(data.error || '更新失败')
    }
  } catch (error) {
    console.error('更新资料失败:', error)
    alert('更新失败，请稍后重试')
  }
}

onMounted(async () => {
  // 滚动到页面顶部，确保显示封面和用户名区域
  window.scrollTo({ top: 0, behavior: 'smooth' })
  
  // 检查并更新用户信息
  if (authStore.token && (!authStore.user || !authStore.user.userId)) {
    await authStore.fetchCurrentUser()
  }
  
  fetchUserData()
  fetchUserArticles()
  // 再次滚动到顶部，确保数据加载后仍在顶部
  setTimeout(() => {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }, 100)
  
  // 添加全局点击事件监听器，点击其他区域关闭弹窗
  document.addEventListener('click', handleOutsideClick)
})

onUnmounted(() => {
  // 移除全局点击事件监听器
  document.removeEventListener('click', handleOutsideClick)
})

// 处理点击外部区域关闭弹窗
const handleOutsideClick = (event) => {
  const followersPopup = document.querySelector('.stat-popup')
  const statsItems = document.querySelectorAll('.stat-item')
  
  // 检查点击目标是否在弹窗或触发弹窗的元素之外
  const isClickInsidePopup = followersPopup && followersPopup.contains(event.target)
  const isClickInsideStatsItem = Array.from(statsItems).some(item => item.contains(event.target))
  
  if (!isClickInsidePopup && !isClickInsideStatsItem) {
    showFollowers.value = false
    showFollowing.value = false
  }
}

watch(() => route.params.id, () => {
  // 当路由参数变化时（切换用户），滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
  fetchUserData()
  fetchUserArticles()
  // 再次滚动到顶部，确保数据加载后仍在顶部
  setTimeout(() => {
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }, 100)
})
</script>

<style scoped>
.nav-gap { height: 62px; }

.wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 36px;
}

/* 作者头部区域 */
.author-hero {
  background: #1A1008;
  padding: 0;
  position: relative;
  overflow: visible;
}

/* 封面照片 */
.author-cover {
  height: 200px;
  position: relative;
  overflow: hidden;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  transition: height 0.5s ease-in-out;
  cursor: pointer;
}

/* 展开状态 */
.author-cover.expanded {
  height: 480px;
  background-size: cover;
  background-position: center;
}

/* 展开/收起按钮 */
.cover-expand-btn {
  position: absolute;
  bottom: 16px;
  right: 16px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
  z-index: 10;
}

.cover-expand-btn:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: translateY(-2px);
}

.author-cover-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, rgba(26,16,8,0.1) 0%, rgba(26,16,8,0.4) 100%);
}

.author-cover-char {
  position: absolute;
  right: -10px;
  bottom: -30px;
  font-family: 'Ma Shan Zheng', serif;
  font-size: 280px;
  color: rgba(255, 255, 255, .04);
  line-height: 1;
  user-select: none;
  pointer-events: none;
}

/* 更换封面按钮 */
.cover-upload-btn {
  position: absolute;
  bottom: 16px;
  left: 16px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
}

.cover-upload-btn:hover {
  background: rgba(0, 0, 0, 0.8);
}

/* 更换头像遮罩 */
.avatar-upload-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
}

.avatar-upload-overlay span {
  font-size: 20px;
}

.author-avt-big:hover .avatar-upload-overlay {
  opacity: 1;
}

.author-hero-bottom {
  padding: 0 0 12px;
  position: relative;
}

.author-avt-wrap {
  position: absolute;
  top: -24px;
  left: 0;
}

.author-avt-big {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  border: 3px solid #1A1008;
  box-shadow: 0 4px 20px rgba(0, 0, 0, .4);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  font-family: 'ZCOOL XiaoWei', serif;
  overflow: hidden;
  background: linear-gradient(135deg, #C0392B, #D4A017);
}

.author-avt-big img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 编辑表单样式 */
.form-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #E5DDD4;
  border-radius: 6px;
  font-size: 14px;
  background: #fff;
  color: var(--ink);
}

.form-input:focus {
  outline: none;
  border-color: var(--v);
}

textarea.form-input {
  resize: vertical;
  min-height: 80px;
}

.author-info {
  padding-top: 32px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
}

.author-name {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 20px;
  color: #fff;
  letter-spacing: 2px;
  margin-bottom: 2px;
}

.author-title-badge {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  background: rgba(212, 160, 23, .12);
  border: 1px solid rgba(212, 160, 23, .25);
  color: #D4A017;
  font-size: 9px;
  letter-spacing: 1px;
  padding: 2px 8px;
  border-radius: 2px;
  margin-bottom: 4px;
}

.author-bio {
  font-size: 11px;
  color: rgba(255, 255, 255, .5);
  line-height: 1.4;
  max-width: 500px;
  margin-bottom: 4px;
}

.author-stats {
  display: flex;
  gap: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 255, 255, .08);
}

.author-stat-n {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 20px;
  color: #D4A017;
  line-height: 1;
}

.author-stat-l {
  font-size: 11px;
  letter-spacing: 2px;
  color: rgba(255, 255, 255, .35);
  margin-top: 4px;
}

/* 统计项样式 */
.stat-item {
  position: relative;
  cursor: pointer;
  padding: 0 8px;
  border-radius: 4px;
  transition: background-color 0.2s ease;
}

.stat-item:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

/* 悬浮弹窗样式 */
.stat-popup {
  position: absolute;
  top: 100%;
  left: 50%;
  transform: translateX(-50%);
  margin-top: 8px;
  width: 240px;
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  z-index: 9999;
  padding: 12px;
}

/* 确保统计项有定位上下文 */
.stat-item {
  position: relative;
  z-index: 1;
}

.stat-popup::before {
  content: '';
  position: absolute;
  top: -6px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-bottom: 6px solid #fff;
}

.popup-title {
  font-size: 14px;
  font-weight: 600;
  color: #1A1008;
  margin-bottom: 12px;
  text-align: center;
}

.popup-list {
  max-height: 200px;
  overflow-y: auto;
}

.popup-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #F5F0E6;
}

.popup-item:last-child {
  border-bottom: none;
}

.popup-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 12px;
  object-fit: cover;
}

.popup-name {
  font-size: 13px;
  color: #6B5744;
  flex: 1;
}

.empty-popup {
  text-align: center;
  padding: 20px 0;
  color: #999;
  font-size: 13px;
}

.author-actions {
  display: flex;
  gap: 12px;
  align-self: flex-start;
  margin-top: 8px;
}

/* 按钮样式 */
.btn {
  padding: 10px 24px;
  font-size: 13px;
  letter-spacing: 2px;
  border-radius: 3px;
  cursor: pointer;
  transition: all .2s;
  font-family: 'Noto Serif SC', serif;
  border: none;
}

.btn-v {
  background: #C0392B;
  color: #fff;
}

.btn-v:hover {
  background: #E04B3A;
}

.btn-ghost {
  background: transparent;
  color: #C0392B;
  border: 1px solid #C0392B;
}

.btn-ghost:hover {
  background: rgba(192, 57, 43, 0.1);
  border-color: #C0392B;
}

/* 内容区域 */
.author-body {
  padding: 40px 0 72px;
  background: #FBF6EC;
  min-height: calc(100vh - 400px);
}

.author-tabs {
  display: flex;
  gap: 0;
  border-bottom: 2px solid #EAD8BE;
  margin-bottom: 36px;
}

.author-tab {
  padding: 12px 24px;
  font-size: 13px;
  letter-spacing: 2px;
  color: #6B5744;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all .2s;
  background: none;
  border-left: none;
  border-right: none;
  border-top: none;
  font-family: 'Noto Serif SC', serif;
}

.author-tab:hover {
  color: #C0392B;
}

.author-tab.on {
  color: #C0392B;
  border-bottom-color: #C0392B;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 0;
  color: rgba(107,87,68,0.5);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-title {
  font-size: 16px;
  margin-bottom: 8px;
  color: #6B5744;
}

.empty-desc {
  font-size: 13px;
}

/* 关于页面 */
.about-content {
  max-width: 800px;
}

.card {
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  box-shadow: 0 2px 16px rgba(26,16,8,0.08);
}

.sec-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 20px;
  letter-spacing: 3px;
  color: #1A1008;
}

.about-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.about-item {
  display: flex;
  padding-bottom: 16px;
  border-bottom: 1px solid #EAD8BE;
}

.about-item:last-child {
  border-bottom: none;
}

.about-label {
  width: 100px;
  color: rgba(107,87,68,0.6);
  font-size: 13px;
}

.about-value {
  flex: 1;
  color: #1A1008;
  font-size: 14px;
}

/* 文章列表样式 */
.list-art {
  display: flex;
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 16px rgba(26,16,8,0.08);
  transition: all .25s;
  cursor: pointer;
  margin-bottom: 20px;
}

.list-art:hover {
  box-shadow: 0 4px 24px rgba(26,16,8,0.12);
  transform: translateY(-2px);
}

.list-art-img {
  width: 210px;
  height: 140px;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.list-art-img-in {
  width: 100%;
  height: 100%;
  transition: transform .4s;
  object-fit: cover;
}

.list-art:hover .list-art-img-in {
  transform: scale(1.06);
}

.list-art-body {
  padding: 26px 30px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.list-art-hdr {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.tag {
  padding: 4px 12px;
  font-size: 11px;
  letter-spacing: 1px;
  border-radius: 2px;
}

.tag-v {
  background: rgba(192,57,43,0.1);
  color: #C0392B;
}

.list-art-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 20px;
  letter-spacing: 3px;
  color: #1A1008;
  margin-bottom: 12px;
  line-height: 1.4;
}

.list-art-exc {
  font-size: 13.5px;
  color: #6B5744;
  line-height: 1.85;
  flex: 1;
  margin-bottom: 18px;
}

.list-art-foot {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 14px;
  border-top: 1px solid #EAD8BE;
}

.list-art-avt {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  flex-shrink: 0;
  border: 2px solid #EAD8BE;
  background-size: cover;
  background-position: center;
}

.list-art-authr {
  font-size: 12px;
  color: #6B5744;
  flex: 1;
}

.list-art-stats {
  display: flex;
  gap: 14px;
}

.list-art-stat {
  font-size: 11px;
  color: rgba(107,87,68,0.5);
  display: flex;
  align-items: center;
  gap: 3px;
}

/* 分页样式 */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 40px;
}

.pg-btn {
  width: 34px;
  height: 34px;
  border-radius: 3px;
  border: 1px solid #EAD8BE;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6B5744;
  transition: all .2s;
}

.pg-btn:hover:not(:disabled) {
  background: #C0392B;
  color: #fff;
  border-color: #C0392B;
}

.pg-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pg-btn.on {
  background: #C0392B;
  color: #fff;
  border-color: #C0392B;
}

/* 图片占位渐变 */
.img-yuju {
  background: linear-gradient(135deg, #6B0F0F 0%, #C0392B 55%, #D4A017 100%);
}

.img-yuecai {
  background: linear-gradient(135deg, #8B4513 0%, #D2691E 50%, #F4A460 100%);
}

.img-yueyue {
  background: linear-gradient(135deg, #1B4A40 0%, #2E8B57 50%, #4AA08A 100%);
}

.img-minshu {
  background: linear-gradient(135deg, #4A0A1A 0%, #8B3A5A 50%, #C45C7A 100%);
}

.img-feiyi {
  background: linear-gradient(135deg, #3D1F0A 0%, #8B4F1A 50%, #CD853F 100%);
}

/* 响应式 */
@media (max-width: 768px) {
  .author-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .author-actions {
    align-self: flex-start;
  }
  
  .list-art {
    flex-direction: column;
  }
  
  .list-art-img {
    width: 100%;
    height: 180px;
  }
  
  .author-cover {
    height: 150px;
  }
  
  .author-cover.expanded {
    height: 320px;
  }
}
</style>
