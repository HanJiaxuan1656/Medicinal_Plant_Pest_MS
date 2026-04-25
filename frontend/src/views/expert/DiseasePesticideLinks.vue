<template>
  <div class="disease-pesticide-links-container">
    <div class="page-header">
      <h2>病虫害-农药关系管理</h2>
      <el-button type="primary" @click="handleAdd">添加关系</el-button>
    </div>

    <!-- 搜索和过滤区域 -->
    <div class="search-container">
      <div class="search-left">
        <el-input v-model="searchDiseaseName" placeholder="请输入病虫害名称" style="width: 200px; margin-right: 10px" />
        <el-input v-model="searchPesticideName" placeholder="请输入农药名称" style="width: 200px; margin-right: 10px" />
        <el-select v-model="filterEffectiveness" placeholder="选择效果" style="width: 150px; margin-right: 10px" clearable>
          <el-option label="高" value="高" />
          <el-option label="中" value="中" />
          <el-option label="低" value="低" />
        </el-select>
        <el-button type="primary" @click="fetchData">搜索</el-button>
      </div>
      <div class="search-right">
        <el-button 
          type="danger" 
          icon="el-icon-delete"
          :disabled="selectedLinks.length === 0"
          @click="handleBatchDelete"
        >
          批量删除 ({{ selectedLinks.length }})
        </el-button>
      </div>
    </div>

    <!-- 表格区域 -->
    <el-table :data="tableData" border style="width: 100%; margin-top: 20px" v-loading="loading" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" label="序号" width="80" align="center" />
      
      <!-- 病虫害信息 -->
      <el-table-column label="病虫害" min-width="120">
        <template slot-scope="scope">
          <div class="disease-info">
            <div class="disease-name">{{ scope.row.pestDisease ? scope.row.pestDisease.name : '-' }}</div>
            <el-tag v-if="scope.row.pestDisease" :type="scope.row.pestDisease.type === '病害' ? 'danger' : 'warning'" size="mini">
              {{ scope.row.pestDisease.type }}
            </el-tag>
          </div>
        </template>
      </el-table-column>

      <!-- 农药信息 -->
      <el-table-column label="农药" min-width="120">
        <template slot-scope="scope">
          <div class="pesticide-info">
            <div class="pesticide-name">{{ scope.row.pesticide ? scope.row.pesticide.name : '-' }}</div>
            <div class="pesticide-category">{{ scope.row.pesticide ? scope.row.pesticide.category : '' }}</div>
          </div>
        </template>
      </el-table-column>

      <!-- 效果 -->
      <el-table-column label="效果" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="getEffectivenessType(scope.row.effectiveness)" size="mini">
            {{ scope.row.effectiveness }}
          </el-tag>
        </template>
      </el-table-column>

      <!-- 用法用量 -->
      <el-table-column prop="usageDosage" label="用法用量" min-width="120" />

      <!-- 施用方法 -->
      <el-table-column prop="applicationMethod" label="施用方法" min-width="120" />

      <!-- 安全间隔期 -->
      <el-table-column label="安全间隔期" width="120" align="center">
        <template slot-scope="scope">
          {{ scope.row.safeIntervalDays ? scope.row.safeIntervalDays + '天' : '-' }}
        </template>
      </el-table-column>

      <!-- 副作用 -->
      <el-table-column label="副作用" min-width="150">
        <template slot-scope="scope">
          <div class="truncate-text" :title="scope.row.sideEffects">
            {{ scope.row.sideEffects || '-' }}
          </div>
        </template>
      </el-table-column>

      <!-- 备注 -->
      <el-table-column label="备注" min-width="150">
        <template slot-scope="scope">
          <div class="truncate-text" :title="scope.row.notes">
            {{ scope.row.notes || '-' }}
          </div>
        </template>
      </el-table-column>

      <!-- 创建时间 -->
      <el-table-column label="创建时间" width="120" align="center">
        <template slot-scope="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column label="操作" width="150" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[3,5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
      />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog :visible.sync="dialogVisible" :title="dialogTitle" width="70%">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="病虫害" prop="pdId">
              <el-select v-model="form.pdId" placeholder="请选择病虫害" style="width: 100%" filterable>
                <el-option
                  v-for="disease in diseases"
                  :key="disease.id"
                  :label="`${disease.name}(${disease.type})`"
                  :value="disease.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="农药" prop="pesticideId">
              <el-select v-model="form.pesticideId" placeholder="请选择农药" style="width: 100%" filterable>
                <el-option
                  v-for="pesticide in pesticides"
                  :key="pesticide.id"
                  :label="`${pesticide.name}${pesticide.category ? '(' + pesticide.category + ')' : ''}`"
                  :value="pesticide.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="效果" prop="effectiveness">
              <el-select v-model="form.effectiveness" placeholder="请选择效果" style="width: 100%">
                <el-option label="高" value="高" />
                <el-option label="中" value="中" />
                <el-option label="低" value="低" />
              </el-select>
            </el-form-item>
            <el-form-item label="用法用量" prop="usageDosage">
              <el-input v-model="form.usageDosage" placeholder="如：每亩用药50ml" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="施用方法" prop="applicationMethod">
              <el-input v-model="form.applicationMethod" placeholder="如：叶面喷雾、土壤灌根等" />
            </el-form-item>
            <el-form-item label="安全间隔期" prop="safeIntervalDays">
              <el-input-number v-model="form.safeIntervalDays" :min="0" :max="365" placeholder="天数" style="width: 100%" />
            </el-form-item>
            <el-form-item label="副作用">
              <el-input v-model="form.sideEffects" type="textarea" :rows="2" placeholder="请输入可能的副作用" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="form.notes" type="textarea" :rows="2" placeholder="请输入备注信息" />
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
  name: 'DiseasePesticideLinks',
  data() {
    return {
      loading: false,
      tableData: [],
      selectedLinks: [],
      currentPage: 1,
      pageSize: 5,
      total: 0,
      searchDiseaseName: '',
      searchPesticideName: '',
      filterEffectiveness: '',
      dialogVisible: false,
      dialogTitle: '添加关系',
      diseases: [], // 病虫害列表
      pesticides: [], // 农药列表
      form: {
        id: null,
        pdId: null,
        pesticideId: null,
        effectiveness: '中',
        usageDosage: '',
        applicationMethod: '',
        safeIntervalDays: null,
        sideEffects: '',
        notes: ''
      },
      rules: {
        pdId: [{ required: true, message: '请选择病虫害', trigger: 'change' }],
        pesticideId: [{ required: true, message: '请选择农药', trigger: 'change' }],
        effectiveness: [{ required: true, message: '请选择效果', trigger: 'change' }]
      }
    }
  },
  created() {
    this.fetchData()
    this.fetchDiseases()
    this.fetchPesticides()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const response = await this.$axios.get('/disease-pesticide-links', {
          params: {
            page: this.currentPage,
            pageSize: this.pageSize,
            diseaseName: this.searchDiseaseName,
            pesticideName: this.searchPesticideName,
            effectiveness: this.filterEffectiveness
          }
        })
        
        if (response.data.code === 1) {
          this.tableData = response.data.data.data
          this.total = response.data.data.total
        } else {
          this.$message.error(response.data.msg || '获取数据失败')
        }
      } catch (error) {
        console.error('获取数据失败:', error)
        this.$message.error('获取数据失败')
      } finally {
        this.loading = false
      }
    },
    async fetchDiseases() {
      try {
        const response = await this.$axios.get('/pest-diseases/list/all')
        if (response.data.code === 1) {
          this.diseases = response.data.data
        }
      } catch (error) {
        console.error('获取病虫害列表失败:', error)
      }
    },
    async fetchPesticides() {
      try {
        const response = await this.$axios.get('/pesticides/all')
        if (response.data.code === 1) {
          this.pesticides = response.data.data
        }
      } catch (error) {
        console.error('获取农药列表失败:', error)
      }
    },
    handleAdd() {
      this.dialogTitle = '添加关系'
      this.form = {
        id: null,
        pdId: null,
        pesticideId: null,
        effectiveness: '中',
        usageDosage: '',
        applicationMethod: '',
        safeIntervalDays: null,
        sideEffects: '',
        notes: ''
      }
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.dialogTitle = '编辑关系'
      this.form = {
        id: row.id,
        pdId: row.pdId,
        pesticideId: row.pesticideId,
        effectiveness: row.effectiveness,
        usageDosage: row.usageDosage,
        applicationMethod: row.applicationMethod,
        safeIntervalDays: row.safeIntervalDays,
        sideEffects: row.sideEffects,
        notes: row.notes
      }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该关系记录吗？', '提示', {
          type: 'warning'
        })

        const response = await this.$axios.delete(`/disease-pesticide-links/${row.id}`)
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
    handleSelectionChange(selection) {
      this.selectedLinks = selection
    },
    async handleBatchDelete() {
      if (this.selectedLinks.length === 0) {
        this.$message.warning('请选择要删除的关系')
        return
      }
      
      try {
        await this.$confirm(`确定要删除选中的 ${this.selectedLinks.length} 个关系吗？`, '批量删除确认', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        const ids = this.selectedLinks.map(link => link.id)
        const response = await this.$axios.delete('/disease-pesticide-links/batch', { data: ids })
        
        if (response.data.code === 1) {
          this.$message.success(`成功删除 ${ids.length} 个关系`)
          this.selectedLinks = []
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
    async handleSubmit() {
      this.$refs.formRef.validate(async (valid) => {
        if (valid) {
          try {
            let response
            if (this.form.id) {
              response = await this.$axios.put(`/disease-pesticide-links/${this.form.id}`, this.form)
            } else {
              response = await this.$axios.post('/disease-pesticide-links', this.form)
            }
            
            if (response.data.code === 1) {
              this.$message.success(this.form.id ? '更新成功' : '添加成功')
              this.dialogVisible = false
              this.fetchData()
            } else {
              this.$message.error(response.data.msg || '操作失败')
            }
          } catch (error) {
            this.$message.error('操作失败')
          }
        }
      })
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
      this.fetchData()
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchData()
    },
    getEffectivenessType(effectiveness) {
      switch (effectiveness) {
        case '高': return 'success'
        case '中': return 'warning'
        case '低': return 'danger'
        default: return ''
      }
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
.disease-pesticide-links-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-left {
  display: flex;
  align-items: center;
}

.search-right {
  display: flex;
  gap: 10px;
}

.disease-info, .pesticide-info {
  display: flex;
  flex-direction: column;
}

.disease-name, .pesticide-name {
  font-weight: bold;
  margin-bottom: 2px;
}

.pesticide-category {
  font-size: 12px;
  color: #999;
}

.truncate-text {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.el-dialog__body {
  padding-top: 20px;
}
</style>
