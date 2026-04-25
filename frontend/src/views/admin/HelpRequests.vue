<template>
  <div class="help-requests-container">
    <div class="page-header">
      <h1 class="page-title">
        <i class="el-icon-question"></i>
        求助审核管理
      </h1>
      <p class="page-description">审核用户提交的求助请求，确保内容合规</p>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input v-model="searchQuery" placeholder="搜索求助标题或描述..." prefix-icon="el-icon-search" size="large"
            @input="handleSearch" clearable></el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="selectedStatus" placeholder="审核状态" size="large" @change="handleStatusChange" clearable>
            <el-option label="全部状态" value=""></el-option>
            <el-option label="待审核" value="pending"></el-option>
            <el-option label="已通过" value="approved"></el-option>
            <el-option label="已拒绝" value="rejected"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="sortBy" placeholder="排序方式" size="large" @change="handleSortChange">
            <el-option label="最新提交" value="created_at"></el-option>
            <el-option label="最早提交" value="created_at_asc"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" size="large" @click="fetchHelpRequests" style="width: 100%">
            <i class="el-icon-refresh"></i>
            刷新
          </el-button>
        </el-col>
        <el-col :span="6" style="text-align: right;">
          <div class="batch-actions-top" v-if="selectedHelps.length > 0">
            <el-button type="success" size="large" @click="batchApprove">
              <i class="el-icon-check"></i>
              批量通过 ({{ selectedHelps.length }})
            </el-button>
            <el-button type="danger" size="large" @click="batchReject" style="margin-left: 8px;">
              <i class="el-icon-close"></i>
              批量拒绝 ({{ selectedHelps.length }})
            </el-button>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 求助列表 -->
    <div class="help-list">
      <el-table :data="helpRequests" v-loading="loading" stripe style="width: 100%"
        @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"></el-table-column>

        <el-table-column prop="id" label="ID" width="80"></el-table-column>

        <el-table-column prop="title" label="求助标题" width="150">
          <template slot-scope="scope">
            <div class="help-title">{{ scope.row.title }}</div>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="描述" min-width="250">
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
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="提交时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" fixed="right">
          <template slot-scope="scope">
            <el-button v-if="scope.row.status === 'pending'" type="success" size="mini" @click="approveHelp(scope.row)">
              通过
            </el-button>
            <el-button v-if="scope.row.status === 'pending'" type="danger" size="mini" @click="rejectHelp(scope.row)">
              拒绝
            </el-button>
            <el-button type="primary" size="mini" @click="viewDetail(scope.row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
        :page-sizes="[3,5,10, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper"
        :total="totalHelps"></el-pagination>
    </div>



    <!-- 详情对话框 -->
    <el-dialog title="求助详情" :visible.sync="detailDialogVisible" width="800px" :close-on-click-modal="false">
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
          <label>当前状态：</label>
          <el-tag :type="getStatusType(currentHelp.status)">
            {{ getStatusText(currentHelp.status) }}
          </el-tag>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button v-if="currentHelp && currentHelp.status === 'pending'" type="success"
          @click="approveHelp(currentHelp)">
          通过审核
        </el-button>
        <el-button v-if="currentHelp && currentHelp.status === 'pending'" type="danger" @click="rejectHelp(currentHelp)">
          拒绝审核
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
  getAdminHelpRequests,
  approveHelpRequest,
  rejectHelpRequest,
  batchApproveHelpRequests,
  batchRejectHelpRequests
} from '@/api/admin'

export default {
  name: 'AdminHelpRequests',
  data() {
    return {
      searchQuery: '',
      selectedStatus: '',
      sortBy: 'created_at',
      helpRequests: [],
      loading: false,
      totalHelps: 0,
      pageSize: 5,
      currentPage: 1,
      selectedHelps: [],
      detailDialogVisible: false,
      currentHelp: null,
      imagePreviewVisible: false,
      previewImageUrl: ''
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
          auditStatus: this.selectedStatus,
          sortBy: this.sortBy
        }

        const response = await getAdminHelpRequests(params)
        this.helpRequests = response.data.data.list || []
        this.totalHelps = response.data.data.total || 0
      } catch (error) {
        this.$message.error('获取求助列表失败: ' + (error.message || error))
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

    handleSelectionChange(selection) {
      this.selectedHelps = selection
    },

    async approveHelp(help) {
      try {
        await this.$confirm('确认通过这个求助审核?', '确认操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        })

        await approveHelpRequest(help.id)
        this.$message.success('审核通过成功')
        this.fetchHelpRequests()
        this.detailDialogVisible = false
      } catch (error) {
        if (error !== 'cancel') {
          console.error('审核通过失败:', error)
          this.$message.error('操作失败')
        }
      }
    },

    async rejectHelp(help) {
      try {
        await this.$confirm('确认拒绝这个求助审核?', '确认操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        await rejectHelpRequest(help.id)
        this.$message.success('审核拒绝成功')
        this.fetchHelpRequests()
        this.detailDialogVisible = false
      } catch (error) {
        if (error !== 'cancel') {
          console.error('审核拒绝失败:', error)
          this.$message.error('操作失败')
        }
      }
    },

    async batchApprove() {
      try {
        await this.$confirm(`确认批量通过 ${this.selectedHelps.length} 个求助?`, '批量操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'success'
        })

        const ids = this.selectedHelps.map(help => help.id)
        await batchApproveHelpRequests(ids)
        this.$message.success('批量审核通过成功')
        this.fetchHelpRequests()
        this.selectedHelps = []
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量审核通过失败:', error)
          this.$message.error('批量操作失败')
        }
      }
    },

    async batchReject() {
      try {
        await this.$confirm(`确认批量拒绝 ${this.selectedHelps.length} 个求助?`, '批量操作', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const ids = this.selectedHelps.map(help => help.id)
        await batchRejectHelpRequests(ids)
        this.$message.success('批量审核拒绝成功')
        this.fetchHelpRequests()
        this.selectedHelps = []
      } catch (error) {
        if (error !== 'cancel') {
          console.error('批量审核拒绝失败:', error)
          this.$message.error('批量操作失败')
        }
      }
    },

    viewDetail(help) {
      this.currentHelp = help
      this.detailDialogVisible = true
    },

    previewImage(imageUrl) {
      this.previewImageUrl = imageUrl
      this.imagePreviewVisible = true
    },

    getStatusType(status) {
      const typeMap = {
        'pending': 'warning',
        'approved': 'success',
        'rejected': 'danger'
      }
      return typeMap[status] || 'info'
    },

    getStatusText(status) {
      const textMap = {
        'pending': '待审核',
        'approved': '已通过',
        'rejected': '已拒绝'
      }
      return textMap[status] || '未知'
    },

    formatDate(date) {
      if (!date) return '未知'
      return new Date(date).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.help-requests-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.page-header {
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
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

.batch-actions-top {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;
}

.batch-actions-top .el-button {
  margin: 0;
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
  max-width: 130px;
}

.help-description {
  color: #606266;
  line-height: 1.4;
  max-height: 80px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  word-break: break-word;
}

.pagination-section {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}



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
  width: 80px;
  flex-shrink: 0;
  margin-right: 12px;
}

.description-content {
  flex: 1;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
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
.el-button--success {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  border: none;
}

.el-button--danger {
  background: linear-gradient(135deg, #e74c3c, #c0392b);
  border: none;
}

.el-button--primary {
  background: linear-gradient(135deg, #3498db, #2980b9);
  border: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-section .el-col {
    margin-bottom: 12px;
  }

  .batch-actions-top {
    flex-direction: column;
    gap: 8px;
  }

  .batch-actions-top .el-button {
    width: 100%;
  }
}
</style>
