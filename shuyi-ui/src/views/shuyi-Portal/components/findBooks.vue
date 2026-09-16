<template>
  <div class="find-container">
    <section class="find-hero">
      <div class="hero-background"></div>
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="main-title">馆藏分类</h1>
        <p class="sub-title">点击标签，指尖轻触间的墨香流转</p>
      </div>
    </section>

    <div class="filter-sticky-bar">
      <div class="content-limit">
        <div class="tags-wrapper">
          <div class="pill-tag" :class="{ active: activeTag === '' }" @click="activeTag = ''">全部</div>
          <div 
            v-for="tag in tagList" :key="tag.bookTypeId" class="pill-tag"
            :class="{ active: activeTag == tag.bookTypeId }" @click="activeTag = tag.bookTypeId"
          >
            {{ tag.bookTypeName }}
          </div>
        </div>
        
        <div class="filter-footer">
          <div class="status-left">
            <span class="pulse-dot"></span>
            <span class="count-info">
              {{ activeTagName }} <span class="highlight">{{ filteredBooks.length }}</span> 本
            </span>
          </div>
          <div class="sort-tool">
            <el-button-group>
              <el-button size="small" :plain="sortType !== 'new'" @click="sortType = 'new'">最新</el-button>
              <el-button size="small" :plain="sortType !== 'name'" @click="sortType = 'name'">标题</el-button>
            </el-button-group>
          </div>
        </div>
      </div>
    </div>

    <main class="grid-section">
      <div class="content-limit">
        <div v-loading="loading" element-loading-text="整理书架中...">
          <TransitionGroup name="book-list" tag="div" class="books-grid">
            <div v-for="book in filteredBooks" :key="book.booksId" class="book-card-item" @click="goToBookDetail(book)">
              <div class="modern-card">
                <div class="cover-wrapper">
                  <el-image :src="getRealImagePath(book.booksCover)" fit="cover" class="book-image">
                    <template #error><div class="img-placeholder">IMAGE</div></template>
                  </el-image>
                  <div class="status-indicator" :class="{ 'is-busy': book.booksStatus !== '0' }">
                    {{ book.booksStatus === '0' ? '可借' : '借出' }}
                  </div>
                </div>
                <div class="book-info">
                  <h3 class="name">{{ book.booksName }}</h3>
                  <p class="author">{{ book.booksAuthor || '佚名' }}</p>
                  <p class="book-stock" :class="{ 'zero': book.availableStock <= 0 && book.availableStock != null }">
                    剩余: <span class="stock-num">{{ book.availableStock != null ? book.availableStock : 1 }}</span> / {{ book.totalStock != null ? book.totalStock : 1 }}
                  </p>
                </div>
              </div>
            </div>
          </TransitionGroup>
          
          <div v-if="filteredBooks.length === 0 && !loading" class="empty-holder">
            <el-empty description="该分类暂无藏书" :image-size="80" />
          </div>
        </div>
      </div>
    </main>

    <footer class="portal-footer">
      <div class="footer-container">
        <div class="footer-links">
          <div class="link-group">
            <h4 class="footer-title">友情链接</h4>
            <a href="https://bgm.tv/" target="_blank">番组计划</a>
            <a href="https://moegirl.icu/Mainpage" target="_blank">萌娘百科</a>
            <a href="https://www.kadokawa.com.tw/" target="_blank">台湾角川</a>
            <a href="https://www.mianshiya.com/" target="_blank">面试鸭</a>
          </div>
          <div class="link-group">
            <h4 class="footer-title">友情链接</h4>
            <a href="https://www.mianshiya.com/" target="_blank">面试鸭</a>
            <a href="https://github.com/shuaigeljh66-gif" target="_blank">我的github</a>
            <a href="https://gitee.com/shuaige-liang-jiahao" target="_blank">我的gitee</a>
            <a href="https://gemini.google.com/app" target="_blank">技术支持TT</a>

          </div>
          <div class="link-group">
            <h4 class="footer-title">关于图书管理系统</h4>
            <router-link to="/about">关于我们</router-link>
            <router-link to="/dream">梦开始的地方</router-link>
            
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2026 流动图书馆. 让阅读在分享中永恒.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup name="FindBooks">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { portalListBooks } from "@/api/Books/books"
import { listType } from "@/api/BookType/type"

const router = useRouter()
const loading = ref(false)
const allBooksList = ref([]) 
const tagList = ref([])      
const activeTag = ref('')    
const sortType = ref('new')  

const filteredBooks = computed(() => {
  let list = activeTag.value 
    ? allBooksList.value.filter(book => book.booksTypeId == activeTag.value)
    : [...allBooksList.value]

  if (sortType.value === 'new') {
    list.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  } else {
    list.sort((a, b) => a.booksName.localeCompare(b.booksName, 'zh-Hans-CN'))
  }
  return list
})

const activeTagName = computed(() => {
  if (!activeTag.value) return '全部馆藏'
  const tag = tagList.value.find(item => item.bookTypeId == activeTag.value)
  return tag ? tag.bookTypeName : '分类'
})

const initData = async () => {
  loading.value = true
  try {
    const [resTag, resBooks] = await Promise.all([
      listType({ pageSize: 100 }),
      portalListBooks({ pageNum: 1, pageSize: 500 })
    ])
    tagList.value = resTag.rows.filter(t => t.bookTypeStatus === '0')
    allBooksList.value = resBooks.rows
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const getRealImagePath = (url) => {
  if (!url) return '';
  if (url.startsWith("http") || url.startsWith("data:image")) return url;
  return import.meta.env.VITE_APP_BASE_API + url;
}

const goToBookDetail = (book) => {
  router.push({ path: '/book/detail', query: { id: book.booksId } })
}

onMounted(initData)
</script>

<style scoped>
.find-container { background-color: #f8fafc; min-height: 100vh; }
.content-limit { max-width: 1200px; margin: 0 auto; padding: 0 25px; }

/* =========== Hero 增强 =========== */
.find-hero { 
  height: 280px; /* 比首页稍微矮一点 */
  position: relative; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  overflow: hidden; 
  color: #fff;
  background-color: #000;
}

.hero-background {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  /* 换一张更有分类目录感的背景图 */
  background-image: url('@/assets/images/我等你 女孩 窗前 日出 日落 唯美背影 艺术站设计4k壁纸3840x2160_彼岸图网.jpg') ;
  background-size: cover;
  background-position: center;
  transition: transform 2s ease-out;
  z-index: 1;
}

.find-hero:hover .hero-background { transform: scale(1.1); }

.hero-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(rgba(0,0,0,0.2), rgba(0,0,0,0.6));
  z-index: 2;
  backdrop-filter: blur(2px); /* 轻轻的模糊感 */
}

.hero-content { 
  position: relative; z-index: 3; text-align: center; 
  animation: fadeInDown 0.8s backwards;
}

@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

.main-title { font-size: 2.5rem; font-weight: 700; color: #fff; letter-spacing: 12px; margin-bottom: 10px; }
.sub-title { font-size: 0.9rem; color: rgba(255, 255, 255, 0.8); letter-spacing: 2px; }

/* =========== Sticky Bar 增强 =========== */
.filter-sticky-bar { 
  background: rgba(255, 255, 255, 0.9); 
  backdrop-filter: blur(20px); 
  padding: 15px 0; 
  border-bottom: 1px solid #e2e8f0; 
  position: sticky; 
  top: 64px; /* 假设导航栏高64px，根据实际情况调整 */
  z-index: 100; 
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.tags-wrapper { display: flex; gap: 10px; overflow-x: auto; padding-bottom: 15px; scrollbar-width: none; }
.tags-wrapper::-webkit-scrollbar { display: none; }

.pill-tag { 
  padding: 8px 20px; border-radius: 50px; background: #f1f5f9; color: #64748b; 
  font-size: 0.85rem; cursor: pointer; transition: all 0.3s ease; 
  white-space: nowrap; border: 1px solid transparent; 
}
.pill-tag.active { background: #6366f1; color: #fff; box-shadow: 0 10px 15px -3px rgba(99, 102, 241, 0.3); }
.pill-tag:hover:not(.active) { background: #e2e8f0; color: #334155; }

.filter-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 10px; padding-top: 10px; border-top: 1px dashed #e2e8f0; }
.status-left { display: flex; align-items: center; gap: 8px; font-size: 0.8rem; color: #64748b; }
.pulse-dot { width: 8px; height: 8px; background: #10b981; border-radius: 50%; animation: pulse 2s infinite; }
@keyframes pulse { 0% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0.7); } 70% { box-shadow: 0 0 0 10px rgba(16, 185, 129, 0); } 100% { box-shadow: 0 0 0 0 rgba(16, 185, 129, 0); } }
.highlight { color: #6366f1; font-weight: 700; font-size: 1rem; }

/* =========== 书籍网格 & 卡片 =========== */
.grid-section { padding: 50px 0 100px; }
.books-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 40px; }

.modern-card { cursor: pointer; transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1); }
.cover-wrapper { position: relative; width: 100%; padding-top: 142%; border-radius: 16px; overflow: hidden; background: #fff; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.04); }
.book-image { position: absolute; top: 0; left: 0; width: 100%; height: 100%; transition: 0.8s ease; }

.status-indicator { position: absolute; top: 12px; right: 12px; padding: 4px 12px; border-radius: 8px; font-size: 11px; font-weight: 600; color: #fff; background: #10b981; backdrop-filter: blur(4px); z-index: 2; }
.status-indicator.is-busy { background: rgba(148, 163, 184, 0.9); }

.book-info { padding: 18px 5px; }
.name { font-size: 1rem; font-weight: 600; color: #1e293b; margin-bottom: 6px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.author { font-size: 0.85rem; color: #94a3b8; }
.book-stock { font-size: 0.8rem; color: #64748b; margin-top: 10px; }
.stock-num { color: #6366f1; font-weight: 600; }

.book-card-item:hover .modern-card { transform: translateY(-12px); }
.book-card-item:hover .book-image { transform: scale(1.1); }
.book-card-item:hover .cover-wrapper { box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1); }

/* =========== Footer 底部 (全黑背景) =========== */
.portal-footer {
  background-color: #121212;
  color: #a1a1aa;
  padding: 80px 0 40px;
  margin-top: 0; /* 找书页列表较长，直接衔接即可 */
}
.footer-container { max-width: 1100px; margin: 0 auto; padding: 0 20px; }
.footer-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
  margin-bottom: 60px;
}
.footer-title { color: #fff; font-size: 1rem; margin-bottom: 20px; font-weight: 600; }
.link-group a {
  display: block;
  color: #71717a;
  text-decoration: none;
  font-size: 0.9rem;
  margin-bottom: 12px;
  transition: color 0.3s;
}
.link-group a:hover { color: #6366f1; }
.footer-bottom {
  border-top: 1px solid #27272a;
  padding-top: 30px;
  text-align: center;
  font-size: 0.85rem;
}

/* =========== 动画 =========== */
.book-list-enter-active, .book-list-leave-active { transition: all 0.5s ease; }
.book-list-enter-from { opacity: 0; transform: translateY(30px); }
.book-list-leave-to { opacity: 0; transform: scale(0.9); }
.book-list-move { transition: transform 0.5s ease; }

@media (max-width: 768px) {
  .main-title { font-size: 1.8rem; letter-spacing: 6px; }
  .books-grid { grid-template-columns: repeat(2, 1fr); gap: 20px; }
  .filter-sticky-bar { top: 0; } /* 移动端可能需要贴顶 */
}
</style>