<template>
  <div class="home-container">
    <section class="hero-section">
      <div class="hero-background"></div>
      <div class="hero-overlay"></div>
      <div class="hero-content">
        <h1 class="hero-title">流动图书馆</h1>
        <p class="hero-desc">每一本书，都是一段可以分享的旅程</p>
        <div class="hero-action">
          <el-button type="primary" size="large" round @click="router.push('/findBooks')">开启寻书之旅</el-button>
        </div>
      </div>
    </section>

    <div class="main-content" v-loading="loading">
      <section class="books-section">
        <div class="section-container">
          <div class="section-header">
            <div class="header-left">
              <span class="header-accent"></span>
              <h2 class="section-title">可借阅</h2>
              <el-tag round effect="plain" class="count-tag">{{ availableBooks.length }}本</el-tag>
            </div>
            <router-link to="/findBooks" class="more-link">
              更多 <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          
          <el-empty v-if="availableBooks.length === 0 && !loading" description="书架休息中..." />
          
          <div v-else class="books-grid">
            <div v-for="book in availableBooks" :key="book.booksId" class="book-card-wrapper" @click="goToBookDetail(book)">
              <div class="modern-card">
                <div class="cover-box">
                  <el-image :src="getRealImagePath(book.booksCover)" fit="cover" class="book-image">
                    <template #error><div class="img-err">NO COVER</div></template>
                  </el-image>
                  <div class="status-pill available">可借</div>
                </div>
                <div class="book-details">
                  <h3 class="book-name">{{ book.booksName }}</h3>
                  <p class="book-author">{{ book.booksAuthor || '佚名' }}</p>
                  <p class="book-stock">
                    剩余: <span class="stock-num">{{ book.availableStock != null ? book.availableStock : 1 }}</span> / {{ book.totalStock != null ? book.totalStock : 1 }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="books-section borrowed-area">
        <div class="section-container">
          <div class="section-header">
            <div class="header-left">
              <span class="header-accent grey"></span>
              <h2 class="section-title">已借出</h2>
              <el-tag round type="info" effect="plain" class="count-tag">{{ borrowedBooks.length }}本</el-tag>
            </div>
            <router-link to="/findBooks" class="more-link">
              更多 <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
          
          <div class="books-grid">
            <div v-for="book in borrowedBooks" :key="book.booksId" class="book-card-wrapper" @click="goToBookDetail(book)">
              <div class="modern-card is-borrowed">
                <div class="cover-box">
                  <el-image :src="getRealImagePath(book.booksCover)" fit="cover" class="book-image">
                    <template #error><div class="img-err">NO COVER</div></template>
                  </el-image>
                  <div class="status-pill borrowed">待预约</div>
                </div>
                <div class="book-details">
                  <h3 class="book-name">{{ book.booksName }}</h3>
                  <p class="book-author">{{ book.booksAuthor || '佚名' }}</p>
                  <p class="book-stock zero">库存已空</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

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
            <h4 class="footer-title">关于图书分享系统</h4>
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

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { portalListBooks } from "@/api/Books/books"; // 修正路径大小写
import { ArrowRight } from '@element-plus/icons-vue';

const router = useRouter();
const loading = ref(false);
const allBooksList = ref([]); 

const getRealImagePath = (url) => {
  if (!url) return '';
  if (url.indexOf("http") !== -1 || url.indexOf("data:image") !== -1) return url;
  return import.meta.env.VITE_APP_BASE_API + url;
};

const getList = async () => {
  loading.value = true;
  try {
    const res = await portalListBooks({}); 
    allBooksList.value = res.rows;
  } catch (error) {
    console.error("获取图书失败:", error);
  } finally {
    loading.value = false;
  }
};

const availableBooks = computed(() => {
  return allBooksList.value.filter(book => book.booksStatus === '0');
});

const borrowedBooks = computed(() => {
  return allBooksList.value.filter(book => book.booksStatus === '2');
});

const goToBookDetail = (book) => {
  router.push({
    path: '/book/detail',
    query: { id: book.booksId }
  });
};

onMounted(() => {
  getList();
});
</script>

<style scoped>
.home-container { background-color: #f8fafc; min-height: 100vh; }

/* ================= Hero 区域增强 ================= */
.hero-section { 
  height: 400px; 
  position: relative; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  overflow: hidden; 
  color: #fff;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  /* 你可以将下面的 URL 替换为你指定的图片地址 */
  background-image: url('@/assets/images/天空 云 少女 草地 唯美人物风景4k动漫壁纸3840x2160_彼岸图网.jpg'); 
  background-size: cover;
  background-position: center;
  transition: transform 1.5s ease-out;
  z-index: 1;
}

.hero-section:hover .hero-background {
  transform: scale(1.1); /* 鼠标悬停时背景缓慢放大 */
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(rgba(0,0,0,0.3), rgba(0,0,0,0.7)); /* 蒙版：上浅下深 */
  z-index: 2;
}

.hero-content { 
  position: relative; 
  z-index: 3; 
  text-align: center; 
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.8s forwards 0.2s; /* 文字向上浮现动画 */
}

@keyframes fadeInUp {
  to { opacity: 1; transform: translateY(0); }
}

.hero-title { font-size: 3.5rem; font-weight: 700; letter-spacing: 8px; margin-bottom: 15px; text-shadow: 0 4px 10px rgba(0,0,0,0.3); }
.hero-desc { font-size: 1.2rem; letter-spacing: 2px; opacity: 0.9; margin-bottom: 30px; }

/* ================= 列表头部美化 ================= */
.section-header { 
  display: flex; 
  align-items: center; 
  justify-content: space-between; /* 撑开两端 */
  margin-bottom: 30px; 
}
.header-left { display: flex; align-items: center; }
.more-link {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #94a3b8;
  text-decoration: none;
  transition: color 0.3s;
}
.more-link:hover { color: #6366f1; }
.more-link .el-icon { margin-left: 4px; }

/* ================= 列表样式保持 ================= */
.section-container { max-width: 1100px; margin: 0 auto; padding: 60px 20px 0; }
.header-accent { width: 4px; height: 18px; background: #6366f1; border-radius: 10px; margin-right: 12px; }
.header-accent.grey { background: #cbd5e1; }
.section-title { font-size: 1.4rem; font-weight: 600; color: #334155; margin: 0 10px 0 0; }
.books-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 35px; }

.modern-card { transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275); cursor: pointer; }
.cover-box { position: relative; width: 100%; padding-top: 140%; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.08); background: #fff; }
.book-image { position: absolute; top: 0; left: 0; width: 100%; height: 100%; transition: transform 0.6s ease; }
.modern-card:hover .book-image { transform: scale(1.1); }
.modern-card:hover { transform: translateY(-8px); }
.status-pill { position: absolute; bottom: 10px; left: 10px; padding: 4px 10px; border-radius: 20px; font-size: 10px; font-weight: 600; backdrop-filter: blur(4px); z-index: 3; }
.status-pill.available { background: rgba(255, 255, 255, 0.9); color: #10b981; }
.status-pill.borrowed { background: rgba(255, 255, 255, 0.9); color: #94a3b8; }
.book-details { padding: 15px 5px; }
.book-name { font-size: 1rem; color: #1e293b; font-weight: 600; margin: 0 0 6px 0; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.book-author { font-size: 0.85rem; color: #94a3b8; margin: 0; }
.book-stock { font-size: 0.8rem; color: #64748b; margin-top: 8px; }
.stock-num { color: #6366f1; font-weight: 600; }

/* ================= Footer 底部 ================= */
.portal-footer {
  background-color: #121212; /* 全黑背景 */
  color: #a1a1aa;
  padding: 80px 0 40px;
  margin-top: 100px;
}
.footer-container { max-width: 1100px; margin: 0 auto; padding: 0 20px; }
.footer-links {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 40px;
  margin-bottom: 60px;
}
.link-group h4 { color: #fff; font-size: 1rem; margin-bottom: 20px; }
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

@media (max-width: 768px) {
  .hero-title { font-size: 2.2rem; }
  .books-grid { grid-template-columns: repeat(2, 1fr); gap: 20px; }
}
</style>