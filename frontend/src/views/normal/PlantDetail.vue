<template>
  <div class="plant-detail-container" v-loading="loading">
    <!-- 返回按钮 -->
    <div class="back-section">
      <el-button @click="goBack" icon="el-icon-arrow-left" type="text" size="large">
        返回植物列表
      </el-button>
    </div>

    <div v-if="plant" class="plant-detail">
      <!-- 植物基本信息 -->
      <div class="plant-header">
        <div class="plant-image-section">
          <img :src="plant.imageUrl || '/default-plant.jpg'" :alt="plant.name" class="plant-image" />
        </div>
        
        <div class="plant-info-section">
          <h1 class="plant-title">{{ plant.name }}</h1>
          <div class="plant-meta">
            <el-tag v-if="plant.category" type="primary" size="medium">{{ plant.category }}</el-tag>
            <el-tag v-if="plant.origin" type="info" size="medium">产地：{{ plant.origin }}</el-tag>
            <span class="view-count">
              <i class="el-icon-view"></i>
              {{ plant.viewCount || 0 }} 次查看
            </span>
          </div>
          
          <div class="plant-description">
            <h3>植物描述</h3>
            <p>{{ plant.description || '暂无描述' }}</p>
          </div>
          
          <div class="plant-efficacy" v-if="plant.efficacy">
            <h3>功效作用</h3>
            <p>{{ plant.efficacy }}</p>
          </div>
        </div>
      </div>

      <!-- 详细信息标签页 -->
      <div class="detail-tabs">
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="相关病虫害" name="diseases">
            <div class="related-diseases">
              <div v-if="relatedDiseases.length > 0" class="disease-list">
                <div
                  v-for="disease in relatedDiseases"
                  :key="disease.id"
                  class="disease-item clickable"
                  :class="{ 'navigating': navigating }"
                  @click="viewDiseaseDetail(disease.id)"
                >
                  <div class="disease-header">
                    <div class="disease-title-section">
                      <h4 class="disease-name">{{ disease.name }}</h4>
                      <el-tag
                        :type="getDiseaseType(disease.type)"
                        size="small"
                        class="disease-type-tag"
                      >
                        {{ disease.type }}
                      </el-tag>
                    </div>
                    <el-tag
                      :type="getVulnerabilityType(disease.vulnerability)"
                      size="medium"
                      class="vulnerability-tag"
                    >
                      易感性：{{ disease.vulnerability }}
                    </el-tag>
                  </div>

                  <div class="disease-content">
                    <div class="disease-description">
                      <h5>病害描述</h5>
                      <p>{{ disease.description || '暂无描述' }}</p>
                    </div>

                    <div v-if="disease.symptoms" class="disease-symptoms">
                      <h5>主要症状</h5>
                      <p>{{ disease.symptoms }}</p>
                    </div>

                    <div v-if="disease.occurrenceSeason || disease.affectedParts" class="disease-details">
                      <div v-if="disease.occurrenceSeason" class="detail-item">
                        <span class="detail-label">发生季节：</span>
                        <span class="detail-value">{{ disease.occurrenceSeason }}</span>
                      </div>
                      <div v-if="disease.affectedParts" class="detail-item">
                        <span class="detail-label">受害部位：</span>
                        <span class="detail-value">{{ disease.affectedParts }}</span>
                      </div>
                    </div>

                    <div v-if="disease.notes" class="disease-notes">
                      <h5>备注信息</h5>
                      <p>{{ disease.notes }}</p>
                    </div>
                  </div>

                  <div class="disease-click-hint">
                    <i class="el-icon-right"></i>
                    <span>点击查看详情</span>
                  </div>
                </div>
              </div>
              <div v-else class="empty-state">
                <i class="el-icon-info"></i>
                <p>暂无相关病虫害信息</p>
                <p class="empty-tip">该植物目前没有记录相关的病虫害信息</p>
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
import { getPlantDetail, getComments, addComment } from '@/api/normal'

export default {
  name: 'PlantDetail',
  data() {
    return {
      loading: false,
      plant: null,
      relatedDiseases: [],
      comments: [],
      activeTab: 'diseases',
      newComment: '',
      submittingComment: false,
      navigating: false
    }
  },
  created() {
    this.fetchPlantDetail()
    this.fetchComments()
  },
  methods: {
    async fetchPlantDetail() {
      try {
        this.loading = true
        const plantId = this.$route.params.id
        const response = await getPlantDetail(plantId)
        this.plant = response.data.data.plant
        this.relatedDiseases = response.data.data.relatedDiseases || []
      } catch (error) {
        console.error('获取植物详情失败:', error)
        this.$message.error('获取植物详情失败')
      } finally {
        this.loading = false
      }
    },

    async fetchComments() {
      try {
        const plantId = this.$route.params.id
        const response = await getComments('plant', plantId, { page: 1, pageSize: 50 })
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
        const plantId = this.$route.params.id
        await addComment({
          targetType: 'plant',
          targetId: plantId,
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

    formatDate(date) {
      if (!date) return '未知'
      return new Date(date).toLocaleDateString('zh-CN')
    },

    getVulnerabilityType(vulnerability) {
      const typeMap = {
        '高': 'danger',
        '中': 'warning',
        '低': 'success'
      }
      return typeMap[vulnerability] || 'info'
    },

    getDiseaseType(type) {
      const typeMap = {
        '病害': 'warning',
        '虫害': 'danger'
      }
      return typeMap[type] || 'info'
    },

    viewDiseaseDetail(diseaseId) {
      if (this.navigating) return
      this.navigating = true

      // 跳转到病虫害详情页面
      this.$router.push(`/normal/pest-disease-detail/${diseaseId}`).finally(() => {
        this.navigating = false
      })
    }
  }
}
</script>

<style scoped>
.plant-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.back-section {
  margin-bottom: 20px;
}

.plant-detail {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.plant-header {
  display: flex;
  padding: 40px;
  gap: 40px;
}

.plant-image-section {
  flex: 0 0 400px;
}

.plant-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 12px;
}

.plant-info-section {
  flex: 1;
}

.plant-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 20px 0;
}

.plant-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.view-count {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 0.9rem;
}

.plant-description,
.plant-efficacy {
  margin-bottom: 24px;
}

.plant-description h3,
.plant-efficacy h3 {
  font-size: 1.2rem;
  color: #2c3e50;
  margin: 0 0 12px 0;
}

.plant-description p,
.plant-efficacy p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.detail-tabs {
  padding: 0 40px 40px;
}

.basic-info {
  padding: 20px 0;
}

.info-item {
  display: flex;
  margin-bottom: 16px;
}

.info-item label {
  font-weight: 600;
  color: #2c3e50;
  width: 100px;
  flex-shrink: 0;
}

.info-item span {
  color: #606266;
}

.related-diseases {
  padding: 20px 0;
}

.disease-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
}

.disease-item {
  padding: 24px;
  border: 1px solid #e1e8ed;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;
}

.disease-item.clickable {
  cursor: pointer;
}

.disease-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: #409eff;
}

.disease-item.clickable:hover {
  background: #f8fbff;
}

.disease-item.clickable:active {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.disease-item.navigating {
  opacity: 0.7;
  pointer-events: none;
  cursor: wait;
}

.disease-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f2f5;
}

.disease-title-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.disease-name {
  margin: 0;
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
}

.disease-type-tag {
  flex-shrink: 0;
}

.vulnerability-tag {
  flex-shrink: 0;
  font-weight: 500;
}

.disease-content {
  margin-bottom: 16px;
}

.disease-description,
.disease-symptoms,
.disease-notes {
  margin-bottom: 16px;
}

.disease-description h5,
.disease-symptoms h5,
.disease-notes h5 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 0.9rem;
  font-weight: 600;
}

.disease-description p,
.disease-symptoms p,
.disease-notes p {
  margin: 0;
  color: #606266;
  font-size: 0.9rem;
  line-height: 1.6;
}

.disease-details {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-label {
  font-size: 0.85rem;
  color: #909399;
  font-weight: 500;
}

.detail-value {
  font-size: 0.85rem;
  color: #2c3e50;
  font-weight: 500;
}

.disease-click-hint {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 0.8rem;
  opacity: 0;
  transition: all 0.3s ease;
}

.disease-item:hover .disease-click-hint {
  opacity: 1;
  color: #409eff;
}

.disease-click-hint i {
  font-size: 0.7rem;
}

.empty-tip {
  font-size: 0.8rem;
  color: #c0c4cc;
  margin-top: 8px;
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
  .plant-header {
    flex-direction: column;
    padding: 20px;
    gap: 20px;
  }
  
  .plant-image-section {
    flex: none;
  }
  
  .plant-title {
    font-size: 2rem;
  }
  
  .detail-tabs {
    padding: 0 20px 20px;
  }
}
</style>
