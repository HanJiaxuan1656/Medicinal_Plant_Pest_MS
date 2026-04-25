<template>
  <div class="normal-layout">
    <!-- 顶部导航栏 -->
    <div class="header">
      <div class="header-content">
        <div class="logo">
          <div class="logo-icon">
            <i class="el-icon-s-opportunity"></i>
          </div>
          <div class="logo-text">
            <span class="main-title">药用植物管理系统</span>
            <span class="sub-title">普通用户版</span>
          </div>
        </div>

        <div class="nav-menu">
          <el-menu
            :default-active="activeIndex"
            mode="horizontal"
            @select="handleSelect"
            background-color="transparent"
            text-color="#fff"
            active-text-color="#ffd700"
          >
            <el-menu-item index="/normal/plants" class="nav-item">
              <div class="nav-item-content">
                <i class="el-icon-s-opportunity"></i>
                <span>药用植物</span>
              </div>
            </el-menu-item>
            <el-menu-item index="/normal/pests" class="nav-item">
              <div class="nav-item-content">
                <i class="el-icon-warning-outline"></i>
                <span>病虫害</span>
              </div>
            </el-menu-item>
            <el-menu-item index="/normal/pesticides" class="nav-item">
              <div class="nav-item-content">
                <i class="el-icon-s-tools"></i>
                <span>农药信息</span>
              </div>
            </el-menu-item>
            <el-menu-item index="/normal/help" class="nav-item">
              <div class="nav-item-content">
                <i class="el-icon-question"></i>
                <span>求助中心</span>
              </div>
            </el-menu-item>
          </el-menu>
        </div>

        <div class="user-info">
          <div class="user-stats">
            <div class="stat-item">
              <i class="el-icon-view"></i>
              <span>{{ userStats.viewCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <i class="el-icon-chat-dot-round"></i>
              <span>{{ userStats.commentCount || 0 }}</span>
            </div>
          </div>

          <el-dropdown trigger="click" @command="handleCommand" class="user-dropdown-wrapper">
            <span class="user-dropdown">
              <el-avatar
                :size="36"
                :src="currentUser.avatarUrl"
                icon="el-icon-user-solid"
                class="user-avatar"
              ></el-avatar>
              <div class="user-text">
                <span class="username">{{ currentUser.nickname || '用户' }}</span>
                <span class="user-role">普通用户</span>
              </div>
              <i class="el-icon-arrow-down dropdown-arrow"></i>
            </span>
            <el-dropdown-menu slot="dropdown" class="user-dropdown-menu">
              <el-dropdown-item command="profile" class="dropdown-item">
                <i class="el-icon-user"></i>
                <span>个人中心</span>
              </el-dropdown-item>
              <el-dropdown-item command="my-comments" class="dropdown-item">
                <i class="el-icon-chat-dot-round"></i>
                <span>我的评论</span>
              </el-dropdown-item>
              <el-dropdown-item command="my-help" class="dropdown-item">
                <i class="el-icon-question"></i>
                <span>我的求助</span>
              </el-dropdown-item>
              <el-dropdown-item divided command="logout" class="dropdown-item logout-item">
                <i class="el-icon-switch-button"></i>
                <span>退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="content-wrapper">
        <router-view />
      </div>
    </div>

    <!-- 底部信息 -->
    <div class="footer">
      <div class="footer-content">
        <div class="footer-info">
          <div class="footer-logo">
            <i class="el-icon-s-opportunity"></i>
            <span>药用植物管理系统</span>
          </div>
          <div class="footer-links">
            <a href="#" class="footer-link">关于我们</a>
            <a href="#" class="footer-link">联系方式</a>
            <a href="#" class="footer-link">使用帮助</a>
            <a href="#" class="footer-link">意见反馈</a>
          </div>
        </div>
        <div class="footer-copyright">
          <p>&copy; 2025 药用植物病虫害管理系统 - 普通用户版 | 版权所有</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getNormalProfile } from '@/api/normal'

export default {
  name: 'NormalLayout',
  data() {
    return {
      activeIndex: '/normal/plants',
      currentUser: {},
      userStats: {
        viewCount: 0,
        commentCount: 0,
        helpCount: 0
      }
    }
  },
  created() {
    this.activeIndex = this.$route.path
    this.fetchCurrentUser()

    // 监听头像更新事件
    this.$eventBus && this.$eventBus.$on('avatarUpdated', this.handleAvatarUpdate)

    // 监听localStorage变化
    window.addEventListener('storage', this.handleStorageChange)

    // 监听用户信息更新事件
    window.addEventListener('userInfoUpdated', this.handleUserInfoUpdated)
  },

  beforeDestroy() {
    // 清理事件监听
    this.$eventBus && this.$eventBus.$off('avatarUpdated', this.handleAvatarUpdate)
    window.removeEventListener('storage', this.handleStorageChange)
    window.removeEventListener('userInfoUpdated', this.handleUserInfoUpdated)
  },
  watch: {
    '$route'(to) {
      this.activeIndex = to.path
    }
  },
  methods: {
    async fetchCurrentUser() {
      try {
        const response = await getNormalProfile()
        const data = response.data.data
        this.currentUser = data.userInfo || {}
        this.userStats = data.userStats || {
          viewCount: 0,
          commentCount: 0,
          helpCount: 0
        }
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 设置默认值
        this.currentUser = {
          nickname: '用户',
          avatarUrl: ''
        }
      }
    },

    handleSelect(key) {
      if (key !== this.$route.path) {
        this.$router.push(key)
      }
    },

    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/normal/profile')
          break
        case 'my-comments':
          this.$router.push('/normal/my-comments')
          break
        case 'my-help':
          this.$router.push('/normal/help')
          break
        case 'logout':
          this.handleLogout()
          break
      }
    },

    handleLogout() {
      this.$confirm('确认退出登录?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('userType')
        
        this.$message.success('退出登录成功!')
        this.$router.push('/login')
      }).catch(() => {
        // 取消操作
      })
    },

    handleAvatarUpdate(avatarUrl) {
      this.currentUser.avatarUrl = avatarUrl
      this.$forceUpdate() // 强制更新组件
    },

    handleStorageChange(event) {
      if (event.key === 'userInfo') {
        try {
          const userInfo = JSON.parse(event.newValue || '{}')
          this.currentUser = userInfo
          this.$forceUpdate()
        } catch (error) {
          // 解析用户信息失败
        }
      }
    },

    // 手动刷新用户信息
    refreshUserInfo() {
      this.fetchCurrentUser()
    },

    // 处理用户信息更新事件
    handleUserInfoUpdated(event) {
      if (event.detail && event.detail.avatarUrl) {
        this.currentUser.avatarUrl = event.detail.avatarUrl
        this.$forceUpdate() // 强制更新组件
      }
    }
  }
}
</script>

<style scoped>
.normal-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f8fafc;
}

/* 顶部导航栏 */
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  box-shadow:
    0 8px 32px rgba(102, 126, 234, 0.3),
    0 0 0 1px rgba(255, 255, 255, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 50%, rgba(255, 215, 0, 0.1) 0%, transparent 50%);
  opacity: 0.6;
  pointer-events: none;
}

.header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg,
    transparent 0%,
    #ffd700 25%,
    #ffd700 75%,
    transparent 100%);
  opacity: 0.8;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 75px;
  position: relative;
  z-index: 1;
}

.header-content::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 1px;
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(255, 255, 255, 0.1) 20%,
    rgba(255, 255, 255, 0.2) 50%,
    rgba(255, 255, 255, 0.1) 80%,
    transparent 100%);
  pointer-events: none;
}

/* Logo样式 */
.logo {
  display: flex;
  align-items: center;
  gap: 16px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logo:hover {
  transform: translateY(-1px);
}

.logo-icon {
  width: 52px;
  height: 52px;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 8px 25px rgba(255, 215, 0, 0.4),
    0 0 0 2px rgba(255, 255, 255, 0.2);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.logo-icon::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.3) 50%, transparent 70%);
  transform: rotate(45deg);
  transition: all 0.6s ease;
  opacity: 0;
}

.logo:hover .logo-icon::before {
  opacity: 1;
  animation: shine 1.5s ease-in-out;
}

@keyframes shine {
  0% { transform: translateX(-100%) translateY(-100%) rotate(45deg); }
  100% { transform: translateX(100%) translateY(100%) rotate(45deg); }
}

.logo-icon i {
  font-size: 26px;
  color: #2d3748;
  transition: all 0.3s ease;
  position: relative;
  z-index: 1;
}

.logo:hover .logo-icon {
  transform: scale(1.05) rotate(5deg);
  box-shadow:
    0 12px 35px rgba(255, 215, 0, 0.5),
    0 0 0 3px rgba(255, 255, 255, 0.3);
}

.logo:hover .logo-icon i {
  transform: scale(1.1);
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.main-title {
  font-size: 20px;
  font-weight: 700;
  line-height: 1.2;
}

.sub-title {
  font-size: 12px;
  opacity: 0.8;
  font-weight: 400;
}

/* 导航菜单 */
.nav-menu {
  flex: 1;
  margin: 0 68px;
}

.nav-menu >>> .el-menu {
  border: none;
  background: transparent !important;
  height: 70px;
}

.nav-menu >>> .el-menu-item {
  border: none !important;
  margin: 0 6px;
  border-radius: 16px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  height: 52px;
  line-height: 52px;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
}

.nav-menu >>> .el-menu-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.08) 100%);
  opacity: 0;
  transition: all 0.4s ease;
  transform: scale(0.8);
}

.nav-menu >>> .el-menu-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, #ffd700 0%, #ffed4e 100%);
  border-radius: 2px;
  transition: width 0.3s ease;
}

.nav-menu >>> .el-menu-item:hover::before {
  opacity: 1;
  transform: scale(1);
}

.nav-menu >>> .el-menu-item:hover::after {
  width: 80%;
}

.nav-menu >>> .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.18) !important;
  color: #ffd700 !important;
  transform: translateY(-3px) scale(1.02);
  box-shadow:
    0 8px 25px rgba(0, 0, 0, 0.15),
    0 0 0 1px rgba(255, 255, 255, 0.2);
}

.nav-menu >>> .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.25) !important;
  color: #ffd700 !important;
  box-shadow:
    0 8px 25px rgba(255, 215, 0, 0.3),
    0 0 0 2px rgba(255, 215, 0, 0.4);
  transform: translateY(-2px);
}

.nav-menu >>> .el-menu-item.is-active::after {
  width: 90%;
}
.nav-item{
  position: relative;
  top: 10px;
}
.nav-item-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 600;
  width: 100%;
  text-align: center;
}

.nav-item-content i {
  font-size: 18px;
}

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
  color: white;
}

.user-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.stat-item i {
  font-size: 16px;
  opacity: 0.9;
}

.user-dropdown-wrapper {
  position: relative;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 10px 18px;
  border-radius: 14px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(15px);
  border: 2px solid rgba(255, 255, 255, 0.25);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.user-dropdown:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.user-dropdown:hover .user-avatar {
  border-color: #ffd700;
  box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.3);
}

.user-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-weight: 700;
  font-size: 15px;
  line-height: 1.2;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.user-role {
  font-size: 13px;
  opacity: 0.9;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.9);
}

.dropdown-arrow {
  font-size: 12px;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.user-dropdown:hover .dropdown-arrow {
  opacity: 1;
  transform: rotate(180deg);
}

/* 下拉菜单样式 */
.user-dropdown-menu >>> .el-dropdown-menu {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  padding: 8px;
  margin-top: 8px;
}

.user-dropdown-menu >>> .el-dropdown-menu__item {
  border-radius: 8px;
  margin: 2px 0;
  padding: 12px 16px;
  transition: all 0.3s ease;
  color: #2d3748;
  font-weight: 500;
}

.user-dropdown-menu >>> .el-dropdown-menu__item:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: translateX(4px);
}

.user-dropdown-menu >>> .el-dropdown-menu__item.logout-item:hover {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dropdown-item i {
  font-size: 16px;
  width: 20px;
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: calc(100vh - 140px);
  position: relative;
}

.main-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 80%, rgba(102, 126, 234, 0.05) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(118, 75, 162, 0.05) 0%, transparent 50%);
  pointer-events: none;
}

.content-wrapper {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
}

/* 底部样式 */
.footer {
  background: linear-gradient(135deg, #2d3748 0%, #1a202c 100%);
  color: white;
  padding: 40px 0 20px;
  margin-top: auto;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
}

.footer-logo i {
  font-size: 24px;
  color: #ffd700;
}

.footer-links {
  display: flex;
  gap: 32px;
}

.footer-link {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.footer-link:hover {
  color: #ffd700;
  transform: translateY(-1px);
}

.footer-link::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: #ffd700;
  transition: width 0.3s ease;
}

.footer-link:hover::after {
  width: 100%;
}

.footer-copyright {
  text-align: center;
}

.footer-copyright p {
  margin: 0;
  opacity: 0.7;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .header-content {
    max-width: 100%;
    padding: 0 20px;
  }

  .content-wrapper {
    padding: 20px;
  }

  .nav-menu {
    margin: 0 32px;
  }
}

@media (max-width: 992px) {
  .user-stats {
    display: none;
  }

  .nav-menu {
    margin: 0 24px;
  }

  .footer-info {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .footer-links {
    gap: 24px;
  }
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    padding: 16px 20px;
    height: auto;
    gap: 16px;
  }

  .nav-menu {
    margin: 0;
    width: 100%;
  }

  .nav-menu >>> .el-menu {
    justify-content: center;
    flex-wrap: wrap;
    height: auto;
  }

  .nav-menu >>> .el-menu-item {
    margin: 4px;
    height: 40px;
    line-height: 40px;
  }

  .logo-text .main-title {
    font-size: 18px;
  }

  .user-dropdown {
    padding: 6px 12px;
  }

  .user-text {
    display: none;
  }

  .content-wrapper {
    padding: 16px;
  }

  .footer-links {
    flex-direction: column;
    gap: 16px;
  }

  .main-content {
    min-height: calc(100vh - 180px);
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 12px 16px;
  }

  .logo-icon {
    width: 40px;
    height: 40px;
  }

  .logo-icon i {
    font-size: 20px;
  }

  .logo-text .main-title {
    font-size: 16px;
  }

  .nav-menu >>> .el-menu-item {
    font-size: 14px;
    padding: 0 12px;
  }

  .content-wrapper {
    padding: 12px;
  }

  .footer {
    padding: 24px 0 16px;
  }

  .footer-content {
    padding: 0 16px;
  }
}
</style>
