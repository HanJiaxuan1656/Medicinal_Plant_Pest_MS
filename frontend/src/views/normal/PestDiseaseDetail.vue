<template>
  <div class="pest-disease-detail-container" v-loading="loading">
    <!-- 返回按钮 -->
    <div class="back-section">
      <el-button @click="goBack" icon="el-icon-arrow-left" type="text" size="large">
        返回病虫害列表
      </el-button>
    </div>

    <div v-if="pestDisease" class="pest-disease-detail">
      <!-- 病虫害基本信息 -->
      <div class="pest-disease-header">
        <div class="pest-disease-image-section">
          <img :src="pestDisease.imageUrl || '/default-pest.jpg'" :alt="pestDisease.name" class="pest-disease-image" />
        </div>
        
        <div class="pest-disease-info-section">
          <h1 class="pest-disease-title">{{ pestDisease.name }}</h1>
          <div class="pest-disease-meta">
            <el-tag :type="getTypeColor(pestDisease.type)" size="medium">{{ pestDisease.type }}</el-tag>
            <span class="view-count">
              <i class="el-icon-view"></i>
              {{ pestDisease.viewCount || 0 }} 次查看
            </span>
          </div>
          
          <div class="pest-disease-description">
            <h3>基本描述</h3>
            <p>{{ pestDisease.description || '暂无描述' }}</p>
          </div>
          
          <div class="pest-disease-symptoms" v-if="pestDisease.symptoms">
            <h3>主要症状</h3>
            <p>{{ pestDisease.symptoms }}</p>
          </div>
        </div>
      </div>

      <!-- 详细信息标签页 -->
      <div class="detail-tabs">
        <el-tabs v-model="activeTab" type="card">
          <el-tab-pane label="相关农药" name="pesticides">
            <div class="related-pesticides">
              <div v-if="relatedPesticides.length > 0" class="pesticide-list">
                <div
                  v-for="pesticide in relatedPesticides"
                  :key="pesticide.id"
                  class="pesticide-item clickable"
                  @click="viewPesticideDetail(pesticide.id)"
                >
                  <div class="pesticide-header">
                    <div class="pesticide-title-section">
                      <h4 class="pesticide-name">{{ pesticide.name }}</h4>
                      <el-tag
                        :type="getCategoryType(pesticide.category)"
                        size="small"
                        class="pesticide-category-tag"
                      >
                        {{ pesticide.category }}
                      </el-tag>
                    </div>
                    <el-tag
                      :type="getEffectivenessType(pesticide.effectiveness)"
                      size="medium"
                      class="effectiveness-tag"
                    >
                      效果：{{ pesticide.effectiveness }}
                    </el-tag>
                  </div>

                  <div class="pesticide-content">
                    <div class="pesticide-description">
                      <h5>农药描述</h5>
                      <p>{{ pesticide.description || '暂无描述' }}</p>
                    </div>

                    <div v-if="pesticide.activeIngredient" class="pesticide-ingredient">
                      <h5>有效成分</h5>
                      <p>{{ pesticide.activeIngredient }}</p>
                    </div>

                    <div v-if="pesticide.dosage || pesticide.applicationMethod" class="pesticide-details">
                      <div v-if="pesticide.dosage" class="detail-item">
                        <span class="detail-label">推荐用量：</span>
                        <span class="detail-value">{{ pesticide.dosage }}</span>
                      </div>
                      <div v-if="pesticide.applicationMethod" class="detail-item">
                        <span class="detail-label">使用方法：</span>
                        <span class="detail-value">{{ pesticide.applicationMethod }}</span>
                      </div>
                    </div>

                    <div v-if="pesticide.notes" class="pesticide-notes">
                      <h5>注意事项</h5>
                      <p>{{ pesticide.notes }}</p>
                    </div>
                  </div>

                  <div class="pesticide-click-hint">
                    <i class="el-icon-right"></i>
                    <span>点击查看详情</span>
                  </div>
                </div>
              </div>
              <div v-else class="empty-state">
                <i class="el-icon-info"></i>
                <p>暂无相关农药信息</p>
                <p class="empty-tip">该病虫害目前没有记录相关的农药信息</p>
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
import { getPestDiseaseDetail, getComments, addComment } from '@/api/normal'

export default {
  name: 'PestDiseaseDetail',
  data() {
    return {
      loading: false,
      pestDisease: null,
      relatedPesticides: [],
      comments: [],
      activeTab: 'pesticides',
      newComment: '',
      submittingComment: false
    }
  },
  created() {
    this.fetchPestDiseaseDetail()
    this.fetchComments()
  },
  methods: {
    async fetchPestDiseaseDetail() {
      try {
        this.loading = true
        const pestDiseaseId = this.$route.params.id
        const response = await getPestDiseaseDetail(pestDiseaseId)
        this.pestDisease = response.data.data.pestDisease
        this.relatedPesticides = response.data.data.relatedPesticides || []
      } catch (error) {
        console.error('获取病虫害详情失败:', error)
        this.$message.error('获取病虫害详情失败')
      } finally {
        this.loading = false
      }
    },

    async fetchComments() {
      try {
        const pestDiseaseId = this.$route.params.id
        const response = await getComments('pest_disease', pestDiseaseId, { page: 1, pageSize: 50 })
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
        const pestDiseaseId = this.$route.params.id
        await addComment({
          targetType: 'pest_disease',
          targetId: pestDiseaseId,
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

    getTypeColor(type) {
      return type === '病害' ? 'warning' : 'danger'
    },

    viewPesticideDetail(pesticideId) {
      // 跳转到农药详情页面
      this.$router.push(`/normal/pesticide-detail/${pesticideId}`)
    },

    getCategoryType(category) {
      const typeMap = {
        '杀虫剂': 'danger',
        '杀菌剂': 'warning',
        '除草剂': 'success',
        '植物生长调节剂': 'info'
      }
      return typeMap[category] || 'info'
    },

    getEffectivenessType(effectiveness) {
      const typeMap = {
        '高': 'success',
        '中': 'warning',
        '低': 'info'
      }
      return typeMap[effectiveness] || 'info'
    }
  }
}
</script>

<style scoped>
.pest-disease-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.back-section {
  margin-bottom: 20px;
}

.pest-disease-detail {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.pest-disease-header {
  display: flex;
  padding: 40px;
  gap: 40px;
}

.pest-disease-image-section {
  flex: 0 0 400px;
}

.pest-disease-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 12px;
}

.pest-disease-info-section {
  flex: 1;
}

.pest-disease-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 20px 0;
}

.pest-disease-meta {
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

.pest-disease-description,
.pest-disease-symptoms {
  margin-bottom: 24px;
}

.pest-disease-description h3,
.pest-disease-symptoms h3 {
  font-size: 1.2rem;
  color: #2c3e50;
  margin: 0 0 12px 0;
}

.pest-disease-description p,
.pest-disease-symptoms p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
}

.detail-tabs {
  padding: 0 40px 40px;
}



.related-pesticides {
  padding: 20px 0;
}

.pesticide-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
}

.pesticide-item {
  padding: 24px;
  border: 1px solid #e1e8ed;
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  position: relative;
}

.pesticide-item.clickable {
  cursor: pointer;
}

.pesticide-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
  border-color: #409eff;
}

.pesticide-item.clickable:hover {
  background: #f8fbff;
}

.pesticide-item.clickable:active {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.pesticide-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f2f5;
}

.pesticide-title-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.pesticide-name {
  margin: 0;
  color: #2c3e50;
  font-size: 1.1rem;
  font-weight: 600;
}

.pesticide-category-tag {
  flex-shrink: 0;
}

.effectiveness-tag {
  flex-shrink: 0;
  font-weight: 500;
}

.pesticide-content {
  margin-bottom: 16px;
}

.pesticide-description,
.pesticide-ingredient,
.pesticide-notes {
  margin-bottom: 16px;
}

.pesticide-description h5,
.pesticide-ingredient h5,
.pesticide-notes h5 {
  margin: 0 0 8px 0;
  color: #2c3e50;
  font-size: 0.9rem;
  font-weight: 600;
}

.pesticide-description p,
.pesticide-ingredient p,
.pesticide-notes p {
  margin: 0;
  color: #606266;
  font-size: 0.9rem;
  line-height: 1.6;
}

.pesticide-details {
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

.pesticide-click-hint {
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

.pesticide-item:hover .pesticide-click-hint {
  opacity: 1;
  color: #409eff;
}

.pesticide-click-hint i {
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
  .pest-disease-header {
    flex-direction: column;
    padding: 20px;
    gap: 20px;
  }
  
  .pest-disease-image-section {
    flex: none;
  }
  
  .pest-disease-title {
    font-size: 2rem;
  }
  
  .detail-tabs {
    padding: 0 20px 20px;
  }
}
</style>
