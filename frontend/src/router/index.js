import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录 - 药用植物病虫害管理系统' }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册 - 药用植物病虫害管理系统' }
  },
  // 管理员路由
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true, userType: 'admin' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue'),
        meta: { title: '管理控制台 - 药用植物病虫害管理系统' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/Users.vue'),
        meta: { title: '普通用户管理 - 药用植物病虫害管理系统' }
      },
      {
        path: 'experts',
        name: 'AdminExperts',
        component: () => import('../views/admin/Experts.vue'),
        meta: { title: '专家用户管理 - 药用植物病虫害管理系统' }
      },
      {
        path: 'comments',
        name: 'AdminComments',
        component: () => import('../views/admin/Comments.vue'),
        meta: { title: '评论审核 - 药用植物病虫害管理系统' }
      },
      {
        path: 'help-requests',
        name: 'AdminHelpRequests',
        component: () => import('../views/admin/HelpRequests.vue'),
        meta: { title: '求助审核 - 药用植物病虫害管理系统' }
      },
      {
        path: 'analytics',
        name: 'AdminAnalytics',
        component: () => import('../views/admin/Analytics.vue'),
        meta: { title: '数据可视化 - 药用植物病虫害管理系统' }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('../views/admin/Profile.vue'),
        meta: { title: '个人中心 - 药用植物病虫害管理系统' }
      }
    ]
  },
  // 专家用户路由
  {
    path: '/expert',
    component: () => import('../views/expert/Layout.vue'),
    redirect: '/expert/dashboard',
    meta: { requiresAuth: true, userType: 'expert' },
    children: [
      {
        path: 'dashboard',
        name: 'ExpertDashboard',
        component: () => import('../views/expert/Dashboard.vue'),
        meta: { title: '专家工作台 - 药用植物病虫害管理系统' }
      },
      { path: 'plants', name: 'ExpertPlants', component: () => import('../views/expert/Plants.vue'), meta: { title: '药用植物管理 - 药用植物病虫害管理系统' } },
      { path: 'pests', name: 'ExpertPests', component: () => import('../views/expert/Pests.vue'), meta: { title: '病虫害管理 - 药用植物病虫害管理系统' } },
      { path: 'pesticides', name: 'ExpertPesticides', component: () => import('../views/expert/Pesticides.vue'), meta: { title: '农药管理 - 药用植物病虫害管理系统' } },
      { path: 'plant-disease-links', name: 'PlantDiseaseLinks', component: () => import('../views/expert/PlantDiseaseLinks.vue'), meta: { title: '植物-病虫害关系管理 - 药用植物病虫害管理系统' } },
      { path: 'disease-pesticide-links', name: 'DiseasePesticideLinks', component: () => import('../views/expert/DiseasePesticideLinks.vue'), meta: { title: '病虫害-农药关系管理 - 药用植物病虫害管理系统' } },
      { path: 'helps', name: 'ExpertHelps', component: () => import('../views/expert/Helps.vue'), meta: { title: '求助回复 - 药用植物病虫害管理系统' } },
      { path: 'profile', name: 'ExpertProfile', component: () => import('../views/expert/Profile.vue'), meta: { title: '个人中心 - 药用植物病虫害管理系统' } },
    ]
  },
  // 普通用户路由
  {
    path: '/normal',
    component: () => import('../views/normal/Layout.vue'),
    redirect: '/normal/plants',
    meta: { requiresAuth: true, userType: 'normal' },
    children: [
      {
        path: 'plants',
        name: 'NormalPlants',
        component: () => import('../views/normal/Plants.vue'),
        meta: { title: '药用植物 - 药用植物病虫害管理系统' }
      },
      {
        path: 'plant-detail/:id',
        name: 'PlantDetail',
        component: () => import('../views/normal/PlantDetail.vue'),
        meta: { title: '植物详情 - 药用植物病虫害管理系统' }
      },
      {
        path: 'pests',
        name: 'NormalPestDiseases',
        component: () => import('../views/normal/PestDiseases.vue'),
        meta: { title: '病虫害 - 药用植物病虫害管理系统' }
      },
      {
        path: 'pest-disease-detail/:id',
        name: 'PestDiseaseDetail',
        component: () => import('../views/normal/PestDiseaseDetail.vue'),
        meta: { title: '病虫害详情 - 药用植物病虫害管理系统' }
      },
      {
        path: 'pesticides',
        name: 'NormalPesticides',
        component: () => import('../views/normal/Pesticides.vue'),
        meta: { title: '农药信息 - 药用植物病虫害管理系统' }
      },
      {
        path: 'pesticide-detail/:id',
        name: 'PesticideDetail',
        component: () => import('../views/normal/PesticideDetail.vue'),
        meta: { title: '农药详情 - 药用植物病虫害管理系统' }
      },
      {
        path: 'help',
        name: 'HelpCenter',
        component: () => import('../views/normal/HelpCenter.vue'),
        meta: { title: '求助中心 - 药用植物病虫害管理系统' }
      },
      {
        path: 'help-detail/:id',
        name: 'HelpDetail',
        component: () => import('../views/normal/HelpDetail.vue'),
        meta: { title: '求助详情 - 药用植物病虫害管理系统' }
      },

      {
        path: 'my-comments',
        name: 'MyComments',
        component: () => import('../views/normal/MyComments.vue'),
        meta: { title: '我的评论 - 药用植物病虫害管理系统' }
      },
      {
        path: 'profile',
        name: 'NormalProfile',
        component: () => import('../views/normal/Profile.vue'),
        meta: { title: '个人中心 - 药用植物病虫害管理系统' }
      }
    ]
  },
  // 兼容旧路由
  {
    path: '/plants',
    redirect: '/normal/plants'
  },
  // 404页面
  {
    path: '*',
    component: () => import('../views/404.vue'),
    meta: { title: '页面未找到 - 药用植物病虫害管理系统' }
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

// 全局导航守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title || '药用植物病虫害管理系统'
  
  // 检查是否需要登录验证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const userType = localStorage.getItem('userType')
    const userInfo = localStorage.getItem('userInfo')
    
    if (!userInfo) {
      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else if (to.meta.userType && to.meta.userType !== userType) {
      // 用户类型不匹配，跳转到对应的首页
      switch (userType) {
        case 'admin':
          next('/admin/dashboard')
          break
        case 'expert':
          next('/expert/dashboard')
          break
        case 'normal':
          next('/normal/plants')
          break
        default:
          next('/login')
      }
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router