<template>
  <div class="plant-table">
    <!-- 搜索区域 -->
    <div class="search-area">
      <div class="search-left">
        <el-input v-model="searchQuery" placeholder="请输入药用植物名称或别名" style="width: 300px; margin-right: 10px" clearable @clear="handleClear" @keyup.enter.native="handleSearch" />
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
      </div>
      <div class="search-actions">
        <el-button
          type="danger"
          icon="el-icon-delete"
          :disabled="selectedPlants.length === 0"
          @click="handleBatchDelete"
        >
          批量删除 ({{ selectedPlants.length }})
        </el-button>
        <!-- <el-button type="primary" icon="el-icon-plus" @click="handleAdd">添加</el-button> -->
      </div>
    </div>

    <el-table :data="paginatedData" style="width: 100%; margin-top: 20px" @selection-change="handleSelectionChange">
      <!-- 复选框列 -->
      <el-table-column type="selection" width="55" align="center" />
      <!-- 序号列 -->
      <el-table-column type="index" label="序号" width="80" align="center" />

      <!-- 图片列 -->
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

      <!-- 基本信息列 -->
      <el-table-column prop="name" label="名称" width="120" />
      <el-table-column prop="alias" label="别名" width="120" />

      <!-- 描述列（带省略） -->
      <el-table-column label="描述" min-width="180">
        <template slot-scope="scope">
          <el-tooltip class="box-item" effect="dark" :content="scope.row.description" placement="top-start">
            <span class="truncate-text">{{ scope.row.description || '-' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>

      <!-- 药用部位列（带省略） -->
      <el-table-column label="药用部位" min-width="150">
        <template slot-scope="scope">
          <el-tooltip class="box-item" effect="dark" :content="formatMedicinalParts(scope.row.medicinalParts)" placement="top-start">
            <span class="truncate-text">{{ formatMedicinalParts(scope.row.medicinalParts) || '-' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>

      <!-- 功效列（带省略） -->
      <el-table-column label="功效" min-width="200">
        <template slot-scope="scope">
          <el-tooltip class="box-item" effect="dark" :content="scope.row.efficacy" placement="top-start">
            <span class="truncate-text">{{ scope.row.efficacy || '-' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>

      <!-- 创建时间列 -->
      <el-table-column label="创建时间" width="120" align="center">
        <template slot-scope="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>

      <!-- 操作列 -->
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

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[3, 5, 10, 20]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="displayPlants.length" />
    </div>

    <!-- 编辑对话框 -->
    <el-dialog :visible.sync="dialogVisible" :title="dialogTitle" width="60%">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="24">
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
              <el-select v-model="form.medicinalParts" multiple placeholder="请选择药用部位" style="width: 100%">
                <el-option v-for="item in medicinalPartsOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="功效" prop="efficacy">
              <el-input v-model="form.efficacy" type="textarea" :rows="3" placeholder="请输入功效" />
            </el-form-item>
            <el-form-item label="图片">
              <el-upload class="avatar-uploader" :action="uploadUrl" :show-file-list="false" :on-success="handleUploadSuccess" :before-upload="beforeUpload">
                <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar">
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              </el-upload>
              <div class="upload-tip">建议上传比例1:1的图片</div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'PlantTable',
  props: {
    plants: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      searchQuery: '',
      displayPlants: [],
      selectedPlants: [], // 选中的植物列表
      currentPage: 1,
      pageSize: 5,
      dialogVisible: false,
      dialogTitle: '',
      uploadUrl: '/api/files/upload',
      form: {
        id: null,
        name: '',
        alias: '',
        description: '',
        medicinalParts: [],
        efficacy: '',
        imageUrl: ''
      },
      medicinalPartsOptions: [
        { label: '根', value: 'root' },
        { label: '茎', value: 'stem' },
        { label: '叶', value: 'leaf' },
        { label: '花', value: 'flower' },
        { label: '果实', value: 'fruit' },
        { label: '种子', value: 'seed' },
        { label: '全草', value: 'whole' }
      ],
      rules: {
        name: [{ required: true, message: '请输入药用植物名称', trigger: 'blur' }],
        description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
        medicinalParts: [{ required: true, message: '请选择药用部位', trigger: 'change' }],
        efficacy: [{ required: true, message: '请输入功效', trigger: 'blur' }]
      }
    }
  },
  computed: {
    paginatedData() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.displayPlants.slice(start, end)
    }
  },
  created() {
    // 初始化显示所有植物
    this.displayPlants = this.plants
  },
  watch: {
    // 当props中的plants变化时更新显示
    plants: {
      immediate: true,
      handler(newPlants) {
        if (!this.searchQuery) {
          this.displayPlants = newPlants
        }
      }
    }
  },
  methods: {
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1 // 重置到第一页
    },
    handleCurrentChange(val) {
      this.currentPage = val
    },
    handleSearch() {
      if (!this.searchQuery) {
        this.displayPlants = this.plants
        this.currentPage = 1 // 搜索时重置到第一页
        return
      }
      const query = this.searchQuery.toLowerCase()
      this.displayPlants = this.plants.filter(plant => 
        plant.name.toLowerCase().includes(query) || 
        (plant.alias && plant.alias.toLowerCase().includes(query))
      )
      this.currentPage = 1 // 搜索时重置到第一页
    },
    handleClear() {
      this.searchQuery = ''
      this.displayPlants = this.plants
      this.currentPage = 1 // 清空搜索时重置到第一页
    },
    // 格式化药用部位显示
    formatMedicinalParts(parts) {
      if (!parts) return '-'
      const partsArray = parts.split(',')
      return partsArray
        .map(part => {
          const option = this.medicinalPartsOptions.find(opt => opt.value === part)
          return option ? option.label : part
        })
        .join('、')
    },
    // 格式化日期显示
    formatDate(date) {
      if (!date) return '-'
      const d = new Date(date)
      const year = d.getFullYear()
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${year}-${month}-${day}`
    },
    handleAdd() {
      this.dialogTitle = '添加药用植物'
      this.form = {
        id: null,
        name: '',
        alias: '',
        description: '',
        medicinalParts: [],
        efficacy: '',
        imageUrl: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑药用植物'
      // 深拷贝对象，确保所有字段都被正确复制
      this.form = JSON.parse(
        JSON.stringify({
          id: row.id,
          name: row.name,
          alias: row.alias,
          description: row.description,
          medicinalParts: row.medicinalParts ? row.medicinalParts.split(',') : [],
          efficacy: row.efficacy,
          imageUrl: row.imageUrl
        })
      )
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除这个药用植物吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.$emit('delete', row.id)
        })
        .catch(() => {})
    },
    // 处理表格选择变化
    handleSelectionChange(selection) {
      this.selectedPlants = selection
    },
    // 批量删除
    handleBatchDelete() {
      if (this.selectedPlants.length === 0) {
        this.$message.warning('请选择要删除的药用植物')
        return
      }

      this.$confirm(`确定要删除选中的 ${this.selectedPlants.length} 个药用植物吗？`, '批量删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          const ids = this.selectedPlants.map(plant => plant.id)
          this.$emit('batch-delete', ids)
          this.selectedPlants = [] // 清空选择
        })
        .catch(() => {})
    },
    handleSubmit() {
      this.$refs.formRef.validate(valid => {
        if (valid) {
          // 转换药用部位数组为字符串
          const formData = {
            ...this.form,
            medicinalParts: this.form.medicinalParts.join(',')
          }
          this.$emit('update', formData)
          this.dialogVisible = false
        }
      })
    },
    handleUploadSuccess(response) {
      if (response.code === 1) {
        this.form.imageUrl = response.data
        this.$message.success('上传成功')
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
    }
  }
}
</script>

<style scoped>
.plant-table {
  margin: 20px 0;
}

.search-area {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.search-left {
  display: flex;
  align-items: center;
}

.search-actions {
  display: flex;
  gap: 10px;
}

.truncate-text {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
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