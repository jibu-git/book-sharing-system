<template>
  <div class="orders-container">
    <div class="page-header">
      <h1 class="page-title">借出管理 (我是书主)</h1>
    </div>

    <div v-loading="loading" element-loading-text="加载中..."></div>

    <div class="empty-orders" v-if="!loading && orderList.length === 0">
      <el-empty description="暂时没有人借阅您的书"></el-empty>
    </div>

    <div class="orders-list" v-else>
      <div v-for="order in orderList" :key="order.orderId" class="order-item">
        <el-card 
          :id="'order-' + order.orderId"
          class="order-card" 
          shadow="hover" 
          :style="{ borderLeft: '5px solid ' + getStatusColor(order.orderStatus) }"
        >
          <div class="order-header">
            <div class="order-info">
              <span class="order-time">申请时间：{{ order.createTime }}</span>
              <span class="order-id">单号：{{ order.orderId }}</span>
            </div>
            <div class="order-status">
              <el-tag :type="getStatusType(order.orderStatus)">
                {{ getStatusText(order.orderStatus) }}
              </el-tag>
            </div>
          </div>

          <div class="order-items">
            <div 
              v-for="item in orderItemsMap[order.orderId]" 
              :key="item.orderItemId"
              class="order-item-row"
            >
              <div class="item-info">
                <div class="item-image">
                  <image-preview 
                    :src="item.bookImage" 
                    :width="60" 
                    :height="80"
                    fit="cover"
                  />
                </div>
                <div class="item-details">
                  <h4 class="item-title">{{ item.bookTitle }}</h4>
                </div>
              </div>
            </div>
          </div>

          <div class="order-footer">
            <div class="borrower-name">借阅人：{{ order.borrowerName || '匿名书友' }}</div>
            <div class="actions">
              <el-button 
                v-if="order.orderStatus === '1'" 
                type="success" 
                size="small" 
                @click="handleAgree(order.orderId)"
              >同意借阅申请</el-button>

              <el-button 
                v-if="order.orderStatus === '3'" 
                type="primary" 
                size="small" 
                @click="handleConfirm(order.orderId)"
              >确认收到归还</el-button>

              <span v-if="order.orderStatus === '4'" class="finish-text">交易已圆满完成</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router'; // 引入 useRoute
import { listOrder, agreeLendOrder, confirmReceipt } from "@/api/Order/torder";
import { listItem } from "@/api/orderItem/item";
import { ElMessage, ElMessageBox } from 'element-plus';

const route = useRoute(); // 初始化 route
const loading = ref(false);
const orderList = ref([]);
const orderItemsMap = ref({});

// 核心修改：高亮逻辑
const handleHighlight = () => {
  const highlightId = route.query.highlight;
  if (highlightId) {
    setTimeout(() => {
      const el = document.getElementById(`order-${highlightId}`);
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        el.classList.add('highlight-flash-owner');
      }
    }, 500);
  }
};

const getList = async () => {
  loading.value = true;
  try {
    const res = await listOrder({ role: 'owner' });
    orderList.value = res.rows;
    
    for (const order of res.rows) {
      const itemRes = await listItem({ orderId: order.orderId });
      orderItemsMap.value[order.orderId] = itemRes.rows;
    }
    // 核心修改：数据加载完后执行高亮
    handleHighlight();
  } catch (error) {
    console.error("加载借出记录失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleAgree = (orderId) => {
  ElMessageBox.confirm('同意借阅后，图书库存将正式扣减，确定吗？', '提示').then(async () => {
    await agreeLendOrder(orderId);
    ElMessage.success("已同意借阅");
    getList();
  });
};

const handleConfirm = (orderId) => {
  ElMessageBox.confirm('确定已收到归还的图书？确认后库存将恢复。', '确认收货').then(async () => {
    await confirmReceipt(orderId);
    ElMessage.success("订单已完成，库存已恢复");
    getList();
  });
};

const getStatusText = (status) => {
  const map = { '1': '待审核', '0': '出库中/借阅中', '3': '对方已还书', '4': '已归还', '2': '已拒绝' };
  return map[status] || '未知';
};

const getStatusType = (status) => {
  const map = { '1': 'warning', '0': 'success', '3': 'primary', '4': 'info', '2': 'danger' };
  return map[status] || 'info';
};

const getStatusColor = (status) => {
  const map = { '1': '#e6a23c', '0': '#67c23a', '3': '#409eff', '4': '#67c23a', '2': '#f56c6c' };
  return map[status] || '#909399';
};

onMounted(getList);
</script>

<style scoped>
.orders-container { padding: 20px; max-width: 900px; margin: 0 auto; background-color: #f8f9fa; }
.page-title { font-size: 1.5rem; margin-bottom: 20px; border-left: 4px solid #67c23a; padding-left: 15px; }
.order-card { margin-bottom: 15px; border: none; border-radius: 8px; transition: all 0.3s; }
.order-header { display: flex; justify-content: space-between; padding-bottom: 10px; border-bottom: 1px solid #eee; margin-bottom: 15px; }
.order-item-row { display: flex; align-items: center; margin-bottom: 10px; }
.item-info { display: flex; gap: 15px; }
.item-title { margin: 0; font-size: 15px; }
.order-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 15px; padding-top: 10px; border-top: 1px dashed #eee; }
.borrower-name { font-size: 13px; color: #409eff; font-weight: bold; }
.finish-text { color: #67c23a; font-weight: bold; font-size: 13px; }

/* 核心修改：书主端专属绿色系闪烁动画 */
.highlight-flash-owner {
  animation: flash-animation-green 3s ease-in-out;
  border: 2px solid #67c23a !important;
  box-shadow: 0 0 15px rgba(103, 194, 58, 0.4) !important;
}
@keyframes flash-animation-green {
  0% { transform: scale(1); background-color: #fff; }
  25% { transform: scale(1.02); background-color: #f0f9eb; }
  50% { transform: scale(1); background-color: #fff; }
  75% { transform: scale(1.01); background-color: #f0f9eb; }
  100% { transform: scale(1); background-color: #fff; }
}
</style>