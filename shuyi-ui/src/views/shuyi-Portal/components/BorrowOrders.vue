<template>
  <div class="orders-container">
    <div class="page-header">
      <h1 class="page-title">我的借入申请</h1>
    </div>

    <div v-loading="loading" element-loading-text="正在加载借阅记录..."></div>

    <div class="empty-orders" v-if="!loading && orderList.length === 0">
      <el-empty description="暂无借入记录">
        <el-button type="primary" @click="goToBooks" round>去逛逛</el-button>
      </el-empty>
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
              <span class="order-id">订单号：{{ order.orderId }}</span>
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
            <div class="owner-name">书主：{{ order.ownerName || '共享书友' }}</div>
            <div class="actions">
              <el-button 
                v-if="order.orderStatus === '0'" 
                type="primary" 
                size="small" 
                @click="handleReturn(order.orderId)"
              >归还图书</el-button>
              
              <span v-if="order.orderStatus === '3'" class="wait-text">等待书主确认收货...</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router'; // 引入 useRoute
import { listOrder, returnOrder } from "@/api/Order/torder";
import { listItem } from "@/api/orderItem/item";
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const route = useRoute(); // 初始化 route
const loading = ref(false);
const orderList = ref([]);
const orderItemsMap = ref({});

// 核心修改：处理高亮逻辑
const handleHighlight = () => {
  const highlightId = route.query.highlight;
  if (highlightId) {
    setTimeout(() => {
      const el = document.getElementById(`order-${highlightId}`);
      if (el) {
        el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        el.classList.add('highlight-flash');
      }
    }, 500); // 预留半秒给 DOM 加载
  }
};

const getList = async () => {
  loading.value = true;
  try {
    const res = await listOrder({ role: 'borrower' });
    orderList.value = res.rows;
    
    for (const order of res.rows) {
      const itemRes = await listItem({ orderId: order.orderId });
      orderItemsMap.value[order.orderId] = itemRes.rows;
    }
    // 核心修改：数据加载完成后触发高亮
    handleHighlight();
  } catch (error) {
    console.error("加载借入记录失败:", error);
  } finally {
    loading.value = false;
  }
};

const handleReturn = (orderId) => {
  ElMessageBox.confirm('确定已将图书寄出或交还给书主？', '提示').then(async () => {
    await returnOrder(orderId);
    ElMessage.success("已发起归还申请，请等待书主确认");
    getList();
  });
};

const goToBooks = () => router.push('/findBooks');

const getStatusText = (status) => {
  const map = { '1': '已申请', '0': '借阅中', '3': '已归还(待确认)', '4': '已完成', '2': '已取消' };
  return map[status] || '未知';
};

const getStatusType = (status) => {
  const map = { '1': 'warning', '0': 'success', '3': 'primary', '4': 'info', '2': 'danger' };
  return map[status] || 'info';
};

const getStatusColor = (status) => {
  const map = { '1': '#e6a23c', '0': '#67c23a', '3': '#409eff', '4': '#909399', '2': '#f56c6c' };
  return map[status] || '#909399';
};

onMounted(getList);
</script>

<style scoped>
.orders-container { padding: 20px; max-width: 900px; margin: 0 auto; background-color: #f8f9fa; }
.page-title { font-size: 1.5rem; margin-bottom: 20px; border-left: 4px solid #409eff; padding-left: 15px; }
.order-card { margin-bottom: 15px; border: none; border-radius: 8px; transition: all 0.3s; }
.order-header { display: flex; justify-content: space-between; padding-bottom: 10px; border-bottom: 1px solid #eee; margin-bottom: 15px; }
.order-time { font-size: 13px; color: #999; }
.order-item-row { display: flex; align-items: center; margin-bottom: 10px; }
.item-info { display: flex; gap: 15px; }
.item-title { margin: 0; font-size: 15px; color: #333; }
.order-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 15px; padding-top: 10px; border-top: 1px dashed #eee; }
.owner-name { font-size: 13px; color: #666; }
.wait-text { font-size: 12px; color: #409eff; }

/* 核心修改：新增高亮闪烁动画 */
.highlight-flash {
  animation: flash-animation 3s ease-in-out;
  border: 2px solid #409eff !important;
  box-shadow: 0 0 15px rgba(64, 158, 255, 0.4) !important;
}
@keyframes flash-animation {
  0% { transform: scale(1); background-color: #fff; }
  25% { transform: scale(1.02); background-color: #ecf5ff; }
  50% { transform: scale(1); background-color: #fff; }
  75% { transform: scale(1.01); background-color: #ecf5ff; }
  100% { transform: scale(1); background-color: #fff; }
}
</style>