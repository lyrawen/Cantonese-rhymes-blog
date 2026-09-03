<template>
  <div class="page active">
    <div class="nav-gap"></div>
    
    <!-- HERO 区域 - 完全复刻HTML效果 -->
    <section class="hero">
      <div class="hero-grid"></div>
      <div class="hero-bigchar">韵</div>
      <div class="wrap" style="width:100%">
        <div class="hero-inner">
          <div class="hero-left">
            <!-- 眉毛区域 - 完全按照HTML代码复刻 -->
            <div class="hero-eyebrow">
              <span class="hero-eyebrow-dot"></span>
              岭南文化博客平台 · 2026
              <span class="hero-eyebrow-dot"></span>
            </div>
            <h1 class="hero-h1">
              千年
              <em>粤韵</em>
              今朝叙
            </h1>
            <p class="hero-sub">YUE YUN ZHI · CANTONESE CULTURE BLOG</p>
            <p class="hero-desc">
              以岭南文化为核，融粤剧、粤语、粤菜、民俗、非遗于一体的文化博客平台。珠三角文化记忆，连结粤港澳文化认同。
            </p>
            <div class="hero-ctas">
              <button class="btn btn-v btn-lg" @click="$router.push('/explore')">探索文章</button>
              <button class="btn btn-ghost btn-lg" @click="$router.push('/auth')">加入我们</button>
            </div>
            <div class="hero-stats">
              <div>
                <div class="hero-stat-n">1,248</div>
                <div class="hero-stat-l">篇文章</div>
              </div>
              <div>
                <div class="hero-stat-n">386</div>
                <div class="hero-stat-l">位作者</div>
              </div>
              <div>
                <div class="hero-stat-n">8.6万</div>
                <div class="hero-stat-l">月活读者</div>
              </div>
            </div>
          </div>
          <div class="hero-right">
            <div class="card hero-card" @click="goToArticleDetail(featuredArticle?.articleId || 1)">
              <div class="hero-card-img">
                <div class="hero-card-img-inner" :style="{ backgroundImage: `url('${fixImagePath(featuredArticle?.coverImage || '/uploads/covers/yue ju-100.png')}')` }"></div>
                <div class="hero-card-img-grad"></div>
                <div class="hero-card-overlay">
                  <div class="hero-card-overlay-title">{{ featuredArticle?.title || '粤剧·百年回響' }}</div>
                  <div class="hero-card-overlay-sub">YUE JU · CENTENNIAL RESONANCE</div>
                </div>
              </div>
              <div class="hero-card-body">
                <div class="hero-card-meta">
                  <div class="hero-card-avt" style="width:26px;height:26px;border-radius:50%;overflow:hidden;cursor:pointer;" @click="goToUserProfile(featuredArticle?.userId)">
                    <img 
                      v-if="featuredArticle?.userAvatar"
                      :src="fixAvatarPath(featuredArticle.userAvatar)" 
                      :alt="featuredArticle.userNickname"
                      style="width:100%;height:100%;object-fit:cover;"
                    />
                    <div v-else class="av1" style="width:100%;height:100%;"></div>
                  </div>
                  <span class="hero-card-author" style="cursor:pointer;" @click="goToUserProfile(featuredArticle?.userId)">{{ featuredArticle?.userNickname || '陈志远' }} · 粤剧研究员</span>
                  <span class="tag tag-v" style="margin-left:auto">精选</span>
                </div>
                <div class="hero-card-title">{{ featuredArticle?.title || '粤剧百年：从省港大班到当代创新的文化演变史' }}</div>
                <div class="hero-card-exc">{{ featuredArticle?.summary || '探寻粤剧艺术在一个世纪风云变幻中的传承脉络，以及新生代艺术家如何将传统程式融入当代美学……' }}</div>
                <div class="hero-card-foot">
                  <span><img src="/icons/浏览.png" alt="浏览" class="stat-icon" /> {{ featuredArticle?.viewCount || 2840 }}</span>
                  <span><img src="/icons/喜欢2.png" alt="喜欢" class="stat-icon" /> {{ featuredArticle?.likeCount || 186 }}</span>
                  <span><img src="/icons/收藏 -已收藏-copy-copy.png" alt="收藏" class="stat-icon" /> 94</span>
                  <span style="margin-left:auto;font-size:11px;color:#8B5A2B">{{ featuredArticle?.createTime?.substring(0, 10) || '2025-01-18' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 分类导航条 - 完全保持您原有的样式 -->
    <section class="catbar">
      <div class="wrap catbar-inner">
        <button 
          class="catbar-item" 
          :class="{ on: selectedCategory === null }"
          @click="selectedCategory = null"
        >全部</button>
        <button 
          v-for="category in categories" 
          :key="category.categoryId"
          class="catbar-item"
          :class="{ on: selectedCategory === category.categoryId }"
          @click="selectedCategory = category.categoryId"
        >{{ category.categoryName }}</button>
      </div>
    </section>

    <!-- 以下部分完全保持不变 -->
    <section class="wrap" style="padding-top: 48px; padding-bottom: 48px;">
      <div class="sec-head">
        <div>
          <div class="sec-eyebrow">FEATURED</div>
          <div class="sec-title">精选文章</div>
        </div>
        <div class="sec-more" @click="$router.push('/explore')">查看更多 →</div>
      </div>

      <div class="grid-main-side">
        <div class="main-content">
          <div class="grid3">
            <div v-for="article in filteredArticles" :key="article.articleId" class="card art-card" @click="goToArticleDetail(article.articleId)">
              <div class="art-card-img">
                <div class="art-card-img-inner" :style="{ backgroundImage: `url('${fixImagePath(article.coverImage)}')` }"></div>
              </div>
              <div class="art-card-body">
                <div class="art-card-tags">
                  <span class="tag" :class="getTagClass(article.categoryName)">{{ article.categoryName }}</span>
                  <span 
                    v-for="tag in article.tags?.split(',') || []" 
                    :key="tag" 
                    class="tag" 
                    :class="getTagClass(tag)"
                  >{{ tag }}</span>
                </div>
                <div class="art-card-title">{{ article.title }}</div>
                <div class="art-card-exc">{{ article.summary }}</div>
                <div class="art-card-foot">
                  <div class="art-card-avt" @click="goToUserProfile(article.userId)" style="cursor:pointer;">
                    <img 
                      v-if="article.userAvatar"
                      :src="fixAvatarPath(article.userAvatar)" 
                      :alt="article.userNickname"
                      class="art-card-avt-img"
                    />
                  </div>
                  <div class="art-card-authr" @click="goToUserProfile(article.userId)" style="cursor:pointer;">{{ article.userNickname }}</div>
                  <div class="art-card-stats">
                    <span class="art-card-stat"><img src="/icons/浏览.png" alt="浏览" class="stat-icon" /> {{ (article.viewCount / 1000).toFixed(1) }}K</span>
                    <span class="art-card-stat"><img src="/icons/喜欢2.png" alt="喜欢" class="stat-icon" /> {{ article.likeCount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <aside class="sidebar">
          <div class="widget">
            <div class="widget-head">
              <div class="widget-head-dot"></div>
              <div class="widget-head-title">热门文章</div>
            </div>
            <div class="hot-list">
              <div v-for="(article, index) in hotArticles" :key="article.articleId" class="hot-item" @click="goToArticleDetail(article.articleId)">
                <div class="hot-rank" :class="index < 3 ? `hr${index + 1}` : 'hrn'">
                  {{ index + 1 }}
                </div>
                <div>
                  <div class="hot-ttl">{{ article.title }}</div>
                  <div class="hot-vw">👁 {{ (article.viewCount / 1000).toFixed(1) }}K</div>
                </div>
              </div>
            </div>
          </div>

          <div class="widget">
            <div class="widget-head">
              <div class="widget-head-dot"></div>
              <div class="widget-head-title">推荐作者</div>
            </div>
            <div class="author-list">
              <div 
                v-for="author in topAuthors" 
                :key="author.userId"
                class="author-item" 
                @click="goToUserProfile(author.userId)" 
                style="cursor:pointer;"
              >
                <div class="author-avt" style="width:48px;height:48px;border-radius:50%;overflow:hidden;">
                  <img 
                    v-if="author.avatar" 
                    :src="fixAvatarPath(author.avatar)" 
                    :alt="author.nickname"
                    style="width:100%;height:100%;object-fit:cover;"
                  />
                  <div v-else class="av1" style="width:100%;height:100%;"></div>
                </div>
                <div class="author-info">
                  <div class="author-name">{{ author.nickname }}</div>
                  <div class="author-desc">{{ author.bio || '暂无简介' }}</div>
                </div>
                <button class="btn btn-ol btn-sm" @click.stop>关注</button>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </section>

    <section class="newsletter">
      <div class="wrap newsletter-inner">
        <div class="newsletter-content">
          <div class="newsletter-eyebrow">SUBSCRIBE</div>
          <div class="newsletter-title">订阅粤韵志</div>
          <div class="newsletter-desc">获取最新的岭南文化资讯和精选文章</div>
          <div class="newsletter-form">
            <input type="email" class="newsletter-input" placeholder="请输入您的邮箱地址">
            <button class="btn btn-v">订阅</button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const API_BASE_URL = '/api'

// 跳转到用户个人主页
const goToUserProfile = (userId) => {
  if (userId) {
    router.push(`/author/${userId}`)
  }
}

// 修复图片路径的辅助函数
const fixImagePath = (path) => {
  if (!path) return ''
  let fixedPath = path.startsWith('/uploads') ? path : `/uploads${path}`
  fixedPath = fixedPath.replace(/\s+/g, '')
  fixedPath = fixedPath.replace(/\.jpg$/i, '.png')
  return fixedPath
}

// 修复头像路径的辅助函数
const fixAvatarPath = (path) => {
  if (!path) return ''
  if (path.startsWith('/avatars') || path.startsWith('http')) return path
  return `/avatars/${path}`
}

const categories = ref([])
const selectedCategory = ref(null)

// 文章数据
const featuredArticles = ref([])
const featuredArticle = ref(null) // 用于首页粤剧卡片的文章
const hotArticles = ref([])

// 推荐作者数据
const topAuthors = ref([])

const loading = ref(false)
const error = ref(null)

// 根据选中的分类筛选文章
const filteredArticles = computed(() => {
  if (!selectedCategory.value) {
    return featuredArticles.value
  }
  return featuredArticles.value.filter(article => article.categoryId === selectedCategory.value)
})

const fetchCategories = async () => {
  try {
    console.log('开始获取分类数据...')
    error.value = null
    const response = await fetch(`${API_BASE_URL}/categories`)
    console.log('响应状态:', response.status)
    if (response.ok) {
      const data = await response.json()
      console.log('获取到的分类数据:', data)
      categories.value = data
      console.log('分类数组长度:', categories.value.length)
    } else {
      console.error('响应失败:', response)
      error.value = '获取分类数据失败'
    }
  } catch (err) {
    console.error('获取分类失败:', err)
    error.value = '网络错误，请稍后重试'
  }
}

const fetchFeaturedArticles = async () => {
  try {
    loading.value = true
    error.value = null
    console.log('开始获取推荐文章...')
    const response = await fetch(`${API_BASE_URL}/articles/featured`)
    console.log('响应状态:', response.status)
    if (response.ok) {
      const data = await response.json()
      console.log('获取到的推荐文章:', data)
      featuredArticles.value = data
      
      // 找到粤剧相关的文章
      const yuejuArticle = data.find(article => 
        article.categoryName && article.categoryName.includes('粤剧')
      )
      
      if (yuejuArticle) {
        featuredArticle.value = yuejuArticle
        console.log('找到粤剧文章:', yuejuArticle)
      } else {
        // 如果没有粤剧文章，使用第一篇文章
        featuredArticle.value = data[0] || null
        console.log('没有找到粤剧文章，使用第一篇文章:', featuredArticle.value)
      }
    } else {
      console.error('响应失败:', response)
      // 使用静态数据作为后备
      featuredArticles.value = [
        {
          articleId: 1,
          title: '粤剧：岭南文化的活化石',
          summary: '粤剧作为岭南地区最具代表性的戏曲艺术形式，承载着深厚的文化底蕴和历史传承...',
          coverImage: '/uploads/covers/yue ju-100.png',
          userNickname: '陈志远',
          userAvatar: '/avatars/av1.png',
          userId: 2,
          categoryName: '粤剧戏曲',
          viewCount: 2300,
          likeCount: 186,
          createTime: '2025-01-18'
        },
        {
          articleId: 2,
          title: '广式早茶的文化内涵',
          summary: '广式早茶不仅是一种饮食方式，更是岭南人生活方式的体现...',
          coverImage: '/uploads/covers/zaocha.png',
          userNickname: '张明',
          userAvatar: '/avatars/av3.png',
          userId: 4,
          categoryName: '粤菜食艺',
          viewCount: 2100,
          likeCount: 168,
          createTime: '2025-01-15'
        },
        {
          articleId: 3,
          title: '粤语俚语的趣味解读',
          summary: '粤语中蕴含着丰富的俚语和俗语，反映了岭南人的智慧...',
          coverImage: '/uploads/covers/yueyu.png',
          userNickname: '陈志远',
          userAvatar: '/avatars/av1.png',
          userId: 2,
          categoryName: '粤语方言',
          viewCount: 1500,
          likeCount: 124,
          createTime: '2025-01-10'
        }
      ]
      
      // 设置默认粤剧文章
      featuredArticle.value = featuredArticles.value[0]
    }
  } catch (err) {
    console.error('获取推荐文章失败:', err)
    error.value = '获取推荐文章失败'
    featuredArticles.value = [
      {
        articleId: 1,
        title: '粤剧：岭南文化的活化石',
        summary: '粤剧作为岭南地区最具代表性的戏曲艺术形式，承载着深厚的文化底蕴和历史传承...',
        coverImage: '/uploads/covers/yue ju-100.png',
        userNickname: '陈文轩',
        userAvatar: '/avatars/av1.png',
        userId: 2,
        categoryName: '粤剧戏曲',
        viewCount: 2300,
        likeCount: 186,
        createTime: '2025-01-18'
      },
      {
        articleId: 2,
        title: '广式早茶的文化内涵',
        summary: '广式早茶不仅是一种饮食方式，更是岭南人生活方式的体现...',
        coverImage: '/uploads/covers/zaocha.png',
        userNickname: '张美玲',
        userAvatar: '/avatars/av3.png',
        userId: 4,
        categoryName: '粤菜食艺',
        viewCount: 2100,
        likeCount: 168,
        createTime: '2025-01-15'
      },
      {
        articleId: 3,
        title: '粤语俚语的趣味解读',
        summary: '粤语中蕴含着丰富的俚语和俗语，反映了岭南人的智慧...',
        coverImage: '/uploads/covers/yueyu.png',
        userNickname: '陈文轩',
        userAvatar: '/avatars/av1.png',
        userId: 2,
        categoryName: '粤语方言',
        viewCount: 1500,
        likeCount: 124,
        createTime: '2025-01-10'
      }
    ]
    
    // 设置默认粤剧文章
    featuredArticle.value = featuredArticles.value[0]
  } finally {
    loading.value = false
  }
}

const fetchHotArticles = async () => {
  try {
    error.value = null
    console.log('开始获取热门文章...')
    const response = await fetch(`${API_BASE_URL}/articles/hot`)
    console.log('响应状态:', response.status)
    if (response.ok) {
      const data = await response.json()
      console.log('获取到的热门文章:', data)
      hotArticles.value = data
    } else {
      console.error('响应失败:', response)
      hotArticles.value = [
        {
          articleId: 1,
          title: '粤剧：岭南文化的活化石',
          viewCount: 2300
        },
        {
          articleId: 2,
          title: '广式早茶的文化内涵',
          viewCount: 2100
        },
        {
          articleId: 3,
          title: '岭南传统节庆文化',
          viewCount: 1900
        },
        {
          articleId: 4,
          title: '粤语俚语的趣味解读',
          viewCount: 1500
        },
        {
          articleId: 5,
          title: '岭南建筑的独特魅力',
          viewCount: 1600
        }
      ]
    }
  } catch (err) {
    console.error('获取热门文章失败:', err)
    error.value = '获取热门文章失败'
    hotArticles.value = [
      {
        articleId: 1,
        title: '粤剧：岭南文化的活化石',
        viewCount: 2300
      },
      {
        articleId: 2,
        title: '广式早茶的文化内涵',
        viewCount: 2100
      },
      {
        articleId: 3,
        title: '岭南传统节庆文化',
        viewCount: 1900
      },
      {
        articleId: 4,
        title: '粤语俚语的趣味解读',
        viewCount: 1500
      },
      {
        articleId: 5,
        title: '岭南建筑的独特魅力',
        viewCount: 1600
      }
    ]
  }
}

// 获取粉丝数最多的前三位作者
const fetchTopAuthors = async () => {
  try {
    console.log('开始获取推荐作者...')
    error.value = null
    const response = await fetch(`${API_BASE_URL}/users/top-authors`)
    console.log('响应状态:', response.status)
    if (response.ok) {
      const data = await response.json()
      console.log('获取到的推荐作者:', data)
      topAuthors.value = data
    } else {
      console.error('响应失败:', response)
      // 使用静态数据作为后备
      topAuthors.value = [
        {
          userId: 2,
          nickname: '陈文轩',
          bio: '文化研究者',
          avatar: '/avatars/av1.png',
          followerCount: 1280
        },
        {
          userId: 3,
          nickname: '林雅琴',
          bio: '粤剧演员',
          avatar: '/avatars/av2.png',
          followerCount: 960
        },
        {
          userId: 4,
          nickname: '张美玲',
          bio: '美食博主',
          avatar: '/avatars/av3.png',
          followerCount: 850
        }
      ]
    }
  } catch (err) {
    console.error('获取推荐作者失败:', err)
    error.value = '获取推荐作者失败'
    // 使用静态数据作为后备
    topAuthors.value = [
      {
        userId: 2,
        nickname: '陈文轩',
        bio: '文化研究者',
        avatar: '/avatars/av1.png',
        followerCount: 1280
      },
      {
        userId: 3,
        nickname: '林雅琴',
        bio: '粤剧演员',
        avatar: '/avatars/av2.png',
        followerCount: 960
      },
      {
        userId: 4,
        nickname: '张美玲',
        bio: '美食博主',
        avatar: '/avatars/av3.png',
        followerCount: 850
      }
    ]
  }
}

const getTagClass = (tag) => {
  // 红色标签
  const redTags = ['粤剧', '戏曲', '民俗', '精选']
  // 翡翠标签
  const greenTags = ['非遗', '已发布', '通过']
  // 金色标签
  const goldTags = ['精选推荐', '审核中', '新人']
  // 中性标签
  const neutralTags = ['历史', '文化', '岭南', '早茶']
  
  if (redTags.includes(tag)) {
    return 'tag-red'
  } else if (greenTags.includes(tag)) {
    return 'tag-green'
  } else if (goldTags.includes(tag)) {
    return 'tag-gold'
  } else if (neutralTags.includes(tag)) {
    return 'tag-neutral'
  }
  
  // 分类名称映射
  const categoryMap = {
    '粤剧戏曲': 'tag-red',
    '粤菜食艺': 'tag-gold',
    '粤语方言': 'tag-neutral',
    '民俗节庆': 'tag-red',
    '非遗工艺': 'tag-green',
    '历史建筑': 'tag-neutral',
    '粤乐南音': 'tag-red',
    '岭南文学': 'tag-neutral'
  }
  
  return categoryMap[tag] || 'tag-neutral'
}

// 跳转到文章详情页
const goToArticleDetail = (articleId) => {
  router.push(`/article/${articleId}`)
}

onMounted(() => {
  fetchCategories()
  fetchFeaturedArticles()
  fetchHotArticles()
  fetchTopAuthors()
})
</script>

<style scoped>
/* 所有样式保持不变，完全使用您原有的样式 */
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@300;400;500;600;700;900&family=ZCOOL+XiaoWei&family=Ma+Shan+Zheng&display=swap');

/* 定义CSS变量，与HTML保持一致 */
:root {
  --v: #C0392B;
  --v2: #E04B3A;
  --vd: rgba(192,57,43,0.10);
  --g: #D4A017;
  --gd: #A67C00;
  --glow: rgba(212,160,23,0.14);
  --ink: #1A1008;
  --ink7: rgba(26,16,8,0.7);
  --ink4: rgba(26,16,8,0.4);
  --ink1: rgba(26,16,8,0.10);
  --rice: #F5EDD6;
  --cream: #FBF6EC;
  --paper: #F0E6D2;
  --jade: #2E7D6A;
  --jd: rgba(46,125,106,0.10);
  --dim: #6B5744;
  --dim5: rgba(107,87,68,0.5);
  --bd: #D4B896;
  --bd2: #EAD8BE;
  --sh: 0 2px 16px rgba(26,16,8,0.08);
  --sh2: 0 8px 36px rgba(26,16,8,0.14);
  --nav-h: 62px;
  --r: 3px;
  --r2: 6px;
}

.nav-gap { height: var(--nav-h); }

.wrap {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 36px;
}

/* Hero 区域样式 - 完全复刻HTML */
.hero {
  min-height: 88vh;
  background: var(--cream);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
}

.hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse 56% 65% at 78% 50%, rgba(192,57,43,.065) 0%, transparent 70%),
    radial-gradient(ellipse 45% 55% at 15% 75%, rgba(212,160,23,.09) 0%, transparent 60%);
}

.hero-grid {
  position: absolute;
  inset: 0;
  background-image:
    repeating-linear-gradient(0deg, transparent, transparent 48px, rgba(212,160,23,.04) 48px, rgba(212,160,23,.04) 49px),
    repeating-linear-gradient(90deg, transparent, transparent 48px, rgba(212,160,23,.04) 48px, rgba(212,160,23,.04) 49px);
}

.hero-bigchar {
  position: absolute;
  right: -2vw;
  bottom: -8vh;
  font-family: 'Ma Shan Zheng', serif;
  font-size: 32vw;
  color: rgba(192,57,43,.038);
  line-height: 1;
  user-select: none;
  transform: rotate(-6deg);
}

.hero-inner {
  position: relative;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
  padding: 80px 0;
  width: 100%;
}

/* 眉毛区域 - 完全按照HTML代码复刻 */
.hero-eyebrow {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  background: #1A1008;  /* 直接使用十六进制颜色，不使用变量 */
  color: #D4A017;        /* 直接使用十六进制颜色，不使用变量 */
  font-size: 10px;
  letter-spacing: 4px;
  padding: 6px 18px;
  border-radius: 2px;
  margin-bottom: 28px;
  text-transform: uppercase;
  font-family: 'Noto Serif SC', serif;  /* 确保字体一致 */
  font-weight: 400;  /* 确保字重一致 */
  line-height: 1.5;  /* 确保行高一致 */
}

.hero-eyebrow-dot {
  width: 5px;
  height: 5px;
  background: #D4A017;  /* 直接使用十六进制颜色 */
  border-radius: 50%;
  display: inline-block;  /* 确保圆点正确显示 */
}

.hero-h1 {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: clamp(52px, 6.5vw, 86px);
  line-height: 1.15;
  letter-spacing: 8px;
  margin-bottom: 10px;
  color: #1A1008;  /* 直接使用十六进制颜色 */
  font-weight: 400;
}

.hero-h1 em {
  color: #C0392B;  /* 直接使用十六进制颜色 */
  font-style: normal;
  display: block;
}

.hero-sub {
  font-size: 12px;
  letter-spacing: 6px;
  color: #6B5744;  /* 直接使用十六进制颜色 */
  margin-bottom: 28px;
  text-transform: uppercase;
  font-family: 'Noto Serif SC', serif;
  font-weight: 400;
}

.hero-desc {
  font-size: 15px;
  color: #6B5744;  /* 直接使用十六进制颜色 */
  line-height: 1.95;
  max-width: 460px;
  margin-bottom: 36px;
  border-left: 3px solid #D4A017;  /* 直接使用十六进制颜色 */
  padding-left: 18px;
  font-family: 'Noto Serif SC', serif;
  font-weight: 400;
}

.hero-ctas {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
}

.hero-stats {
  display: flex;
  gap: 32px;
  margin-top: 40px;
  padding-top: 32px;
  border-top: 1px solid #EAD8BE;  /* 直接使用十六进制颜色 */
}

.hero-stat-n {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 34px;
  letter-spacing: 2px;
  color: #C0392B;  /* 直接使用十六进制颜色 */
  line-height: 1;
  font-weight: 400;
}

.hero-stat-l {
  font-size: 11px;
  letter-spacing: 2px;
  color: #6B5744;  /* 直接使用十六进制颜色 */
  margin-top: 4px;
  font-family: 'Noto Serif SC', serif;
  font-weight: 400;
}

/* 右侧卡片样式 */
.hero-right {
  display: flex;
  justify-content: flex-end;
}

.hero-card {
  cursor: pointer;
  width: 100%;
  max-width: 500px;
  margin-left: -40px;
}

.hero-card-img {
  height: 240px;
  position: relative;
  overflow: hidden;
  background-color: #f5f5f5;
}

.hero-card-img-inner {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  transition: transform 0.3s ease;
}

.hero-card:hover .hero-card-img-inner {
  transform: scale(1.05);
}

.hero-card-img-grad {
  background: linear-gradient(135deg,#3A0A06 0%,#8B1A14 35%,#C0392B 65%,#D4A017 100%);
  width: 100%;
  height: 100%;
}

.hero-card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, transparent 35%, rgba(26,16,8,.72));
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 22px;
}

.hero-card-overlay-title {
  font-family: 'Ma Shan Zheng', serif;
  font-size: 30px;
  color: rgba(255,255,255,.95);
  letter-spacing: 4px;
  line-height: 1.3;
  font-weight: 400;
}

.hero-card-overlay-sub {
  font-size: 11px;
  color: rgba(255,255,255,.5);
  letter-spacing: 3px;
  margin-top: 4px;
  font-family: 'Noto Serif SC', serif;
  font-weight: 400;
}

.hero-card-body {
  padding: 22px;
}

.hero-card-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.hero-card-avt {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  flex-shrink: 0;
}

.av1 { background: linear-gradient(135deg,#8B1A14,#D4A017); }
.av2 { background: linear-gradient(135deg,#1B4A40,#4AA08A); }
.av3 { background: linear-gradient(135deg,#4A0A1A,#8B3A5A); }
.av4 { background: linear-gradient(135deg,#1A1A4A,#2C5FBD); }
.av5 { background: linear-gradient(135deg,#3D1F0A,#8B4F1A); }

.hero-card-author {
  font-size: 12px;
  color: #6B5744;  /* 直接使用十六进制颜色 */
  font-family: 'Noto Serif SC', serif;
  font-weight: 400;
}

.hero-card-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 17px;
  letter-spacing: 2px;
  margin-bottom: 10px;
  line-height: 1.5;
  color: #1A1008;  /* 直接使用十六进制颜色 */
  font-weight: 400;
}

.hero-card-exc {
  font-size: 13px;
  color: #6B5744;  /* 直接使用十六进制颜色 */
  line-height: 1.85;
  font-family: 'Noto Serif SC', serif;
  font-weight: 400;
}

.hero-card-foot {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #EAD8BE;  /* 直接使用十六进制颜色 */
  font-size: 12px;
  color: rgba(107,87,68,0.5);  /* 直接使用rgba */
}

/* 标签样式 */
.tag {
  display: inline-block;
  font-size: 11px;
  letter-spacing: 1px;
  padding: 3px 12px;
  border-radius: 2px;
  cursor: default;
  font-family: 'Noto Serif SC', serif;
  font-weight: 400;
}

.tag-v {
  background: rgba(192,57,43,0.10);
  color: #C0392B;
  border: 1px solid rgba(192,57,43,.2);
}

.tag-g {
  background: rgba(212,160,23,0.14);
  color: #A67C00;
  border: 1px solid rgba(212,160,23,.3);
}

.tag-d {
  background: rgba(107,87,68,.08);
}

/* 新标签样式 - 根据配色参考图三 */
.tag-red {
  background: rgba(192, 57, 43, 0.10);
  color: #C0392B;
  border: 1px solid rgba(192, 57, 43, 0.20);
}

.tag-green {
  background: rgba(46, 119, 106, 0.10);
  color: #2E776A;
  border: 1px solid rgba(46, 119, 106, 0.20);
}

.tag-gold {
  background: rgba(212, 160, 23, 0.14);
  color: #A67C00;
  border: 1px solid rgba(212, 160, 23, 0.28);
}

.tag-neutral {
  background: rgba(26, 16, 8, 0.08);
  color: #6B5744;
  border: 1px solid #EAD8BE;
}

/* 分类导航条 - 完全保持您原有的样式 */
.catbar {
  background: #1A1008;
  padding: 16px 0;
  position: sticky;
  top: 62px;
  z-index: 100;
}

.catbar-inner {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  scrollbar-width: none;
}

.catbar-inner::-webkit-scrollbar {
  display: none;
}

.catbar-item {
  padding: 8px 20px;
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  color: rgba(255, 255, 255, 0.45);
  font-size: 14px;
  font-family: 'Noto Serif SC', serif;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.catbar-item:hover {
  color: #D4A017;
}

.catbar-item.on {
  color: #D4A017;
  border-bottom-color: #D4A017;
}

/* 以下样式完全保持您原有的不变 */
.btn {
  padding: 10px 24px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-family: 'Noto Serif SC', serif;
  cursor: pointer;
  transition: all 0.2s ease;
  letter-spacing: 1px;
}

.btn-v {
  background: #C0392B;
  color: #fff;
}

.btn-v:hover {
  background: #A93226;
  box-shadow: 0 4px 18px rgba(192, 57, 43, 0.35);
  transform: translateY(-1px);
}

.btn-ghost {
  background: #fff;
  color: #6B5744;
  border: 1px solid #D4B896;
}

.btn-ghost:hover {
  border-color: #C0392B;
  color: #C0392B;
}

.btn-ol {
  background: transparent;
  color: #C0392B;
  border: 1.5px solid #C0392B;
}

.btn-ol:hover {
  background: rgba(192, 57, 43, 0.1);
}

.btn-lg {
  padding: 14px 36px;
  font-size: 15px;
}

.btn-sm {
  padding: 6px 16px;
  font-size: 12px;
  letter-spacing: 1px;
}

.sec-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 32px;
}

.sec-eyebrow {
  font-size: 12px;
  letter-spacing: 3px;
  color: #C0392B;
  margin-bottom: 8px;
}

.sec-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 28px;
  color: #1A1008;
}

.sec-more {
  font-size: 14px;
  color: #8B5A2B;
  cursor: pointer;
  transition: color 0.2s ease;
}

.sec-more:hover {
  color: #C0392B;
}

.grid-main-side {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 32px;
}

.main-content {
  min-width: 0;
}

.grid3 {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.card {
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 16px rgba(26,16,8,0.08);
  transition: all .25s ease;
}

.card:hover {
  box-shadow: 0 8px 36px rgba(26,16,8,0.14);
  transform: translateY(-3px);
}

.art-card {
  cursor: pointer;
}

.art-card-img {
  height: 160px;
  overflow: hidden;
  border-radius: 8px 8px 0 0;
}

.art-card-img-inner {
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.art-card-body {
  padding: 16px;
}

.art-card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.art-card-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 16px;
  color: #1A1008;
  margin-bottom: 8px;
  line-height: 1.4;
}

.art-card-exc {
  font-size: 13px;
  color: #8B5A2B;
  line-height: 1.6;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.art-card-foot {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-top: 14px;
  border-top: 1px solid #EAD8BE;
}

.art-card-avt {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #E8DCC8;
  overflow: hidden;
}

.art-card-avt-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.art-card-authr {
  font-size: 13px;
  color: #5A4A3A;
  flex: 1;
  cursor: pointer;
  transition: color 0.2s;
}

.art-card-authr:hover {
  color: #C0392B;
}

.art-card-stats {
  display: flex;
  gap: 12px;
}

.art-card-stat {
  font-size: 12px;
  color: #8B5A2B;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.widget {
  background: #fff;
  border: 1px solid #E8DCC8;
  border-radius: 12px;
  padding: 20px;
}

.widget-head {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.widget-head-dot {
  width: 6px;
  height: 6px;
  background: #C0392B;
  border-radius: 50%;
}

.widget-head-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 16px;
  color: #1A1008;
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #F5F0E8;
  cursor: pointer;
}

.hot-item:last-child {
  border-bottom: none;
}

.hot-rank {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.hr1 { background: #C0392B; color: #fff; }
.hr2 { background: #D4A017; color: #fff; }
.hr3 { background: #8B5A2B; color: #fff; }
.hrn { background: #F5F0E8; color: #8B5A2B; }

.hot-ttl {
  font-size: 14px;
  color: #1A1008;
  margin-bottom: 4px;
  line-height: 1.4;
}

.hot-vw {
  font-size: 12px;
  color: #8B5A2B;
}

.author-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.author-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
}

.author-avt {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #E8DCC8;
  flex-shrink: 0;
}

.author-info {
  flex: 1;
}

.author-name {
  font-size: 14px;
  color: #1A1008;
  margin-bottom: 2px;
}

.author-desc {
  font-size: 12px;
  color: #8B5A2B;
}

.newsletter {
  background: #1A1008;
  padding: 64px 0;
  margin-top: 64px;
}

.newsletter-inner {
  text-align: center;
}

.newsletter-eyebrow {
  font-size: 12px;
  letter-spacing: 3px;
  color: #D4A017;
  margin-bottom: 16px;
}

.newsletter-title {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 32px;
  color: #fff;
  margin-bottom: 12px;
}

.newsletter-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 32px;
}

.newsletter-form {
  display: flex;
  gap: 12px;
  justify-content: center;
  max-width: 480px;
  margin: 0 auto;
}

.newsletter-input {
  flex: 1;
  padding: 14px 20px;
  border: 1px solid rgba(212, 160, 23, 0.3);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
  font-size: 14px;
}

.newsletter-input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.newsletter-input:focus {
  outline: none;
  border-color: #D4A017;
}

/* 响应式样式 */
@media (max-width: 1024px) {
  .hero-inner {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .hero-left {
    padding: 32px 0;
  }
  
  .hero-desc {
    max-width: none;
  }
  
  .hero-ctas {
    justify-content: center;
  }
  
  .hero-stats {
    justify-content: center;
  }
  
  .hero-right {
    display: none;
  }
  
  .grid-main-side {
    grid-template-columns: 1fr;
  }
  
  .grid3 {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .grid3 {
    grid-template-columns: 1fr;
  }
  
  .newsletter-form {
    flex-direction: column;
  }
}

/* 统计图标样式 */
.stat-icon {
  width: 16px;
  height: 16px;
  margin-right: 4px;
  vertical-align: middle;
}
</style>