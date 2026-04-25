<template>
  <div class="dashboard-container" v-loading="loading" element-loading-text="加载统计数据中...">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="el-icon-data-analysis"></i>
            管理员控制台
          </h1>
          <p class="page-subtitle">系统数据概览与待审核内容管理</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="fetchStats" :loading="loading" icon="el-icon-refresh">
            刷新数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stats-row">
      <el-col :span="8">
        <div class="stat-card experts-card" @click="goToExperts">
          <div class="stat-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.expertCount || 0 }}</div>
            <div class="stat-label">专家用户</div>
            <div class="stat-trend">
              <i class="el-icon-arrow-up"></i>
              <span>+5% 本月</span>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="stat-card users-card" @click="goToUsers">
          <div class="stat-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stats.normalUserCount || 0 }}</div>
            <div class="stat-label">普通用户</div>
            <div class="stat-trend">
              <i class="el-icon-arrow-up"></i>
              <span>+8% 本月</span>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="8">
        <div class="stat-card total-users-card">
          <div class="stat-icon">
            <i class="el-icon-s-custom"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ (stats.expertCount || 0) + (stats.normalUserCount || 0) }}</div>
            <div class="stat-label">总用户数</div>
            <div class="stat-breakdown">
              专家: {{ stats.expertCount || 0 }} | 普通: {{ stats.normalUserCount || 0 }}
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 待审核统计 -->
    <el-row :gutter="24" class="pending-stats-row">
      <el-col :span="12">
        <div class="pending-stat-card comments-pending" @click="goToComments">
          <div class="pending-icon">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="pending-content">
            <div class="pending-number">{{ stats.pendingCommentCount || 0 }}</div>
            <div class="pending-label">待审核评论</div>
          </div>
          <div class="pending-action">
            <el-button type="primary" size="small">立即处理</el-button>
          </div>
        </div>
      </el-col>

      <el-col :span="12">
        <div class="pending-stat-card helps-pending" @click="goToHelpRequests">
          <div class="pending-icon">
            <i class="el-icon-question"></i>
          </div>
          <div class="pending-content">
            <div class="pending-number">{{ stats.pendingHelpRequestCount || 0 }}</div>
            <div class="pending-label">待审核求助</div>
          </div>
          <div class="pending-action">
            <el-button type="warning" size="small">立即处理</el-button>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 待审核内容详情 -->
    <el-row :gutter="24" class="pending-details-row">
      <el-col :span="12">
        <div class="pending-detail-card">
          <div class="card-header">
            <h3>
              <i class="el-icon-chat-dot-round"></i>
              最新待审核评论
            </h3>
            <el-button type="text" @click="goToComments" class="view-all-btn">
              查看全部 <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
          <div class="pending-list">
            <div
              v-for="comment in stats.recentPendingComments || []"
              :key="comment.id"
              class="pending-item comment-item"
            >
              <div class="item-header">
                <span class="username">{{ comment.username }}</span>
                <span class="time">{{ formatTime(comment.createdAt) }}</span>
              </div>
              <div class="item-content">
                <p class="content-text">{{ comment.content }}</p>
                <span class="target-info">{{ getTargetTypeText(comment.targetType) }}</span>
              </div>
              <div class="item-actions">
                <el-button size="mini" type="success" @click="approveComment(comment.id)">
                  <i class="el-icon-check"></i> 通过
                </el-button>
                <el-button size="mini" type="danger" @click="rejectComment(comment.id)">
                  <i class="el-icon-close"></i> 拒绝
                </el-button>
              </div>
            </div>
            <div v-if="!stats.recentPendingComments || stats.recentPendingComments.length === 0" class="empty-state">
              <i class="el-icon-chat-dot-round"></i>
              <p>暂无待审核评论</p>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :span="12">
        <div class="pending-detail-card">
          <div class="card-header">
            <h3>
              <i class="el-icon-question"></i>
              最新待审核求助
            </h3>
            <el-button type="text" @click="goToHelpRequests" class="view-all-btn">
              查看全部 <i class="el-icon-arrow-right"></i>
            </el-button>
          </div>
          <div class="pending-list">
            <div
              v-for="help in stats.recentPendingHelps || []"
              :key="help.id"
              class="pending-item help-item"
            >
              <div class="item-header">
                <span class="username">{{ help.username }}</span>
                <span class="time">{{ formatTime(help.createdAt) }}</span>
              </div>
              <div class="item-content">
                <h4 class="help-title">{{ help.title }}</h4>
              </div>
              <div class="item-actions">
                <el-button size="mini" type="success" @click="approveHelp(help.id)">
                  <i class="el-icon-check"></i> 通过
                </el-button>
                <el-button size="mini" type="danger" @click="rejectHelp(help.id)">
                  <i class="el-icon-close"></i> 拒绝
                </el-button>
              </div>
            </div>
            <div v-if="!stats.recentPendingHelps || stats.recentPendingHelps.length === 0" class="empty-state">
              <i class="el-icon-question"></i>
              <p>暂无待审核求助</p>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getAdminStats, approveComment, rejectComment, approveHelpRequest, rejectHelpRequest } from '@/api/admin'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      loading: false,
      stats: {
        plantCount: 0,
        pestDiseaseCount: 0,
        pesticideCount: 0,
        expertCount: 0,
        normalUserCount: 0,
        pendingCommentCount: 0,
        pendingHelpRequestCount: 0,
        recentPendingComments: [],
        recentPendingHelps: []
      }
    }
  },

  mounted() {
    this.fetchStats()
  },

  methods: {
    async fetchStats() {
      try {
        this.loading = true
        const response = await getAdminStats()

        // 检查响应状态，后端返回的code是1表示成功
        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          this.stats = response.data.data
          this.$message.success('统计数据加载成功')
        } else {
          this.$message.error((response.data && response.data.msg) || '获取统计数据失败')
        }
      } catch (error) {
        let errorMsg = '获取统计数据失败'
        if (error.response && error.response.data && error.response.data.msg) {
          errorMsg = error.response.data.msg
        } else if (error.message) {
          errorMsg = error.message
        }
        this.$message.error(errorMsg)
      } finally {
        this.loading = false
      }
    },

    async approveComment(id) {
      try {
        const response = await approveComment(id)
        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          this.$message.success('评论审核通过')
          this.fetchStats() // 刷新数据
        } else {
          this.$message.error((response.data && response.data.msg) || '审核失败')
        }
      } catch (error) {
        console.error('审核评论失败:', error)
        this.$message.error('审核失败')
      }
    },

    async rejectComment(id) {
      try {
        const response = await rejectComment(id)
        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          this.$message.success('评论审核拒绝')
          this.fetchStats() // 刷新数据
        } else {
          this.$message.error((response.data && response.data.msg) || '审核失败')
        }
      } catch (error) {
        console.error('审核评论失败:', error)
        this.$message.error('审核失败')
      }
    },

    async approveHelp(id) {
      try {
        const response = await approveHelpRequest(id)
        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          this.$message.success('求助审核通过')
          this.fetchStats() // 刷新数据
        } else {
          this.$message.error((response.data && response.data.msg) || '审核失败')
        }
      } catch (error) {
        console.error('审核求助失败:', error)
        this.$message.error('审核失败')
      }
    },

    async rejectHelp(id) {
      try {
        const response = await rejectHelpRequest(id)
        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          this.$message.success('求助审核拒绝')
          this.fetchStats() // 刷新数据
        } else {
          this.$message.error((response.data && response.data.msg) || '审核失败')
        }
      } catch (error) {
        console.error('审核求助失败:', error)
        this.$message.error('审核失败')
      }
    },

    formatTime(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      const now = new Date()
      const diff = now - date

      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return Math.floor(diff / 60000) + '分钟前'
      } else if (diff < 86400000) { // 1天内
        return Math.floor(diff / 3600000) + '小时前'
      } else {
        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString()
      }
    },

    getTargetTypeText(targetType) {
      const typeMap = {
        'plant': '药用植物',
        'pest_disease': '病虫害',
        'pesticide': '农药'
      }
      return typeMap[targetType] || '未知类型'
    },

    // 导航方法
    goToUsers() {
      this.$router.push('/admin/users')
    },

    goToExperts() {
      this.$router.push('/admin/experts')
    },

    goToComments() {
      this.$router.push('/admin/comments')
    },

    goToHelpRequests() {
      this.$router.push('/admin/help-requests')
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* 页面标题 */
.page-header {
  margin-bottom: 32px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.title-section {
  flex: 1;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title i {
  font-size: 2.2rem;
  color: #409eff;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #606266;
  margin: 0;
  font-weight: 400;
}

.header-actions {
  flex-shrink: 0;
}

.header-actions .el-button {
  padding: 12px 24px;
  font-size: 1rem;
  border-radius: 8px;
  font-weight: 500;
}

/* 统计卡片行 */
.stats-row {
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-card.experts-card::before {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.stat-card.users-card::before {
  background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card.total-users-card::before {
  background: linear-gradient(90deg, #43e97b 0%, #38f9d7 100%);
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

.experts-card .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.users-card .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.total-users-card .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 1rem;
  color: #606266;
  font-weight: 500;
  margin-bottom: 8px;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.85rem;
  color: #67c23a;
  font-weight: 500;
}

.stat-breakdown {
  font-size: 0.85rem;
  color: #909399;
  font-weight: 400;
}

/* 待审核统计行 */
.pending-stats-row {
  margin-bottom: 32px;
}

.pending-stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  overflow: hidden;
}

.pending-stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
}

.pending-stat-card.comments-pending::before {
  background: linear-gradient(90deg, #ff9a9e 0%, #fecfef 100%);
}

.pending-stat-card.helps-pending::before {
  background: linear-gradient(90deg, #ffecd2 0%, #fcb69f 100%);
}

.pending-stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(0, 0, 0, 0.1);
}

.pending-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  color: white;
  flex-shrink: 0;
}

.comments-pending .pending-icon {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
}

.helps-pending .pending-icon {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
}

.pending-content {
  flex: 1;
}

.pending-number {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 4px;
}

.pending-label {
  font-size: 1rem;
  color: #606266;
  font-weight: 500;
}

.pending-action {
  flex-shrink: 0;
}

/* 待审核详情行 */
.pending-details-row {
  margin-bottom: 32px;
}

.pending-detail-card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.card-header {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #f0f2f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-header h3 i {
  color: #409eff;
}

.view-all-btn {
  color: #409eff;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 4px;
}

.view-all-btn:hover {
  color: #66b1ff;
}

.pending-list {
  max-height: 400px;
  overflow-y: auto;
}

.pending-item {
  padding: 20px 24px;
  border-bottom: 1px solid #f8f9fa;
  transition: all 0.2s ease;
}

.pending-item:hover {
  background: #fafbfc;
}

.pending-item:last-child {
  border-bottom: none;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.username {
  font-weight: 600;
  color: #2c3e50;
  font-size: 0.9rem;
}

.time {
  font-size: 0.8rem;
  color: #c0c4cc;
}

.item-content {
  margin-bottom: 16px;
}

.content-text {
  margin: 0 0 8px 0;
  color: #606266;
  line-height: 1.5;
  font-size: 0.9rem;
}

.help-title {
  margin: 0;
  color: #2c3e50;
  font-size: 1rem;
  font-weight: 600;
  line-height: 1.4;
}

.target-info {
  display: inline-block;
  padding: 2px 8px;
  background: #f0f9ff;
  color: #409eff;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}

.item-actions {
  display: flex;
  gap: 8px;
}

.item-actions .el-button {
  padding: 6px 12px;
  font-size: 0.8rem;
  border-radius: 6px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #c0c4cc;
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 16px;
  display: block;
}

.empty-state p {
  margin: 0;
  font-size: 1rem;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-row .el-col {
    margin-bottom: 16px;
  }

  .stat-card {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .stat-icon {
    width: 56px;
    height: 56px;
  }

  .stat-number {
    font-size: 2rem;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .page-title {
    font-size: 2rem;
  }

  .header-actions {
    width: 100%;
  }

  .header-actions .el-button {
    width: 100%;
  }

  .pending-stat-card {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .item-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }

  .item-actions {
    flex-direction: column;
  }
}
</style>