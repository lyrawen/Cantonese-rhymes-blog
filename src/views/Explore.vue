<template>
  <div class="page active">
    <div class="nav-gap"></div>
    
    <section class="explore-hero">
      <div class="explore-hero-bg"></div>
      <div class="wrap explore-hero-pos">
        <h1 class="explore-h1">探索<em>粤韵</em></h1>
        <div class="explore-sub">EXPLORE CANTONESE CULTURE</div>
      </div>
    </section>

    <section class="wrap" style="padding-top: 36px;">
      <div class="cat-tabs">
        <button 
          class="cat-tab" 
          :class="{ on: selectedCategory === null }"
          @click="handleCategoryClick(null)"
        >全部</button>
        <button 
          v-for="category in categories" 
          :key="category.categoryId"
          class="cat-tab"
          :class="{ on: selectedCategory === category.categoryId }"
          @click="handleCategoryClick(category.categoryId)"
        >{{ category.categoryName }}</button>
      </div>

      <div class="filter-bar">
        <span class="filter-lbl">排序:</span>
        <div class="filter-opts">
          <button 
            class="filter-opt" 
            :class="{ on: selectedSort === 'latest' }"
            @click="handleSortClick('latest')"
          >最新</button>
          <button 
            class="filter-opt" 
            :class="{ on: selectedSort === 'hot' }"
            @click="handleSortClick('hot')"
          >最热</button>
          <button 
            class="filter-opt" 
            :class="{ on: selectedSort === 'featured' }"
            @click="handleSortClick('featured')"
          >精选</button>
        </div>
        <div class="filt-div"></div>
        <div class="filter-cnt">共 {{ filteredArticles.length }} 篇文章</div>
      </div>

      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <div class="loading-text">加载中...</div>
      </div>

      <div v-else-if="error" class="error-container">
        <div class="error-icon">⚠️</div>
        <div class="error-text">{{ error }}</div>
      </div>

      <div v-else>
        <div 
          v-for="article in filteredArticles" 
          :key="article.articleId" 
          class="list-art"
          @click="handleArticleClick(article.articleId)"
        >
          <div class="list-art-img">
            <img 
              :src="fixImagePath(article.coverImage)" 
              :alt="article.title"
              class="list-art-img-in"
            />
          </div>
          <div class="list-art-body">
            <div class="list-art-hdr">
              <span class="tag" :class="getTagClass(article.categoryName)">{{ article.categoryName }}</span>
              <span v-if="article.isFeatured" class="tag tag-g">精选</span>
            </div>
            <div class="list-art-title">{{ article.title }}</div>
            <div class="list-art-exc">{{ article.summary }}</div>
            <div class="list-art-foot">
              <div class="list-art-avt" @click="goToUserProfile(article.userId)" style="cursor:pointer;">
                <img 
                  v-if="article.userAvatar"
                  :src="fixAvatarPath(article.userAvatar)" 
                  :alt="article.userNickname"
                  class="list-art-avt-img"
                  @load="console.log('头像加载成功:', article.userNickname, fixAvatarPath(article.userAvatar))"
                  @error="console.error('头像加载失败:', article.userAvatar, fixAvatarPath(article.userAvatar))"
                />
                <div v-else class="list-art-avt-placeholder">{{ article.userNickname?.charAt(0) || '?' }}</div>
              </div>
              <div class="list-art-authr" @click="goToUserProfile(article.userId)" style="cursor:pointer;">{{ article.userNickname }}</div>
              <div class="list-art-stats">
                <span class="list-art-stat">👁 {{ (article.viewCount / 1000).toFixed(1) }}K</span>
                <span class="list-art-stat">❤️ {{ article.likeCount }}</span>
                <span class="list-art-stat">💬 {{ article.commentCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination">
        <button class="pg-btn">←</button>
        <button class="pg-btn on">1</button>
        <button class="pg-btn">2</button>
        <button class="pg-btn">3</button>
        <button class="pg-btn">4</button>
        <button class="pg-btn">5</button>
        <button class="pg-btn">→</button>
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
  // 如果已经是完整路径，直接返回
  if (path.startsWith('/avatars') || path.startsWith('http')) {
    return path
  }
  // 如果路径包含avatars，直接返回
  if (path.includes('avatars')) {
    return path
  }
  // 否则添加/avatars前缀
  return `/avatars/${path}`
}

const categories = ref([])
const articles = ref([])
const selectedCategory = ref(null)
const selectedSort = ref('latest')
const loading = ref(false)
const error = ref(null)

// 根据选中的分类筛选文章
const filteredArticles = computed(() => {
  let result = [...articles.value]
  
  // 按分类筛选
  if (selectedCategory.value) {
    result = result.filter(article => article.categoryId === selectedCategory.value)
  }
  
  // 按排序方式排序
  if (selectedSort.value === 'latest') {
    result.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  } else if (selectedSort.value === 'hot') {
    result.sort((a, b) => b.viewCount - a.viewCount)
  } else if (selectedSort.value === 'featured') {
    result = result.filter(article => article.isFeatured)
  }
  
  return result
})

const getTagClass = (category) => {
  const categoryMap = {
    '粤剧戏曲': 'tag-v',
    '粤菜食艺': 'tag-j',
    '粤语方言': 'tag-g',
    '民俗节庆': 'tag-d',
    '非遗工艺': 'tag-p',
    '历史建筑': 'tag-b',
    '粤乐南音': 'tag-v',
    '岭南文学': 'tag-g'
  }
  return categoryMap[category] || 'tag-v'
}

const fetchCategories = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/categories`)
    if (response.ok) {
      const data = await response.json()
      categories.value = data
    }
  } catch (err) {
    console.error('获取分类失败:', err)
  }
}

const fetchArticles = async () => {
  try {
    loading.value = true
    error.value = null
    const response = await fetch(`${API_BASE_URL}/articles/all`)
    if (response.ok) {
      const data = await response.json()
      articles.value = data || []
      console.log('获取到的文章数据:', articles.value)
      console.log('第一篇文章的头像信息:', articles.value[0]?.userNickname, articles.value[0]?.userAvatar)
    } else {
      error.value = '获取文章失败'
      // 使用默认数据
      articles.value = [
        {
          id: 1,
          title: '粤剧：岭南文化的活化石',
          summary: '粤剧作为岭南地区最具代表性的戏曲艺术形式，承载着深厚的文化底蕴和历史传承...',
          userNickname: '陈文轩',
          userAvatar: '/avatars/av1.png',
          userId: 2,
          categoryName: '粤剧戏曲',
          viewCount: 2300,
          likeCount: 186,
          commentCount: 23,
          createTime: '2025-01-18'
        },
        {
          id: 2,
          title: '广式早茶的文化内涵',
          summary: '广式早茶不仅是一种饮食方式，更是岭南人生活方式的体现...',
          userNickname: '张美玲',
          userAvatar: '/avatars/av3.png',
          userId: 4,
          categoryName: '粤菜食艺',
          viewCount: 2100,
          likeCount: 168,
          commentCount: 18,
          createTime: '2025-01-15'
        },
        {
          id: 3,
          title: '粤语俚语的趣味解读',
          summary: '粤语中蕴含着丰富的俚语和俗语，反映了岭南人的智慧...',
          userNickname: '林少华',
          userAvatar: '/avatars/av2.png',
          userId: 3,
          categoryName: '粤语方言',
          viewCount: 1500,
          likeCount: 124,
          commentCount: 15,
          createTime: '2025-01-10'
        }
      ]
    }
  } catch (err) {
    console.error('获取文章失败:', err)
    error.value = '网络错误'
    // 使用默认数据
    articles.value = [
      {
        id: 1,
        title: '粤剧：岭南文化的活化石',
        summary: '粤剧作为岭南地区最具代表性的戏曲艺术形式，承载着深厚的文化底蕴和历史传承...',
        userNickname: '陈文轩',
        userAvatar: '/avatars/av1.png',
        userId: 2,
        categoryName: '粤剧戏曲',
        viewCount: 2300,
        likeCount: 186,
        commentCount: 23,
        createTime: '2025-01-18'
      },
      {
        id: 2,
        title: '广式早茶的文化内涵',
        summary: '广式早茶不仅是一种饮食方式，更是岭南人生活方式的体现...',
        userNickname: '张美玲',
        userAvatar: '/avatars/av3.png',
        userId: 4,
        categoryName: '粤菜食艺',
        viewCount: 2100,
        likeCount: 168,
        commentCount: 18,
        createTime: '2025-01-15'
      },
      {
        id: 3,
        title: '粤语俚语的趣味解读',
        summary: '粤语中蕴含着丰富的俚语和俗语，反映了岭南人的智慧...',
        userNickname: '林少华',
        userAvatar: '/avatars/av2.png',
        userId: 3,
        categoryName: '粤语方言',
        viewCount: 1500,
        likeCount: 124,
        commentCount: 15,
        createTime: '2025-01-10'
      }
    ]
  } finally {
    loading.value = false
  }
}

const handleCategoryClick = (categoryId) => {
  selectedCategory.value = categoryId
}

const handleSortClick = (sortType) => {
  selectedSort.value = sortType
}

const handleArticleClick = (articleId) => {
  router.push(`/article/${articleId}`)
}

onMounted(() => {
  fetchCategories()
  fetchArticles()
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
  min-height: 40vh;
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

.explore-hero {
  background: #1A1008;
  padding: 56px 0;
  position: relative;
  overflow: hidden;
}

.explore-hero-bg {
  position: absolute;
  inset: 0;
  background-image: repeating-linear-gradient(45deg, transparent, transparent 20px, rgba(212, 160, 23, 0.03) 20px, rgba(212, 160, 23, 0.03) 21px);
}

.explore-hero-pos {
  position: relative;
}

.explore-h1 {
  font-family: 'ZCOOL XiaoWei', serif;
  font-size: 50px;
  color: #fff;
  letter-spacing: 8px;
  margin-bottom: 8px;
}

.explore-h1 em {
  color: #D4A017;
  font-style: normal;
}

.explore-sub {
  font-size: 12px;
  letter-spacing: 4px;
  color: rgba(255, 255, 255, 0.4);
}

.cat-tabs {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #EAD8BE;
  overflow-x: auto;
  scrollbar-width: none;
}

.cat-tabs::-webkit-scrollbar {
  display: none;
}

.cat-tab {
  padding: 16px 28px;
  font-size: 13px;
  letter-spacing: 2px;
  color: #6B5744;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  white-space: nowrap;
  transition: all 0.2s;
  background: none;
  border-left: none;
  border-right: none;
  border-top: none;
  font-family: 'Noto Serif SC', serif;
}

.cat-tab:hover {
  color: #C0392B;
}

.cat-tab.on {
  color: #C0392B;
  border-bottom-color: #C0392B;
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 28px;
  padding: 14px 22px;
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
}

.filter-lbl {
  font-size: 11px;
  letter-spacing: 2px;
  color: #6B5744;
  white-space: nowrap;
}

.filter-opts {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.filter-opt {
  padding: 5px 16px;
  border-radius: 20px;
  font-size: 12px;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #EAD8BE;
  color: #6B5744;
  background: none;
}

.filter-opt.on,
.filter-opt:hover {
  background: #C0392B;
  color: #fff;
  border-color: #C0392B;
}

.filt-div {
  width: 1px;
  height: 22px;
  background: #EAD8BE;
}

.filter-cnt {
  margin-left: auto;
  font-size: 12px;
  color: rgba(107, 87, 68, 0.5);
}

.list-art {
  display: flex;
  background: #fff;
  border: 1px solid #EAD8BE;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 16px rgba(26, 16, 8, 0.08);
  transition: all 0.25s;
  cursor: pointer;
  margin-bottom: 20px;
}

.list-art:hover {
  box-shadow: 0 8px 36px rgba(26, 16, 8, 0.14);
  transform: translateY(-2px);
}

.list-art-img {
  width: 210px;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.list-art-img-in {
  width: 100%;
  height: auto;
  max-width: 100%;
  object-fit: contain;
  transition: transform 0.4s;
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
  border: 2px solid #D4B896;
  overflow: hidden;
  cursor: pointer;
  transition: opacity 0.2s;
}

.list-art-avt:hover {
  opacity: 0.8;
}

.list-art-avt-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
}

.list-art-avt-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #C0392B, #D4A017);
  color: #fff;
  font-size: 12px;
  font-weight: bold;
}

.list-art-authr {
  font-size: 12px;
  color: #6B5744;
  flex: 1;
  cursor: pointer;
  transition: color 0.2s;
}

.list-art-authr:hover {
  color: #C0392B;
}

.list-art-stats {
  display: flex;
  gap: 14px;
}

.list-art-stat {
  font-size: 11px;
  color: rgba(107, 87, 68, 0.5);
  display: flex;
  align-items: center;
  gap: 3px;
}

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
  border: 1px solid #D4B896;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6B5744;
  transition: all 0.2s;
}

.pg-btn:hover,
.pg-btn.on {
  background: #C0392B;
  color: #fff;
  border-color: #C0392B;
}

.tag {
  display: inline-block;
  font-size: 11px;
  letter-spacing: 1px;
  padding: 3px 12px;
  border-radius: 2px;
  cursor: default;
}

.tag-v {
  background: rgba(192, 57, 43, 0.10);
  color: #C0392B;
  border: 1px solid rgba(192, 57, 43, 0.2);
}

.tag-j {
  background: rgba(46, 125, 106, 0.10);
  color: #2E7D6A;
  border: 1px solid rgba(46, 125, 106, 0.2);
}

.tag-g {
  background: rgba(212, 160, 23, 0.14);
  color: #A67C00;
  border: 1px solid rgba(212, 160, 23, 0.3);
}

.tag-d {
  background: rgba(107, 87, 68, 0.08);
  color: #6B5744;
  border: 1px solid #EAD8BE;
}

.av1 {
  background: linear-gradient(135deg, #8B1A14, #D4A017);
}

.av3 {
  background: linear-gradient(135deg, #4A0A1A, #8B3A5A);
}

.av4 {
  background: linear-gradient(135deg, #1A1A4A, #2C5FBD);
}

.av5 {
  background: linear-gradient(135deg, #3D1F0A, #8B4F1A);
}

.img-yuju {
  background: linear-gradient(135deg, #6B0F0F 0%, #C0392B 55%, #D4A017 100%);
}

.img-yuecai {
  background: linear-gradient(135deg, #0E2A1A 0%, #1B5A40 50%, #2E9A7A 100%);
}

.img-yuyu {
  background: linear-gradient(135deg, #2A1004 0%, #7B3010 50%, #D4A017 100%);
}

.img-minshu {
  background: linear-gradient(135deg, #0E1530 0%, #1A3070 50%, #5A80C0 100%);
}
</style>