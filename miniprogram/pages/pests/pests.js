// 病虫害列表页面逻辑
const app = getApp()

Page({
  data: {
    loading: false,
    pests: [],
    searchKeyword: '',
    selectedType: '',
    sortBy: 'default',
    currentPage: 1,
    pageSize: 10,
    hasMore: true,
    showBackTop: false
  },

  onLoad(options) {
    this.loadPests(true)
  },

  onShow() {
    if (this.data.pests.length === 0) {
      this.loadPests(true)
    }
  },

  // 加载病虫害列表
  async loadPests(reset = false) {
    if (this.data.loading) return

    try {
      this.setData({ loading: true })

      const page = reset ? 1 : this.data.currentPage

      // 尝试从API加载数据
      try {
        const response = await app.request({
          url: '/normal/pest-diseases',
          data: {
            page: page,
            pageSize: this.data.pageSize,
            search: this.data.searchKeyword,
            type: this.data.selectedType,
            sortBy: this.data.sortBy
          }
        })

        const newPests = response.data.list || []

        // 格式化数据
        newPests.forEach(pest => {
          if (pest.createdAt) {
            pest.createdAt = this.formatDate(pest.createdAt)
          }
          // 确保图片路径正确
          if (pest.imageUrl && !pest.imageUrl.startsWith('http')) {
            // 如果是相对路径，添加服务器地址
            pest.imageUrl = app.globalData.baseUrl + pest.imageUrl
          }
        })

        this.setData({
          pests: reset ? newPests : [...this.data.pests, ...newPests],
          currentPage: page,
          hasMore: newPests.length === this.data.pageSize,
          loading: false
        })

      } catch (apiError) {
        console.error('API加载失败:', apiError)
        this.setData({
          loading: false,
          pests: reset ? [] : this.data.pests
        })
        wx.showToast({
          title: 'API加载失败',
          icon: 'none'
        })
      }

    } catch (error) {
      console.error('加载病虫害列表失败:', error)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  // 搜索输入
  onSearchInput(e) {
    this.setData({
      searchKeyword: e.detail.value
    })
  },

  // 执行搜索
  onSearch() {
    this.setData({
      currentPage: 1
    })
    this.loadPests(true)
  },

  // 筛选变化
  onFilterChange(e) {
    const { type, value } = e.currentTarget.dataset

    if (type === 'type') {
      this.setData({
        selectedType: value,
        currentPage: 1
      })
      this.loadPests(true)
    }
  },

  // 排序变化
  onSortChange(e) {
    const value = e.currentTarget.dataset.value
    this.setData({
      sortBy: value,
      currentPage: 1
    })
    this.loadPests(true)
  },

  // 加载更多
  loadMore() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({
        currentPage: this.data.currentPage + 1
      })
      this.loadPests(false)
    }
  },

  // 跳转到详情页
  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/pest-detail/pest-detail?id=${id}`
    })
  },

  // 图片加载失败
  onImageError(e) {
    console.log('图片加载失败:', e);

    // 获取失败的图片信息
    const dataset = e.currentTarget.dataset || e.target.dataset;
    if (dataset && dataset.index !== undefined) {
      const index = dataset.index;
      const pests = this.data.pests;
      if (pests[index]) {
        console.log('原始图片URL:', pests[index].imageUrl);
        // 尝试重新处理图片URL
        const processedUrl = app.processImageUrl(pests[index].imageUrl);
        if (processedUrl !== pests[index].imageUrl) {
          console.log('重新处理图片URL:', processedUrl);
          pests[index].imageUrl = processedUrl;
          this.setData({ pests });
        }
      }
    }
  },

  // 返回顶部
  backToTop() {
    wx.pageScrollTo({
      scrollTop: 0,
      duration: 300
    })
  },

  // 页面滚动
  onPageScroll(e) {
    const showBackTop = e.scrollTop > 500
    if (showBackTop !== this.data.showBackTop) {
      this.setData({
        showBackTop: showBackTop
      })
    }
  },

  // 触底加载更多
  onReachBottom() {
    this.loadMore()
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.setData({
      currentPage: 1
    })
    this.loadPests(true).then(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 格式化日期
  formatDate(dateString) {
    if (!dateString) return ''

    const date = new Date(dateString)
    const now = new Date()
    const diff = now - date

    if (diff < 24 * 60 * 60 * 1000) {
      const hours = Math.floor(diff / (60 * 60 * 1000))
      if (hours < 1) {
        const minutes = Math.floor(diff / (60 * 1000))
        return `${minutes}分钟前`
      }
      return `${hours}小时前`
    }

    const month = date.getMonth() + 1
    const day = date.getDate()
    return `${month}月${day}日`
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '病虫害防治大全 - 专业的病虫害知识库',
      path: '/pages/pests/pests'
    }
  }
})