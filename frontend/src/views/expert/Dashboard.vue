<template>
  <div class="dashboard">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="welcome-text">
          <h1 class="welcome-title">
            <i class="el-icon-sunny"></i>
            欢迎回来，{{ currentUser.name || '专家' }}！
          </h1>
          <p class="welcome-subtitle">{{ getCurrentTimeGreeting() }}，今天是个美好的一天</p>
          <div class="current-time">{{ currentTime }}</div>
        </div>
        <div class="banner-actions">
          <el-button type="primary" icon="el-icon-plus" @click="quickAction('plant')">
            添加植物
          </el-button>
          <el-button type="success" icon="el-icon-warning" @click="quickAction('pest')">
            添加病虫害
          </el-button>
          <el-button type="warning" icon="el-icon-s-tools" @click="quickAction('pesticide')">
            添加农药
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <h2 class="section-title">
        <i class="el-icon-data-analysis"></i>
        数据概览
      </h2>
      <el-row :gutter="24">
        <!-- 核心数据统计 -->
        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card primary-card" @click="navigateTo('/expert/plants')">
            <div class="stat-icon">
              <i class="el-icon-s-opportunity"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboardData.plantCount || 0 }}</div>
              <div class="stat-label">药用植物</div>
              <div class="stat-trend">
                <i class="el-icon-top trend-up"></i>
                <span>总计数量</span>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card danger-card" @click="navigateTo('/expert/pests')">
            <div class="stat-icon">
              <i class="el-icon-warning-outline"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboardData.pestDiseaseCount || 0 }}</div>
              <div class="stat-label">病虫害</div>
              <div class="stat-trend">
                <i class="el-icon-top trend-up"></i>
                <span>总计数量</span>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card warning-card" @click="navigateTo('/expert/pesticides')">
            <div class="stat-icon">
              <i class="el-icon-s-tools"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboardData.pesticideCount || 0 }}</div>
              <div class="stat-label">农药</div>
              <div class="stat-trend">
                <i class="el-icon-top trend-up"></i>
                <span>总计数量</span>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8" :lg="6">
          <div class="stat-card info-card" @click="navigateTo('/expert/helps')">
            <div class="stat-icon">
              <i class="el-icon-question"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ dashboardData.pendingHelpRequestCount || 0 }}</div>
              <div class="stat-label">可回复求助</div>
              <div class="stat-trend">
                <i class="el-icon-warning trend-warning"></i>
                <span>需要关注</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 用户统计区域 -->
    <div class="user-stats-section">
      <h2 class="section-title">
        <i class="el-icon-user"></i>
        用户统计
      </h2>
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :md="8">
          <div class="user-stat-card expert-card">
            <div class="user-stat-icon">
              <i class="el-icon-user-solid"></i>
            </div>
            <div class="user-stat-content">
              <div class="user-stat-number">{{ dashboardData.expertUserCount || 0 }}</div>
              <div class="user-stat-label">专家用户</div>
              <div class="user-stat-desc">系统专家总数</div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8">
          <div class="user-stat-card normal-card">
            <div class="user-stat-icon">
              <i class="el-icon-user"></i>
            </div>
            <div class="user-stat-content">
              <div class="user-stat-number">{{ dashboardData.normalUserCount || 0 }}</div>
              <div class="user-stat-label">普通用户</div>
              <div class="user-stat-desc">注册用户总数</div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="8">
          <div class="user-stat-card comment-card">
            <div class="user-stat-icon">
              <i class="el-icon-chat-dot-round"></i>
            </div>
            <div class="user-stat-content">
              <div class="user-stat-number">{{ dashboardData.pendingCommentCount || 0 }}</div>
              <div class="user-stat-label">待处理评论</div>
              <div class="user-stat-desc">需要审核的评论</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 快速操作区域 -->
    <div class="quick-actions-section">
      <h2 class="section-title">
        <i class="el-icon-s-operation"></i>
        快速操作
      </h2>
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="quick-action-card" @click="navigateTo('/expert/plants')">
            <div class="action-icon plants-icon">
              <i class="el-icon-s-opportunity"></i>
            </div>
            <div class="action-content">
              <h3>管理植物</h3>
              <p>查看和管理药用植物信息</p>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <div class="quick-action-card" @click="navigateTo('/expert/pests')">
            <div class="action-icon pests-icon">
              <i class="el-icon-warning-outline"></i>
            </div>
            <div class="action-content">
              <h3>管理病虫害</h3>
              <p>查看和管理病虫害信息</p>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <div class="quick-action-card" @click="navigateTo('/expert/pesticides')">
            <div class="action-icon pesticides-icon">
              <i class="el-icon-s-tools"></i>
            </div>
            <div class="action-content">
              <h3>管理农药</h3>
              <p>查看和管理农药信息</p>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <div class="quick-action-card" @click="navigateTo('/expert/plant-disease-links')">
            <div class="action-icon relations-icon">
              <i class="el-icon-connection"></i>
            </div>
            <div class="action-content">
              <h3>关系管理</h3>
              <p>管理植物与病虫害关系</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 最近活动区域 -->
    <div class="recent-activity-section">
      <h2 class="section-title">
        <i class="el-icon-time"></i>
        系统状态
      </h2>
      <el-row :gutter="24">
        <el-col :xs="24" :md="12">
          <div class="activity-card">
            <div class="activity-header">
              <h3>
                <i class="el-icon-data-line"></i>
                数据统计趋势
              </h3>
            </div>
            <div class="activity-content">
              <div class="trend-item">
                <span class="trend-label">药用植物</span>
                <div class="trend-bar">
                  <div class="trend-progress" :style="{ width: getProgressWidth(dashboardData.plantCount, 50) }"></div>
                </div>
                <span class="trend-value">{{ dashboardData.plantCount || 0 }}</span>
              </div>
              <div class="trend-item">
                <span class="trend-label">病虫害</span>
                <div class="trend-bar">
                  <div class="trend-progress danger" :style="{ width: getProgressWidth(dashboardData.pestDiseaseCount, 30) }"></div>
                </div>
                <span class="trend-value">{{ dashboardData.pestDiseaseCount || 0 }}</span>
              </div>
              <div class="trend-item">
                <span class="trend-label">农药</span>
                <div class="trend-bar">
                  <div class="trend-progress warning" :style="{ width: getProgressWidth(dashboardData.pesticideCount, 20) }"></div>
                </div>
                <span class="trend-value">{{ dashboardData.pesticideCount || 0 }}</span>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :md="12">
          <div class="activity-card">
            <div class="activity-header">
              <h3>
                <i class="el-icon-bell"></i>
                系统提醒
              </h3>
            </div>
            <div class="activity-content">
              <div class="reminder-item" v-if="dashboardData.pendingHelpRequestCount > 0">
                <div class="reminder-icon warning">
                  <i class="el-icon-warning"></i>
                </div>
                <div class="reminder-content">
                  <div class="reminder-title">有 {{ dashboardData.pendingHelpRequestCount }} 个可回复求助</div>
                  <div class="reminder-desc">这些求助已通过审核，可以进行回复</div>
                </div>
                <el-button size="mini" type="primary" @click="navigateTo('/expert/helps')">
                  查看
                </el-button>
              </div>

              <div class="reminder-item" v-if="!dashboardData.pendingHelpRequestCount">
                <div class="reminder-icon success">
                  <i class="el-icon-circle-check"></i>
                </div>
                <div class="reminder-content">
                  <div class="reminder-title">系统运行正常</div>
                  <div class="reminder-desc">暂无需要处理的事项</div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getExpertProfile } from '@/api/expert'
import request from '@/utils/request'

export default {
  name: 'ExpertDashboard',
  data () {
    return {
      dashboardData: {},
      currentUser: {},
      currentTime: '',
      timer: null
    }
  },
  created () {
    this.fetchDashboardData()
    this.fetchCurrentUser()
    this.updateCurrentTime()
    this.startTimer()
  },
  beforeDestroy () {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    async fetchDashboardData () {
      try {
        // 使用专家专用的仪表盘接口
        const response = await request({
          url: '/dashboard/expert',
          method: 'get'
        })
        if (response.data.code === 1) {
          this.dashboardData = response.data.data
        } else {
          throw new Error(response.data.message || '获取数据失败')
        }
      } catch (error) {
        this.$message.error('获取仪表盘数据失败')
      }
    },

    async fetchCurrentUser () {
      try {
        const userRes = await getExpertProfile()
        this.currentUser = userRes.data.data
      } catch (error) {
        console.error('获取用户信息失败', error)
      }
    },

    getCurrentTimeGreeting () {
      const hour = new Date().getHours()
      if (hour < 6) {
        return '夜深了'
      } else if (hour < 9) {
        return '早上好'
      } else if (hour < 12) {
        return '上午好'
      } else if (hour < 14) {
        return '中午好'
      } else if (hour < 18) {
        return '下午好'
      } else if (hour < 22) {
        return '晚上好'
      } else {
        return '夜深了'
      }
    },

    updateCurrentTime () {
      const now = new Date()
      const options = {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      }
      this.currentTime = now.toLocaleDateString('zh-CN', options)
    },

    startTimer () {
      this.timer = setInterval(() => {
        this.updateCurrentTime()
      }, 1000)
    },

    quickAction (type) {
      const routes = {
        plant: '/expert/plants',
        pest: '/expert/pests',
        pesticide: '/expert/pesticides'
      }
      if (routes[type]) {
        this.$router.push(routes[type])
      }
    },

    navigateTo (path) {
      this.$router.push(path)
    },

    getProgressWidth (value, max) {
      const percentage = Math.min((value / max) * 100, 100)
      return `${percentage}%`
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 24px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 32px;
  color: white;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.banner-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 24px;
}

.welcome-text {
  flex: 1;
  min-width: 300px;
}

.welcome-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome-title i {
  font-size: 2.8rem;
  color: #ffd700;
}

.welcome-subtitle {
  font-size: 1.2rem;
  margin: 0 0 16px 0;
  opacity: 0.9;
}

.current-time {
  font-size: 1rem;
  opacity: 0.8;
  font-weight: 500;
}

.banner-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.banner-actions .el-button {
  border: 2px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  color: white;
  font-weight: 600;
  padding: 12px 24px;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.banner-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* 区域标题 */
.section-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 24px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title i {
  font-size: 1.8rem;
  color: #667eea;
}

/* 统计区域 */
.stats-section {
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 2px solid transparent;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.stat-card.primary-card {
  border-left: 4px solid #409eff;
}

.stat-card.primary-card:hover {
  border-color: #409eff;
}

.stat-card.danger-card {
  border-left: 4px solid #f56c6c;
}

.stat-card.danger-card:hover {
  border-color: #f56c6c;
}

.stat-card.warning-card {
  border-left: 4px solid #e6a23c;
}

.stat-card.warning-card:hover {
  border-color: #e6a23c;
}

.stat-card.info-card {
  border-left: 4px solid #909399;
}

.stat-card.info-card:hover {
  border-color: #909399;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  color: white;
  flex-shrink: 0;
}

.primary-card .stat-icon {
  background: linear-gradient(135deg, #409eff, #66b1ff);
}

.danger-card .stat-icon {
  background: linear-gradient(135deg, #f56c6c, #f78989);
}

.warning-card .stat-icon {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
}

.info-card .stat-icon {
  background: linear-gradient(135deg, #909399, #a6a9ad);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px 0;
  line-height: 1;
}

.stat-label {
  font-size: 1.1rem;
  color: #606266;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.9rem;
  color: #909399;
}

.trend-up {
  color: #67c23a;
}

.trend-warning {
  color: #e6a23c;
}

/* 用户统计区域 */
.user-stats-section {
  margin-bottom: 32px;
}

.user-stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  margin-bottom: 24px;
  text-align: center;
}

.user-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.user-stat-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  color: white;
  margin: 0 auto 20px auto;
}

.expert-card .user-stat-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.normal-card .user-stat-icon {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.comment-card .user-stat-icon {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.user-stat-number {
  font-size: 2.2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.user-stat-label {
  font-size: 1.1rem;
  color: #606266;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.user-stat-desc {
  font-size: 0.9rem;
  color: #909399;
}

/* 快速操作区域 */
.quick-actions-section {
  margin-bottom: 32px;
}

.quick-action-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  margin-bottom: 24px;
  text-align: center;
  border: 2px solid transparent;
}

.quick-action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
}

.action-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  color: white;
  margin: 0 auto 16px auto;
}

.plants-icon {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.pests-icon {
  background: linear-gradient(135deg, #ff4d4f, #ff7875);
}

.pesticides-icon {
  background: linear-gradient(135deg, #fa8c16, #ffa940);
}

.relations-icon {
  background: linear-gradient(135deg, #722ed1, #9254de);
}

.action-content h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 8px 0;
}

.action-content p {
  font-size: 0.9rem;
  color: #909399;
  margin: 0;
}

/* 最近活动区域 */
.recent-activity-section {
  margin-bottom: 32px;
}

.activity-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  overflow: hidden;
}

.activity-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.activity-header h3 {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.activity-content {
  padding: 24px;
}

/* 趋势条 */
.trend-item {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.trend-item:last-child {
  margin-bottom: 0;
}

.trend-label {
  width: 80px;
  font-size: 0.9rem;
  color: #606266;
  font-weight: 500;
}

.trend-bar {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.trend-progress {
  height: 100%;
  background: linear-gradient(90deg, #409eff, #66b1ff);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.trend-progress.danger {
  background: linear-gradient(90deg, #f56c6c, #f78989);
}

.trend-progress.warning {
  background: linear-gradient(90deg, #e6a23c, #ebb563);
}

.trend-value {
  width: 40px;
  text-align: right;
  font-size: 0.9rem;
  font-weight: 600;
  color: #2c3e50;
}

/* 提醒项 */
.reminder-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: #f8f9fa;
  margin-bottom: 12px;
}

.reminder-item:last-child {
  margin-bottom: 0;
}

.reminder-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  color: white;
  flex-shrink: 0;
}

.reminder-icon.warning {
  background: #e6a23c;
}

.reminder-icon.info {
  background: #409eff;
}

.reminder-icon.success {
  background: #67c23a;
}

.reminder-content {
  flex: 1;
}

.reminder-title {
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
}

.reminder-desc {
  font-size: 0.9rem;
  color: #909399;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }

  .banner-content {
    flex-direction: column;
    text-align: center;
  }

  .welcome-title {
    font-size: 2rem;
  }

  .banner-actions {
    justify-content: center;
  }

  .stat-card {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .stat-number {
    font-size: 2rem;
  }

  .trend-item {
    flex-direction: column;
    gap: 8px;
  }

  .trend-label {
    width: auto;
  }

  .reminder-item {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
}

@media (max-width: 480px) {
  .welcome-title {
    font-size: 1.8rem;
  }

  .stat-number {
    font-size: 1.8rem;
  }

  .user-stat-number {
    font-size: 1.8rem;
  }
}
</style>