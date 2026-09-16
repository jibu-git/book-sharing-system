<template>
    <div class="my-comments-container">
      <el-card class="box-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span class="title">我的评论记录</span>
          </div>
        </template>
  
        <div class="comments-wrapper" v-loading="loading">
          <div v-if="commentList.length > 0" class="comment-list">
            <div v-for="item in commentList" :key="item.commentId" class="comment-item">
              <div class="item-header">
                <span class="time">
                  <el-icon><Calendar /></el-icon>
                  {{ item.createTime }}
                </span>
                <div class="actions">
                  <el-button link type="primary" @click="goToBook(item.bookId)">
                    <el-icon><Reading /></el-icon> 查看图书
                  </el-button>
                  <el-button link type="danger" @click="handleDelete(item)">
                    <el-icon><Delete /></el-icon> 删除
                  </el-button>
                </div>
              </div>
  
              <div class="item-body">
                <div v-if="item.replyToName" class="reply-badge">
                  回复 <span class="reply-target">@{{ item.replyToName }}</span> :
                </div>
                <div class="content">{{ item.content }}</div>
              </div>
  
              <div class="item-footer" v-if="item.likeCount > 0">
                <span class="likes">
                  <el-icon><Pointer /></el-icon> 获赞 {{ item.likeCount }}
                </span>
              </div>
            </div>
          </div>
  
          <el-empty 
            v-else 
            description="您还没有发表过任何评论，快去看看书吧~" 
            :image-size="120"
          >
            <el-button type="primary" @click="goToIndex">去发现好书</el-button>
          </el-empty>
        </div>
  
        <div class="pagination-container" v-if="total > 0">
          <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :page-sizes="[10, 20, 30, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            background
          />
        </div>
      </el-card>
    </div>
  </template>
  
  <script setup name="MyComments">
  import { ref, reactive, onMounted, computed } from "vue";
  import { useRouter } from "vue-router";
  import { ElMessage, ElMessageBox } from "element-plus";
  import { Calendar, Reading, Delete, Pointer } from "@element-plus/icons-vue";
  import useUserStore from '@/store/modules/user';
  
  import { delComment,listComment } from "@/api/Comment/comment";
  import request from '@/utils/request'; 
  
  const router = useRouter();
  const userStore = useUserStore();
  
  // 获取当前登录用户ID
  const currentUserId = computed(() => userStore.userId || userStore.id);
  
  const loading = ref(false);
  const commentList = ref([]);
  const total = ref(0);
  
  // 查询参数
  const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    userId: null, // 稍后在 onMounted 中赋值
    orderByColumn: 'create_time',
    isAsc: 'desc' // 默认按时间倒序排列
  });
  
  /** 查询评论列表 */
  const getList = async () => {
    loading.value = true;
    try {
      // 强制指定只查询当前用户的评论
      queryParams.userId = currentUserId.value;
      
      // 调用若依标准的列表查询接口，如果你的 api 文件中封装了 listComment，请直接替换为:

      const res = await listComment(queryParams);
      
      commentList.value = res.rows;
      total.value = res.total;
    } catch (error) {
      console.error("获取评论列表失败", error);
    } finally {
      loading.value = false;
    }
  };
  
  /** 跳转到对应的图书详情页 */
  const goToBook = (bookId) => {
    if (!bookId) {
      ElMessage.warning("无法找到关联的图书");
      return;
    }
    router.push({
      path: "/book/detail",
      query: { id: bookId },
    });
  };
  
  /** 跳转到首页 */
  const goToIndex = () => {
    router.push("/index");
  };
  
  /** 删除按钮操作 */
  const handleDelete = (item) => {
    ElMessageBox.confirm('确定要删除这条评论吗？', '系统提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await delComment(item.commentId);
      ElMessage.success("删除成功");
      // 删除后重新加载列表
      getList();
    }).catch(() => {});
  };
  
  /** 分页处理 */
  const handleSizeChange = (newSize) => {
    queryParams.pageSize = newSize;
    getList();
  };
  
  const handleCurrentChange = (newPage) => {
    queryParams.pageNum = newPage;
    getList();
  };
  
  onMounted(() => {
    // 确保拿到了用户ID再去请求
    if (currentUserId.value) {
      getList();
    }
  });
  </script>
  
  <style scoped>
  .my-comments-container {
    padding: 20px;
    max-width: 1000px;
    margin: 0 auto;
  }
  
  .box-card {
    border-radius: 8px;
    border: 1px solid #ebeef5;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .title {
    font-size: 16px;
    font-weight: bold;
    color: #303133;
  }
  
  .comments-wrapper {
    min-height: 200px;
  }
  
  /* 评论列表项样式 */
  .comment-item {
    padding: 20px 0;
    border-bottom: 1px solid #f0f2f5;
    transition: background-color 0.3s ease;
  }
  
  .comment-item:last-child {
    border-bottom: none;
  }
  
  .comment-item:hover {
    background-color: #fafafa;
    border-radius: 4px;
  }
  
  .item-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    padding: 0 10px;
  }
  
  .time {
    font-size: 13px;
    color: #909399;
    display: flex;
    align-items: center;
    gap: 5px;
  }
  
  .actions {
    display: flex;
    gap: 15px;
  }
  
  .item-body {
    padding: 0 10px;
    margin-bottom: 12px;
  }
  
  .reply-badge {
    font-size: 13px;
    color: #606266;
    margin-bottom: 8px;
    background-color: #f4f4f5;
    padding: 4px 8px;
    border-radius: 4px;
    display: inline-block;
  }
  
  .reply-target {
    color: #409eff;
    font-weight: 500;
  }
  
  .content {
    font-size: 14px;
    color: #303133;
    line-height: 1.6;
    white-space: pre-wrap; /* 保持评论换行格式 */
  }
  
  .item-footer {
    padding: 0 10px;
  }
  
  .likes {
    font-size: 12px;
    color: #ff9900;
    display: flex;
    align-items: center;
    gap: 4px;
    background-color: #fff8e6;
    padding: 2px 8px;
    border-radius: 10px;
    display: inline-block;
  }
  
  /* 分页容器 */
  .pagination-container {
    margin-top: 30px;
    display: flex;
    justify-content: flex-end;
  }
  </style>