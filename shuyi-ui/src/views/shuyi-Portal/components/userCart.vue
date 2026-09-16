<template>
  <div class="cart-container">
    <div class="page-header">
      <h1 class="page-title">借阅清单</h1>
      <p class="page-subtitle">管理您打算借阅的图书，确认后发起借阅申请</p>
    </div>

    <div v-loading="loading" element-loading-text="正在同步图书实时状态..."></div>

    <div class="empty-cart" v-if="!loading && cartList.length === 0">
      <el-empty description="您的借阅清单还是空的">
        <template #extra>
          <el-button type="primary" size="large" @click="goToBooks" round>
            去馆藏看看
          </el-button>
        </template>
      </el-empty>
    </div>

    <div class="cart-content" v-else-if="cartList.length > 0">
      <div class="cart-header">
        <div class="col-select">
          <el-checkbox 
            v-model="isSelectAll" 
            @change="handleSelectAll"
          >全选</el-checkbox>
        </div>
        <div class="col-info">图书详细信息</div>
        <div class="col-quantity">借阅数量</div>
        <div class="col-action">操作</div>
      </div>

      <div class="cart-list">
        <div 
          v-for="item in cartList" 
          :key="item.cartId" 
          class="cart-item"
        >
          <div class="item-select">
            <el-checkbox 
              v-model="item.selected" 
              @change="handleSelectChange"
            />
          </div>
          
          <div class="item-info">
            <div class="book-cover-wrapper">
              <el-image 
                :src="getRealImagePath(item.bookInfo?.booksCover)" 
                :width="80" 
                :height="110"
                fit="cover"
                class="book-cover"
                @click="goToBookDetail(item.cartBooksId)"
              >
                <template #error><div class="img-err">无封面</div></template>
              </el-image>
            </div>
            <div class="book-details">
              <h3 class="book-title" @click="goToBookDetail(item.cartBooksId)">
                {{ item.bookInfo?.booksName || item.cartBooksName }}
              </h3>
              <div class="book-meta">
                <span>作者：{{ item.bookInfo?.booksAuthor || '未知' }}</span>
                <span class="divider">|</span>
                <span>出版社：{{ item.bookInfo?.booksPublisher || '待补全' }}</span>
              </div>
              <div class="book-tags">
                <el-tag type="info" size="small" effect="plain">
                  馆内读物
                </el-tag>
                <el-tag v-if="item.bookInfo?.availableStock > 0" size="small" type="success" class="ml-2">
                  库存: {{ item.bookInfo.availableStock }}
                </el-tag>
                <el-tag v-else size="small" type="danger" class="ml-2">
                  暂时借完
                </el-tag>
              </div>
            </div>
          </div>

          <div class="item-quantity">
            <el-input-number 
              v-model="item.cartNum" 
              :min="1" 
              :max="1"
              size="small" 
              disabled
            />
          </div>

          <div class="item-action">
            <el-button 
              type="danger" 
              link 
              @click="handleDeleteItem(item.cartId)"
              class="delete-btn"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>
        </div>
      </div>

      <div class="cart-footer">
        <div class="footer-left">
          <el-checkbox 
            v-model="isSelectAll" 
            @change="handleSelectAll"
          >全选</el-checkbox>
          <span class="selected-count">已选中 <strong>{{ selectedCount }}</strong> 本图书</span>
        </div>
        
        <div class="footer-right">
          <div class="total-info">
            <span class="label">预计借阅总数：</span>
            <span class="count">{{ selectedCount }} 本</span>
          </div>
          <el-button 
            type="primary" 
            size="large" 
            round 
            class="checkout-btn"
            :disabled="selectedCount === 0"
            @click="handleCheckout"
            :loading="submitting"
          >
            发起借阅申请
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Delete } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { listCart, delCart, orderCart } from "@/api/TCart/cart";
import { portalListBooks } from "@/api/Books/books"; // 引入图书列表接口获取详情

const router = useRouter();
const loading = ref(false);
const submitting = ref(false);
const cartList = ref([]);
const isSelectAll = ref(false);

/**
 * 获取购物车数据并与图书详情合并
 */
const getListData = async () => {
  loading.value = true;
  try {
    // 同时请求购物车和图书列表（获取最新库存、作者、出版社）
    const [cartRes, booksRes] = await Promise.all([
      listCart(),
      portalListBooks({ pageNum: 1, pageSize: 1000 })
    ]);

    const remoteBooks = booksRes.rows || [];
    
    // 逻辑缝合：将图书对象嵌入购物车对象中
    cartList.value = cartRes.rows.map(cartItem => {
      // 在 portalListBooks 的结果中查找对应的图书详情
      const bookDetail = remoteBooks.find(b => b.booksId === cartItem.cartBooksId);
      
      return {
        ...cartItem,
        selected: false,
        // 将匹配到的图书详情挂载到 bookInfo 属性下，方便 HTML 访问
        bookInfo: bookDetail || null 
      };
    });
  } catch (error) {
    console.error("加载数据失败:", error);
    ElMessage.error("获取清单信息失败");
  } finally {
    loading.value = false;
  }
};

const selectedCount = computed(() => {
  return cartList.value.filter(item => item.selected).length;
});

const handleSelectAll = (val) => {
  cartList.value.forEach(item => { item.selected = val; });
};

const handleSelectChange = () => {
  isSelectAll.value = cartList.value.length > 0 && cartList.value.every(item => item.selected);
};

const handleCheckout = async () => {
  const selectedItems = cartList.value.filter(item => item.selected);
  if (selectedItems.length === 0) return;

  // 下单前的前端库存校验
  const outOfStock = selectedItems.find(item => !item.bookInfo || item.bookInfo.availableStock <= 0);
  if (outOfStock) {
    ElMessage.warning(`图书 [${outOfStock.cartBooksName}] 已被借完，请先移除`);
    return;
  }

  const cartIds = selectedItems.map(item => item.cartId);

  try {
    await ElMessageBox.confirm(`确定为这 ${selectedItems.length} 本图书发起借阅申请吗？`, '确认申请');
    submitting.value = true;
    const res = await orderCart(cartIds);
    if (res) {
      ElMessage.success("借阅申请提交成功！");
      router.push('/user/borrowOrders');
    }
  } catch (error) {
    if (error !== 'cancel') console.error(error);
  } finally {
    submitting.value = false;
  }
};

const handleDeleteItem = (id) => {
  ElMessageBox.confirm('确定要移除吗？', '提示', { type: 'warning' }).then(async () => {
    await delCart(id);
    ElMessage.success("已移除");
    getListData();
  });
};

const getRealImagePath = (url) => {
  if (!url) return '';
  if (url.startsWith('http')) return url;
  return import.meta.env.VITE_APP_BASE_API + url;
};

const goToBookDetail = (id) => {
  router.push({ path: '/book/detail', query: { id } });
};

const goToBooks = () => router.push('/findBooks');

onMounted(getListData);
</script>

<style scoped>
/* 保持你的样式不变 */
.cart-container { max-width: 1100px; margin: 0 auto; padding: 40px 20px 80px; min-height: 80vh; }
.page-header { margin-bottom: 40px; }
.page-title { font-size: 2rem; font-weight: 300; color: #1e293b; letter-spacing: 2px; margin-bottom: 8px; }
.page-subtitle { color: #94a3b8; font-size: 0.95rem; }
.cart-content { background: #fff; border-radius: 20px; box-shadow: 0 10px 30px rgba(0,0,0,0.03); overflow: hidden; border: 1px solid #f1f5f9; }
.cart-header { display: flex; align-items: center; padding: 20px 30px; background: #f8fafc; border-bottom: 1px solid #f1f5f9; color: #64748b; font-size: 0.9rem; font-weight: 600; }
.col-select { width: 80px; }
.col-info { flex: 1; }
.col-quantity { width: 180px; text-align: center; }
.col-action { width: 120px; text-align: center; }
.cart-item { display: flex; align-items: center; padding: 25px 30px; border-bottom: 1px solid #f8fafc; transition: all 0.3s ease; }
.item-select { width: 80px; }
.item-info { flex: 1; display: flex; align-items: center; }
.book-cover-wrapper { margin-right: 25px; }
.book-cover { width: 80px; height: 110px; border-radius: 12px; object-fit: cover; box-shadow: 0 5px 15px rgba(0,0,0,0.08); cursor: pointer; }
.book-details { flex: 1; }
.book-title { font-size: 1.15rem; font-weight: 700; color: #1e293b; margin: 0 0 10px; cursor: pointer; }
.book-title:hover { color: #6366f1; }
.book-meta { font-size: 0.9rem; color: #64748b; margin-bottom: 12px; display: flex; align-items: center; }
.divider { margin: 0 10px; color: #cbd5e1; }
.book-tags { display: flex; align-items: center; gap: 8px; }
.ml-2 { margin-left: 8px; }
.item-quantity { width: 180px; display: flex; justify-content: center; }
.item-action { width: 120px; text-align: center; }
.delete-btn { color: #94a3b8; }
.delete-btn:hover { color: #f43f5e; }
.cart-footer { display: flex; justify-content: space-between; align-items: center; padding: 20px 30px; background: #fff; border-top: 2px solid #f1f5f9; }
.selected-count { margin-left: 20px; font-size: 0.9rem; color: #64748b; }
.selected-count strong { color: #1e293b; margin: 0 4px; }
.footer-right { display: flex; align-items: center; gap: 30px; }
.total-info { text-align: right; }
.total-info .label { color: #94a3b8; font-size: 0.9rem; }
.total-info .count { display: block; font-size: 1.4rem; font-weight: 800; color: #1e293b; }
.checkout-btn { padding: 0 40px; font-weight: 600; height: 50px; }
.empty-cart { padding: 100px 0; background: #fff; border-radius: 20px; }
.img-err { background: #f1f5f9; color: #94a3b8; display: flex; align-items: center; justify-content: center; height: 110px; width: 80px; border-radius: 12px; font-size: 12px; }
</style>