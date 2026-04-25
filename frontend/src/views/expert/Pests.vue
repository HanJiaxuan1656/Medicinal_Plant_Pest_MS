<template>
  <div class="pests-container">
    <div class="page-header">
      <h2>病虫害管理</h2>
      <el-button type="primary" @click="handleAddPest">添加病虫害</el-button>
    </div>

    <div class="search-container">
      <div class="search-left">
        <el-input placeholder="搜索病虫害名称" v-model="searchQuery" class="search-input" prefix-icon="el-icon-search" @keyup.enter.native="handleSearch" clearable>
        </el-input>

        <el-select v-model="filterType" placeholder="类型筛选" clearable>
          <el-option v-for="item in pestTypes" :key="item.value" :label="item.label" :value="item.value">
          </el-option>
        </el-select>

        <el-button type="primary" icon="el-icon-search" @click="handleSearch">
          查询
        </el-button>
      </div>

      <div class="search-right">
        <el-button
          type="danger"
          icon="el-icon-delete"
          :disabled="selectedPests.length === 0"
          @click="handleBatchDelete"
        >
          批量删除 ({{ selectedPests.length }})
        </el-button>
      </div>
    </div>

    <el-table :data="tableData" border style="width: 100%; margin-top: 20px" v-loading="loading" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" label="序号" width="80" align="center" :index="indexMethod">
      </el-table-column>
      <el-table-column prop="name" label="病虫害名称" min-width="120">
      </el-table-column>
      <el-table-column label="类型" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.type === '病害' ? 'danger' : 'warning'">
            {{ scope.row.type }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="描述" min-width="180">
        <template slot-scope="scope">
          <div class="truncate-text" :title="scope.row.description">
            {{ scope.row.description || '-' }}
          </div>
        </template>
      </el-table-column>
      <el-table-column label="症状" min-width="180">
        <template slot-scope="scope">
          <div class="truncate-text" :title="scope.row.symptoms">
            {{ scope.row.symptoms || '-' }}
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
          {{ scope.row.createdAt ? formatDate(scope.row.createdAt) : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[3, 5, 10, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="total">
      </el-pagination>
    </div>

    <!-- 添加/编辑病虫害对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="60%">
      <el-form :model="pestForm" :rules="rules" ref="pestForm" label-width="100px">
        <el-form-item label="病虫害名称" prop="name">
          <el-input v-model="pestForm.name"></el-input>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="pestForm.type">
            <el-radio label="病害">病害</el-radio>
            <el-radio label="虫害">虫害</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" v-model="pestForm.description" :rows="4" placeholder="请详细描述该病虫害的特征、发生规律等"></el-input>
        </el-form-item>
        <el-form-item label="症状" prop="symptoms">
          <el-input type="textarea" v-model="pestForm.symptoms" :rows="4" placeholder="请描述该病虫害的主要症状表现"></el-input>
        </el-form-item>
        <el-form-item label="图片">
          <el-upload class="avatar-uploader" action="/api/files/upload" :headers="uploadHeaders" :show-file-list="false" :on-success="handleUploadSuccess" :before-upload="beforeUpload">
            <img v-if="pestForm.imageUrl" :src="pestForm.imageUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
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
  name: 'Pests',
  data() {
    return {
      searchQuery: '',
      filterType: '',
      loading: false,
      tableData: [],
      selectedPests: [], // 选中的病虫害列表
      currentPage: 1,
      pageSize: 5,
      total: 0,
      dialogVisible: false,
      dialogTitle: '添加病虫害',
      isEdit: false,
      pestForm: {
        id: null,
        name: '',
        type: '病害',
        description: '',
        symptoms: '',
        imageUrl: '',
        image_url: '',
        create_at: null,
        update_at: null,
        created_by: null
      },
      rules: {
        name: [
          { required: true, message: '请输入病虫害名称', trigger: 'blur' },
          { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
        ],
        type: [{ required: true, message: '请选择类型', trigger: 'change' }],
        description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
        symptoms: [{ required: true, message: '请输入症状', trigger: 'blur' }]
      },
      fileList: [],
      pestTypes: [
        { value: '病害', label: '病害' },
        { value: '虫害', label: '虫害' }
      ],
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
        const response = await this.$axios.get('/pest-diseases', {
          params: {
            search: this.searchQuery,
            type: this.filterType,
            page: this.currentPage,
            pageSize: this.pageSize
          }
        })

        if (response.data.code === 0 || response.data.code === 1) {
          this.tableData = response.data.data.items
          this.total = response.data.data.total
        }
      } catch (error) {
        this.$message.error('获取病虫害数据失败')
        console.error('获取病虫害数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      this.fetchData()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchData()
    },
    handleAddPest() {
      this.dialogTitle = '添加病虫害'
      this.isEdit = false
      this.pestForm = {
        id: null,
        name: '',
        type: '病害',
        description: '',
        symptoms: '',
        imageUrl: '',
        image_url: '',
        create_at: null,
        update_at: null,
        created_by: null
      }
      this.fileList = []
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑病虫害'
      this.isEdit = true
      this.pestForm = {
        ...row,
        imageUrl: row.imageUrl,
        image_url: row.imageUrl
      }
      if (row.image_url) {
        this.fileList = [
          {
            name: row.name,
            url: row.image_url
          }
        ]
      } else {
        this.fileList = []
      }
      this.dialogVisible = true
    },
    handleView(row) {
      // 跳转到详情页
      this.$router.push(`/expert/pest/${row.id}`)
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该病虫害记录吗？', '提示', {
          type: 'warning'
        })

        const response = await this.$axios.delete(`/pest-diseases/${row.id}`)
        if (response.data.code === 0 || response.data.code === 1) {
          this.$message.success('删除成功')
          this.fetchData()
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
      this.selectedPests = selection
    },
    // 批量删除
    async handleBatchDelete() {
      if (this.selectedPests.length === 0) {
        this.$message.warning('请选择要删除的病虫害')
        return
      }

      try {
        await this.$confirm(`确定要删除选中的 ${this.selectedPests.length} 个病虫害吗？`, '批量删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })

        const ids = this.selectedPests.map(pest => pest.id)
        const response = await this.$axios.delete('/pest-diseases/batch', { data: ids })

        if (response.data.code === 0 || response.data.code === 1) {
          this.$message.success(`成功删除 ${ids.length} 个病虫害`)
          this.selectedPests = [] // 清空选择
          this.fetchData()
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
        await this.$refs.pestForm.validate()

        const data = {
          ...this.pestForm,
          image_url: this.pestForm.imageUrl
        }
        let response

        if (this.isEdit) {
          response = await this.$axios.put(`/pest-diseases/${data.id}`, data)
        } else {
          response = await this.$axios.post('/pest-diseases', data)
        }

        if (response.data.code === 0 || response.data.code === 1) {
          this.$message.success(this.isEdit ? '更新成功' : '添加成功')
          this.dialogVisible = false
          this.fetchData()
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error(this.isEdit ? '更新失败' : '添加失败')
          console.error('表单提交失败:', error)
        }
      }
    },
    handlePreview(file) {
      if (file.url) {
        window.open(file.url)
      }
    },
    handleRemove() {
      this.pestForm.imageUrl = ''
      this.pestForm.image_url = ''
    },
    handleUploadSuccess(response) {
      if (response.code === 1) {
        this.pestForm.imageUrl = response.data
        this.pestForm.image_url = response.data
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
    formatDate(dateStr) {
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    indexMethod(index) {
      return (this.currentPage - 1) * this.pageSize + index + 1
    }
  }
}
</script>

<style scoped>
.pests-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.search-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-left {
  display: flex;
  gap: 15px;
  align-items: center;
}

.search-right {
  display: flex;
  gap: 10px;
}

.search-input {
  width: 300px;
}

.el-select {
  width: 150px;
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

.el-dialog__body {
  padding-top: 20px;
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

/* 统一文本省略样式 */
.truncate-text {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
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