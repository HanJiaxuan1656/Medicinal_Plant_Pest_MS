<template>
  <div class="pesticide-container">
    <!-- 搜索和过滤区域 -->
    <div class="search-container">
      <div class="left-section">
        <el-input v-model="searchQuery" placeholder="请输入农药名称" style="width: 200px; margin-right: 10px" />
        <el-select v-model="filterCategory" placeholder="选择农药类别" style="width: 200px; margin-right: 10px" clearable>
          <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
      <div class="right-section">
        <el-button
          type="danger"
          icon="el-icon-delete"
          :disabled="selectedPesticides.length === 0"
          @click="handleBatchDelete"
        >
          批量删除 ({{ selectedPesticides.length }})
        </el-button>
        <el-button type="primary" plain icon="el-icon-plus" @click="handleAddPesticide">添加农药</el-button>
      </div>
    </div>

    <!-- 表格区域 -->
    <el-table v-loading="loading" :data="tableData" border style="width: 100%; margin-top: 20px" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" label="序号" width="80" align="center" />
      <el-table-column prop="name" label="农药名称" min-width="120" />
      <el-table-column prop="category" label="农药类别" min-width="120" />
      <el-table-column prop="activeIngredient" label="有效成分" min-width="150" />
      <el-table-column label="使用说明" min-width="200">
        <template slot-scope="scope">
          <div class="usage-instructions" :title="scope.row.usageInstructions">
            {{ scope.row.usageInstructions || '-' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="图片" width="100" align="center">
        <template slot-scope="scope">
          <div class="image-container">
            <el-image
              v-if="scope.row.imageUrl"
              class="table-image"
              :src="scope.row.imageUrl"
              :preview-src-list="[scope.row.imageUrl]"
              fit="cover"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
            <div v-else class="no-image">
              <i class="el-icon-picture-outline"></i>
              <span>暂无图片</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" width="120" align="center">
        <template slot-scope="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">
            编辑
          </el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination :current-page.sync="currentPage" :page-sizes="[3, 5, 10, 20]" :page-size.sync="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="50%" @close="$refs.pesticideForm.resetFields()">
      <el-form ref="pesticideForm" :model="pesticideForm" :rules="rules" label-width="100px">
        <el-form-item label="农药名称" prop="name">
          <el-input v-model="pesticideForm.name" />
        </el-form-item>
        <el-form-item label="农药类别" prop="category">
          <el-select v-model="pesticideForm.category" placeholder="请选择农药类别" style="width: 100%">
            <el-option v-for="item in categories" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="有效成分" prop="activeIngredient">
          <el-input v-model="pesticideForm.activeIngredient" />
        </el-form-item>
        <el-form-item label="使用说明" prop="usageInstructions">
          <el-input v-model="pesticideForm.usageInstructions" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload class="avatar-uploader" :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false" :on-success="handleUploadSuccess" :before-upload="beforeUpload">
            <img v-if="pesticideForm.imageUrl" :src="pesticideForm.imageUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
          <div class="upload-tip">建议上传比例1:1的图片，大小不超过2MB</div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'Pesticides',
  data() {
    return {
      searchQuery: '',
      filterCategory: '',
      loading: false,
      tableData: [],
      selectedPesticides: [], // 选中的农药列表
      currentPage: 1,
      pageSize: 5,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      isEdit: false,
      categories: [
        { value: '杀虫剂', label: '杀虫剂' },
        { value: '杀菌剂', label: '杀菌剂' },
        { value: '除草剂', label: '除草剂' },
        { value: '植物生长调节剂', label: '植物生长调节剂' }
      ],
      pesticideForm: {
        id: null,
        name: '',
        category: '',
        activeIngredient: '',
        usageInstructions: '',
        imageUrl: '',
        createdAt: null,
        updatedAt: null,
        createdBy: null
      },
      rules: {
        name: [
          { required: true, message: '请输入农药名称', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        category: [{ required: true, message: '请选择农药类别', trigger: 'change' }],
        activeIngredient: [{ max: 255, message: '长度不能超过 255 个字符', trigger: 'blur' }],
        usageInstructions: [{ required: true, message: '请输入使用说明', trigger: 'blur' }]
      },
      uploadUrl: '/api/files/upload',
      uploadHeaders: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      }
    }
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      try {
        this.loading = true
        const response = await this.$axios.get('/pesticides', {
          params: {
            search: this.searchQuery,
            category: this.filterCategory,
            page: this.currentPage,
            pageSize: this.pageSize
          }
        })

        if (response.data.code === 1) {
          this.tableData = response.data.data.list
          this.total = response.data.data.total
        } else {
          this.$message.error(response.data.msg || '获取数据失败')
        }
      } catch (error) {
        this.$message.error('获取农药数据失败')
        console.error('获取农药数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    handleAddPesticide() {
      this.dialogTitle = '添加农药'
      this.isEdit = false
      this.pesticideForm = {
        id: null,
        name: '',
        category: '',
        activeIngredient: '',
        usageInstructions: '',
        imageUrl: '',
        createdAt: null,
        updatedAt: null,
        createdBy: null
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑农药'
      this.isEdit = true
      this.pesticideForm = {
        ...row,
        activeIngredient: row.activeIngredient || '',
        usageInstructions: row.usageInstructions || '',
        imageUrl: row.imageUrl || ''
      }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该农药记录吗？', '提示', {
          type: 'warning'
        })

        const response = await this.$axios.delete(`/pesticides/${row.id}`)
        if (response.data.code === 1) {
          this.$message.success('删除成功')
          this.fetchData()
        } else {
          this.$message.error(response.data.msg || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
          console.error('删除失败:', error)
        }
      }
    },
    // 处理表格选择变化
    handleSelectionChange(selection) {
      this.selectedPesticides = selection
    },
    // 批量删除
    async handleBatchDelete() {
      if (this.selectedPesticides.length === 0) {
        this.$message.warning('请选择要删除的农药')
        return
      }

      try {
        await this.$confirm(`确定要删除选中的 ${this.selectedPesticides.length} 个农药吗？`, '批量删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const ids = this.selectedPesticides.map(pesticide => pesticide.id)
        const response = await this.$axios.delete('/pesticides/batch', { data: ids })

        if (response.data.code === 1) {
          this.$message.success(`成功删除 ${ids.length} 个农药`)
          this.selectedPesticides = [] // 清空选择
          this.fetchData()
        } else {
          this.$message.error(response.data.msg || '批量删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('批量删除失败')
          console.error('批量删除失败:', error)
        }
      }
    },
    async submitForm() {
      try {
        await this.$refs.pesticideForm.validate()

        const data = {
          ...this.pesticideForm
        }

        // 如果是新增，添加createdBy字段
        if (!this.isEdit) {
          data.createdBy = parseInt(localStorage.getItem('userId'))
        }

        let response

        if (this.isEdit) {
          response = await this.$axios.put(`/pesticides/${data.id}`, data)
        } else {
          response = await this.$axios.post('/pesticides', data)
        }

        if (response.data.code === 1) {
          this.$message.success(this.isEdit ? '更新成功' : '添加成功')
          this.dialogVisible = false
          this.fetchData()
        } else {
          this.$message.error(response.data.msg || (this.isEdit ? '更新失败' : '添加失败'))
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(this.isEdit ? '更新失败' : '添加失败')
          console.error('表单提交失败:', error)
        }
      }
    },
    handleUploadSuccess(response) {
      if (response.code === 1) {
        this.pesticideForm.imageUrl = response.data
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
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchData()
    },
    formatDate(date) {
      if (!date) return '-'
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    }
  }
}
</script>

<style scoped>
.pesticide-container {
  padding: 20px;
}

.search-container {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.search-container .left-section {
  display: flex;
  align-items: center;
}

.search-container .right-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 178px;
  margin: 0 auto;
}

.avatar-uploader:hover {
  border-color: #409eff;
}

.avatar-uploader >>> .el-upload {
  border: none;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
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

.upload-tip {
  font-size: 12px;
  color: #606266;
  margin-top: 8px;
  text-align: center;
}

.usage-instructions {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 180px;
  cursor: pointer;
}

/* 统一图片样式 */
.image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60px;
}

.table-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.no-image {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background-color: #f5f7fa;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  color: #c0c4cc;
  font-size: 12px;
}

.no-image i {
  font-size: 20px;
  margin-bottom: 2px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #c0c4cc;
  font-size: 20px;
}

/* 统一表格样式 */
.el-table .el-table__row {
  height: 80px;
}

.el-table .cell {
  padding: 8px 10px;
}

/* 图片悬停效果 */
.table-image {
  transition: all 0.3s ease;
}

.table-image:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 按钮组样式优化 */
.el-button + .el-button {
  margin-left: 8px;
}
</style>