<template>
  <div class="plants-container">
    <div class="page-header">
      <h2>药用植物管理</h2>
      <el-button type="primary" @click="$refs.plantTable.handleAdd()">添加植物</el-button>
    </div>
    <!-- 使用PlantTable组件 -->
    <plant-table ref="plantTable" :plants="plants" @update="handleUpdate" @delete="handleDelete" @batch-delete="handleBatchDelete" v-loading="loading" />
    <!-- 添加植物对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="60%">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="14">
            <el-form-item label="名称" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="别名" prop="alias">
              <el-input v-model="form.alias" />
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入药用植物描述" />
            </el-form-item>
            <el-form-item label="药用部位" prop="medicinalParts">
              <el-input v-model="form.medicinalParts" type="textarea" :rows="3" placeholder="请输入药用部位" />
            </el-form-item>
            <el-form-item label="功效" prop="efficacy">
              <el-input v-model="form.efficacy" type="textarea" :rows="3" placeholder="请输入功效" />
            </el-form-item>
          </el-col>
          <el-col :span="10">
            <el-form-item label="图片">
              <el-upload class="avatar-uploader" :action="uploadUrl" :show-file-list="false" :on-success="handleUploadSuccess" :before-upload="beforeUpload">
                <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar">
                <el-icon v-else class="avatar-uploader-icon">
                  <plus />
                </el-icon>
              </el-upload>
              <div class="upload-tip">建议上传比例1:1的图片</div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import PlantTable from '@/components/plant/PlantTable.vue'

export default {
  name: 'Plants',
  components: {
    PlantTable
  },
  data() {
    return {
      loading: false,
      plants: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      searchQuery: '',
      dialogVisible: false,
      dialogTitle: '添加植物',
      formRef: null,
      form: {
        id: null,
        name: '',
        alias: '',
        description: '',
        medicinalParts: '',
        efficacy: '',
        imageUrl: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入植物名称', trigger: 'blur' },
          { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
        ],
        description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
        efficacy: [{ required: true, message: '请输入功效', trigger: 'blur' }]
      },
      uploadUrl: '/api/plants/upload/image'
    }
    
  },
  created() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const response = await this.$axios.get('/plants', {
          params: {
            page: this.currentPage,
            pageSize: this.pageSize,
            keyword: this.searchQuery
          }
        })
        this.plants = response.data.data
        this.total = response.data.total
      } catch (error) {
        console.error('获取药用植物数据失败:', error)
        this.$message.error('获取药用植物数据失败')
      } finally {
        this.loading = false
      }
    },
    async handleUpdate(data) {
      try {
        if (data.id) {
          await this.$axios.put(`/plants/${data.id}`, data)
          this.$message.success('更新成功')
        } else {
          await this.$axios.post('/plants', data)
          this.$message.success('添加成功')
        }
        this.dialogVisible = false
        this.fetchData()
      } catch (error) {
        console.error('操作失败:', error)
        this.$message.error('操作失败')
      }
    },
    async handleDelete(id) {
      try {
        await this.$axios.delete(`/plants/${id}`)
        this.$message.success('删除成功')
        this.fetchData()
      } catch (error) {
        console.error('删除失败:', error)
        this.$message.error('删除失败')
      }
    },
    async handleBatchDelete(ids) {
      try {
        await this.$axios.delete('/plants/batch', { data: ids })
        this.$message.success(`成功删除 ${ids.length} 个药用植物`)
        this.fetchData()
      } catch (error) {
        console.error('批量删除失败:', error)
        this.$message.error('批量删除失败')
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
    handleUploadSuccess(response) {
      this.form.imageUrl = response.data
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
    handleSubmit() {
      if (!this.formRef) return

      this.formRef.validate(valid => {
        if (valid) {
          this.handleUpdate(this.form)
        }
      })
    }
  }
}
</script>

<style scoped>
.plants-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-container {
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.avatar-uploader {
  width: 178px;
  height: 178px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 8px;
  text-align: center;
}

:deep(.el-dialog__body) {
  padding-top: 20px;
}
</style>