<template>
  <div class="helps-container">
    <div class="page-header">
      <h1 class="page-title">
        <i class="el-icon-question"></i>
        求助回复管理
      </h1>
      <p class="page-description">回复用户提交的求助请求，提供专业指导</p>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchQuery"
            placeholder="搜索求助标题或描述..."
            prefix-icon="el-icon-search"
            size="large"
            @input="handleSearch"
            clearable
          ></el-input>
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="selectedStatus"
            placeholder="审核状态"
            size="large"
            @change="handleStatusChange"
            clearable
          >
            <el-option label="全部状态" value=""></el-option>
            <el-option label="已审核" value="approved"></el-option>
            <el-option label="待审核" value="pending"></el-option>
            <el-option label="已拒绝" value="rejected"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="replyStatus"
            placeholder="回复状态"
            size="large"
            @change="handleReplyStatusChange"
            clearable
          >
            <el-option label="全部回复状态" value=""></el-option>
            <el-option label="待回复" value="pending"></el-option>
            <el-option label="已回复" value="replied"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select
            v-model="sortBy"
            placeholder="排序方式"
            size="large"
            @change="handleSortChange"
          >
            <el-option label="最新提交" value="created_at"></el-option>
            <el-option label="最早提交" value="created_at_asc"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" size="large" @click="fetchHelpRequests" style="width: 100%">
            <i class="el-icon-refresh"></i>
            刷新
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 求助列表 -->
    <div class="help-list">
      <el-table
        :data="helpRequests"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80"></el-table-column>

        <el-table-column prop="title" label="求助标题" width="180">
          <template slot-scope="scope">
            <div class="help-title">{{ scope.row.title }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="描述" min-width="200">
          <template slot-scope="scope">
            <div class="help-description">{{ scope.row.description }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="imageUrl" label="图片" width="120" align="center">
          <template slot-scope="scope">
            <div class="image-container">
              <img
                v-if="scope.row.imageUrl"
                :src="scope.row.imageUrl"
                alt="求助图片"
                class="help-thumbnail"
                @click="previewImage(scope.row.imageUrl)"
              />
              <div v-else class="no-image">
                <i class="el-icon-picture-outline"></i>
                <span>无图片</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="审核状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="getAuditStatusType(scope.row.status)" size="small">
              {{ getAuditStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="回复状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="getReplyStatusType(scope.row)" size="small">
              {{ getReplyStatusText(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="提交时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === 'approved'"
              type="primary"
              size="mini"
              @click="handleReply(scope.row)"
            >
              {{ getReplyCount(scope.row) > 0 ? '继续回复' : '回复' }}
            </el-button>
            <el-button
              type="info"
              size="mini"
              @click="viewDetail(scope.row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[3,5,10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalHelps"
      ></el-pagination>
    </div>

    <!-- 回复对话框 -->
    <el-dialog
      title="专家回复"
      :visible.sync="replyDialogVisible"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentHelp" class="reply-content">
        <!-- 求助信息 -->
        <div class="help-info">
          <h3>{{ currentHelp.title }}</h3>
          <div class="help-meta">
            <span>提交时间：{{ formatDate(currentHelp.createdAt) }}</span>
            <el-tag :type="getAuditStatusType(currentHelp.status)" size="small">
              {{ getAuditStatusText(currentHelp.status) }}
            </el-tag>
          </div>
          <div class="help-description-full">
            {{ currentHelp.description }}
          </div>
          <div v-if="currentHelp.imageUrl" class="help-image-container">
            <img :src="currentHelp.imageUrl" alt="求助图片" class="help-image" />
          </div>
        </div>

        <!-- 历史回复 -->
        <div v-if="helpReplies.length > 0" class="reply-history">
          <h4>历史回复</h4>
          <div v-for="reply in helpReplies" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <span class="reply-expert">专家回复</span>
              <span class="reply-time">{{ formatDate(reply.createdAt) }}</span>
            </div>
            <div class="reply-content-text">{{ reply.content }}</div>
          </div>
        </div>

        <!-- 新回复表单 -->
        <div class="new-reply">
          <h4>添加回复</h4>
          <el-form :model="replyForm" :rules="replyRules" ref="replyForm">
            <el-form-item prop="content">
              <el-input
                type="textarea"
                v-model="replyForm.content"
                :rows="6"
                placeholder="请输入您的专业回复..."
              ></el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="replyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReply" :loading="submitting">
          发送回复
        </el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      title="求助详情"
      :visible.sync="detailDialogVisible"
      width="800px"
      :close-on-click-modal="false"
    >
      <div v-if="currentHelp" class="help-detail">
        <div class="detail-item">
          <label>标题：</label>
          <span>{{ currentHelp.title }}</span>
        </div>
        <div class="detail-item">
          <label>描述：</label>
          <div class="description-content">{{ currentHelp.description }}</div>
        </div>
        <div class="detail-item">
          <label>图片：</label>
          <div v-if="currentHelp.imageUrl" class="detail-image-container">
            <img
              :src="currentHelp.imageUrl"
              alt="求助图片"
              class="help-image"
              @click="previewImage(currentHelp.imageUrl)"
            />
          </div>
          <div v-else class="no-image-detail">
            <i class="el-icon-picture-outline"></i>
            <span>用户未上传图片</span>
          </div>
        </div>
        <div class="detail-item">
          <label>提交时间：</label>
          <span>{{ formatDate(currentHelp.createdAt) }}</span>
        </div>
        <div class="detail-item">
          <label>审核状态：</label>
          <el-tag :type="getAuditStatusType(currentHelp.status)">
            {{ getAuditStatusText(currentHelp.status) }}
          </el-tag>
        </div>
        <div class="detail-item">
          <label>回复状态：</label>
          <el-tag :type="getReplyStatusType(currentHelp)">
            {{ getReplyStatusText(currentHelp) }}
          </el-tag>
        </div>

        <!-- 回复列表 -->
        <div v-if="helpReplies.length > 0" class="replies-section">
          <label>专家回复：</label>
          <div class="replies-list">
            <div v-for="reply in helpReplies" :key="reply.id" class="reply-item">
              <div class="reply-header">
                <span class="reply-expert">专家回复</span>
                <span class="reply-time">{{ formatDate(reply.createdAt) }}</span>
              </div>
              <div class="reply-content-text">{{ reply.content }}</div>
            </div>
          </div>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentHelp && currentHelp.status === 'approved'"
          type="primary"
          @click="handleReply(currentHelp)"
        >
          {{ getReplyCount(currentHelp) > 0 ? '继续回复' : '回复' }}
        </el-button>
      </div>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog
      title="图片预览"
      :visible.sync="imagePreviewVisible"
      width="600px"
      :close-on-click-modal="true"
    >
      <div class="image-preview-container">
        <img :src="previewImageUrl" alt="预览图片" class="preview-image" />
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getExpertHelpRequests,
  createHelpReply,
  getHelpReplies
} from '@/api/expert'

export default {
  name: 'ExpertHelps',
  data() {
    return {
      searchQuery: '',
      selectedStatus: '',
      replyStatus: '',
      sortBy: 'created_at',
      loading: false,
      helpRequests: [],
      currentPage: 1,
      pageSize: 5,
      totalHelps: 0,

      // 回复相关
      replyDialogVisible: false,
      detailDialogVisible: false,
      currentHelp: null,
      helpReplies: [],
      submitting: false,
      imagePreviewVisible: false,
      previewImageUrl: '',

      replyForm: {
        content: ''
      },

      replyRules: {
        content: [
          { required: true, message: '请输入回复内容', trigger: 'blur' },
          { min: 10, message: '回复内容至少10个字符', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.initExpertInfo()
    this.fetchHelpRequests()
  },
  methods: {
    initExpertInfo() {
      // 优先显示真实登录的专家信息
      const userInfoStr = localStorage.getItem('userInfo')
      if (userInfoStr) {
        try {
          const userInfo = JSON.parse(userInfoStr)
          return
        } catch (e) {
          console.error('解析登录信息失败:', e)
        }
      }

      // 如果没有登录信息，检查测试用的专家信息
      const expertInfo = localStorage.getItem('expertInfo')
      if (!expertInfo) {
        // 如果没有专家信息，设置默认专家
        const defaultExpert = {
          id: 1,
          name: '张教授',
          title: '高级农艺师',
          specialization: '病虫害防治'
        }
        localStorage.setItem('expertInfo', JSON.stringify(defaultExpert))
      } else {
        const currentExpert = JSON.parse(expertInfo)
      }
    },

    async fetchHelpRequests() {
      try {
        this.loading = true
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize,
          search: this.searchQuery,
          auditStatus: this.selectedStatus,
          replyStatus: this.replyStatus,
          sortBy: this.sortBy
        }

        const response = await getExpertHelpRequests(params)
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

    handleReplyStatusChange() {
      this.currentPage = 1
      this.fetchHelpRequests()
    },

    handleSortChange() {
      this.currentPage = 1
      this.fetchHelpRequests()
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.fetchHelpRequests()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchHelpRequests()
    },
    async handleReply(help) {
      try {
        this.currentHelp = help
        this.replyForm = { content: '' }

        // 获取历史回复
        const response = await getHelpReplies(help.id)
        this.helpReplies = response.data.data || []

        this.replyDialogVisible = true
      } catch (error) {
        console.error('获取回复列表失败:', error)
        this.$message.error('获取回复列表失败')
      }
    },

    async viewDetail(help) {
      try {
        this.currentHelp = help

        // 获取历史回复
        const response = await getHelpReplies(help.id)
        this.helpReplies = response.data.data || []

        this.detailDialogVisible = true
      } catch (error) {
        console.error('获取详情失败:', error)
        this.$message.error('获取详情失败')
      }
    },
    async submitReply() {
      try {
        await this.$refs.replyForm.validate()

        this.submitting = true
        await createHelpReply(this.currentHelp.id, {
          content: this.replyForm.content
        })

        this.$message.success('回复发送成功')
        this.replyDialogVisible = false
        this.fetchHelpRequests()
      } catch (error) {
        if (error !== false) { // 不是表单验证错误
          console.error('发送回复失败:', error)
          this.$message.error('发送回复失败')
        }
      } finally {
        this.submitting = false
      }
    },

    // 状态相关方法
    getAuditStatusType(status) {
      const typeMap = {
        'pending': 'warning',
        'approved': 'success',
        'rejected': 'danger'
      }
      return typeMap[status] || 'info'
    },

    getAuditStatusText(status) {
      const textMap = {
        'pending': '待审核',
        'approved': '已审核',
        'rejected': '已拒绝'
      }
      return textMap[status] || '未知'
    },

    getReplyStatusType(help) {
      const replyCount = this.getReplyCount(help)
      return replyCount > 0 ? 'success' : 'warning'
    },

    getReplyStatusText(help) {
      const replyCount = this.getReplyCount(help)
      return replyCount > 0 ? '已回复' : '待回复'
    },

    getReplyCount(help) {
      return help.replyCount || 0
    },

    formatDate(date) {
      if (!date) return '未知'
      return new Date(date).toLocaleString('zh-CN')
    },

    previewImage(imageUrl) {
      this.previewImageUrl = imageUrl
      this.imagePreviewVisible = true
    }
    }
  }
</script>

<style scoped>
.helps-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.page-header {
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: #fff;
  padding: 24px;
  text-align: center;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.page-title i {
  font-size: 28px;
}

.page-description {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.filter-section {
  padding: 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.help-list {
  padding: 20px;
}

.help-title {
  font-weight: 600;
  color: #2c3e50;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 160px;
}

.help-description {
  color: #606266;
  line-height: 1.4;
  max-height: 60px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  word-break: break-word;
}

.pagination-section {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}

/* 回复对话框样式 */
.reply-content {
  max-height: 600px;
  overflow-y: auto;
}

.help-info {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.help-info h3 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 18px;
}

.help-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
}

.help-description-full {
  line-height: 1.6;
  color: #2c3e50;
  margin-bottom: 12px;
}

.help-image-container {
  text-align: center;
}

.help-image {
  max-width: 300px;
  max-height: 200px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  cursor: pointer;
  transition: all 0.3s ease;
}

.help-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.detail-image-container {
  display: inline-block;
}

.no-image-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-size: 14px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px dashed #dcdfe6;
}

.no-image-detail i {
  font-size: 18px;
}

/* 表格中的图片样式 */
.image-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 60px;
  width: 100%;
}

.help-thumbnail {
  max-width: 80px;
  max-height: 50px;
  border-radius: 4px;
  border: 1px solid #e9ecef;
  cursor: pointer;
  transition: all 0.3s ease;
}

.help-thumbnail:hover {
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 12px;
  width: 100%;
  text-align: center;
}

.no-image i {
  font-size: 24px;
  margin-bottom: 4px;
}

/* 图片预览样式 */
.image-preview-container {
  text-align: center;
  padding: 20px;
}

.preview-image {
  max-width: 100%;
  max-height: 500px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.reply-history {
  margin-bottom: 20px;
}

.reply-history h4 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 16px;
}

.reply-item {
  background: #fff;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-expert {
  font-weight: 600;
  color: #3498db;
  font-size: 14px;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-content-text {
  line-height: 1.6;
  color: #2c3e50;
}

.new-reply h4 {
  margin: 0 0 12px 0;
  color: #2c3e50;
  font-size: 16px;
}

/* 详情对话框样式 */
.help-detail {
  padding: 20px 0;
}

.detail-item {
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
}

.detail-item label {
  font-weight: 600;
  color: #2c3e50;
  width: 100px;
  flex-shrink: 0;
  margin-right: 12px;
}

.description-content {
  flex: 1;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
}

.replies-section {
  margin-top: 20px;
}

.replies-list {
  margin-top: 12px;
}

.dialog-footer {
  text-align: right;
}

/* 表格样式优化 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background: #f8f9fa;
  color: #2c3e50;
  font-weight: 600;
}

.el-table td {
  border-bottom: 1px solid #f0f0f0;
}

.el-table tr:hover td {
  background: #f8f9fa;
}

/* 按钮样式 */
.el-button--primary {
  background: linear-gradient(135deg, #3498db, #2980b9);
  border: none;
}

.el-button--info {
  background: linear-gradient(135deg, #95a5a6, #7f8c8d);
  border: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-section .el-col {
    margin-bottom: 12px;
  }

  .help-title {
    max-width: 120px;
  }
}
</style>