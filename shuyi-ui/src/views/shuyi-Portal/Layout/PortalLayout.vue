<template>
  <div class="portal-layout">
    <header class="portal-header" :class="{ 'is-scrolled': isScrolled }">
      <div class="nav-container">
        <div class="nav-left">
          <router-link to="/index" class="logo-link">
            <div class="logo">
              <img src="@/assets/logo/shuyi2.png" alt="书易" class="logo-img">
            </div>
          </router-link>
        </div>

        <nav class="nav-center">
          <router-link to="/index" class="nav-item">首页</router-link>
          <router-link to="/findBooks" class="nav-item">找书</router-link>
          <router-link to="/user/publishBook" class="nav-item highlight-nav">分享藏书</router-link>
          <router-link to="/user/borrowOrders" class="nav-item">我的借入</router-link>
          <router-link to="/user/lendOrders" class="nav-item">我的借出</router-link>
          
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索书籍..."
              clearable
              class="custom-search"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </nav>

        <div class="nav-right">
          <div v-if="isLoggedIn" class="nav-icon-item" @click="$router.push('/user/message')">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99" class="msg-badge">
              <el-icon class="header-icon"><Bell /></el-icon>
            </el-badge>
          </div>

          <div v-if="isLoggedIn" class="nav-icon-item" @click="$router.push('/userCart')">
            <el-badge :value="cartCount" :hidden="cartCount === 0" :max="99" class="cart-badge">
              <el-icon class="header-icon"><ShoppingCart /></el-icon>
            </el-badge>
          </div>

          <router-link 
            v-if="isAdmin" 
            to="/sysIndex" 
            class="nav-item admin-link"
          >
            进入后台
          </router-link>
          
          <el-dropdown @command="handleUserCommand" v-if="isLoggedIn">
            <div class="user-info">
              <el-avatar :size="36" :src="fullAvatarUrl" class="user-avatar">
                <img src="@/assets/images/profile.jpg" />
              </el-avatar>
              <span class="user-name">{{ userStore.nickName || '用户' }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </div>
            
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown-menu">
                <el-dropdown-item command="profile"><el-icon><User /></el-icon>个人中心</el-dropdown-item>
                <el-dropdown-item command="message"><el-icon><Bell /></el-icon>消息中心</el-dropdown-item>
                <el-dropdown-item command="comments"><el-icon><ChatDotRound /></el-icon>我的评论</el-dropdown-item>
                <el-dropdown-item command="myShared"><el-icon><Collection /></el-icon>我的共享管理</el-dropdown-item>
                <el-dropdown-item command="userCart"><el-icon><ShoppingCart /></el-icon>我的借阅车</el-dropdown-item>
                <el-dropdown-item command="orders"><el-icon><Document /></el-icon>总订单管理</el-dropdown-item>
                <el-dropdown-item command="address"><el-icon><Location /></el-icon>收货地址</el-dropdown-item>
                <el-dropdown-item divided command="logout" class="logout-item">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          
          <div v-else class="login-tip">
            <el-button type="primary" round plain size="small" @click="$router.push('/login')">登录 / 注册</el-button>
          </div>
        </div>
      </div>
    </header>

    <main class="portal-main">
      <router-view v-slot="{ Component }">
        <transition name="fade-transform" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
  </div>
</template>

<script setup name="PortalLayout">
import { ref, onMounted, computed, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import {
  Search, User, ShoppingCart, Document, SwitchButton, Location, ArrowDown, Collection, Bell, ChatDotRound
} from "@element-plus/icons-vue";
import useUserStore from "@/store/modules/user";
import { ElMessageBox } from "element-plus";
import { getUnreadCount } from "@/api/message/message"; 

const userStore = useUserStore();
const router = useRouter();

const searchKeyword = ref("");
const isScrolled = ref(false);
const cartCount = ref(0); 
const unreadCount = ref(0); 

const isLoggedIn = computed(() => !!userStore.token);

/** 【新增】管理员判断逻辑：必须登录且 ID 为 1 */
const isAdmin = computed(() => {
  return isLoggedIn.value && (userStore.userId === 1 || userStore.id === 1);
});

const fullAvatarUrl = computed(() => {
  const avatar = userStore.avatar;
  if (!avatar || avatar === '') return "";
  if (avatar.startsWith("/profile")) {
    return import.meta.env.VITE_APP_BASE_API + avatar;
  }
  return avatar;
});

const getLatestUserInfo = async () => {
  if (isLoggedIn.value) {
    try {
      await userStore.getInfo();
      const res = await getUnreadCount();
      unreadCount.value = res.data || 0;
    } catch (error) {
      console.error("同步用户信息失败:", error);
    }
  }
};

const handleScroll = () => { isScrolled.value = window.scrollY > 10; };
const handleSearch = () => {
  const keyword = searchKeyword.value.trim();
  if (keyword) router.push({ path: "/search", query: { keyword } });
};

const handleUserCommand = (command) => {
  const routeMap = {
    profile: "/user/profileIndex", 
    message: "/user/message",
    comments: "/user/comments",
    myShared: "/user/mySharedBooks",
    address: "/user/addressIndex",
    userCart: "/userCart",
    orders: "/userOrders"
  };
  if (command === "logout") {
    logout();
  } else if (routeMap[command]) {
    router.push(routeMap[command]);
  }
};

const logout = () => {
  ElMessageBox.confirm("确定注销并退出系统吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  }).then(() => {
    userStore.logOut().then(() => {
      router.push("/index");
    });
  }).catch(() => {});
};

onMounted(() => {
  getLatestUserInfo();
  window.addEventListener('scroll', handleScroll);
});

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<style scoped>
.portal-layout { min-height: 100vh; display: flex; flex-direction: column; background-color: #f4f6f8; }
.portal-header { position: fixed; top: 0; left: 0; right: 0; background: rgba(255, 255, 255, 0.95); backdrop-filter: blur(10px); z-index: 1000; height: 64px; border-bottom: 1px solid #ebeef5; transition: all 0.3s ease; }
.portal-header.is-scrolled { box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06); }
.nav-container { display: flex; align-items: center; justify-content: space-between; max-width: 1200px; margin: 0 auto; padding: 0 20px; height: 100%; }
.nav-left { flex-shrink: 0; }
.logo-img { width: 120px; height: auto; max-height: 40px; }

.nav-center { display: flex; flex: 1; justify-content: center; gap: 20px; align-items: center; }
.nav-item { text-decoration: none; color: #475669; font-size: 14px; padding: 8px 12px; transition: color 0.2s; }
.nav-item:hover, .router-link-active { color: #6366f1; font-weight: bold; }
.highlight-nav { color: #6366f1 !important; font-weight: 600 !important; }

.search-box { width: 200px; margin-left: 10px; }
.custom-search :deep(.el-input__wrapper) { border-radius: 20px; background-color: #f5f7fa; box-shadow: none !important; border: 1px solid #e2e8f0; }

.nav-right { display: flex; align-items: center; gap: 20px; margin-left: 20px; }

.nav-icon-item { cursor: pointer; display: flex; align-items: center; transition: transform 0.2s; }
.nav-icon-item:hover { transform: scale(1.1); }
.header-icon { font-size: 22px; color: #475669; }
.header-icon:hover { color: #6366f1; }

.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; padding: 4px 8px; border-radius: 8px; transition: background 0.2s; }
.user-info:hover { background: #f1f5f9; }
.user-name { font-size: 14px; color: #1e293b; font-weight: 500; }

/* 强调后台入口样式，区别于普通导航 */
.admin-link { color: #94a3b8; font-size: 13px; border: 1px solid #e2e8f0; border-radius: 4px; padding: 4px 8px !important; }
.admin-link:hover { background-color: #f8fafc; border-color: #6366f1; }

.portal-main { flex: 1; margin-top: 64px; padding: 0; }
.logout-item { color: #f56c6c; }

.fade-transform-enter-active, .fade-transform-leave-active { transition: all 0.3s; }
.fade-transform-enter-from { opacity: 0; transform: translateX(-20px); }
.fade-transform-leave-to { opacity: 0; transform: translateX(20px); }
</style>