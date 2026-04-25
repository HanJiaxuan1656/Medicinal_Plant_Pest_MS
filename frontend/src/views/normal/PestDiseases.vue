<template>
  <div class="pest-diseases-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-warning-outline"></i>
          病虫害防治指南
        </h1>
        <p class="page-subtitle">了解常见病虫害，掌握科学防治方法</p>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="10">
          <el-input
            v-model="searchQuery"
            placeholder="搜索病虫害名称..."
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
            v-model="selectedType"
            placeholder="选择类型"
            size="large"
            class="type-select"
            @change="handleTypeChange"
            clearable
          >
            <el-option label="全部类型" value=""></el-option>
            <el-option label="病害" value="病害"></el-option>
            <el-option label="虫害" value="虫害"></el-option>
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

    <!-- 病虫害列表 -->
    <div class="pest-diseases-grid" v-loading="loading">
      <div class="grid-container">
        <div
          v-for="pestDisease in pestDiseases"
          :key="pestDisease.id"
          class="pest-disease-card"
          @click="viewPestDiseaseDetail(pestDisease)"
        >
          <div class="pest-disease-image-container">
            <img
              v-if="pestDisease.imageUrl || pestDisease.image_url"
              :src="pestDisease.imageUrl || pestDisease.image_url"
              :alt="pestDisease.name"
              class="pest-disease-image"
              @error="handleImageError"
            />
            <div v-else class="pest-disease-image-placeholder">
              <i class="el-icon-picture-outline"></i>
              <span>暂无图片</span>
            </div>
            <div class="pest-disease-overlay">
              <el-button type="primary" icon="el-icon-view" circle></el-button>
            </div>
            <div class="type-badge" :class="getTypeBadgeClass(pestDisease.type)">
              {{ pestDisease.type }}
            </div>
          </div>
          
          <div class="pest-disease-content">
            <h3 class="pest-disease-name">{{ pestDisease.name }}</h3>
            <div class="pest-disease-description">
              <span class="description-label">描述：</span>
              <span class="description-text">{{ pestDisease.description || '暂无描述' }}</span>
            </div>

            <div class="pest-disease-info">
              <div class="info-item">
                <span class="label">症状：</span>
                <span class="value">{{ pestDisease.symptoms || '未知' }}</span>
              </div>
              <div class="info-item">
                <span class="label">创建时间：</span>
                <span class="value">{{ formatDate(pestDisease.createdAt) || '未知' }}</span>
              </div>
            </div>
            
            <div class="pest-disease-footer">
              <span class="view-count">
                <i class="el-icon-view"></i>
                {{ pestDisease.viewCount || 0 }} 次查看
              </span>
              <span class="comment-count">
                <i class="el-icon-chat-dot-round"></i>
                {{ pestDisease.commentCount || 0 }} 条评论
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && pestDiseases.length === 0" class="empty-state">
        <i class="el-icon-search"></i>
        <h3>未找到相关病虫害</h3>
        <p>请尝试调整搜索条件或筛选选项</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-section" v-if="totalPestDiseases > 0">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalPestDiseases"
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
import { getPestDiseases } from '@/api/normal'

export default {
  name: 'NormalPestDiseases',
  data() {
    return {
      searchQuery: '',
      selectedType: '',
      sortBy: 'default',
      pestDiseases: [],
      loading: false,
      totalPestDiseases: 0,
      pageSize: 6,
      currentPage: 1
    }
  },
  created() {
    this.fetchPestDiseases()
  },
  methods: {
    async fetchPestDiseases() {
      try {
        this.loading = true
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize,
          search: this.searchQuery,
          type: this.selectedType,
          sortBy: this.sortBy
        }

        const response = await getPestDiseases(params)
        this.pestDiseases = response.data.data.list || []
        this.totalPestDiseases = response.data.data.total || 0
      } catch (error) {
        console.error('获取病虫害列表失败:', error)
        this.$message.error('获取病虫害列表失败')
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.currentPage = 1
      this.fetchPestDiseases()
    },

    handleTypeChange() {
      this.currentPage = 1
      this.fetchPestDiseases()
    },

    handleSortChange() {
      this.currentPage = 1
      this.fetchPestDiseases()
    },

    handlePageChange(page) {
      this.currentPage = page
      this.fetchPestDiseases()
    },

    handleSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
      this.fetchPestDiseases()
    },

    viewPestDiseaseDetail(pestDisease) {
      this.$router.push({
        path: `/normal/pest-disease-detail/${pestDisease.id}`,
        query: { name: pestDisease.name }
      })
    },

    handleImageError(event) {
      // 图片加载失败时隐藏图片，显示占位符
      event.target.style.display = 'none'
      const placeholder = event.target.parentNode.querySelector('.pest-disease-image-placeholder')
      if (!placeholder) {
        const placeholderDiv = document.createElement('div')
        placeholderDiv.className = 'pest-disease-image-placeholder'
        placeholderDiv.innerHTML = '<i class="el-icon-picture-outline"></i><span>暂无图片</span>'
        event.target.parentNode.appendChild(placeholderDiv)
      }
    },

    getTypeBadgeClass(type) {
      return {
        'disease-badge': type === '病害',
        'pest-badge': type === '虫害'
      }
    },

    formatDate(dateString) {
      if (!dateString) return '未知'
      try {
        const date = new Date(dateString)
        return date.toLocaleDateString('zh-CN', {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit'
        })
      } catch (error) {
        return '未知'
      }
    }
  }
}
</script>

<style scoped>
.pest-diseases-container {
  padding: 0;
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  position: relative;
}

.pest-diseases-container::before {
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
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
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
.type-select,
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
.type-select >>> .el-input__inner,
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
.type-select >>> .el-input__inner:focus,
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

/* 病虫害网格 */
.pest-diseases-grid {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px 48px;
  min-height: 400px;
  position: relative;
  z-index: 1;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 32px;
}

/* 病虫害卡片 */
.pest-disease-card {
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

.pest-disease-card::before {
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

.pest-disease-card:hover::before {
  opacity: 1;
}

.pest-disease-card:hover {
  transform: translateY(-12px) scale(1.02);
  box-shadow:
    0 25px 50px rgba(102, 126, 234, 0.2),
    0 0 0 2px rgba(102, 126, 234, 0.3);
  border-color: #667eea;
}

.pest-disease-image-container {
  position: relative;
  height: 220px;
  overflow: hidden;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
}

.pest-disease-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  filter: brightness(1.1) contrast(1.05);
}

.pest-disease-card:hover .pest-disease-image {
  transform: scale(1.08) rotate(1deg);
  filter: brightness(1.2) contrast(1.1);
}

.pest-disease-image-placeholder {
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

.pest-disease-image-placeholder i {
  font-size: 3rem;
  margin-bottom: 8px;
  color: #cbd5e0;
}

.pest-disease-card:hover .pest-disease-image-placeholder {
  background: linear-gradient(135deg, #edf2f7 0%, #e2e8f0 100%);
  color: #718096;
}

.pest-disease-overlay {
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

.pest-disease-card:hover .pest-disease-overlay {
  opacity: 1;
}

.pest-disease-overlay >>> .el-button {
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

.pest-disease-overlay >>> .el-button:hover {
  transform: scale(1.1);
  background: white;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.3);
}

.type-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 700;
  color: white;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.disease-badge {
  background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
}

.pest-badge {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
}

.pest-disease-card:hover .type-badge {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

.pest-disease-content {
  padding: 16px;
}

.pest-disease-name {
  font-size: 1.3rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 12px 0;
  line-height: 1.3;
}

.pest-disease-description {
  margin: 0 0 16px 0;
  font-size: 0.9rem;
  line-height: 1.5;
  display: flex;
  align-items: flex-start;
}

.description-label {
  font-weight: 600;
  color: #2c3e50;
  width: 50px;
  flex-shrink: 0;
  margin-right: 8px;
}

.description-text {
  color: #606266;
  flex: 1;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.pest-disease-info {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 0.9rem;
  white-space: nowrap;
}

.info-item .label {
  font-weight: 600;
  color: #2c3e50;
  width: 80px;
  flex-shrink: 0;
  white-space: nowrap;
}

.info-item .value {
  color: #606266;
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.pest-disease-footer {
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
    flex-direction: column;
    gap: 8px;
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
  
  .pest-diseases-grid {
    padding: 0 20px 20px;
  }
}

@media (max-width: 480px) {
  .grid-container {
    grid-template-columns: 1fr;
  }
}
</style>
