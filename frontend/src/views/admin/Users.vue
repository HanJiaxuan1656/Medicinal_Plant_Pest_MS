<template>
  <div class="users-container">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="el-icon-user-solid"></i>
            普通用户管理
          </h1>
          <p class="page-description">管理系统中的普通用户账户</p>
        </div>
      </div>
    </div>

    <!-- 搜索 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索用户姓名、用户名或邮箱"
            @keyup.enter.native="handleSearch"
            clearable
          >
            <el-button slot="append" @click="handleSearch" icon="el-icon-search">搜索</el-button>
          </el-input>
        </el-col>
      </el-row>
    </div>

    <!-- 用户列表 -->
    <div class="table-section">
      <el-table
        :data="users"
        v-loading="loading"
        element-loading-text="加载用户数据中..."
        stripe
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>

        <el-table-column label="头像" width="120" align="center">
          <template slot-scope="scope">
            <div class="avatar-container">
              <img v-if="scope.row.avatarUrl" :src="scope.row.avatarUrl" alt="头像" class="user-avatar" />
              <i v-else class="el-icon-user-solid default-avatar"></i>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="realName" label="真实姓名" width="180" show-overflow-tooltip></el-table-column>

        <el-table-column prop="username" label="用户名" width="180" show-overflow-tooltip></el-table-column>

        <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip></el-table-column>

        <el-table-column prop="phone" label="手机号" width="180" show-overflow-tooltip></el-table-column>

        <el-table-column label="统计信息" width="220" align="center">
          <template slot-scope="scope">
            <div class="stats-info">
              <div class="stat-row">
                <span class="stat-item">
                  <span class="stat-label">评论:</span>
                  <span class="stat-value">{{ scope.row.commentCount || 0 }}</span>
                </span>
                <span class="stat-divider">|</span>
                <span class="stat-item">
                  <span class="stat-label">求助:</span>
                  <span class="stat-value">{{ scope.row.helpRequestCount || 0 }}</span>
                </span>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 'active' ? 'success' : 'danger'" size="small">
              {{ scope.row.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createdAt" label="注册时间" width="180" show-overflow-tooltip>
          <template slot-scope="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template slot-scope="scope">
            <div class="operation-buttons">
              <el-button size="mini" type="primary" @click="viewUser(scope.row)" icon="el-icon-view">
                查看
              </el-button>
              <el-button size="mini" type="warning" @click="resetPassword(scope.row)" icon="el-icon-key">
                重置密码
              </el-button>
              <el-button size="mini" type="danger" @click="deleteUser(scope.row)" icon="el-icon-delete">
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[3,5,10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import { getUsers, deleteUser as deleteUserAPI, resetUserPassword } from '@/api/admin'

export default {
  name: 'AdminUsers',
  data() {
    return {
      loading: false,
      users: [],
      total: 0,
      currentPage: 1,
      pageSize: 5,
      searchForm: {
        keyword: ''
      }
    }
  },

  mounted() {
    this.fetchUsers()
  },

  methods: {
    async fetchUsers() {
      try {
        this.loading = true
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize,
          ...this.searchForm
        }

        // 处理日期范围
        if (this.searchForm.dateRange && this.searchForm.dateRange.length === 2) {
          params.startDate = this.searchForm.dateRange[0]
          params.endDate = this.searchForm.dateRange[1]
          delete params.dateRange
        }

        const response = await getUsers(params)

        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          this.users = response.data.data.list || []
          this.total = response.data.data.total || 0
        } else {
          this.$message.error((response.data && response.data.msg) || '获取用户列表失败')
        }
      } catch (error) {
        let errorMsg = '获取用户列表失败'
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

    handleSearch() {
      this.currentPage = 1
      this.fetchUsers()
    },



    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.fetchUsers()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchUsers()
    },

    viewUser(user) {
      this.$alert(`
        <div style="text-align: left;">
          <h3>${user.realName || user.username}</h3>
          <p><strong>用户名：</strong>${user.username}</p>
          <p><strong>邮箱：</strong>${user.email || '未设置'}</p>
          <p><strong>手机：</strong>${user.phone || '未设置'}</p>
          <p><strong>评论数量：</strong>${user.commentCount || 0} 条</p>
          <p><strong>求助数量：</strong>${user.helpRequestCount || 0} 条</p>
          <p><strong>注册时间：</strong>${this.formatDate(user.createdAt)}</p>
          <p><strong>最后登录：</strong>${this.formatDate(user.lastLoginAt) || '未知'}</p>
          <p><strong>状态：</strong>${user.status === 'active' ? '正常' : '禁用'}</p>
        </div>
      `, '用户详情', {
        dangerouslyUseHTMLString: true,
        customClass: 'user-detail-dialog'
      })
    },

    async resetPassword(user) {
      try {
        await this.$confirm(`确认重置用户 "${user.realName || user.username}" 的密码吗？\n重置后密码将变为：123456`, '重置密码确认', {
          confirmButtonText: '确认重置',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await resetUserPassword(user.id)

        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          this.$message.success('密码重置成功！新密码为：123456')
        } else {
          this.$message.error((response.data && response.data.msg) || '密码重置失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('重置密码失败:', error)
          this.$message.error('密码重置失败')
        }
      }
    },

    async deleteUser(user) {
      try {
        await this.$confirm(`确认删除用户 "${user.realName || user.username}" 吗？\n此操作不可恢复！`, '删除确认', {
          confirmButtonText: '确认删除',
          cancelButtonText: '取消',
          type: 'error'
        })

        const response = await deleteUserAPI(user.id)

        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          this.$message.success('用户删除成功')
          this.fetchUsers() // 刷新列表
        } else {
          this.$message.error((response.data && response.data.msg) || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除用户失败:', error)
          this.$message.error('删除失败')
        }
      }
    },

    formatDate(dateTime) {
      if (!dateTime) return ''
      const date = new Date(dateTime)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.users-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
  padding: 24px;
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
  font-size: 1.8rem;
  font-weight: 700;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title i {
  font-size: 1.6rem;
  color: #ffd700;
}

.page-description {
  margin: 0;
  opacity: 0.9;
  font-size: 1rem;
}

.header-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.header-actions .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
  font-weight: 500;
}

.header-actions .el-button--primary {
  background: rgba(255, 255, 255, 0.9);
  color: #4facfe;
}

.header-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.header-actions .el-button--primary:hover {
  background: #fff;
  color: #3d8bfe;
}

/* 搜索区域 */
.search-section {
  padding: 24px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.search-section .el-input {
  width: 100%;
}

.search-section .el-select {
  width: 100%;
}

.search-section .el-date-editor {
  width: 100%;
}

/* 表格区域 */
.table-section {
  padding: 24px;
}

.avatar-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e9ecef;
}

.default-avatar {
  font-size: 24px;
  color: #c0c4cc;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 50%;
  border: 2px solid #e9ecef;
}

.stats-info {
  display: flex;
  justify-content: center;
  align-items: center;
}

.stat-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.85rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-label {
  color: #909399;
  font-weight: 500;
  font-size: 0.8rem;
}

.stat-value {
  color: #2c3e50;
  font-weight: 600;
  background: #f0f9ff;
  padding: 2px 6px;
  border-radius: 4px;
  min-width: 20px;
  text-align: center;
  font-size: 0.8rem;
}

.stat-divider {
  color: #dcdfe6;
  font-weight: 300;
  margin: 0 4px;
}

/* 分页区域 */
.pagination-section {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

/* 表格样式优化 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background: #f8f9fa;
  color: #495057;
  font-weight: 600;
}

.el-table td {
  padding: 12px 0;
}

.el-table .el-button {
  margin: 0 2px;
}

/* 操作按钮样式 */
.operation-buttons {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  flex-wrap: nowrap;
}

.operation-buttons .el-button {
  margin: 0;
  padding: 5px 8px;
  font-size: 12px;
  white-space: nowrap;
}

.operation-buttons .el-button--mini {
  padding: 4px 6px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .search-section .el-row {
    flex-direction: column;
  }

  .search-section .el-col {
    width: 100%;
    margin-bottom: 12px;
  }

  .table-section {
    padding: 16px;
    overflow-x: auto;
  }

  .stat-row {
    flex-direction: column;
    gap: 4px;
  }

  .stat-divider {
    display: none;
  }

  .operation-buttons {
    flex-direction: column;
    gap: 4px;
  }

  .operation-buttons .el-button {
    width: 100%;
    font-size: 11px;
  }
}

/* 用户详情对话框样式 */
.user-detail-dialog .el-message-box {
  width: 500px;
}

.user-detail-dialog .el-message-box__content {
  text-align: left;
}

.user-detail-dialog h3 {
  color: #4facfe;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #f0f2f5;
}

.user-detail-dialog p {
  margin: 8px 0;
  line-height: 1.6;
}

.user-detail-dialog strong {
  color: #495057;
  font-weight: 600;
}
</style>
