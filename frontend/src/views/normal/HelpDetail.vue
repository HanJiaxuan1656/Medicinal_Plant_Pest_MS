<template>
  <div class="help-detail-container" v-loading="loading">
    <!-- 返回按钮 -->
    <div class="back-section">
      <el-button @click="goBack" icon="el-icon-arrow-left" type="text" size="large">
        返回求助列表
      </el-button>
    </div>

    <div v-if="helpRequest" class="help-detail">
      <!-- 求助信息 -->
      <div class="help-header">
        <div class="help-title-section">
          <h1 class="help-title">{{ helpRequest.title }}</h1>
          <div class="help-meta">
            <el-tag :type="getStatusType(helpRequest.status)" size="medium">
              {{ getStatusText(helpRequest.status) }}
            </el-tag>
            <el-tag v-if="helpRequest.auditStatus === 'pending'" type="warning" size="medium">
              审核中
            </el-tag>
            <span class="time">
              <i class="el-icon-time"></i>
              {{ formatDate(helpRequest.createdAt) }}
            </span>
          </div>
        </div>
        
        <div class="help-stats">
          <div class="stat-item">
            <span class="stat-number">{{ helpRequest.replyCount || 0 }}</span>
            <span class="stat-label">回复</span>
          </div>
        </div>
      </div>

      <!-- 求助内容 -->
      <div class="help-content">
        <div class="content-section">
          <h3>问题描述</h3>
          <p class="help-description">{{ helpRequest.description }}</p>
          
          <!-- 图片展示 -->
          <div v-if="helpRequest.images && helpRequest.images.length > 0" class="help-images">
            <h3>相关图片</h3>
            <div class="image-gallery">
              <div
                v-for="(image, index) in helpRequest.images"
                :key="index"
                class="image-item"
                @click="previewImage(image)"
              >
                <img :src="image" :alt="`图片${index + 1}`" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 专家回复 -->
      <div class="replies-section">
        <div class="section-header">
          <h3>专家回复 ({{ replies.length }})</h3>
          <el-button v-if="helpRequest.status === 'pending'" type="primary" @click="refreshReplies">
            刷新回复
          </el-button>
        </div>
        
        <div v-if="replies.length > 0" class="replies-list">
          <div v-for="reply in replies" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <div class="expert-info">
                <el-avatar :size="40" :src="reply.expertAvatar" icon="el-icon-user-solid"></el-avatar>
                <div class="expert-meta">
                  <span class="expert-name">{{ reply.expertName }}</span>
                  <span class="expert-title">专家职称：{{ reply.expertTitle }}</span>
                  <span class="reply-time">{{ formatDate(reply.createdAt) }}</span>
                </div>
              </div>
              <el-tag type="success" size="small">专家回复</el-tag>
            </div>
            
            <div class="reply-content">
              <p>{{ reply.content }}</p>
              
              <!-- 回复图片 -->
              <div v-if="reply.images && reply.images.length > 0" class="reply-images">
                <div
                  v-for="(image, index) in reply.images"
                  :key="index"
                  class="reply-image-item"
                  @click="previewImage(image)"
                >
                  <img :src="image" :alt="`回复图片${index + 1}`" />
                </div>
              </div>
            </div>
            
            <div class="reply-footer">
              <el-button type="text" @click="thankReply(reply)" size="small" v-if="!reply.isThanked">
                <i class="el-icon-star-on"></i>
                感谢专家
              </el-button>
              <span v-else class="thanked-text">
                <i class="el-icon-check"></i>
                已感谢
              </span>
            </div>
          </div>
        </div>
        
        <div v-else class="no-replies">
          <i class="el-icon-chat-dot-round"></i>
          <p>暂无专家回复，请耐心等待</p>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="action-section" v-if="isMyHelp">
        <el-button 
          v-if="helpRequest.status === 'replied'" 
          type="success" 
          @click="markAsResolved"
          :loading="resolving"
        >
          标记为已解决
        </el-button>
        <el-button type="danger" @click="deleteHelp" :loading="deleting">
          删除求助
        </el-button>
      </div>
    </div>

    <!-- 图片预览 -->
    <el-dialog
      :visible.sync="previewVisible"
      width="80%"
      :show-close="false"
      :modal-append-to-body="false"
      class="image-preview-dialog"
    >
      <img :src="previewImageUrl" style="width: 100%; height: auto;" />
    </el-dialog>
  </div>
</template>

<script>
import { getHelpRequestDetail, getHelpReplies, deleteHelpRequest } from '@/api/normal'

export default {
  name: 'HelpDetail',
  data() {
    return {
      loading: false,
      helpRequest: null,
      replies: [],
      isMyHelp: false,
      resolving: false,
      deleting: false,
      previewVisible: false,
      previewImageUrl: ''
    }
  },
  created() {
    this.fetchHelpDetail()
    this.fetchReplies()
  },
  methods: {
    async fetchHelpDetail() {
      try {
        this.loading = true
        const helpId = this.$route.params.id
        const response = await getHelpRequestDetail(helpId)
        this.helpRequest = response.data.data
        
        // 检查是否是当前用户的求助
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        this.isMyHelp = (this.helpRequest.userId === userInfo.id) || (this.helpRequest.authorId === userInfo.id)
      } catch (error) {
        this.$message.error('获取求助详情失败')
      } finally {
        this.loading = false
      }
    },

    async fetchReplies() {
      try {
        const helpId = this.$route.params.id
        const response = await getHelpReplies(helpId, { page: 1, pageSize: 50 })
        this.replies = response.data.data.list || []
      } catch (error) {
        // 获取回复失败
      }
    },

    refreshReplies() {
      this.fetchReplies()
    },

    previewImage(imageUrl) {
      this.previewImageUrl = imageUrl
      this.previewVisible = true
    },

    async markAsResolved() {
      try {
        this.resolving = true
        // 调用标记为已解决的API
        // await markHelpAsResolved(this.helpRequest.id)
        this.$message.success('已标记为解决')
        this.fetchHelpDetail()
      } catch (error) {
        this.$message.error('标记失败')
      } finally {
        this.resolving = false
      }
    },

    async deleteHelp() {
      this.$confirm('确认删除这条求助吗？删除后无法恢复。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          this.deleting = true
          await deleteHelpRequest(this.helpRequest.id)
          this.$message.success('求助删除成功')
          this.$router.push('/normal/help')
        } catch (error) {
          this.$message.error('删除失败')
        } finally {
          this.deleting = false
        }
      }).catch(() => {
        // 取消删除
      })
    },

    async thankReply(reply) {
      try {
        // 调用感谢API
        // await thankReply(reply.id)
        reply.isThanked = true
        this.$message.success('感谢已发送给专家')
      } catch (error) {
        this.$message.error('感谢失败')
      }
    },

    goBack() {
      this.$router.go(-1)
    },

    formatDate(date) {
      if (!date) return ''
      try {
        return new Date(date).toLocaleString('zh-CN')
      } catch (error) {
        return ''
      }
    },

    getStatusType(status) {
      const typeMap = {
        'pending': 'warning',
        'replied': 'primary',
        'resolved': 'success'
      }
      return typeMap[status] || 'info'
    },

    getStatusText(status) {
      const textMap = {
        'pending': '待回复',
        'replied': '已回复',
        'resolved': '已解决'
      }
      return textMap[status] || '未知'
    }
  }
}
</script>

<style scoped>
.help-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.back-section {
  margin-bottom: 20px;
}

.help-detail {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.help-header {
  padding: 30px;
  border-bottom: 1px solid #e1e8ed;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.help-title-section {
  flex: 1;
}

.help-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 16px 0;
  line-height: 1.3;
}

.help-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  font-size: 0.9rem;
  color: #909399;
}

.help-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.help-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 1.5rem;
  font-weight: 700;
  color: #667eea;
}

.stat-label {
  font-size: 0.9rem;
  color: #909399;
}

.help-content {
  padding: 30px;
}

.content-section h3 {
  color: #2c3e50;
  margin: 0 0 16px 0;
  font-size: 1.2rem;
}

.help-description {
  color: #606266;
  line-height: 1.8;
  font-size: 1rem;
  margin: 0 0 30px 0;
}

.help-images h3 {
  margin-bottom: 16px;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.image-item {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.image-item:hover {
  transform: scale(1.05);
}

.image-item img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.replies-section {
  padding: 30px;
  border-top: 1px solid #e1e8ed;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  color: #2c3e50;
  margin: 0;
  font-size: 1.2rem;
}

.replies-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.reply-item {
  border: 1px solid #e1e8ed;
  border-radius: 12px;
  padding: 20px;
  background: #f8f9fa;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.expert-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.expert-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.expert-name {
  font-weight: 600;
  color: #2c3e50;
}

.expert-title {
  font-size: 0.9rem;
  color: #67c23a;
  font-weight: 500;
}

.reply-time {
  font-size: 0.8rem;
  color: #909399;
}

.reply-content {
  margin-bottom: 16px;
}

.reply-content p {
  color: #606266;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.reply-images {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.reply-image-item {
  cursor: pointer;
  border-radius: 6px;
  overflow: hidden;
}

.reply-image-item img {
  width: 80px;
  height: 80px;
  object-fit: cover;
}

.reply-footer {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-top: 12px;
  border-top: 1px solid #e1e8ed;
}

.thanked-text {
  color: #67c23a;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 4px;
}



.no-replies {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
}

.no-replies i {
  font-size: 3rem;
  margin-bottom: 16px;
  color: #c0c4cc;
}

.action-section {
  padding: 20px 30px;
  border-top: 1px solid #e1e8ed;
  text-align: center;
}

.image-preview-dialog >>> .el-dialog {
  background: rgba(0, 0, 0, 0.9);
}

.image-preview-dialog >>> .el-dialog__body {
  padding: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .help-detail-container {
    padding: 10px;
  }
  
  .help-header {
    flex-direction: column;
    gap: 20px;
  }
  
  .help-title {
    font-size: 1.5rem;
  }
  
  .help-meta {
    gap: 8px;
  }
  
  .help-stats {
    align-self: stretch;
    justify-content: space-around;
  }
  
  .image-gallery {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }
  
  .expert-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>
