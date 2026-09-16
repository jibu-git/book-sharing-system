<template>
  <div class="orders-container">
    <div class="page-header">
      <h1 class="page-title">我的订单</h1>
    </div>

    <div v-loading="loading" element-loading-text="正在加载订单..."></div>

    <div class="empty-orders" v-if="!loading && ordersList.length === 0">
      <el-empty description="暂无订单">
        <template #extra>
          <el-button type="primary" @click="goToBooks" round>去逛逛</el-button>
        </template>
      </el-empty>
    </div>

    <div class="orders-list" v-else>
      <div 
        v-for="order in ordersList" 
        :key="order.orderId"
        class="order-item"
      >
        <el-card class="order-card" shadow="hover">
          <div class="order-header">
            <div class="order-info">
              <span class="order-time">下单时间：{{ order.createTime }}</span>
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
              v-for="item in getOrderItems(order.orderId)" 
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
                  <p class="item-quantity">数量：{{ item.bookNum }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="order-footer">
            <div class="order-address">
              <span class="address-label">收货地址：</span>
              <span class="address-text">
                {{ order.orderRecvProvince }}{{ order.orderRecvCity }}{{ order.orderRecvAddress }}
                ({{ order.orderRecvName }} {{ order.orderRecvPhone }})
              </span>
            </div>
            <div class="order-actions">
              <div class="action-buttons">
                <el-button 
                  v-if="order.orderStatus === '0'"
                  type="danger" 
                  size="small"
                  plain
                  @click="handleCancelOrder(order)"
                >
                  取消订单
                </el-button>

                <el-button 
                  v-if="order.orderStatus === '3' || order.orderStatus === '4'"
                  type="info" 
                  size="small"
                  plain
                  @click="handleDeleteOrder(order)"
                >
                  删除记录
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div class="pagination-container" v-show="total > 0">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup name="UserOrders">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listOrder, delOrder, updateOrder } from "@/api/Order/torder" // 假设 updateOrder 用于取消
import { listItem } from "@/api/orderItem/item"

const router = useRouter()
const ordersList = ref([])
const orderItemsList = ref([])
const loading = ref(true)
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

const orderItemsMap = computed(() => {
  const map = new Map()
  orderItemsList.value.forEach(item => {
    const oId = item.orderId 
    if (!map.has(oId)) {
      map.set(oId, [])
    }
    map.get(oId).push(item)
  })
  return map
})

const getOrderItems = (orderId) => {
  return orderItemsMap.value.get(orderId) || []
}

/** 获取订单列表 */
const fetchOrders = async () => {
  loading.value = true
  try {
    const response = await listOrder(queryParams)
    ordersList.value = response.rows
    total.value = response.total
    
    if (ordersList.value.length > 0) {
      await fetchOrderItems()
    }
  } catch (error) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

/** 获取订单图书明细 */
const fetchOrderItems = async () => {
  try {
    const orderIds = ordersList.value.map(order => order.orderId)
    if (orderIds.length === 0) return

    const response = await listItem({
      orderItemOrderIdList: orderIds 
    })
    
    orderItemsList.value = response.rows
  } catch (error) {
    console.error('获取明细失败:', error)
  }
}

/** 取消订单逻辑：通常是把状态改为已取消(4) */
const handleCancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', { type: 'warning' })
    // 这里调用更新接口，将状态改为已拒绝/取消（4）
    await updateOrder({ orderId: order.orderId, orderStatus: '4' })
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {}
}

/** 【新增】删除订单逻辑：限制只能删除状态为 3 或 4 的订单 */
const handleDeleteOrder = async (order) => {
  try {
    await ElMessageBox.confirm('删除后将无法找回该订单记录，确定删除吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'error'
    })
    await delOrder(order.orderId)
    ElMessage.success('订单记录已删除')
    fetchOrders()
  } catch (error) {}
}

const getStatusText = (status) => {
  const statusMap = { '0': '待审核', '1': '待发货', '2': '已发货', '3': '已完成', '4': '已拒绝/取消' }
  return statusMap[status] || '未知'
}

const getStatusType = (status) => {
  const typeMap = { '0': 'warning', '1': 'primary', '2': 'success', '3': 'info', '4': 'danger' }
  return typeMap[status] || 'info'
}

const handleSizeChange = (val) => { queryParams.pageSize = val; fetchOrders(); }
const handleCurrentChange = (val) => { queryParams.pageNum = val; fetchOrders(); }
const goToBooks = () => router.push('/findBooks')

onMounted(fetchOrders)
</script>

<style scoped>
/* 保持原有 CSS 样式不变 */
.orders-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 80vh;
}

.page-header {
  margin-bottom: 30px;
}

.page-title {
  font-size: 2rem;
  font-weight: bold;
  color: #333;
  text-align: center;
}

.empty-orders {
  text-align: center;
  padding: 60px 0;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  border-radius: 12px;
  border: none;
}

.order-card :deep(.el-card__body) {
  padding: 0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e8e8e8;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.order-time,
.order-id {
  color: #666;
  font-size: 0.9rem;
}

.order-items {
  padding: 20px;
}

.order-item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
}

.order-item-row:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  gap: 15px;
  align-items: center;
  flex: 1;
}

.item-image {
  flex-shrink: 0;
}

.item-details {
  flex: 1;
}

.item-title {
  font-size: 1rem;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
  line-height: 1.3;
}

.item-quantity {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 5px;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f8f9fa;
  border-top: 1px solid #e8e8e8;
}

.order-address {
  flex: 1;
}

.address-label {
  font-weight: bold;
  color: #333;
}

.address-text {
  color: #666;
}

.order-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

@media (max-width: 768px) {
  .orders-container { padding: 15px; }
  .order-header { flex-direction: column; gap: 10px; align-items: flex-start; }
  .order-footer { flex-direction: column; gap: 15px; align-items: flex-start; }
  .order-actions { width: 100%; justify-content: flex-end; }
}
</style>