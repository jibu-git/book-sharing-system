import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
import Layout from '@/layout'

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 * // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 * // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 * // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: "/:pathMatch(.*)*",
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },

  //前台首页路由

  {
    path: '',
    component: () => import('@/views/shuyi-Portal/Home.vue'),
  },

  //后台路由
  {
    path: '/sysIndex',
    component: Layout,
    children: [
      {
        path: '',
        component: () => import('@/views/index'),
        name: 'sysIndex',
        meta: { title: '首页', icon: 'dashboard', affix: true }
      }
    ]
  },

  //后台用户管理路由
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile/:activeTab?',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  },

  // 前台首页路由 - 使用 PortalLayout
  {
    path: '/index',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'PortalHome',
        component: () => import('@/views/shuyi-Portal/Home.vue'),
        meta: { title: '回到书城', icon: 'shopping', affix: true }
      }
    ]
  },

  // 书籍详情页
  {
    path: '/book/detail',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'BookDetail',
        component: () => import('@/views/shuyi-Portal/components/BookDetail.vue'),
      }
    ]
  },

  // 找书页面
  {
    path: '/findBooks',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'findBooks',
        component: () => import('@/views/shuyi-Portal/components/findBooks.vue'),
      }
    ]
  },

  // 搜索页面
  {
    path: '/search',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'Search',
        component: () => import('@/views/shuyi-Portal/components/Search.vue'),
      }
    ]
  },

  // 借阅车页面
  {
    path: '/userCart',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'userCart',
        component: () => import('@/views/shuyi-Portal/components/userCart.vue'),
      }
    ]
  },

  // 订单页面 (总订单)
  {
    path: '/userOrders',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'userOrders',
        component: () => import('@/views/shuyi-Portal/components/userOrders.vue'),
      }
    ]
  },

  // 收货地址页面
  {
    path: '/user/addressIndex',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'UserAddress',
        component: () => import('@/views/shuyi-Portal/components/AddressIndex.vue'),
      }
    ]
  },

  // 我的借入申请（借阅者订单）
  {
    path: '/user/borrowOrders',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'BorrowOrders',
        component: () => import('@/views/shuyi-Portal/components/BorrowOrders.vue'),
      }
    ]
  },

  // 我的借出申请（被借阅者订单）
  {
    path: '/user/lendOrders',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'LendOrders',
        component: () => import('@/views/shuyi-Portal/components/LendOrders.vue'),
      }
    ]
  },

  //前台个人中心中心页面
  {
    path: '/user/profileIndex',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'PortalProfile',
        component: () => import('@/views/shuyi-Portal/components/user/index.vue'),
        // meta: { title: '个人信息', icon: 'user' }
      }
    ]
  },

  // 1. 发布图书页面 (提交审核)
  {
    path: '/user/publishBook',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'PublishBook',
        component: () => import('@/views/shuyi-Portal/components/PublishBook.vue'),
        // meta: { title: '分享藏书' }
      }
    ]
  },

  // 2. 我的共享管理 (查看申请中、已上架、并支持下架)
  {
    path: '/user/mySharedBooks',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'MySharedBooks',
        component: () => import('@/views/shuyi-Portal/components/MySharedBooks.vue'),
        // meta: { title: '我的共享' }
      }
    ]
  },

  // 消息中心页面
  {
    path: '/user/message',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'UserMessage',
        component: () => import('@/views/shuyi-Portal/components/MessageList.vue'),
        // meta: { title: '消息中心', icon: 'message' }
      }
    ]
  },

  {
    path: '/user/comments',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'UserComments',
        component: () => import('@/views/shuyi-Portal/components/MyComments.vue'),
        // meta: { title: '我的评论', icon: 'comments' }
      }
    ]
  },


  {
    path: '/about',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'About',
        component: () => import('@/views/shuyi-Portal/components/About.vue'),
        // meta: { title: '关于图书分享' }
      }
    ]
  },
  
  

  // === 梦开始的地方页面 ===
  {
    path: '/dream',
    component: () => import('@/views/shuyi-Portal/layout/PortalLayout.vue'),
    children: [
      {
        path: '',
        name: 'Dream',
        component: () => import('@/views/shuyi-Portal/components/Dream.vue'),
        // meta: { title: '梦开始的地方' }
      }
    ]
  },
]



// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
  {
    path: '/system/user-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:user:edit'],
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' }
      }
    ]
  },
  {
    path: '/system/role-auth',
    component: Layout,
    hidden: true,
    permissions: ['system:role:edit'],
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' }
      }
    ]
  },
  {
    path: '/system/dict-data',
    component: Layout,
    hidden: true,
    permissions: ['system:dict:list'],
    children: [
      {
        path: 'index/:dictId(\\d+)',
        component: () => import('@/views/system/dict/data'),
        name: 'Data',
        meta: { title: '字典数据', activeMenu: '/system/dict' }
      }
    ]
  },
  {
    path: '/monitor/job-log',
    component: Layout,
    hidden: true,
    permissions: ['monitor:job:list'],
    children: [
      {
        path: 'index/:jobId(\\d+)',
        component: () => import('@/views/monitor/job/log'),
        name: 'JobLog',
        meta: { title: '调度日志', activeMenu: '/monitor/job' }
      }
    ]
  },
  {
    path: '/tool/gen-edit',
    component: Layout,
    hidden: true,
    permissions: ['tool:gen:edit'],
    children: [
      {
        path: 'index/:tableId(\\d+)',
        component: () => import('@/views/tool/gen/editTable'),
        name: 'GenEdit',
        meta: { title: '修改生成配置', activeMenu: '/tool/gen' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  },
})

export default router