<template>
  <div class="experts-container">
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="el-icon-user"></i>
            专家用户管理
          </h1>
          <p class="page-description">管理系统中的专家用户账户</p>
        </div>
      </div>
    </div>

    <!-- 搜索 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索专家姓名、用户名或邮箱"
            @keyup.enter.native="handleSearch"
            clearable
          >
            <el-button slot="append" @click="handleSearch" icon="el-icon-search">搜索</el-button>
          </el-input>
        </el-col>
      </el-row>
    </div>

    <!-- 专家列表 -->
    <div class="table-section">
      <el-table
        :data="experts"
        v-loading="loading"
        element-loading-text="加载专家数据中..."
        stripe
        border
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="60" align="center"></el-table-column>

        <el-table-column label="头像" width="80" align="center">
          <template slot-scope="scope">
            <div class="avatar-container">
              <img v-if="scope.row.avatarUrl" :src="scope.row.avatarUrl" alt="头像" class="expert-avatar" />
              <i v-else class="el-icon-user-solid default-avatar"></i>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="realName" label="真实姓名" width="120" show-overflow-tooltip></el-table-column>

        <el-table-column prop="username" label="用户名" width="120" show-overflow-tooltip></el-table-column>

        <el-table-column prop="email" label="邮箱" width="200" show-overflow-tooltip></el-table-column>

        <el-table-column prop="phone" label="手机号" width="160" show-overflow-tooltip></el-table-column>

        <el-table-column prop="specialty" label="专业领域" width="120" show-overflow-tooltip></el-table-column>

        <el-table-column prop="title" label="职称" width="120" show-overflow-tooltip></el-table-column>

        <el-table-column prop="institution" label="所属机构" width="150" show-overflow-tooltip></el-table-column>

        <el-table-column label="状态" width="100" align="center">
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

        <el-table-column label="操作" width="260" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="viewExpert(scope.row)" icon="el-icon-view">
              查看
            </el-button>
            <el-button size="mini" type="warning" @click="resetPassword(scope.row)" icon="el-icon-key">
              重置密码
            </el-button>
            <el-button size="mini" type="danger" @click="deleteExpert(scope.row)" icon="el-icon-delete">
              删除
            </el-button>
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
import { getExperts, deleteExpert as deleteExpertAPI, resetExpertPassword } from '@/api/admin'

export default {
  name: 'AdminExperts',
  data() {
    return {
      loading: false,
      experts: [],
      total: 0,
      currentPage: 1,
      pageSize: 5,
      searchForm: {
        keyword: ''
      }
    }
  },

  mounted() {
    this.fetchExperts()
  },

  methods: {
    async fetchExperts() {
      try {
        this.loading = true
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize,
          ...this.searchForm
        }

        const response = await getExperts(params)

        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          this.experts = response.data.data.list || []
          this.total = response.data.data.total || 0
        } else {
          console.error('获取专家列表失败:', response.data)
          this.$message.error((response.data && response.data.msg) || '获取专家列表失败')
        }
      } catch (error) {
        console.error('获取专家列表异常:', error)
        let errorMsg = '获取专家列表失败'
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
      this.fetchExperts()
    },



    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.fetchExperts()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchExperts()
    },

    viewExpert(expert) {
      this.$alert(`
        <div style="text-align: left;">
          <h3>${expert.realName || expert.username}</h3>
          <p><strong>用户名：</strong>${expert.username}</p>
          <p><strong>邮箱：</strong>${expert.email || '未设置'}</p>
          <p><strong>手机：</strong>${expert.phone || '未设置'}</p>
          <p><strong>专业领域：</strong>${expert.specialty || '未设置'}</p>
          <p><strong>职称：</strong>${expert.title || '未设置'}</p>
          <p><strong>所属机构：</strong>${expert.institution || '未设置'}</p>
          <p><strong>个人简介：</strong>${expert.bio || '未设置'}</p>
          <p><strong>注册时间：</strong>${this.formatDate(expert.createdAt)}</p>
          <p><strong>状态：</strong>${expert.status === 'active' ? '正常' : '禁用'}</p>
        </div>
      `, '专家详情', {
        dangerouslyUseHTMLString: true,
        customClass: 'expert-detail-dialog'
      })
    },



    async resetPassword(expert) {
      try {
        await this.$confirm(`确认重置专家 "${expert.realName || expert.username}" 的密码吗？\n重置后密码将变为：123456`, '重置密码确认', {
          confirmButtonText: '确认重置',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const response = await resetExpertPassword(expert.id)

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

    async deleteExpert(expert) {
      try {
        await this.$confirm(`确认删除专家 "${expert.realName || expert.username}" 吗？\n此操作不可恢复！`, '删除确认', {
          confirmButtonText: '确认删除',
          cancelButtonText: '取消',
          type: 'error'
        })

        const response = await deleteExpertAPI(expert.id)

        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          this.$message.success('专家删除成功')
          this.fetchExperts() // 刷新列表
        } else {
          this.$message.error((response.data && response.data.msg) || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除专家失败:', error)
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
.experts-container {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 24px;
}

.header-content {
  display: flex;
  align-items: center;
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

/* 表格区域 */
.table-section {
  padding: 24px;
}

.avatar-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.expert-avatar {
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
  margin: 2px 3px;
  padding: 5px 10px;
  font-size: 12px;
}

.el-table .el-button--mini {
  padding: 5px 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
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
}

/* 专家详情对话框样式 */
.expert-detail-dialog .el-message-box {
  width: 500px;
}

.expert-detail-dialog .el-message-box__content {
  text-align: left;
}

.expert-detail-dialog h3 {
  color: #667eea;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #f0f2f5;
}

.expert-detail-dialog p {
  margin: 8px 0;
  line-height: 1.6;
}

.expert-detail-dialog strong {
  color: #495057;
  font-weight: 600;
}
</style>
