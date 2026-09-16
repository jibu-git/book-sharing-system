<template>
  <div class="search-page-container">
    <section class="search-sticky-header">
      <div class="content-limit">
        <div class="search-bar-wrapper">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索书名、作者或分类标签..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
            class="modern-search-input"
          >
            <template #prefix>
              <el-icon class="search-icon"><Search /></el-icon>
            </template>
            <template #append>
              <el-button @click="handleSearch" :loading="loading">检索</el-button>
            </template>
          </el-input>
        </div>

        <div class="search-meta" v-if="hasSearched">
          <div class="meta-left">
            <span class="pulse-dot"></span>
            <span class="result-text">
              为您寻得 <span class="count">{{ filteredBooks.length }}</span> 本相关藏书
            </span>
          </div>
          <div class="meta-right">
            <el-select v-model="sortType" size="small" variant="text" class="sort-select">
              <el-option label="最匹配" value="match"></el-option>
              <el-option label="最新发布" value="new"></el-option>
            </el-select>
          </div>
        </div>
      </div>
    </section>

    <main class="results-section">
      <div class="content-limit">
        <div v-loading="loading" element-loading-text="正在穿梭书架...">
          
          <div v-if="!hasSearched" class="recommend-zone">
            <h2 class="zone-title">大家都在看</h2>
            <div class="books-grid">
              <div v-for="book in hotBooks" :key="book.booksId" class="book-card-item" @click="goToBookDetail(book)">
                <div class="modern-card small-card">
                  <div class="cover-wrapper">
                    <el-image :src="getRealImagePath(book.booksCover)" fit="cover" class="book-image" />
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
            </div>
          </div>

          <TransitionGroup 
            v-else-if="filteredBooks.length > 0" 
            name="book-list" 
            tag="div" 
            class="books-grid"
          >
            <div v-for="book in filteredBooks" :key="book.booksId" class="book-card-item" @click="goToBookDetail(book)">
              <div class="modern-card">
                <div class="cover-wrapper">
                  <el-image :src="getRealImagePath(book.booksCover)" fit="cover" class="book-image">
                    <template #error><div class="img-placeholder">NO IMAGE</div></template>
                  </el-image>
                  <div class="status-indicator" :class="{ 'is-busy': book.booksStatus !== '0' }">
                    {{ book.booksStatus === '0' ? '可借' : '借出' }}
                  </div>
                </div>
                <div class="book-info">
                  <h3 class="name" v-html="highlight(book.booksName)"></h3>
                  <p class="author">
                    <span v-html="highlight(book.booksAuthor || '佚名')"></span>
                    <span class="tag-label" v-if="getTagText(book.booksTypeId)"> · {{ getTagText(book.booksTypeId) }}</span>
                  </p>
                  <p class="book-stock" :class="{ 'zero': book.availableStock <= 0 && book.availableStock != null }">
                    剩余: <span class="stock-num">{{ book.availableStock != null ? book.availableStock : 1 }}</span> / {{ book.totalStock != null ? book.totalStock : 1 }}
                  </p>
                </div>
              </div>
            </div>
          </TransitionGroup>

          <div v-else class="empty-holder">
            <el-empty description="未找到匹配的书籍，换个词试试？" :image-size="120" />
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup name="Search">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { portalListBooks } from "@/api/books/books"
import { listType } from "@/api/BookType/type"

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const hasSearched = ref(false)
const searchKeyword = ref('')
const sortType = ref('match')

const allBooks = ref([])
const tagList = ref([])
const hotBooks = ref([])

const filteredBooks = computed(() => {
  if (!searchKeyword.value.trim()) return []
  const kw = searchKeyword.value.toLowerCase()
  const result = allBooks.value.filter(book => {
    const nameMatch = book.booksName?.toLowerCase().includes(kw)
    const authorMatch = book.booksAuthor?.toLowerCase().includes(kw)
    const tagName = getTagText(book.booksTypeId).toLowerCase()
    const tagMatch = tagName.includes(kw)
    return nameMatch || authorMatch || tagMatch
  })

  if (sortType.value === 'new') {
    return result.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  }
  return result 
})

const initData = async () => {
  loading.value = true
  try {
    const [resTag, resBooks] = await Promise.all([
      listType({ pageSize: 100 }),
      portalListBooks({ pageNum: 1, pageSize: 1000 })
    ])
    tagList.value = resTag.rows
    allBooks.value = resBooks.rows
    hotBooks.value = [...resBooks.rows].sort(() => 0.5 - Math.random()).slice(0, 8)
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const highlight = (text) => {
  if (!text) return ''
  const kw = searchKeyword.value.trim()
  if (!kw) return text
  const reg = new RegExp(`(${kw})`, 'gi')
  return text.replace(reg, '<span class="hl-mark">$1</span>')
}

const getTagText = (typeId) => {
  const tag = tagList.value.find(t => t.bookTypeId == typeId)
  return tag ? tag.bookTypeName : ''
}

const handleSearch = () => {
  if (!searchKeyword.value.trim()) return
  hasSearched.value = true
  router.replace({ query: { keyword: searchKeyword.value } })
}

const getRealImagePath = (url) => {
  if (!url) return '';
  if (url.startsWith("http") || url.startsWith("data:image")) return url;
  return import.meta.env.VITE_APP_BASE_API + url;
}

const goToBookDetail = (book) => {
  router.push({ path: '/book/detail', query: { id: book.booksId } })
}

onMounted(() => {
  initData()
  if (route.query.keyword) {
    searchKeyword.value = route.query.keyword
    hasSearched.value = true
  }
})

watch(() => route.query.keyword, (val) => {
  if (val) {
    searchKeyword.value = val
    hasSearched.value = true
  }
})
</script>

<style scoped>
/* =========== 保持你原有的全部样式不变 =========== */
.search-page-container { background-color: #f8fafc; min-height: 100vh; }
.content-limit { max-width: 1100px; margin: 0 auto; padding: 0 20px; }
.search-sticky-header { background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(12px); padding: 30px 0 15px; position: sticky; top: 0; z-index: 100; border-bottom: 1px solid #f1f5f9; }
.search-bar-wrapper { max-width: 700px; margin: 0 auto; }
.modern-search-input :deep(.el-input__wrapper) { border-radius: 50px 0 0 50px; padding-left: 25px; box-shadow: 0 4px 15px rgba(0,0,0,0.05) !important; }
.modern-search-input :deep(.el-input-group__append) { border-radius: 0 50px 50px 0; background: #1e293b; color: #fff; border: none; padding: 0 30px; }
.search-meta { display: flex; justify-content: space-between; align-items: center; margin-top: 20px; padding: 0 10px; }
.meta-left { display: flex; align-items: center; gap: 8px; font-size: 0.8rem; color: #94a3b8; }
.pulse-dot { width: 6px; height: 6px; background: #6366f1; border-radius: 50%; animation: pulse 2s infinite; }
.count { color: #1e293b; font-weight: 700; margin: 0 2px; }
.results-section { padding: 40px 0 80px; }
.zone-title { font-size: 1.2rem; font-weight: 300; color: #64748b; margin-bottom: 30px; letter-spacing: 4px; text-align: center; }
.books-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(170px, 1fr)); gap: 30px; }
.modern-card { cursor: pointer; transition: all 0.4s ease; }
.cover-wrapper { position: relative; width: 100%; padding-top: 140%; border-radius: 12px; overflow: hidden; background: #fff; box-shadow: 0 4px 15px rgba(0,0,0,0.04); }
.book-image { position: absolute; top: 0; left: 0; width: 100%; height: 100%; transition: 0.8s; }
.status-indicator { position: absolute; top: 10px; right: 10px; padding: 3px 8px; border-radius: 5px; font-size: 10px; color: #fff; background: #10b981; }
.status-indicator.is-busy { background: #cbd5e1; }
.book-info { padding: 12px 2px; }
.name { font-size: 0.9rem; font-weight: 600; color: #334155; margin-bottom: 4px; }
.author { font-size: 0.8rem; color: #94a3b8; }
.tag-label { color: #6366f1; font-weight: 500; }
:deep(.hl-mark) { background: #fef08a; color: #b45309; padding: 0 2px; border-radius: 2px; }
.book-card-item:hover .modern-card { transform: translateY(-8px); }
.book-card-item:hover .book-image { transform: scale(1.1); }
.book-list-move { transition: transform 0.6s cubic-bezier(0.55, 0, 0.1, 1); }
.book-list-enter-active, .book-list-leave-active { transition: all 0.5s ease; }
.book-list-enter-from, .book-list-leave-to { opacity: 0; transform: translateY(20px) scale(0.9); }
.book-list-leave-active { position: absolute; visibility: hidden; }
@keyframes pulse { 0% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(99, 102, 241, 0.7); } 70% { transform: scale(1); box-shadow: 0 0 0 6px rgba(99, 102, 241, 0); } 100% { transform: scale(0.95); box-shadow: 0 0 0 0 rgba(99, 102, 241, 0); } }
@media (max-width: 768px) { .books-grid { grid-template-columns: repeat(2, 1fr); gap: 15px; } .search-bar-wrapper { padding: 0 10px; } }

.book-stock {
  font-size: 0.75rem; 
  color: #64748b; 
  margin: 6px 0 0 0;
  display: flex;
  align-items: center;
}
.stock-num {
  color: #6366f1; /* 呼应页面主色调靛蓝 */
  font-weight: 600;
  margin: 0 3px;
}
.book-stock.zero .stock-num {
  color: #94a3b8; /* 库存为0时置灰 */
}
</style>