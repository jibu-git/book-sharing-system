<template>
  <div class="book-detail-container">
    <div class="detail-content" v-loading="loading">
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/findBooks' }">所有书籍</el-breadcrumb-item>
          <el-breadcrumb-item>{{ book?.booksName }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="detail-main" v-if="book">
        <el-row :gutter="50">
          <el-col :span="8">
            <div class="book-cover-section">
              <div class="cover-container">
                <image-preview
                  :src="book.booksCover"
                  :width="300"
                  :height="400"
                  fit="cover"
                  class="main-cover"
                />
              </div>
            </div>
          </el-col>

          <el-col :span="16">
            <div class="book-info-section">
              <h1 class="book-title">{{ book.booksName }}</h1>

              <div class="book-meta-box">
                <div class="meta-item">
                  <span class="label">作者：</span>
                  <span class="value">{{ book.booksAuthor }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">出版社：</span>
                  <span class="value">{{ book.booksPublisher }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">可借状态：</span>
                  <el-tag
                    :type="book.booksStatus === '0' ? 'success' : 'danger'"
                    size="default"
                    effect="dark"
                  >
                    {{ book.booksStatus === "0" ? "在架可借" : "已被借走" }}
                  </el-tag>
                </div>
              </div>

              <div class="action-section">
                <el-button
                  type="primary"
                  size="large"
                  :disabled="book.booksStatus !== '0'"
                  @click="handleAddToCart"
                  class="action-btn add-cart-btn"
                >
                  <el-icon><ShoppingCart /></el-icon>加入借阅车
                </el-button>
                <el-button
                  type="success"
                  size="large"
                  :disabled="book.booksStatus !== '0'"
                  @click="handleQuickApply"
                  class="action-btn buy-now-btn"
                >
                  <el-icon><CircleCheck /></el-icon>立即借阅
                </el-button>
                <el-button
                  size="large"
                  @click="handleBack"
                  class="action-btn back-btn"
                >
                  <el-icon><ArrowLeft /></el-icon>返回列表
                </el-button>
              </div>
            </div>
          </el-col>
        </el-row>

        <div class="description-section">
          <el-card class="description-card" shadow="never">
            <template #header>
              <div class="card-header">
                <span>内容简介</span>
              </div>
            </template>
            <div class="description-content">
              <p v-if="book.booksDescription">{{ book.booksDescription }}</p>
              <p v-else class="no-description">主人很懒，没有留下关于这本书的描述哦~</p>
            </div>
          </el-card>
        </div>

        <div class="comment-area-section">
          <el-divider content-position="left">
            <span class="comment-divider-title">读者评论</span>
          </el-divider>

          <div class="comment-post-box">
            <el-input
              v-model="commentForm.content"
              type="textarea"
              :rows="3"
              placeholder="分享你的阅读感受..."
              maxlength="500"
              show-word-limit
            />
            <div class="post-btn-wrapper">
              <el-button
                type="primary"
                @click="submitComment(0, 0, null)"
                :disabled="!commentForm.content"
              >发表评论</el-button>
            </div>
          </div>

          <div v-loading="commentLoading" class="comment-list-container">
            <div
              v-for="item in commentTree"
              :key="item.commentId"
              class="comment-node"
            >
              <el-avatar :size="40" :src="getFullAvatar(item.avatar)">
                {{ item.userName?.charAt(0) }}
              </el-avatar>
              
              <div class="node-main">
                <div class="node-header">
                  <span class="node-author">{{ item.userName }}</span>
                  <span class="node-time">{{ item.createTime }}</span>
                </div>
                <div class="node-body">{{ item.content }}</div>
                
                <div class="node-footer">
                  <el-button
                    link
                    :type="isLiked(item.commentId) ? 'primary' : 'default'"
                    size="small"
                    @click="handleLike(item)"
                    :disabled="isLiked(item.commentId)"
                  >
                    <el-icon><Pointer /></el-icon> {{ item.likeCount || 0 }}
                  </el-button>

                  <el-button link type="primary" size="small" @click="handleReply(item)">回复</el-button>

                  <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, item)">
                    <span class="more-btn"><el-icon><MoreFilled /></el-icon></span>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="report">举报</el-dropdown-item>
                        <el-dropdown-item 
                          v-if="checkIsMine(item.userId)" 
                          command="delete" 
                          style="color: #f56c6c"
                        >删除</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </div>

                <div v-if="item.children && item.children.length > 0" class="sub-comment-list">
                  <div
                    v-for="sub in item.children"
                    :key="sub.commentId"
                    class="sub-comment-item"
                  >
                    <el-avatar :size="28" :src="getFullAvatar(sub.avatar)">
                      {{ sub.userName?.charAt(0) }}
                    </el-avatar>
                    <div class="sub-main">
                      <div class="node-header">
                        <span class="node-author">{{ sub.userName }}</span>
                        <span class="reply-text">回复</span>
                        <span class="reply-target">@{{ sub.replyToName }}</span>
                        <span class="node-time">{{ sub.createTime }}</span>
                      </div>
                      <div class="node-body">{{ sub.content }}</div>
                      
                      <div class="node-footer">
                        <el-button
                          link
                          :type="isLiked(sub.commentId) ? 'primary' : 'default'"
                          size="small"
                          @click="handleLike(sub)"
                          :disabled="isLiked(sub.commentId)"
                        >
                          <el-icon><Pointer /></el-icon> {{ sub.likeCount || 0 }}
                        </el-button>

                        <el-button
                          link
                          type="primary"
                          size="small"
                          @click="handleReply(item, sub)"
                        >回复</el-button>

                        <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, sub)">
                          <span class="more-btn"><el-icon><MoreFilled /></el-icon></span>
                          <template #dropdown>
                            <el-dropdown-menu>
                              <el-dropdown-item command="report">举报</el-dropdown-item>
                              <el-dropdown-item 
                                v-if="checkIsMine(sub.userId)" 
                                command="delete" 
                                style="color: #f56c6c"
                              >删除</el-dropdown-item>
                            </el-dropdown-menu>
                          </template>
                        </el-dropdown>
                      </div>
                    </div>
                  </div>
                </div>

                <div
                  v-if="activeReplyId === item.commentId"
                  class="inline-reply-box"
                >
                  <el-input
                    v-model="replyText"
                    type="textarea"
                    :rows="2"
                    :placeholder="'回复 @' + replyTarget.name + '...'"
                  />
                  <div class="inline-reply-footer">
                    <el-button size="small" @click="activeReplyId = null">取消</el-button>
                    <el-button
                      size="small"
                      type="primary"
                      @click="submitComment(item.commentId, replyTarget.id, replyTarget.name)"
                    >提交回复</el-button>
                  </div>
                </div>
              </div>
            </div>
            <el-empty
              v-if="commentTree.length === 0"
              :image-size="60"
              description="暂无评论"
            />
          </div>
        </div>

        <div class="related-section" v-if="relatedBooks.length > 0">
          <h2 class="section-title">猜你还想借</h2>
          <div class="related-books">
            <div
              v-for="relatedBook in relatedBooks"
              :key="relatedBook.booksId"
              class="related-book-item"
              @click="goToBookDetail(relatedBook)"
            >
              <el-card class="related-book-card" shadow="hover">
                <div class="related-book-cover">
                  <image-preview
                    :src="relatedBook.booksCover"
                    :width="80"
                    :height="110"
                    fit="cover"
                  />
                </div>
                <div class="related-book-info">
                  <h4 class="related-book-title">{{ relatedBook.booksName }}</h4>
                  <p class="related-book-author">{{ relatedBook.booksAuthor }}</p>
                </div>
              </el-card>
            </div>
          </div>
        </div>
      </div>

      <div class="not-found" v-else-if="!loading">
        <el-empty description="哎呀，这本书好像飞走了">
          <el-button type="primary" @click="handleBack">返回首页</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup name="BookDetail">
import { ref, onMounted, watch, reactive, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ShoppingCart, CircleCheck, ArrowLeft, Pointer, MoreFilled } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import useUserStore from '@/store/modules/user'; // 引入若依的用户状态管理

import { quickCheckout } from "@/api/Order/torder";
import { getBooks, portalListBooks } from "@/api/Books/books";
import { addCart } from "@/api/TCart/cart";
import { getCommentTree, addComment, delComment, updateComment } from "@/api/Comment/comment";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 核心修改：利用 computed 确保稳健获取当前用户 ID
const currentUserId = computed(() => userStore.userId || userStore.id);

// 点赞前端防刷缓存
const likedIds = ref(JSON.parse(localStorage.getItem('liked_comments') || '[]'));

const book = ref(null);
const relatedBooks = ref([]);
const loading = ref(true);

const commentLoading = ref(false);
const commentTree = ref([]);
const commentForm = reactive({ content: "" });
const activeReplyId = ref(null);
const replyText = ref("");
const replyTarget = ref({ id: 0, name: "" });

// 监听路由改变，实现无刷新加载推荐书籍
watch(
  () => route.query.id,
  (newId) => {
    if (newId) {
      loading.value = true;
      fetchBookDetail();
    }
  }
);

/** * 核心权限判断函数：判断是否是当前登录用户的评论
 */
const checkIsMine = (commentOwnerId) => {
  if (!currentUserId.value || !commentOwnerId) return false;
  // 强制转换为字符串比较，彻底解决 Long 与 String 比较失败的问题
  return String(commentOwnerId) === String(currentUserId.value);
};

const isLiked = (id) => likedIds.value.includes(id);

/** 下拉菜单指令处理 */
const handleCommand = (command, item) => {
  if (command === 'delete') {
    handleDeleteComment(item.commentId);
  } else if (command === 'report') {
    handleReport(item);
  }
};

const handleLike = async (item) => {
  if (isLiked(item.commentId)) return;
  try {
    const newCount = (item.likeCount || 0) + 1;
    await updateComment({
      commentId: item.commentId,
      likeCount: newCount
    });
    item.likeCount = newCount;
    likedIds.value.push(item.commentId);
    localStorage.setItem('liked_comments', JSON.stringify(likedIds.value));
    ElMessage.success("点赞成功");
  } catch (e) {}
};

const handleDeleteComment = (commentId) => {
  ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await delComment(commentId);
    ElMessage.success("已删除");
    loadComments(book.value.booksId);
  }).catch(() => {});
};

const handleReport = (item) => {
  ElMessageBox.prompt('请输入举报原因', '提示', {
    confirmButtonText: '提交',
    cancelButtonText: '取消',
  }).then(({ value }) => {
    ElMessage.success("感谢反馈，管理员将会在 24 小时内处理");
  }).catch(() => {});
};

const fetchBookDetail = async () => {
  try {
    const bookId = route.query.id;
    if (!bookId) {
      loading.value = false;
      return;
    }
    const response = await getBooks(bookId);
    book.value = response.data;

    fetchRelatedBooks();
    loadComments(bookId);
  } catch (error) {
    book.value = null;
  } finally {
    loading.value = false;
  }
};

const fetchRelatedBooks = async () => {
  try {
    const response = await portalListBooks({
      booksTypeId: book.value.booksTypeId,
      pageSize: 4,
    });
    relatedBooks.value = response.rows
      .filter((item) => item.booksId !== book.value.booksId)
      .slice(0, 3);
  } catch (error) {}
};

const loadComments = async (id) => {
  commentLoading.value = true;
  try {
    const res = await getCommentTree(id);
    commentTree.value = res.data;
  } catch (e) {
  } finally {
    commentLoading.value = false;
  }
};

const submitComment = async (rootId, parentId, replyName) => {
  const content = rootId === 0 ? commentForm.content : replyText.value;
  if (!content.trim()) return;

  const data = {
    bookId: book.value.booksId,
    content: content,
    rootId: rootId,
    replyToId: parentId,
    replyToName: replyName,
  };

  try {
    await addComment(data);
    ElMessage.success("发表成功");
    if (rootId === 0) {
      commentForm.content = "";
    } else {
      replyText.value = "";
      activeReplyId.value = null;
    }
    loadComments(book.value.booksId);
  } catch (e) {}
};

const getFullAvatar = (avatar) => {
  if (!avatar) return "";
  if (avatar.startsWith("http")) return avatar;
  return import.meta.env.VITE_APP_BASE_API + avatar;
};

const handleReply = (root, sub = null) => {
  activeReplyId.value = root.commentId;
  if (sub) {
    replyTarget.value = { id: sub.userId, name: sub.userName };
  } else {
    replyTarget.value = { id: root.userId, name: root.userName };
  }
};

const handleAddToCart = async () => {
  try {
    await addCart({
      cartBooksId: book.value.booksId,
      cartNum: 1,
    });
    ElMessage.success("成功放入借阅车！");
  } catch (error) {}
};

const handleQuickApply = async () => {
  if (!book.value || !book.value.booksId) return;
  try {
    const response = await quickCheckout({
      booksId: book.value.booksId,
      num: 1,
    });
    if (response.code === 200) {
      ElMessage.success("借阅申请已发送，请等待书主确认！");
      router.push("/user/borrowOrders");
    }
  } catch (error) {}
};

const handleBack = () => router.back();

const goToBookDetail = (bookItem) => {
  router.push({
    path: "/book/detail",
    query: { id: bookItem.booksId },
  });
};

onMounted(() => {
  fetchBookDetail();
});
</script>

<style scoped>
/* 原有主体样式全部保留 */
.book-detail-container { max-width: 1000px; margin: 0 auto; padding: 30px 20px; }
.breadcrumb { margin-bottom: 30px; }
.detail-main { margin-bottom: 50px; }
.book-cover-section { display: flex; justify-content: center; }
.cover-container { background: #fff; border-radius: 12px; padding: 10px; box-shadow: 0 8px 24px rgba(149, 157, 165, 0.15); transition: transform 0.3s ease; }
.cover-container:hover { transform: translateY(-5px); }
.main-cover { border-radius: 8px; display: block; }
.book-info-section { padding: 10px 0; display: flex; flex-direction: column; height: 100%; }
.book-title { font-size: 2rem; font-weight: 800; margin-bottom: 25px; color: #2c3e50; line-height: 1.4; }
.book-meta-box { background: #f8fafc; padding: 25px; border-radius: 12px; margin-bottom: 40px; border: 1px solid #e2e8f0; }
.meta-item { display: flex; align-items: center; margin-bottom: 16px; font-size: 1rem; }
.meta-item .label { color: #64748b; min-width: 90px; letter-spacing: 1px; }
.meta-item .value { color: #334155; font-weight: 500; }
.action-section { display: flex; gap: 15px; margin-top: auto; }
.action-btn { flex: 1; border-radius: 8px; font-weight: bold; letter-spacing: 1px; }
.add-cart-btn { background-color: #3b82f6; border-color: #3b82f6; }
.buy-now-btn { background-color: #10b981; border-color: #10b981; }
.back-btn { flex: 0.5; }
.description-section { margin: 40px 0; }
.description-card { border-radius: 12px; background: #fff; border: 1px solid #e2e8f0; }
.card-header { font-size: 1.1rem; font-weight: bold; color: #1e293b; display: flex; align-items: center; }
.card-header::before { content: ""; display: inline-block; width: 4px; height: 16px; background: #3b82f6; margin-right: 8px; border-radius: 2px; }
.description-content { line-height: 1.8; color: #475569; font-size: 1rem; padding: 10px 0; }
.no-description { color: #94a3b8; font-style: italic; }

/* 评论区样式 */
.comment-area-section { margin-top: 50px; }
.comment-divider-title { font-size: 1.1rem; font-weight: bold; color: #1e293b; }
.comment-post-box { margin: 20px 0 40px; }
.post-btn-wrapper { display: flex; justify-content: flex-end; margin-top: 10px; }
.comment-node { display: flex; gap: 15px; margin-bottom: 30px; }
.node-main { flex: 1; }
.node-header { display: flex; align-items: center; gap: 10px; margin-bottom: 8px; }
.node-author { font-weight: 600; color: #334155; font-size: 14px; }
.node-time { font-size: 12px; color: #94a3b8; }
.node-body { font-size: 14px; color: #475569; line-height: 1.6; margin-bottom: 8px; }
.node-footer { display: flex; align-items: center; gap: 15px; margin-top: 8px; }

/* 核心修改：三个点按钮样式（将其变为可点击的内联块区域） */
.more-btn { 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  cursor: pointer; 
  transform: rotate(90deg); 
  color: #94a3b8; 
  padding: 4px 6px; /* 增加点击热区 */
  border-radius: 4px;
  transition: all 0.3s;
}
.more-btn:hover { color: #3b82f6; background-color: #f1f5f9; }

/* 子评论样式 */
.sub-comment-list { background: #f8fafc; border-radius: 8px; padding: 15px; margin-top: 15px; }
.sub-comment-item { display: flex; gap: 10px; margin-bottom: 15px; }
.sub-comment-item:last-child { margin-bottom: 0; }
.sub-main { flex: 1; }
.reply-text { font-size: 12px; color: #94a3b8; margin: 0 4px; }
.reply-target { font-size: 13px; color: #3b82f6; font-weight: 500; }
.inline-reply-box { margin-top: 15px; border: 1px solid #e2e8f0; padding: 15px; border-radius: 8px; background: #fff; }
.inline-reply-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 10px; }

/* 推荐书籍 */
.related-section { margin-top: 50px; }
.section-title { font-size: 1.3rem; font-weight: bold; margin-bottom: 25px; color: #1e293b; }
.related-books { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; }
.related-book-item { cursor: pointer; transition: all 0.3s ease; }
.related-book-item:hover { transform: translateY(-4px); }
.related-book-card { border-radius: 12px; border: 1px solid #e2e8f0; }
.related-book-card :deep(.el-card__body) { padding: 15px; display: flex; gap: 15px; align-items: center; }
.related-book-info { flex: 1; display: flex; flex-direction: column; justify-content: center; }
.related-book-title { font-size: 1rem; font-weight: 600; margin-bottom: 6px; color: #1e293b; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.related-book-author { font-size: 0.85rem; color: #64748b; margin: 0; }

@media (max-width: 768px) {
  .action-section { flex-direction: column; }
  .action-btn { width: 100%; margin-bottom: 10px; }
  .related-books { grid-template-columns: 1fr; }
}
</style>