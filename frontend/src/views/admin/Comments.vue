<template>
  <div class="comments-container">
    <div class="page-header">
      <h1 class="page-title">
        <i class="el-icon-s-comment"></i>
        评论审核管理
      </h1>
      <p class="page-description">审核用户提交的评论，确保内容合规</p>
    </div>

    <div class="content">
      <!-- 搜索和筛选区域 -->
      <div class="filter-section">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-input
              v-model="searchForm.search"
              placeholder="搜索评论内容"
              prefix-icon="el-icon-search"
              clearable
              @keyup.enter.native="handleSearch"
            />
          </el-col>
          <el-col :span="4">
            <el-select v-model="searchForm.status" placeholder="审核状态" clearable>
              <el-option label="待审核" value="pending" />
              <el-option label="已通过" value="approved" />
              <el-option label="已拒绝" value="rejected" />
            </el-select>
          </el-col>
          <el-col :span="4">
            <el-select v-model="searchForm.targetType" placeholder="评论类型" clearable>
              <el-option label="植物评论" value="plant" />
              <el-option label="病虫害评论" value="pest_disease" />
              <el-option label="农药评论" value="pesticide" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" @click="handleSearch" icon="el-icon-search">
              搜索
            </el-button>
            <el-button @click="handleReset" icon="el-icon-refresh">
              重置
            </el-button>
          </el-col>
          <el-col :span="4" class="batch-actions">
            <el-button
              type="success"
              size="small"
              :disabled="selectedComments.length === 0"
              @click="handleBatchApprove"
              icon="el-icon-check"
            >
              批量通过
            </el-button>
            <el-button
              type="danger"
              size="small"
              :disabled="selectedComments.length === 0"
              @click="handleBatchReject"
              icon="el-icon-close"
            >
              批量拒绝
            </el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 评论列表 -->
      <div class="table-section">
        <el-table
          v-loading="loading"
          :data="comments"
          style="width: 100%"
          @selection-change="handleSelectionChange"
          row-key="id"
        >
          <el-table-column type="selection" width="55" />

          <el-table-column prop="id" label="ID" width="80" />

          <el-table-column label="用户信息" width="150">
            <template slot-scope="scope">
              <div class="user-info">
                <el-avatar :size="32" :src="scope.row.avatarUrl" icon="el-icon-user-solid" />
                <div class="user-details">
                  <div class="username">{{ scope.row.nickname || scope.row.username }}</div>
                  <el-tag v-if="scope.row.userType === 'expert'" type="success" size="mini">
                    专家
                  </el-tag>
                  <el-tag v-else type="info" size="mini">
                    用户
                  </el-tag>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="评论内容" min-width="200">
            <template slot-scope="scope">
              <div class="comment-content">
                {{ scope.row.content }}
              </div>
            </template>
          </el-table-column>

          <el-table-column label="评论目标" width="120">
            <template slot-scope="scope">
              <el-tag :type="getTargetTypeColor(scope.row.targetType)" size="small">
                {{ getTargetTypeName(scope.row.targetType) }}
              </el-tag>
              <div class="target-name">{{ scope.row.targetName }}</div>
            </template>
          </el-table-column>

          <el-table-column label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="提交时间" width="160">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200" fixed="right">
            <template slot-scope="scope">
              <el-button
                v-if="scope.row.status === 'pending'"
                type="success"
                size="mini"
                @click="handleApprove(scope.row)"
                icon="el-icon-check"
              >
                通过
              </el-button>
              <el-button
                v-if="scope.row.status === 'pending'"
                type="danger"
                size="mini"
                @click="handleReject(scope.row)"
                icon="el-icon-close"
              >
                拒绝
              </el-button>
              <el-button
                v-if="scope.row.status !== 'pending'"
                type="info"
                size="mini"
                disabled
              >
                已审核
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
          :current-page="pagination.page"
          :page-sizes="[3,5,10, 20]"
          :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
        />
      </div>
    </div>
  </div>
</template>

<script>
import {
  getComments,
  approveComment,
  rejectComment,
  batchApproveComments,
  batchRejectComments
} from '@/api/admin'

export default {
  name: 'AdminComments',
  data() {
    return {
      loading: false,
      comments: [],
      selectedComments: [],
      searchForm: {
        search: '',
        status: '',
        targetType: ''
      },
      pagination: {
        page: 1,
        pageSize: 5,
        total: 0
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
          page: this.pagination.page,
          pageSize: this.pagination.pageSize,
          ...this.searchForm
        }

        const response = await getComments(params)

        this.comments = response.data.data.list || []
        this.pagination.total = response.data.data.total || 0
      } catch (error) {
        this.$message.error('获取评论列表失败')
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.pagination.page = 1
      this.fetchComments()
    },

    handleReset() {
      this.searchForm = {
        search: '',
        status: '',
        targetType: ''
      }
      this.pagination.page = 1
      this.fetchComments()
    },

    handleSelectionChange(selection) {
      this.selectedComments = selection
    },

    async handleApprove(comment) {
      try {
        await approveComment(comment.id)
        this.$message.success('评论审核通过')
        this.fetchComments()
      } catch (error) {
        console.error('审核通过失败:', error)
        this.$message.error('审核通过失败')
      }
    },

    async handleReject(comment) {
      try {
        await rejectComment(comment.id)
        this.$message.success('评论审核拒绝')
        this.fetchComments()
      } catch (error) {
        console.error('审核拒绝失败:', error)
        this.$message.error('审核拒绝失败')
      }
    },

    async handleBatchApprove() {
      if (this.selectedComments.length === 0) {
        this.$message.warning('请选择要审核的评论')
        return
      }

      try {
        const ids = this.selectedComments.map(comment => comment.id)
        await batchApproveComments(ids)
        this.$message.success(`批量审核通过 ${ids.length} 条评论`)
        this.fetchComments()
      } catch (error) {
        console.error('批量审核通过失败:', error)
        this.$message.error('批量审核通过失败')
      }
    },

    async handleBatchReject() {
      if (this.selectedComments.length === 0) {
        this.$message.warning('请选择要审核的评论')
        return
      }

      try {
        const ids = this.selectedComments.map(comment => comment.id)
        await batchRejectComments(ids)
        this.$message.success(`批量审核拒绝 ${ids.length} 条评论`)
        this.fetchComments()
      } catch (error) {
        console.error('批量审核拒绝失败:', error)
        this.$message.error('批量审核拒绝失败')
      }
    },

    handleSizeChange(newSize) {
      this.pagination.pageSize = newSize
      this.pagination.page = 1
      this.fetchComments()
    },

    handleCurrentChange(newPage) {
      this.pagination.page = newPage
      this.fetchComments()
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

    getTargetTypeColor(targetType) {
      const colorMap = {
        'plant': 'success',
        'pest_disease': 'warning',
        'pesticide': 'danger'
      }
      return colorMap[targetType] || 'info'
    },

    getTargetTypeName(targetType) {
      const nameMap = {
        'plant': '植物',
        'pest_disease': '病虫害',
        'pesticide': '农药'
      }
      return nameMap[targetType] || '未知'
    },

    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.comments-container {
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

.content {
  padding: 24px;
}

.filter-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.batch-actions {
  text-align: right;
}

.table-section {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-size: 13px;
  font-weight: 500;
  color: #2c3e50;
}

.comment-content {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
  color: #606266;
}

.target-name {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 表格样式优化 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background-color: #f8f9fa;
  color: #2c3e50;
  font-weight: 600;
}

.el-table td {
  padding: 12px 0;
}

/* 按钮样式 */
.el-button--mini {
  padding: 5px 8px;
  font-size: 12px;
}

/* 标签样式 */
.el-tag--mini {
  height: 20px;
  line-height: 18px;
  font-size: 11px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content {
    padding: 16px;
  }

  .filter-section {
    padding: 16px;
  }

  .batch-actions {
    text-align: left;
    margin-top: 12px;
  }
}
</style>
