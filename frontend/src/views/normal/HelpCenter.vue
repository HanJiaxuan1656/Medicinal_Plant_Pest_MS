<template>
  <div class="help-center-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-question"></i>
          我的求助
        </h1>
        <p class="page-subtitle">查看和管理您的求助记录，专家为您答疑解惑</p>
        <el-button type="primary" size="large" @click="showCreateDialog" class="create-help-btn">
          <i class="el-icon-edit"></i>
          发布求助
        </el-button>
      </div>
    </div>

    <!-- 筛选和搜索区域 -->
    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchQuery"
            placeholder="搜索求助内容..."
            prefix-icon="el-icon-search"
            size="large"
            @input="handleSearch"
            clearable
          ></el-input>
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="selectedStatus"
            placeholder="回复状态"
            size="large"
            @change="handleStatusChange"
            clearable
          >
            <el-option label="全部回复状态" value=""></el-option>
            <el-option label="待回复" value="pending"></el-option>
            <el-option label="已回复" value="replied"></el-option>
            <el-option label="已解决" value="resolved"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="selectedAuditStatus"
            placeholder="审核状态"
            size="large"
            @change="handleAuditStatusChange"
            clearable
          >
            <el-option label="全部审核状态" value=""></el-option>
            <el-option label="待审核" value="pending"></el-option>
            <el-option label="已审核" value="approved"></el-option>
            <el-option label="已拒绝" value="rejected"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="sortBy"
            placeholder="排序方式"
            size="large"
            @change="handleSortChange"
          >
            <el-option label="最新发布" value="created_at"></el-option>
            <el-option label="最新回复" value="updated_at"></el-option>
            <el-option label="回复数量" value="reply_count"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="showCreateDialog" size="large" style="width: 100%">
            发布求助
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 求助列表 -->
    <div class="help-list" v-loading="loading">
      <div class="help-container">
        <div v-for="help in helpRequests" :key="help.id" class="help-item" @click="viewHelpDetail(help)">
          <div class="help-header">
            <div class="help-title">{{ help.title }}</div>
            <div class="help-status">
              <el-tag :type="getStatusType(help.status)" size="small">
                {{ getStatusText(help.status) }}
              </el-tag>
              <el-tag :type="getAuditStatusType(help.status)" size="small">
                {{ getAuditStatusText(help.status) }}
              </el-tag>
            </div>
          </div>
          
          <div class="help-content">
            <p class="help-description">{{ help.description }}</p>
            <div class="help-images" v-if="help.images && help.images.length > 0">
              <img
                v-for="(image, index) in help.images.slice(0, 3)"
                :key="index"
                :src="image"
                class="help-image"
                @error="handleImageError"
              />
              <div v-if="help.images.length > 3" class="more-images">
                +{{ help.images.length - 3 }}
              </div>
            </div>
          </div>
          
          <div class="help-footer">
            <div class="help-meta">
              <span class="time">
                <i class="el-icon-time"></i>
                {{ formatDate(help.createdAt) }}
              </span>
            </div>
            <div class="help-stats">
              <span class="reply-count">
                <i class="el-icon-chat-dot-round"></i>
                {{ help.replyCount || 0 }} 回复
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && helpRequests.length === 0" class="empty-state">
        <i class="el-icon-question"></i>
        <h3>您还没有发布过求助</h3>
        <p>遇到病虫害问题？快来发布您的第一个求助吧！</p>
        <el-button type="primary" @click="showCreateDialog">发布求助</el-button>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-section" v-if="totalHelps > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalHelps"
        :page-size="pageSize"
        :page-sizes="[3, 5, 10, 20]"
        :current-page="currentPage"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      ></el-pagination>
    </div>

    <!-- 创建求助对话框 -->
    <el-dialog
      title="发布求助"
      :visible.sync="createDialogVisible"
      width="600px"
      :close-on-click-modal="false"
      :modal-append-to-body="true"
      :append-to-body="true"
      custom-class="help-dialog"
    >
      <el-form :model="helpForm" :rules="helpRules" ref="helpForm" label-width="80px">
        <el-form-item label="求助标题" prop="title">
          <el-input v-model="helpForm.title" placeholder="请简要描述您的问题"></el-input>
        </el-form-item>
        
        <el-form-item label="详细描述" prop="description">
          <el-input
            v-model="helpForm.description"
            type="textarea"
            :rows="6"
            placeholder="请详细描述病虫害症状、发生时间、环境条件等信息"
            maxlength="1000"
            show-word-limit
          ></el-input>
        </el-form-item>

        <el-form-item label="上传图片">
          <el-upload
            action="/api/files/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
            accept="image/*"
            class="avatar-uploader"
          >
            <img v-if="helpForm.imageUrl" :src="helpForm.imageUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            <div slot="tip" class="el-upload__tip">
              只能上传jpg/png文件，且不超过2MB，仅支持1张图片
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHelp" :loading="submitting">发布</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getHelpRequests, createHelpRequest } from '@/api/normal'

export default {
  name: 'HelpCenter',
  data() {
    return {
      searchQuery: '',
      selectedStatus: '',
      selectedAuditStatus: '',
      sortBy: 'created_at',
      helpRequests: [],
      loading: false,
      totalHelps: 0,
      pageSize: 5,
      currentPage: 1,
      
      // 创建求助
      createDialogVisible: false,
      submitting: false,
      helpForm: {
        title: '',
        description: '',
        imageUrl: ''
      },
      helpRules: {
        title: [
          { required: true, message: '请输入求助标题', trigger: 'blur' },
          { max: 100, message: '标题长度不能超过 100 个字符', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入详细描述', trigger: 'blur' },
          { max: 1000, message: '描述长度不能超过 1000 个字符', trigger: 'blur' }
        ]
      },
      uploadHeaders: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    }
  },
  created() {
    this.fetchHelpRequests()
  },
  methods: {
    async fetchHelpRequests() {
      try {
        this.loading = true
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize,
          search: this.searchQuery,
          status: this.selectedStatus,
          auditStatus: this.selectedAuditStatus,
          sortBy: this.sortBy
        }
        
        const response = await getHelpRequests(params)
        this.helpRequests = response.data.data.list || []
        this.totalHelps = response.data.data.total || 0
      } catch (error) {
        console.error('获取求助列表失败:', error)
        this.$message.error('获取求助列表失败')
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.currentPage = 1
      this.fetchHelpRequests()
    },

    handleStatusChange() {
      this.currentPage = 1
      this.fetchHelpRequests()
    },

    handleAuditStatusChange() {
      this.currentPage = 1
      this.fetchHelpRequests()
    },

    handleSortChange() {
      this.currentPage = 1
      this.fetchHelpRequests()
    },

    handlePageChange(page) {
      this.currentPage = page
      this.fetchHelpRequests()
    },

    handleSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
      this.fetchHelpRequests()
    },

    viewHelpDetail(help) {
      this.$router.push(`/normal/help-detail/${help.id}`)
    },



    showCreateDialog() {
      this.createDialogVisible = true
      this.resetHelpForm()
    },

    resetHelpForm() {
      this.helpForm = {
        title: '',
        description: '',
        imageUrl: ''
      }
      if (this.$refs.helpForm) {
        this.$refs.helpForm.resetFields()
      }
    },

    async submitHelp() {
      this.$refs.helpForm.validate(async (valid) => {
        if (valid) {
          try {
            this.submitting = true

            await createHelpRequest({
              title: this.helpForm.title,
              description: this.helpForm.description,
              imageUrl: this.helpForm.imageUrl
            })
            
            this.$message.success('求助发布成功，等待审核')
            this.createDialogVisible = false
            // 重置筛选条件，确保能看到新发布的求助
            this.selectedStatus = ''
            this.selectedAuditStatus = ''
            this.searchQuery = ''
            this.currentPage = 1
            this.fetchHelpRequests()
          } catch (error) {
            console.error('发布求助失败:', error)
            this.$message.error('发布求助失败')
          } finally {
            this.submitting = false
          }
        }
      })
    },

    handleUploadSuccess(response) {
      if (response.code === 1) {
        this.helpForm.imageUrl = response.data
        this.$message.success('图片上传成功')
      } else {
        this.$message.error(response.msg || '上传失败')
      }
    },

    beforeUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    },

    handleImageError(event) {
      event.target.src = '/default-image.jpg'
    },

    formatDate(date) {
      if (!date) return '未知'
      return new Date(date).toLocaleString('zh-CN')
    },

    getStatusType(help) {
      // 根据回复数量判断回复状态
      const replyCount = help.replyCount || 0
      if (replyCount === 0) {
        return 'warning' // 待回复
      } else {
        return 'primary' // 已回复
      }
    },

    getStatusText(help) {
      // 根据回复数量判断回复状态
      const replyCount = help.replyCount || 0
      if (replyCount === 0) {
        return '待回复'
      } else {
        return '已回复'
      }
    },

    getAuditStatusType(status) {
      // 根据数据库中的status字段判断审核状态
      const typeMap = {
        'pending': 'warning',    // 待审核
        'approved': 'success',   // 已审核通过
        'rejected': 'danger'     // 已拒绝
      }
      return typeMap[status] || 'info'
    },

    getAuditStatusText(status) {
      // 根据数据库中的status字段判断审核状态文本
      const textMap = {
        'pending': '待审核',
        'approved': '已审核',
        'rejected': '已拒绝'
      }
      return textMap[status] || '未知'
    }
  }
}
</script>

<style scoped>
.help-center-container {
  padding: 0;
  min-height: 100vh;
  background: #f5f7fa;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  padding: 40px 0;
  color: white;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 2;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="20" cy="20" r="2" fill="rgba(255,255,255,0.1)"/><circle cx="80" cy="40" r="1.5" fill="rgba(255,255,255,0.1)"/><circle cx="40" cy="80" r="1" fill="rgba(255,255,255,0.1)"/></svg>');
  opacity: 0.3;
  z-index: 1;
}

.page-title {
  font-size: 3rem;
  font-weight: 700;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
}

.page-title i {
  font-size: 3.5rem;
  color: #ffd700;
}

.page-subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
  margin: 0 0 30px 0;
  font-weight: 300;
}

.create-help-btn {
  font-size: 1.1rem;
  padding: 12px 30px;
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  backdrop-filter: blur(10px);
}

.create-help-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

/* 筛选区域 */
.filter-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.filter-section >>> .el-input__inner,
.filter-section >>> .el-select .el-input__inner {
  border-radius: 12px;
  border: 2px solid #e1e8ed;
  transition: all 0.3s ease;
}

.filter-section >>> .el-input__inner:focus,
.filter-section >>> .el-select .el-input__inner:focus {
  border-color: #67c23a;
  box-shadow: 0 0 0 3px rgba(103, 194, 58, 0.1);
}

/* 求助列表 */
.help-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px 40px;
  min-height: 400px;
}

.help-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.help-item {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 2px solid transparent;
}

.help-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  border-color: #67c23a;
}

.help-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.help-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #2c3e50;
  flex: 1;
  margin-right: 16px;
  line-height: 1.4;
}

.help-status {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.help-content {
  margin-bottom: 20px;
}

.help-description {
  color: #606266;
  line-height: 1.6;
  margin: 0 0 16px 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.help-images {
  display: flex;
  gap: 8px;
  align-items: center;
}

.help-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e1e8ed;
}

.more-images {
  width: 60px;
  height: 60px;
  background: #f5f7fa;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 0.9rem;
  font-weight: 600;
}

.help-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  color: #909399;
}

.help-meta {
  display: flex;
  gap: 16px;
  align-items: center;
}

.help-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.help-stats {
  display: flex;
  gap: 16px;
  align-items: center;
}

.help-stats span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #909399;
}

.empty-state i {
  font-size: 4rem;
  margin-bottom: 20px;
  color: #c0c4cc;
}

.empty-state h3 {
  font-size: 1.2rem;
  margin: 0 0 8px 0;
  color: #606266;
}

.empty-state p {
  margin: 0 0 20px 0;
  font-size: 0.9rem;
}

/* 分页区域 */
.pagination-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  text-align: center;
}

/* 对话框样式 */
.dialog-footer {
  text-align: right;
}

/* 修复对话框层级问题 */
.help-dialog {
  z-index: 3000 !important;
}

.help-dialog >>> .el-dialog {
  z-index: 3001 !important;
}

.help-dialog >>> .el-dialog__wrapper {
  z-index: 3000 !important;
}

/* 确保遮罩层不会覆盖对话框内容 */
.el-dialog__wrapper {
  z-index: 2999 !important;
}

.el-overlay {
  z-index: 2998 !important;
}

/* 确保表单元素可以正常交互 */
.help-dialog >>> .el-form-item {
  position: relative;
  z-index: 3002;
}

.help-dialog >>> .el-input,
.help-dialog >>> .el-textarea,
.help-dialog >>> .el-upload {
  position: relative;
  z-index: 3003;
}

/* 图片上传样式 */
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  height: 178px;
  display: block;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
    flex-direction: column;
    gap: 8px;
  }
  
  .page-title i {
    font-size: 2.5rem;
  }
  
  .page-subtitle {
    font-size: 1rem;
  }
  
  .filter-section {
    padding: 20px;
  }
  
  .help-list {
    padding: 0 20px 20px;
  }
  
  .help-header {
    flex-direction: column;
    gap: 12px;
  }
  
  .help-meta {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .help-footer {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
