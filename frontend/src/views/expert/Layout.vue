<template>
  <el-container class="layout-container">
    <!-- 顶部导航栏 -->
    <el-header class="header">
      <div class="header-left">
        <div class="header-logo">
          <i class="el-icon-s-shop header-logo-icon"></i>
          <span class="header-logo-text">药用植物病虫害管理系统</span>
        </div>
      </div>
      <div class="header-right">
        <div class="user-info">
          <span class="welcome-text">{{ username }}</span>
          <div class="user-avatar">
            <img v-if="userAvatar" :src="userAvatar" alt="用户头像" class="avatar-img" />
            <i v-else class="el-icon-user-solid default-avatar"></i>
          </div>
        </div>
        <el-button class="logout-btn" type="danger" size="small" @click="logout">
          <i class="el-icon-switch-button"></i>
          退出登录
        </el-button>
      </div>
    </el-header>

    <el-container>
      <!-- 侧边导航栏 -->
      <el-aside width="240px" class="sidebar">
        <div class="sidebar-header">
          <div class="expert-info">
            <div class="expert-avatar">
              <img v-if="userAvatar" :src="userAvatar" alt="专家头像" class="expert-avatar-img" />
              <i v-else class="el-icon-user-solid expert-default-avatar"></i>
            </div>
            <div class="expert-details">
              <div class="expert-name">{{ username }}</div>
              <div class="expert-role">专家用户</div>
            </div>
          </div>
        </div>

        <el-menu
          :default-active="activeIndex"
          class="sidebar-menu"
          background-color="#2c3e50"
          text-color="#ecf0f1"
          active-text-color="#3498db"
          router
        >
          <el-menu-item index="/expert/dashboard" class="menu-item">
            <i class="el-icon-s-home menu-icon"></i>
            <span slot="title">控制台</span>
          </el-menu-item>
          <el-submenu index="management" class="submenu">
            <template slot="title">
              <i class="el-icon-s-management menu-icon"></i>
              <span>数据管理</span>
            </template>
            <el-menu-item index="/expert/plants" class="submenu-item">
              <i class="el-icon-s-opportunity"></i>
              <span slot="title">药用植物</span>
            </el-menu-item>
            <el-menu-item index="/expert/pests" class="submenu-item">
              <i class="el-icon-warning-outline"></i>
              <span slot="title">病虫害</span>
            </el-menu-item>
            <el-menu-item index="/expert/pesticides" class="submenu-item">
              <i class="el-icon-s-tools"></i>
              <span slot="title">农药</span>
            </el-menu-item>
          </el-submenu>

          <el-submenu index="relationships" class="submenu">
            <template slot="title">
              <i class="el-icon-connection menu-icon"></i>
              <span>关系管理</span>
            </template>
            <el-menu-item index="/expert/plant-disease-links" class="submenu-item">
              <i class="el-icon-link"></i>
              <span slot="title">植物-病虫害</span>
            </el-menu-item>
            <el-menu-item index="/expert/disease-pesticide-links" class="submenu-item">
              <i class="el-icon-paperclip"></i>
              <span slot="title">病虫害-农药</span>
            </el-menu-item>
          </el-submenu>

          <el-menu-item index="/expert/helps" class="menu-item">
            <i class="el-icon-question menu-icon"></i>
            <span slot="title">求助回复</span>
          </el-menu-item>
          <el-menu-item index="/expert/profile" class="menu-item">
            <i class="el-icon-user menu-icon"></i>
            <span slot="title">个人中心</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'ExpertLayout',
  data() {
    return {
      activeIndex: this.$route.path,
      username: '专家用户',
      userAvatar: ''
    }
  },
  created() {
    this.loadUserInfo()
  },

  mounted() {
    // 监听用户信息更新事件
    window.addEventListener('userInfoUpdated', this.handleUserInfoUpdated)
  },

  beforeDestroy() {
    // 移除事件监听器
    window.removeEventListener('userInfoUpdated', this.handleUserInfoUpdated)
  },
  watch: {
    $route(to) {
      this.activeIndex = to.path
    }
  },
  methods: {
    loadUserInfo() {
      // 从localStorage获取专家用户信息
      const expertUser = localStorage.getItem('expert_user')
      if (expertUser) {
        try {
          const user = JSON.parse(expertUser)
          this.username = user.name || user.username || '专家用户'
          this.userAvatar = user.avatarUrl || user.avatar_url || ''
        } catch (e) {
          console.error('解析用户信息失败:', e)
          this.username = '专家用户'
          this.userAvatar = ''
        }
      }

      // 兼容旧版本的userInfo
      const userInfo = localStorage.getItem('userInfo')
      if (userInfo && !expertUser) {
        try {
          const user = JSON.parse(userInfo)
          this.username = user.name || user.nickname || user.username || '专家用户'
          this.userAvatar = user.avatarUrl || user.avatar_url || ''
        } catch (e) {
          console.error('解析用户信息失败:', e)
          this.username = '专家用户'
          this.userAvatar = ''
        }
      }
    },

    // 处理用户信息更新事件
    handleUserInfoUpdated(event) {
      if (event.detail && event.detail.avatarUrl) {
        this.userAvatar = event.detail.avatarUrl
      }
    },

    logout() {
      this.$confirm('确认退出登录?', '退出确认', {
        confirmButtonText: '确定退出',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'logout-confirm-dialog'
      })
        .then(() => {
          // 清除所有本地存储的用户信息
          localStorage.removeItem('expert_token')
          localStorage.removeItem('expert_user')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          localStorage.removeItem('userType')

          this.$message({
            type: 'success',
            message: '退出登录成功!',
            duration: 2000
          })

          // 跳转到专家登录页
          this.$router.push('/login')
        })
        .catch(() => {
          // 取消退出操作
        })
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 顶部导航栏样式 */
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  line-height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-logo {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}

.header-logo-icon {
  font-size: 24px;
  margin-right: 12px;
  color: #ffd700;
}

.header-logo-text {
  background: linear-gradient(45deg, #ffd700, #ffed4e);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome-text {
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}

.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.1);
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-avatar {
  font-size: 18px;
  color: #fff;
}

.logout-btn {
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  border: none;
  color: #fff;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logout-btn:hover {
  background: linear-gradient(135deg, #ff5252, #d32f2f);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.logout-btn i {
  margin-right: 4px;
}

/* 侧边栏样式 */
.sidebar {
  background: linear-gradient(180deg, #2c3e50 0%, #34495e 100%);
  color: #ecf0f1;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  background: rgba(0, 0, 0, 0.1);
}

.expert-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.expert-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #3498db;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(52, 152, 219, 0.1);
}

.expert-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.expert-default-avatar {
  font-size: 24px;
  color: #3498db;
}

.expert-details {
  flex: 1;
}

.expert-name {
  font-size: 16px;
  font-weight: 600;
  color: #ecf0f1;
  margin-bottom: 4px;
}

.expert-role {
  font-size: 12px;
  color: #bdc3c7;
  background: rgba(52, 152, 219, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
  display: inline-block;
}

/* 菜单样式 */
.sidebar-menu {
  border-right: none;
  background: transparent !important;
  padding: 16px 0;
}

.menu-item {
  margin: 8px 16px;
  border-radius: 15px;
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
  overflow: hidden;
  border: 1px solid transparent;
  backdrop-filter: blur(10px);
}

.menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg,
    transparent,
    rgba(0, 255, 255, 0.4),
    rgba(255, 0, 255, 0.4),
    transparent);
  transition: left 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  z-index: 0;
}

.menu-item::after {
  content: '';
  position: absolute;
  top: 50%;
  right: -10px;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-top: 8px solid #00ffff;
  transform: translateY(-50%) rotate(90deg);
  opacity: 0;
  transition: all 0.4s ease;
  z-index: 1;
}

.menu-item:hover::before {
  left: 100%;
}

.menu-item:hover::after {
  right: 15px;
  opacity: 1;
  transform: translateY(-50%) rotate(90deg) scale(1.2);
}

.menu-item:hover {
  background:
    linear-gradient(135deg,
      rgba(0, 255, 255, 0.25),
      rgba(255, 0, 255, 0.25)) !important;
  color: #00ffff !important;
  border-color: rgba(0, 255, 255, 0.6);
  box-shadow:
    0 8px 32px rgba(0, 255, 255, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  transform: translateX(12px) scale(1.02);
}

.menu-item.is-active {
  background:
    linear-gradient(135deg,
      rgba(0, 255, 255, 0.5),
      rgba(255, 0, 255, 0.5)) !important;
  color: #fff !important;
  border-color: rgba(0, 255, 255, 0.9);
  box-shadow:
    0 12px 40px rgba(0, 255, 255, 0.5),
    0 0 40px rgba(0, 255, 255, 0.3),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  transform: translateX(16px) scale(1.05);
}

.menu-item.is-active::after {
  right: 10px;
  opacity: 1;
  border-top-color: #fff;
  transform: translateY(-50%) rotate(90deg) scale(1.3);
  filter: drop-shadow(0 0 8px rgba(255, 255, 255, 0.8));
}

.menu-item.is-active::before {
  left: 0;
  background: linear-gradient(90deg,
    rgba(0, 255, 255, 0.2),
    rgba(255, 0, 255, 0.2));
  animation: activeGlow 2s ease-in-out infinite alternate;
}

@keyframes activeGlow {
  0% { opacity: 0.5; }
  100% { opacity: 1; }
}

.menu-icon {
  margin-right: 12px;
  font-size: 18px;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
  z-index: 2;
}

.menu-item:hover .menu-icon {
  text-shadow: 0 0 15px currentColor;
  transform: scale(1.2) rotate(5deg);
  filter: drop-shadow(0 0 10px rgba(0, 255, 255, 0.8));
}

.menu-item.is-active .menu-icon {
  text-shadow: 0 0 20px currentColor;
  transform: scale(1.3) rotate(-5deg);
  filter: drop-shadow(0 0 15px rgba(255, 255, 255, 1));
  animation: iconPulse 1.5s ease-in-out infinite alternate;
}

@keyframes iconPulse {
  0% { transform: scale(1.3) rotate(-5deg); }
  100% { transform: scale(1.4) rotate(5deg); }
}

/* 菜单文字动画 */
.menu-item span {
  position: relative;
  z-index: 2;
  transition: all 0.3s ease;
  font-weight: 500;
}

.menu-item:hover span {
  font-weight: 600;
  text-shadow: 0 0 10px currentColor;
  transform: translateX(3px);
}

.menu-item.is-active span {
  font-weight: 700;
  text-shadow: 0 0 15px currentColor;
  transform: translateX(5px);
}

/* 子菜单样式 */
.submenu {
  margin: 6px 16px;
}

.submenu .el-submenu__title {
  border-radius: 12px;
  transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  color: #fff !important;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(8px);
}

.submenu .el-submenu__title::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg,
    transparent,
    rgba(0, 255, 255, 0.3),
    transparent);
  transition: left 0.6s ease;
}

.submenu .el-submenu__title:hover::before {
  left: 100%;
}

.submenu .el-submenu__title:hover {
  background:
    linear-gradient(135deg,
      rgba(0, 255, 255, 0.2),
      rgba(255, 0, 255, 0.2)) !important;
  color: #00ffff !important;
  border: 1px solid rgba(0, 255, 255, 0.4);
  box-shadow: 0 6px 20px rgba(0, 255, 255, 0.3);
  transform: translateX(8px);
}

.submenu-item {
  margin: 4px 20px;
  border-radius: 10px;
  font-size: 13px;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  position: relative;
  overflow: hidden;
}

.submenu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg,
    transparent,
    rgba(0, 255, 255, 0.25),
    transparent);
  transition: left 0.5s ease;
}

.submenu-item:hover::before {
  left: 100%;
}

.submenu-item:hover {
  background:
    linear-gradient(135deg,
      rgba(0, 255, 255, 0.15),
      rgba(255, 0, 255, 0.15)) !important;
  color: #00ffff !important;
  border: 1px solid rgba(0, 255, 255, 0.3);
  box-shadow: 0 4px 15px rgba(0, 255, 255, 0.25);
  transform: translateX(6px) scale(1.02);
}

.submenu-item.is-active {
  background:
    linear-gradient(135deg,
      rgba(0, 255, 255, 0.4),
      rgba(255, 0, 255, 0.4)) !important;
  color: #fff !important;
  border: 1px solid rgba(0, 255, 255, 0.7);
  box-shadow:
    0 6px 25px rgba(0, 255, 255, 0.4),
    0 0 20px rgba(0, 255, 255, 0.2);
  transform: translateX(10px) scale(1.05);
}

.submenu-item.is-active::before {
  left: 0;
  background: linear-gradient(90deg,
    rgba(255, 255, 255, 0.1),
    rgba(255, 255, 255, 0.2),
    rgba(255, 255, 255, 0.1));
  animation: submenuActiveGlow 2s ease-in-out infinite alternate;
}

@keyframes submenuActiveGlow {
  0% { opacity: 0.6; }
  100% { opacity: 1; }
}

/* 子菜单图标动画 */
.submenu-item i {
  transition: all 0.3s ease;
  margin-right: 8px;
}

.submenu-item:hover i {
  transform: scale(1.1) rotate(10deg);
  text-shadow: 0 0 10px currentColor;
}

.submenu-item.is-active i {
  transform: scale(1.2) rotate(-10deg);
  text-shadow: 0 0 15px currentColor;
  animation: submenuIconSpin 2s ease-in-out infinite alternate;
}

@keyframes submenuIconSpin {
  0% { transform: scale(1.2) rotate(-10deg); }
  100% { transform: scale(1.3) rotate(10deg); }
}

/* 主内容区域 */
.main-content {
  background: #f8f9fa;
  padding: 24px;
  min-height: calc(100vh - 60px);
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    width: 200px !important;
  }

  .header-logo-text {
    display: none;
  }

  .welcome-text {
    display: none;
  }
}

/* 自定义滚动条 */
.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
}

.sidebar::-webkit-scrollbar-thumb {
  background: rgba(52, 152, 219, 0.5);
  border-radius: 3px;
}

.sidebar::-webkit-scrollbar-thumb:hover {
  background: rgba(52, 152, 219, 0.7);
}

/* 退出确认对话框样式 */
.logout-confirm-dialog .el-message-box__header {
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  color: #fff;
}
</style>