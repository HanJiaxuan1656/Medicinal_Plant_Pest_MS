<template>
  <div class="pesticide-detail-container" v-loading="loading">
    <!-- 返回按钮 -->
    <div class="back-section">
      <el-button @click="goBack" icon="el-icon-arrow-left" type="text" size="large">
        返回农药列表
      </el-button>
    </div>

    <div v-if="pesticide" class="pesticide-detail">
      <!-- 农药基本信息 -->
      <div class="pesticide-header">
        <div class="pesticide-info-section">
          <h1 class="pesticide-title">{{ pesticide.name }}</h1>
          <div class="pesticide-meta">
            <el-tag :type="getCategoryColor(pesticide.category)" size="medium">{{ pesticide.category || '未分类' }}</el-tag>
            <span class="view-count">
              <i class="el-icon-view"></i>
              {{ pesticide.viewCount || 0 }} 次查看
            </span>
          </div>

          <div class="pesticide-description" v-if="pesticide.usageInstructions">
            <h3>使用说明</h3>
            <p>{{ pesticide.usageInstructions }}</p>
          </div>
          
          <div class="pesticide-ingredient" v-if="pesticide.activeIngredient">
            <h3>有效成分</h3>
            <p>{{ pesticide.activeIngredient }}</p>
          </div>
        </div>
      </div>

      <!-- 详细信息标签页 -->
      <div class="detail-tabs">
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="基本信息" name="basic">
            <div class="basic-info">
              <el-row :gutter="20">
                <el-col :span="12">
                  <div class="info-item">
                    <label>产品名称：</label>
                    <span>{{ pesticide.name }}</span>
                  </div>
                  <div class="info-item">
                    <label>产品分类：</label>
                    <span>{{ pesticide.category || '未分类' }}</span>
                  </div>
                  <div class="info-item">
                    <label>有效成分：</label>
                    <span>{{ pesticide.activeIngredient || '未知' }}</span>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="info-item">
                    <label>查看次数：</label>
                    <span>{{ pesticide.viewCount || 0 }} 次</span>
                  </div>
                  <div class="info-item">
                    <label>入库时间：</label>
                    <span>{{ formatDate(pesticide.createdAt) }}</span>
                  </div>
                  <div class="info-item">
                    <label>更新时间：</label>
                    <span>{{ formatDate(pesticide.updatedAt) }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="使用说明" name="usage">
            <div class="usage-instructions">
              <div v-if="pesticide.usageInstructions" class="usage-content">
                <h4>使用说明</h4>
                <div class="usage-text" v-html="formatUsageText(pesticide.usageInstructions)"></div>
              </div>
              <div v-if="pesticide.manualUrl" class="manual-download">
                <h4>产品手册</h4>
                <el-button type="primary" @click="downloadManual" icon="el-icon-download">
                  下载产品手册
                </el-button>
              </div>
              <div v-if="!pesticide.usageInstructions && !pesticide.manualUrl" class="empty-state">
                <i class="el-icon-info"></i>
                <p>暂无使用说明信息</p>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane :label="`评论 (${comments.length})`" name="comments">
            <div class="comments-section">
              <!-- 添加评论 -->
              <div class="add-comment">
                <h3>发表评论</h3>
                <el-input
                  v-model="newComment"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入您的评论..."
                  maxlength="500"
                  show-word-limit
                ></el-input>
                <div class="comment-actions">
                  <el-button @click="submitComment" type="primary" :loading="submittingComment">
                    发表评论
                  </el-button>
                </div>
              </div>
              
              <!-- 评论列表 -->
              <div class="comments-list">
                <div v-if="comments.length > 0">
                  <div v-for="comment in comments" :key="comment.id" class="comment-item">
                    <div class="comment-header">
                      <el-avatar :size="32" :src="comment.avatarUrl" icon="el-icon-user-solid"></el-avatar>
                      <div class="comment-meta">
                        <span class="username">{{ comment.nickname || comment.username }}</span>
                        <span v-if="comment.title" class="user-title">{{ comment.title }}</span>
                        <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                        <el-tag v-if="comment.userType === 'expert'" type="success" size="mini">
                          专家
                        </el-tag>
                      </div>
                    </div>
                    <div class="comment-content">
                      {{ comment.content }}
                    </div>
                  </div>
                </div>
                <div v-else class="empty-comments">
                  <i class="el-icon-chat-dot-round"></i>
                  <p>暂无评论，快来发表第一条评论吧！</p>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import { getPesticideDetail, getComments, addComment } from '@/api/normal'

export default {
  name: 'PesticideDetail',
  data() {
    return {
      loading: false,
      pesticide: null,
      comments: [],
      activeTab: 'basic',
      newComment: '',
      submittingComment: false
    }
  },
  created() {
    this.fetchPesticideDetail()
    this.fetchComments()
  },
  methods: {
    async fetchPesticideDetail() {
      try {
        this.loading = true
        const pesticideId = this.$route.params.id
        const response = await getPesticideDetail(pesticideId)
        this.pesticide = response.data.data.pesticide
      } catch (error) {
        console.error('获取农药详情失败:', error)
        this.$message.error('获取农药详情失败')
      } finally {
        this.loading = false
      }
    },

    async fetchComments() {
      try {
        const pesticideId = this.$route.params.id
        const response = await getComments('pesticide', pesticideId, { page: 1, pageSize: 50 })
        this.comments = response.data.data.list || []
      } catch (error) {
        console.error('获取评论失败:', error)
      }
    },

    async submitComment() {
      if (!this.newComment.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }

      try {
        this.submittingComment = true
        const pesticideId = this.$route.params.id
        await addComment({
          targetType: 'pesticide',
          targetId: pesticideId,
          content: this.newComment
        })
        
        this.$message.success('评论提交成功，等待审核')
        this.newComment = ''
        this.fetchComments()
      } catch (error) {
        console.error('提交评论失败:', error)
        this.$message.error('提交评论失败')
      } finally {
        this.submittingComment = false
      }
    },

    goBack() {
      this.$router.go(-1)
    },

    downloadManual() {
      if (this.pesticide.manualUrl) {
        window.open(this.pesticide.manualUrl, '_blank')
      } else {
        this.$message.warning('暂无产品手册')
      }
    },

    formatDate(date) {
      if (!date) return '未知'
      return new Date(date).toLocaleDateString('zh-CN')
    },

    formatUsageText(text) {
      if (!text) return ''
      return text.replace(/\n/g, '<br>')
    },

    formatPrecautionsText(text) {
      if (!text) return ''
      return text.replace(/\n/g, '<br>')
    },

    getCategoryColor(category) {
      const colorMap = {
        '杀虫剂': 'danger',
        '杀菌剂': 'success',
        '除草剂': 'primary',
        '植物生长调节剂': 'warning'
      }
      return colorMap[category] || 'info'
    }
  }
}
</script>

<style scoped>
.pesticide-detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
}

.pesticide-detail-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 80%, rgba(102, 126, 234, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(118, 75, 162, 0.03) 0%, transparent 50%);
  pointer-events: none;
}

.back-section {
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

.back-section >>> .el-button {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(102, 126, 234, 0.2);
  color: #667eea;
  font-weight: 600;
  padding: 12px 24px;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(10px);
}

.back-section >>> .el-button:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.pesticide-detail {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  overflow: hidden;
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(20px);
  position: relative;
  z-index: 1;
}

.pesticide-header {
  padding: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  position: relative;
  overflow: hidden;
}

.pesticide-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 30% 70%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 70% 30%, rgba(255, 215, 0, 0.1) 0%, transparent 50%);
  opacity: 0.6;
}

.pesticide-info-section {
  max-width: 900px;
  position: relative;
  z-index: 1;
}

.pesticide-title {
  font-size: 3rem;
  font-weight: 800;
  color: white;
  margin: 0 0 24px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  line-height: 1.2;
}

.pesticide-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  flex-wrap: wrap;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 1rem;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

.view-count i {
  font-size: 18px;
}

.pesticide-description,
.pesticide-ingredient {
  margin-bottom: 32px;
}

.pesticide-description h3,
.pesticide-ingredient h3 {
  font-size: 1.4rem;
  color: white;
  margin: 0 0 16px 0;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.pesticide-description h3::before,
.pesticide-ingredient h3::before {
  content: '';
  width: 4px;
  height: 24px;
  background: #ffd700;
  border-radius: 2px;
}

.pesticide-description p,
.pesticide-ingredient p {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.8;
  margin: 0;
  font-size: 1.1rem;
  background: rgba(255, 255, 255, 0.1);
  padding: 20px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
}

.detail-tabs {
  padding: 0 48px 48px;
}

.detail-tabs >>> .el-tabs__header {
  margin: 0 0 32px 0;
  border-bottom: 2px solid #e1e8ed;
}

.detail-tabs >>> .el-tabs__nav-wrap::after {
  display: none;
}

.detail-tabs >>> .el-tabs__item {
  font-size: 16px;
  font-weight: 600;
  padding: 0 24px;
  height: 48px;
  line-height: 48px;
  color: #606266;
  transition: all 0.3s ease;
}

.detail-tabs >>> .el-tabs__item:hover {
  color: #667eea;
}

.detail-tabs >>> .el-tabs__item.is-active {
  color: #667eea;
  font-weight: 700;
}

.detail-tabs >>> .el-tabs__active-bar {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  height: 3px;
  border-radius: 2px;
}

.basic-info {
  padding: 32px 0;
}

.info-item {
  display: flex;
  margin-bottom: 20px;
  padding: 16px 24px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 12px;
  border-left: 4px solid #667eea;
  transition: all 0.3s ease;
}

.info-item:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.info-item label {
  font-weight: 700;
  color: #2c3e50;
  width: 120px;
  flex-shrink: 0;
  font-size: 15px;
}

.info-item span {
  color: #4a5568;
  font-weight: 500;
  font-size: 15px;
}

.usage-instructions {
  padding: 20px 0;
}

.usage-content h4,
.dosage-info h4,
.precautions h4 {
  color: #2c3e50;
  margin: 0 0 16px 0;
}

.usage-text,
.precautions-text {
  color: #606266;
  line-height: 1.6;
  margin: 0 0 30px 0;
}

.dosage-info p {
  color: #606266;
  line-height: 1.6;
  margin: 0 0 30px 0;
}

.manual-download {
  padding: 20px 0;
}

.manual-download h4 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-weight: 600;
}

.manual-download .el-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  padding: 12px 24px;
  font-weight: 600;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.manual-download .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.comments-section {
  padding: 20px 0;
}

.add-comment {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.add-comment h3 {
  margin: 0 0 16px 0;
  color: #2c3e50;
}

.comment-actions {
  margin-top: 12px;
  text-align: right;
}

.comments-list {
  max-height: 600px;
  overflow-y: auto;
}

.comment-item {
  padding: 20px;
  border-bottom: 1px solid #e1e8ed;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-weight: 600;
  color: #2c3e50;
}

.user-title {
  color: #67c23a;
  font-size: 0.75rem;
  font-weight: 500;
}

.comment-time {
  color: #909399;
  font-size: 0.8rem;
}

.comment-content {
  color: #606266;
  line-height: 1.6;
  margin-left: 44px;
}

.empty-state,
.empty-comments {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-state i,
.empty-comments i {
  font-size: 3rem;
  margin-bottom: 16px;
  color: #c0c4cc;
}

.empty-state p,
.empty-comments p {
  margin: 0;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .pesticide-header {
    padding: 20px;
  }
  
  .pesticide-title {
    font-size: 2rem;
  }
  
  .detail-tabs {
    padding: 0 20px 20px;
  }
}
</style>
