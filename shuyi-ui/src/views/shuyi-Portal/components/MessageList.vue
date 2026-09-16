<template>
  <div class="orders-container">
    <div class="page-header">
      <h1 class="page-title">消息中心</h1>
    </div>

    <div v-loading="loading" element-loading-text="正在获取通知..."></div>

    <div class="filter-tabs">
      <el-tabs v-model="queryParams.isRead" @tab-change="handleQuery">
        <el-tab-pane label="全部通知" name="" />
        <el-tab-pane label="未读" name="0" />
        <el-tab-pane label="已读" name="1" />
      </el-tabs>
    </div>

    <div class="orders-list" v-if="!loading && messageList.length > 0">
      <div v-for="msg in messageList" :key="msg.msgId" class="order-item">
        <el-card
          class="order-card msg-card"
          shadow="hover"
          :style="{
            borderLeft:
              '5px solid ' + (msg.isRead === '0' ? '#409eff' : '#dcdfe6'),
          }"
          @click="handleRead(msg)"
        >
          <div class="msg-header">
            <div class="msg-type-tag">
              <el-tag
                :type="msg.isRead === '0' ? 'danger' : 'info'"
                size="small"
                effect="dark"
              >
                {{ msg.isRead === "0" ? "未读" : "已读" }}
              </el-tag>
              <span class="msg-time">{{ msg.createTime }}</span>
            </div>
            <el-button link type="primary" size="small">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>

          <div class="msg-content-box">
            <h4 class="msg-title">{{ msg.title }}</h4>
            <p class="msg-text">{{ msg.content }}</p>
          </div>

          <div class="msg-footer" v-if="msg.relatedId">
            <span class="related-hint">
              <el-icon><Link /></el-icon>
              <template v-if="msg.msgType === '5'"
                >相关图书ID：{{ msg.relatedId }}</template
              >
              <template v-else>关联单号：{{ msg.relatedId }}</template>
            </span>
          </div>
        </el-card>
      </div>
    </div>

    <div class="empty-orders" v-else-if="!loading">
      <el-empty description="暂无相关消息通知" />
    </div>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
      class="msg-pagination"
    />
  </div>
</template>
  
  <script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { listMessage, updateMessage } from "@/api/message/message";
import { ArrowRight, Link } from "@element-plus/icons-vue";

const router = useRouter();
const loading = ref(false);
const total = ref(0);
const messageList = ref([]);

const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  isRead: "",
});

const getList = async () => {
  loading.value = true;
  try {
    const res = await listMessage(queryParams.value);
    messageList.value = res.rows;
    total.value = res.total;
  } catch (error) {
    console.error("加载消息失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleRead = async (msg) => {
  // 1. 如果是未读状态，调用接口标记为已读
  if (msg.isRead === "0") {
    try {
      await updateMessage({ msgId: msg.msgId, isRead: "1" });
      msg.isRead = "1"; // 前端同步更新 UI
    } catch (e) {
      console.error("更新已读状态失败", e);
    }
  }

  // 2. 跳转逻辑判断
  if (!msg.relatedId) return;

  // 根据 msgType 进行精准跳转
  switch (msg.msgType) {
    case "1": // 书主消息 -> 我的借出
      router.push({
        path: "/user/lendOrders",
        query: { highlight: msg.relatedId },
      });
      break;

    case "4": // 借阅者消息 -> 我的借入
      router.push({
        path: "/user/borrowOrders",
        query: { highlight: msg.relatedId },
      });
      break;

    case "5": // 【新增】评论回复消息 -> 跳转到图书详情页
      router.push({
        path: "/book/detail",
        query: { id: msg.relatedId },
      });
      break;

    default:
      // 兜底策略
      if (msg.title.includes("通过") || msg.title.includes("拒绝")) {
        router.push({
          path: "/user/borrowOrders",
          query: { highlight: msg.relatedId },
        });
      } else if (msg.title.includes("回复")) {
        router.push({ path: "/book/detail", query: { id: msg.relatedId } });
      } else {
        router.push({
          path: "/user/lendOrders",
          query: { highlight: msg.relatedId },
        });
      }
      break;
  }
};

const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
};

onMounted(getList);
</script>
  
  <style scoped>
/* 样式统一保持 BorrowOrders 的简约风格 */
.orders-container {
  padding: 20px;
  max-width: 900px;
  margin: 0 auto;
  background-color: #f8f9fa;
}
.page-header {
  margin-bottom: 20px;
}
.page-title {
  font-size: 1.5rem;
  margin-bottom: 20px;
  border-left: 4px solid #409eff;
  padding-left: 15px;
  color: #333;
}

.filter-tabs {
  margin-bottom: 20px;
  background: #fff;
  padding: 0 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.order-card {
  margin-bottom: 15px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}
.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.msg-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.msg-time {
  font-size: 13px;
  color: #999;
  margin-left: 10px;
}

.msg-content-box {
  padding: 5px 0;
}
.msg-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  color: #303133;
  font-weight: bold;
}
.msg-text {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.msg-footer {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px dashed #ebeef5;
}
.related-hint {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 4px;
}

.msg-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 移除 Tabs 下划线，保持清爽 */
:deep(.el-tabs__nav-wrap::after) {
  display: none;
}
:deep(.el-tabs__header) {
  margin-bottom: 0;
}
</style>