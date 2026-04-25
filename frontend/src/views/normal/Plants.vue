<template>
  <div class="plants-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-s-opportunity"></i>
          药用植物大全
        </h1>
        <p class="page-subtitle">探索丰富的药用植物知识，了解植物的功效与应用</p>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="10">
          <el-input
            v-model="searchQuery"
            placeholder="搜索植物名称..."
            prefix-icon="el-icon-search"
            size="large"
            class="search-input"
            clearable
          ></el-input>
        </el-col>
        <el-col :span="2">
          <el-button
            type="primary"
            size="large"
            icon="el-icon-search"
            @click="handleSearch"
            class="search-button"
          >
            搜索
          </el-button>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="selectedMedicinalPart"
            placeholder="选择药用部位"
            size="large"
            class="category-select"
            @change="handleMedicinalPartChange"
            clearable
          >
            <el-option label="全部药用部位" value=""></el-option>
            <el-option label="根" value="根"></el-option>
            <el-option label="茎" value="茎"></el-option>
            <el-option label="叶" value="叶"></el-option>
            <el-option label="花" value="花"></el-option>
            <el-option label="果实" value="果实"></el-option>
            <el-option label="种子" value="种子"></el-option>
            <el-option label="全草" value="全草"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="sortBy"
            placeholder="排序方式"
            size="large"
            class="sort-select"
            @change="handleSortChange"
          >
            <el-option label="默认排序" value="default"></el-option>
            <el-option label="按名称排序" value="name"></el-option>
            <el-option label="最新添加" value="created_at"></el-option>
          </el-select>
        </el-col>
      </el-row>
    </div>

    <!-- 植物列表 -->
    <div class="plants-grid" v-loading="loading">
      <div class="grid-container">
        <div
          v-for="plant in plants"
          :key="plant.id"
          class="plant-card"
          @click="viewPlantDetail(plant)"
        >
          <div class="plant-image-container">
            <img
              v-if="plant.imageUrl || plant.image_url"
              :src="plant.imageUrl || plant.image_url"
              :alt="plant.name"
              class="plant-image"
              @error="handleImageError"
            />
            <div v-else class="plant-image-placeholder">
              <i class="el-icon-picture-outline"></i>
              <span>暂无图片</span>
            </div>
            <div class="plant-overlay">
              <el-button type="primary" icon="el-icon-view" circle></el-button>
            </div>
          </div>

          <div class="plant-content">
            <h3 class="plant-name">{{ plant.name }}</h3>
            <p class="plant-medicinal-part">药用部位：{{ getMedicinalPartsText(plant) }}</p>
            <p class="plant-description">{{ plant.description || '暂无描述' }}</p>

            <div class="plant-tags">
              <el-tag v-if="plant.origin" size="mini" type="info">{{ plant.origin }}</el-tag>
              <el-tag v-if="plant.efficacy" size="mini" type="success">
                <span class="tag-text">{{ plant.efficacy }}</span>
              </el-tag>
            </div>

            <div class="plant-footer">
              <span class="view-count">
                <i class="el-icon-view"></i>
                {{ plant.viewCount || 0 }} 次查看
              </span>
              <span class="comment-count">
                <i class="el-icon-chat-dot-round"></i>
                {{ plant.commentCount || 0 }} 条评论
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && plants.length === 0" class="empty-state">
        <i class="el-icon-search"></i>
        <h3>未找到相关植物</h3>
        <p>请尝试调整搜索条件或筛选选项</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-section" v-if="totalPlants > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalPlants"
        :page-size="pageSize"
        :page-sizes="[3, 6, 12, 24]"
        :current-page="currentPage"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import { getPlants } from '@/api/normal'

export default {
  name: 'NormalPlants',
  data() {
    return {
      searchQuery: '',
      selectedMedicinalPart: '',
      sortBy: 'default',
      plants: [],
      loading: false,
      totalPlants: 0,
      pageSize: 6,
      currentPage: 1
    }
  },
  created() {
    this.fetchPlants()
  },
  methods: {
    async fetchPlants() {
      try {
        this.loading = true
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize
        }

        // 只有当有值时才添加参数，避免发送空字符串
        if (this.searchQuery && this.searchQuery.trim()) {
          params.search = this.searchQuery.trim()
          // 尝试不同的搜索参数名
          params.keyword = this.searchQuery.trim()
          params.name = this.searchQuery.trim()
        }

        if (this.selectedMedicinalPart) {
          params.medicinalParts = this.selectedMedicinalPart
          // 尝试不同的药用部位参数名
          params.medicinalPart = this.selectedMedicinalPart
          params.part = this.selectedMedicinalPart
        }

        if (this.sortBy && this.sortBy !== 'default') {
          params.sortBy = this.sortBy
          // 尝试不同的排序参数名
          params.sort = this.sortBy
          params.orderBy = this.sortBy
        }


        const response = await getPlants(params)

        this.plants = response.data.data.list || []
        this.totalPlants = response.data.data.total || 0
      } catch (error) {
        console.error('获取植物列表失败:', error)
        this.$message.error('获取植物列表失败')
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.currentPage = 1
      this.fetchPlants()
    },

    handleMedicinalPartChange() {
      this.currentPage = 1
      this.fetchPlants()
    },

    handleSortChange() {
      this.currentPage = 1
      this.fetchPlants()
    },

    handlePageChange(page) {
      this.currentPage = page
      this.fetchPlants()
    },

    handleSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
      this.fetchPlants()
    },

    viewPlantDetail(plant) {
      this.$router.push({
        path: `/normal/plant-detail/${plant.id}`,
        query: { name: plant.name }
      })
    },

    getMedicinalPartsText(plant) {
      if (!plant.medicinalParts && !plant.medicinalPart) {
        return '未知'
      }

      // 药用部位映射表
      const partMap = {
        'root': '根',
        'stem': '茎',
        'leaf': '叶',
        'flower': '花',
        'fruit': '果实',
        'seed': '种子',
        'whole': '全草',
        'bark': '皮',
        '根': '根',
        '茎': '茎',
        '叶': '叶',
        '花': '花',
        '果实': '果实',
        '种子': '种子',
        '全草': '全草',
        '皮': '皮'
      }

      // 处理后端返回的 medicinalParts 字段（如 "leaf,flower"）
      const parts = plant.medicinalParts || plant.medicinalPart || ''
      if (parts.includes(',')) {
        return parts.split(',')
          .map(part => partMap[part.trim()] || part.trim())
          .join('、')
      } else {
        return partMap[parts.trim()] || parts.trim() || '未知'
      }
    },

    handleImageError(event) {
      // 图片加载失败时隐藏图片，显示占位符
      event.target.style.display = 'none'
      const placeholder = event.target.parentNode.querySelector('.plant-image-placeholder')
      if (!placeholder) {
        const placeholderDiv = document.createElement('div')
        placeholderDiv.className = 'plant-image-placeholder'
        placeholderDiv.innerHTML = '<i class="el-icon-picture-outline"></i><span>暂无图片</span>'
        event.target.parentNode.appendChild(placeholderDiv)
      }
    }
  }
}
</script>

<style scoped>
.plants-container {
  padding: 0;
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  position: relative;
}

.plants-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 80%, rgba(102, 126, 234, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(118, 75, 162, 0.03) 0%, transparent 50%);
  pointer-events: none;
}

/* 页面头部 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 0;
  color: white;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 30% 70%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 70% 30%, rgba(255, 215, 0, 0.1) 0%, transparent 50%);
  opacity: 0.6;
}

.page-header::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #ffd700 0%, transparent 100%);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  position: relative;
  z-index: 1;
}

.page-title {
  font-size: 3.5rem;
  font-weight: 800;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-title i {
  font-size: 4rem;
  color: #ffd700;
  filter: drop-shadow(0 0 12px rgba(255, 215, 0, 0.4));
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.page-subtitle {
  font-size: 1.3rem;
  opacity: 0.95;
  margin: 0;
  font-weight: 400;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 搜索区域 */
.search-section {
  max-width: 1400px;
  margin: 0 auto;
  padding: 48px 24px;
  position: relative;
  z-index: 1;
}

.search-input,
.category-select,
.sort-select {
  width: 100%;
}

.search-input >>> .el-input__inner {
  padding-left: 50px;
  padding-top: 10px;
}

.search-input >>> .el-input__inner::placeholder {
  transform: translateY(-6px) translateX(5px);
  color: #a0aec0;
}

.search-button {
  width: 100%;
  height: 48px;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.search-input >>> .el-input__inner,
.category-select >>> .el-input__inner,
.sort-select >>> .el-input__inner {
  border-radius: 16px;
  border: 2px solid #e1e8ed;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  font-weight: 500;
  height: 48px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.search-input >>> .el-input__inner:focus,
.category-select >>> .el-input__inner:focus,
.sort-select >>> .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.search-input >>> .el-input__prefix {
  left: 16px;
}

.search-input >>> .el-input__prefix .el-input__icon {
  color: #667eea;
  font-size: 18px;
}

/* 植物网格 */
.plants-grid {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px 48px;
  min-height: 400px;
  position: relative;
  z-index: 1;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 32px;
}

/* 植物卡片 */
.plant-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  overflow: hidden;
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.5);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 2px solid transparent;
  backdrop-filter: blur(20px);
  position: relative;
}

.plant-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
}

.plant-card:hover::before {
  opacity: 1;
}

.plant-card:hover {
  transform: translateY(-12px) scale(1.02);
  box-shadow:
    0 25px 50px rgba(102, 126, 234, 0.2),
    0 0 0 2px rgba(102, 126, 234, 0.3);
  border-color: #667eea;
}

.plant-image-container {
  position: relative;
  height: 220px;
  overflow: hidden;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.plant-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  filter: brightness(1.1) contrast(1.05);
}

.plant-card:hover .plant-image {
  transform: scale(1.08) rotate(-1deg);
  filter: brightness(1.2) contrast(1.1);
}

.plant-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  color: #a0aec0;
  font-size: 0.9rem;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.plant-image-placeholder i {
  font-size: 3rem;
  margin-bottom: 8px;
  color: #cbd5e0;
}

.plant-card:hover .plant-image-placeholder {
  background: linear-gradient(135deg, #edf2f7 0%, #e2e8f0 100%);
  color: #718096;
}

.plant-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.92) 0%, rgba(118, 75, 162, 0.92) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  backdrop-filter: blur(4px);
}

.plant-card:hover .plant-overlay {
  opacity: 1;
}

.plant-overlay >>> .el-button {
  background: rgba(255, 255, 255, 0.9);
  border: none;
  color: #667eea;
  font-size: 20px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.plant-overlay >>> .el-button:hover {
  transform: scale(1.1);
  background: white;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.3);
}

.plant-content {
  padding: 16px;
}

.plant-name {
  font-size: 1.3rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 8px 0;
  line-height: 1.3;
}

.plant-medicinal-part {
  color: #667eea;
  font-size: 0.9rem;
  font-weight: 600;
  margin: 0 0 12px 0;
}

.plant-description {
  color: #606266;
  font-size: 0.9rem;
  line-height: 1.5;
  margin: 0 0 16px 0;
  height: 42px;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.plant-tags {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.plant-tags >>> .el-tag {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tag-text {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  display: inline-block;
}

.plant-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8rem;
  color: #909399;
}

.view-count,
.comment-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #909399;
}

.empty-state i {
  font-size: 4rem;
  margin-bottom: 20px;
  color: #c0c4cc;
}

.empty-state h3 {
  font-size: 1.2rem;
  margin: 0 0 8px 0;
  color: #606266;
}

.empty-state p {
  margin: 0;
  font-size: 0.9rem;
}

/* 分页区域 */
.pagination-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }

  .page-title i {
    font-size: 2.5rem;
  }

  .page-subtitle {
    font-size: 1rem;
  }

  .search-section {
    padding: 20px;
  }

  .grid-container {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;
  }

  .plants-grid {
    padding: 0 20px 20px;
  }
}

@media (max-width: 480px) {
  .grid-container {
    grid-template-columns: 1fr;
  }

  .page-title {
    font-size: 1.8rem;
    flex-direction: column;
    gap: 8px;
  }
}
</style>