<template>
  <div class="my-comments-container">
    <div class="page-header">
      <h1>我的评论</h1>
      <p>查看和管理您发表的所有评论</p>
    </div>

    <!-- 统计信息 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon total">
              <i class="el-icon-chat-dot-round"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ totalComments }}</div>
              <div class="stat-label">总评论数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon pending">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ getStatusCount('pending') }}</div>
              <div class="stat-label">审核中</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon approved">
              <i class="el-icon-check"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ getStatusCount('approved') }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon rejected">
              <i class="el-icon-close"></i>
            </div>
            <div class="stat-content">
              <div class="stat-number">{{ getStatusCount('rejected') }}</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-select v-model="selectedType" placeholder="评论类型" @change="handleTypeChange" clearable>
            <el-option label="全部类型" value=""></el-option>
            <el-option label="植物评论" value="plant"></el-option>
            <el-option label="病虫害评论" value="pest_disease"></el-option>
            <el-option label="农药评论" value="pesticide"></el-option>
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-select v-model="selectedStatus" placeholder="审核状态" @change="handleStatusChange" clearable>
            <el-option label="全部状态" value=""></el-option>
            <el-option label="审核中" value="pending"></el-option>
            <el-option label="已通过" value="approved"></el-option>
            <el-option label="已拒绝" value="rejected"></el-option>
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="handleDateChange"
          ></el-date-picker>
        </el-col>
      </el-row>
    </div>

    <!-- 评论列表 -->
    <div class="comments-list" v-loading="loading">
      <div v-if="comments.length > 0">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <div class="comment-target">
              <el-tag :type="getTypeColor(comment.type)" size="small">
                {{ getTypeText(comment.type) }}
              </el-tag>
              <span class="target-name">{{ comment.targetName }}</span>
            </div>
            <div class="comment-status">
              <el-tag :type="getStatusColor(comment.status)" size="small">
                {{ getStatusText(comment.status) }}
              </el-tag>
              <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
            </div>
          </div>
          
          <div class="comment-content">
            <p>{{ comment.content }}</p>
          </div>

          <div class="comment-meta-info">
            <div class="meta-item">
              <i class="el-icon-document"></i>
              <span>评论对象：{{ comment.targetName }}</span>
            </div>
            <div class="meta-item" v-if="comment.status === 'rejected'">
              <i class="el-icon-warning"></i>
              <span class="reject-reason">审核未通过，请检查评论内容是否符合规范</span>
            </div>
          </div>

          <div class="comment-footer">
            <el-button type="text" @click="viewTarget(comment)" size="small" icon="el-icon-view">
              查看原文
            </el-button>
            <el-button
              type="text"
              @click="editComment(comment)"
              size="small"
              icon="el-icon-edit"
              v-if="comment.status === 'pending'"
              class="edit-btn"
            >
              编辑
            </el-button>
            <el-button type="text" @click="deleteComment(comment)" size="small" icon="el-icon-delete" class="delete-btn">
              删除
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-else-if="!loading" class="empty-state">
        <div class="empty-icon">
          <i class="el-icon-chat-dot-round"></i>
        </div>
        <h3>暂无评论</h3>
        <p v-if="!selectedType && !selectedStatus">您还没有发表过评论，快去浏览植物、病虫害或农药信息并发表您的看法吧！</p>
        <p v-else>没有找到符合筛选条件的评论</p>
        <div class="empty-actions" v-if="!selectedType && !selectedStatus">
          <el-button type="primary" @click="goToPlants" icon="el-icon-plus">
            去发表评论
          </el-button>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-section" v-if="totalComments > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalComments"
        :page-size="pageSize"
        :page-sizes="[3,5,10, 20]"
        :current-page="currentPage"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      ></el-pagination>
    </div>

    <!-- 编辑评论对话框 -->
    <el-dialog
      title="编辑评论"
      :visible.sync="editDialogVisible"
      width="600px"
      :close-on-click-modal="false"
      :modal-append-to-body="true"
      :append-to-body="true"
      custom-class="edit-comment-dialog"
      center
    >
      <div class="dialog-content">
        <div class="dialog-tip">
          <i class="el-icon-info"></i>
          <span>只有待审核的评论才能编辑，编辑后需要重新审核</span>
        </div>

        <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="0">
          <el-form-item prop="content">
            <el-input
              v-model="editForm.content"
              type="textarea"
              :rows="8"
              placeholder="请输入评论内容..."
              maxlength="500"
              show-word-limit
              resize="none"
              class="edit-textarea"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelEdit" size="medium">
          <i class="el-icon-close"></i>
          取消
        </el-button>
        <el-button type="primary" @click="updateComment" :loading="updating" size="medium">
          <i class="el-icon-check"></i>
          保存修改
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getMyComments, deleteComment, updateComment } from '@/api/normal'

export default {
  name: 'MyComments',
  data() {
    return {
      selectedType: '',
      selectedStatus: '',
      dateRange: null,
      comments: [],
      allComments: [], // 存储所有评论用于统计
      loading: false,
      totalComments: 0,
      pageSize: 5,
      currentPage: 1,

      // 编辑评论
      editDialogVisible: false,
      updating: false,
      editForm: {
        id: null,
        content: ''
      },
      editRules: {
        content: [
          { required: true, message: '请输入评论内容', trigger: 'blur' },
          { min: 1, max: 500, message: '评论长度在 1 到 500 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchComments()
  },
  methods: {
    async fetchComments() {
      try {
        this.loading = true
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize,
          type: this.selectedType,
          status: this.selectedStatus
        }
        
        if (this.dateRange && this.dateRange.length === 2) {
          params.startDate = this.dateRange[0]
          params.endDate = this.dateRange[1]
        }
        
        const response = await getMyComments(params)
        this.comments = response.data.data.list || []
        this.totalComments = response.data.data.total || 0

        // 获取所有评论用于统计（不分页）
        if (this.currentPage === 1 && !this.selectedType && !this.selectedStatus && !this.dateRange) {
          const allParams = { page: 1, pageSize: 1000 } // 获取大量数据用于统计
          const allResponse = await getMyComments(allParams)
          this.allComments = allResponse.data.data.list || []
        }
      } catch (error) {
        console.error('获取评论列表失败:', error)
        this.$message.error('获取评论列表失败')
      } finally {
        this.loading = false
      }
    },

    handleTypeChange() {
      this.currentPage = 1
      this.fetchComments()
    },

    handleStatusChange() {
      this.currentPage = 1
      this.fetchComments()
    },

    handleDateChange() {
      this.currentPage = 1
      this.fetchComments()
    },

    handlePageChange(page) {
      this.currentPage = page
      this.fetchComments()
    },

    handleSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
      this.fetchComments()
    },

    viewTarget(comment) {
      // 根据评论类型跳转到对应的详情页
      const routeMap = {
        'plant': `/normal/plant-detail/${comment.targetId}`,
        'pest_disease': `/normal/pest-disease-detail/${comment.targetId}`,
        'pesticide': `/normal/pesticide-detail/${comment.targetId}`
      }
      
      const route = routeMap[comment.type]
      if (route) {
        this.$router.push(route)
      }
    },

    editComment(comment) {
      this.editForm = {
        id: comment.id,
        content: comment.content
      }
      this.editDialogVisible = true

      // 确保弹窗打开后重置表单验证状态
      this.$nextTick(() => {
        if (this.$refs.editForm) {
          this.$refs.editForm.clearValidate()
        }
      })
    },

    cancelEdit() {
      this.editDialogVisible = false
      this.editForm = {
        id: null,
        content: ''
      }
      if (this.$refs.editForm) {
        this.$refs.editForm.clearValidate()
      }
    },

    async updateComment() {
      if (!this.$refs.editForm) {
        this.$message.error('表单引用不存在')
        return
      }

      this.$refs.editForm.validate(async (valid) => {
        if (valid) {
          try {
            this.updating = true

            await updateComment(this.editForm.id, { content: this.editForm.content })

            this.$message.success('评论更新成功，等待重新审核')
            this.cancelEdit()
            this.fetchComments()
          } catch (error) {
            console.error('更新评论失败:', error)
            let errorMsg = '更新评论失败'
            if (error.response && error.response.data && error.response.data.message) {
              errorMsg = error.response.data.message
            } else if (error.message) {
              errorMsg = error.message
            }
            this.$message.error(errorMsg)
          } finally {
            this.updating = false
          }
        } else {
          this.$message.warning('请检查输入内容')
        }
      })
    },

    deleteComment(comment) {
      this.$confirm('确认删除这条评论吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteComment(comment.id)
          this.$message.success('评论删除成功')
          this.fetchComments()
        } catch (error) {
          console.error('删除评论失败:', error)
          this.$message.error('删除评论失败')
        }
      }).catch(() => {
        // 取消删除
      })
    },

    formatDate(date) {
      if (!date) return '未知'
      return new Date(date).toLocaleString('zh-CN')
    },

    getTypeText(type) {
      const textMap = {
        'plant': '植物',
        'pest_disease': '病虫害',
        'pesticide': '农药'
      }
      return textMap[type] || '未知'
    },

    getTypeColor(type) {
      const colorMap = {
        'plant': 'success',
        'pest_disease': 'warning',
        'pesticide': 'info'
      }
      return colorMap[type] || 'info'
    },

    getStatusText(status) {
      const textMap = {
        'pending': '审核中',
        'approved': '已通过',
        'rejected': '已拒绝'
      }
      return textMap[status] || '未知'
    },

    getStatusColor(status) {
      const colorMap = {
        'pending': 'warning',
        'approved': 'success',
        'rejected': 'danger'
      }
      return colorMap[status] || 'info'
    },

    getStatusCount(status) {
      if (!this.allComments || this.allComments.length === 0) {
        return 0
      }
      return this.allComments.filter(comment => comment.status === status).length
    },

    goToPlants() {
      this.$router.push('/normal/plants')
    }
  }
}
</script>

<style scoped>
/* 确保弹窗层级正确 */
.edit-comment-dialog >>> .el-dialog__wrapper {
  z-index: 3000 !important;
}

.edit-comment-dialog >>> .v-modal {
  z-index: 2999 !important;
}
.my-comments-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 40px 0;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.page-header h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin: 0 0 10px 0;
}

.page-header p {
  color: #606266;
  font-size: 1.1rem;
  margin: 0;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.pending {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.approved {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.rejected {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  color: #606266;
  font-size: 0.9rem;
  font-weight: 500;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.comments-list {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  min-height: 400px;
}

.comment-item {
  border-bottom: 1px solid #e1e8ed;
  padding: 20px;
  margin-bottom: 16px;
  border-radius: 12px;
  background: #fafbfc;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.comment-item:hover {
  background: #f8fbff;
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.comment-item:last-child {
  border-bottom: 1px solid #e1e8ed;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.comment-target {
  display: flex;
  align-items: center;
  gap: 8px;
}

.target-name {
  font-weight: 600;
  color: #2c3e50;
}

.comment-status {
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-time {
  color: #909399;
  font-size: 0.9rem;
}

.comment-content {
  margin-bottom: 16px;
}

.comment-content p {
  color: #606266;
  line-height: 1.6;
  margin: 0;
  background: #f8f9fa;
  padding: 12px 16px;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.comment-meta-info {
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 0.9rem;
  color: #909399;
}

.meta-item i {
  font-size: 14px;
}

.reject-reason {
  color: #f56c6c;
  font-weight: 500;
}

.comment-footer {
  display: flex;
  gap: 16px;
}

.edit-btn {
  color: #409eff !important;
}

.edit-btn:hover {
  background-color: rgba(64, 158, 255, 0.1) !important;
}

.delete-btn {
  color: #f56c6c !important;
}

.delete-btn:hover {
  background-color: rgba(245, 108, 108, 0.1) !important;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #909399;
}

.empty-icon {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.empty-icon::before {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  opacity: 0.2;
  transform: scale(1.2);
  animation: pulse 2s infinite;
}

.empty-icon i {
  font-size: 3rem;
  color: white;
  z-index: 1;
}

@keyframes pulse {
  0% {
    transform: scale(1.2);
    opacity: 0.2;
  }
  50% {
    transform: scale(1.4);
    opacity: 0.1;
  }
  100% {
    transform: scale(1.2);
    opacity: 0.2;
  }
}

.empty-state h3 {
  font-size: 1.4rem;
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-weight: 600;
}

.empty-state p {
  margin: 0 0 24px 0;
  font-size: 1rem;
  color: #606266;
  line-height: 1.6;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.empty-actions {
  margin-top: 24px;
}

.empty-actions .el-button {
  padding: 12px 32px;
  font-size: 1rem;
  border-radius: 24px;
}

.empty-state p {
  margin: 0;
  font-size: 0.9rem;
}

.pagination-section {
  margin-top: 20px;
  text-align: center;
}

/* 编辑弹窗样式 */
.edit-comment-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.edit-comment-dialog >>> .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px 32px;
  margin: 0;
}

.edit-comment-dialog >>> .el-dialog__title {
  color: white;
  font-size: 1.2rem;
  font-weight: 600;
}

.edit-comment-dialog >>> .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.edit-comment-dialog >>> .el-dialog__headerbtn .el-dialog__close:hover {
  color: #f0f0f0;
}

.edit-comment-dialog >>> .el-dialog__body {
  padding: 32px;
  background: #fafbfc;
}

.edit-comment-dialog >>> .el-dialog__footer {
  background: white;
  padding: 20px 32px;
  border-top: 1px solid #e1e8ed;
}

.dialog-content {
  position: relative;
}

.dialog-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 8px;
  margin-bottom: 24px;
  color: #1890ff;
  font-size: 0.9rem;
}

.dialog-tip i {
  font-size: 16px;
}

.edit-textarea >>> .el-textarea__inner {
  border: 2px solid #e1e8ed;
  border-radius: 12px;
  padding: 16px;
  font-size: 1rem;
  line-height: 1.6;
  background: white;
  transition: all 0.3s ease;
  resize: none;
}

.edit-textarea >>> .el-textarea__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.edit-textarea >>> .el-input__count {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 4px;
  padding: 2px 6px;
  font-size: 0.8rem;
}

.dialog-footer {
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer .el-button {
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 6px;
}

.dialog-footer .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.dialog-footer .el-button--primary:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .my-comments-container {
    padding: 10px;
  }
  
  .page-header h1 {
    font-size: 2rem;
  }
  
  .comment-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .comment-footer {
    flex-wrap: wrap;
    gap: 8px;
  }
}
</style>

<!-- 全局样式，确保弹窗正常显示 -->
<style>
.edit-comment-dialog .el-dialog {
  background: white !important;
  border-radius: 16px !important;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15) !important;
}

.edit-comment-dialog .el-dialog__wrapper {
  z-index: 3000 !important;
}

.edit-comment-dialog .el-overlay {
  z-index: 2999 !important;
}

.edit-comment-dialog .el-form-item__content {
  position: relative !important;
}

.edit-comment-dialog .el-textarea {
  position: relative !important;
}

.edit-comment-dialog .el-textarea__inner {
  background: white !important;
  color: #2c3e50 !important;
  border: 2px solid #e1e8ed !important;
}

.edit-comment-dialog .el-textarea__inner:focus {
  border-color: #667eea !important;
  background: white !important;
}
</style>
