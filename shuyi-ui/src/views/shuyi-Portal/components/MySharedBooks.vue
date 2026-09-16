<template>
    <div class="my-books-container">
      <header class="page-header">
        <div class="content-limit">
          <h1 class="page-title">共享书柜</h1>
          <p class="page-desc">管理你分享的所有书籍，让知识在传递中产生价值</p>
        </div>
      </header>
  
      <div class="content-limit">
        <el-tabs v-model="activeTab" class="modern-tabs">
          <el-tab-pane label="正式上架" name="published">
            <div class="books-grid" v-loading="loading">
              <div v-for="book in publishedBooks" :key="book.booksId" class="book-card-item">
                <div class="modern-card">
                  <div class="cover-wrapper">
                    <el-image :src="getRealImagePath(book.booksCover)" fit="cover" class="book-image" />
                    <div class="status-badge on-shelf">已上架</div>
                  </div>
                  <div class="book-info">
                    <h3 class="name">{{ book.booksName }}</h3>
                    <p class="author">{{ book.booksAuthor }}</p>
                    <div class="actions">
                      <el-button link type="danger" @click="handleStatusChange(book, '1', '确定要下架这本书吗？')">
                        下架图书
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-if="publishedBooks.length === 0" description="暂无已上架书籍" />
          </el-tab-pane>
  
          <el-tab-pane label="申请中" name="pending">
            <div class="books-grid" v-loading="loading">
              <div v-for="book in pendingBooks" :key="book.booksId" class="book-card-item">
                <div class="modern-card">
                  <div class="cover-wrapper grey-scale">
                    <el-image :src="getRealImagePath(book.booksCover)" fit="cover" class="book-image" />
                    <div class="status-badge in-review">审核中</div>
                  </div>
                  <div class="book-info">
                    <h3 class="name">{{ book.booksName }}</h3>
                    <p class="time">提交时间: {{ book.createTime || '近期' }}</p>
                    <div class="actions">
                      <span class="status-text">待管理员确认</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-if="pendingBooks.length === 0" description="没有正在审核的申请" />
          </el-tab-pane>
  
          <el-tab-pane label="已下架" name="offShelf">
            <div class="books-grid" v-loading="loading">
              <div v-for="book in offShelfBooks" :key="book.booksId" class="book-card-item">
                <div class="modern-card">
                  <div class="cover-wrapper grey-scale shadow-mode">
                    <el-image :src="getRealImagePath(book.booksCover)" fit="cover" class="book-image" />
                    <div class="status-badge off-shelf-tag">已下架</div>
                  </div>
                  <div class="book-info">
                    <h3 class="name">{{ book.booksName }}</h3>
                    <p class="author">{{ book.booksAuthor }}</p>
                    <div class="actions">
                      <el-button link type="primary" @click="handleStatusChange(book, '3', '重新提交上架申请？')">
                        申请上架
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-if="offShelfBooks.length === 0" description="暂无下架书籍" />
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, computed } from 'vue'
  import { ElMessageBox, ElMessage } from 'element-plus'
  import { listBooks, updateBooks } from "@/api/books/books"
  
  const activeTab = ref('published')
  const loading = ref(false)
  const allMyBooks = ref([])
  
  /** 获取数据 */
  const getMyBooks = async () => {
    loading.value = true
    try {
      // 假设 listBooks 后端已根据当前用户过滤，或在此处处理
      const res = await listBooks({ pageNum: 1, pageSize: 100 })
      allMyBooks.value = res.rows 
    } catch (e) {
      console.error("加载失败", e)
    } finally {
      loading.value = false
    }
  }
  
  // 状态过滤逻辑：0上架，1下架，3审核中
  const publishedBooks = computed(() => allMyBooks.value.filter(b => b.booksStatus === '0'))
  const offShelfBooks = computed(() => allMyBooks.value.filter(b => b.booksStatus === '1'))
  const pendingBooks = computed(() => allMyBooks.value.filter(b => b.booksStatus === '3'))
  
  /** 状态切换通用方法 (下架 or 重新申请上架) */
  const handleStatusChange = (book, newStatus, tip) => {
    ElMessageBox.confirm(tip, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: newStatus === '3' ? 'info' : 'warning',
    }).then(async () => {
      try {
        // 关键逻辑：将当前书籍对象的状态修改后提交
        await updateBooks({ ...book, booksStatus: newStatus })
        ElMessage.success(newStatus === '3' ? '申请已重新提交，请等待审核' : '书籍已下架')
        getMyBooks()
      } catch (e) {
        console.error(e)
      }
    }).catch(() => {})
  }
  
  const getRealImagePath = (url) => {
    if (!url) return ''
    return url.startsWith('http') ? url : import.meta.env.VITE_APP_BASE_API + url
  }
  
  onMounted(getMyBooks)
  </script>
  
  <style scoped>
  /* 保持原有高级感样式 */
  .my-books-container { background-color: #f8fafc; min-height: calc(100vh - 64px); padding-bottom: 60px; }
  .content-limit { max-width: 1100px; margin: 0 auto; padding: 0 25px; }
  
  .page-header { background: #fff; padding: 50px 0; border-bottom: 1px solid #f1f5f9; margin-bottom: 30px; }
  .page-title { font-size: 1.8rem; font-weight: 600; color: #1e293b; letter-spacing: 1px; }
  .page-desc { color: #94a3b8; font-size: 0.9rem; margin-top: 8px; }
  
  /* 选项卡样式 */
  :deep(.el-tabs__item) { font-size: 1rem; color: #64748b; }
  :deep(.el-tabs__item.is-active) { color: #6366f1; font-weight: 600; }
  :deep(.el-tabs__active-bar) { background-color: #6366f1; height: 3px; }
  
  .books-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 30px; margin-top: 30px; }
  
  /* 卡片与封面 */
  .modern-card { background: #fff; border-radius: 16px; overflow: hidden; transition: all 0.3s; display: flex; flex-direction: column; }
  .modern-card:hover { transform: translateY(-8px); box-shadow: 0 12px 30px rgba(0,0,0,0.08); }
  
  .cover-wrapper { position: relative; width: 100%; aspect-ratio: 3 / 4; overflow: hidden; background: #f1f5f9; }
  .book-image { width: 100%; height: 100%; object-fit: cover; }
  
  /* 状态滤镜 */
  .grey-scale .book-image { filter: grayscale(0.8) opacity(0.7); }
  .shadow-mode { background: #e2e8f0; }
  
  /* 状态标签 */
  .status-badge { position: absolute; top: 12px; right: 12px; padding: 4px 10px; border-radius: 6px; font-size: 11px; color: #fff; font-weight: 600; z-index: 1; }
  .on-shelf { background: rgba(16, 185, 129, 0.9); }
  .in-review { background: rgba(245, 158, 11, 0.9); }
  .off-shelf-tag { background: rgba(148, 163, 184, 0.9); } /* 灰色下架标签 */
  
  .book-info { padding: 15px; flex: 1; }
  .name { font-size: 1rem; font-weight: 600; color: #1e293b; margin-bottom: 5px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  .author { font-size: 13px; color: #94a3b8; margin-bottom: 10px; }
  .time { font-size: 12px; color: #cbd5e1; }
  .status-text { font-size: 12px; color: #f59e0b; font-style: italic; }
  
  .actions { margin-top: auto; padding-top: 12px; border-top: 1px solid #f8fafc; text-align: right; }
  </style>