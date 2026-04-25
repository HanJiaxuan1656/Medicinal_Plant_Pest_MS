// 农药列表页面
Page({
  data: {
    loading: false,
    pesticides: [],
    searchKeyword: '',
    selectedCategory: '',
    selectedCategoryText: '全部',
    dropdownOpen: false,
    pesticideCategories: ['杀虫剂', '杀菌剂', '除草剂', '植物生长调节剂'],
    sortBy: 'latest',
    currentPage: 1,
    pageSize: 10,
    hasMore: true
  },

  onLoad(options) {
    // 处理从其他页面传来的参数
    if (options.category) {
      this.setData({
        selectedCategory: decodeURIComponent(options.category)
      })
    }

    this.loadPesticides(1, true)
  },

  onShow() {
    // 页面显示时刷新数据
    this.loadPesticides(1, true)
  },

  // 加载农药数据
  async loadPesticides(page = 1, reset = false) {
    if (this.data.loading) return

    try {
      this.setData({ loading: true })

      const app = getApp()
      const response = await app.request({
        url: '/normal/pesticides',
        data: {
          page: page,
          pageSize: this.data.pageSize,
          search: this.data.searchKeyword,
          category: this.data.selectedCategory,
          sortBy: this.data.sortBy
        }
      })

      const newPesticides = response.data.list || []

      // 处理农药数据
      newPesticides.forEach(pesticide => {
        // 处理图片URL
        if (pesticide.imageUrl) {
          pesticide.imageUrl = app.processImageUrl(pesticide.imageUrl)
        }

        // 格式化创建时间
        if (pesticide.createdAt) {
          pesticide.createdAt = this.formatDate(pesticide.createdAt)
        }

        // 处理使用说明长度
        if (pesticide.usageInstructions && pesticide.usageInstructions.length > 60) {
          pesticide.usageInstructions = pesticide.usageInstructions.substring(0, 60) + '...'
        }
      })

      this.setData({
        pesticides: reset ? newPesticides : [...this.data.pesticides, ...newPesticides],
        currentPage: page,
        hasMore: newPesticides.length === this.data.pageSize,
        loading: false
      })

    } catch (error) {
      console.error('加载农药列表失败:', error)
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
    this.loadPesticides(1, true)
  },

  // 切换下拉框显示状态
  toggleDropdown(e) {
    e && e.stopPropagation && e.stopPropagation(); // 阻止事件冒泡

    this.setData({
      dropdownOpen: !this.data.dropdownOpen
    });
  },

  // 选择农药类别
  onCategorySelect(e) {
    const { value, text } = e.currentTarget.dataset

    this.setData({
      selectedCategory: value,
      selectedCategoryText: text,
      dropdownOpen: false,
      currentPage: 1
    })

    this.loadPesticides(1, true)
  },

  // 关闭下拉框
  closeDropdown(e) {
    if (this.data.dropdownOpen) {
      this.setData({
        dropdownOpen: false
      });
    }
  },

  // 阻止事件冒泡
  stopPropagation() {
    // 阻止点击下拉选项区域时关闭下拉框
  },

  // 选择排序
  selectSort(e) {
    const sort = e.currentTarget.dataset.sort
    this.setData({
      sortBy: sort
    })
    this.loadPesticides(1, true)
  },

  // 跳转到农药详情
  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/pesticide-detail/pesticide-detail?id=${id}`
    })
  },

  // 图片加载错误处理
  onImageError(e) {
    const index = e.currentTarget.dataset.index
    const pesticides = this.data.pesticides
    if (pesticides[index]) {
      pesticides[index].imageUrl = ''
      this.setData({
        pesticides: pesticides
      })
    }
  },

  // 加载更多
  loadMore() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadPesticides(this.data.currentPage + 1, false)
    }
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

  // 下拉刷新
  onPullDownRefresh() {
    this.loadPesticides(1, true).then(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 上拉加载更多
  onReachBottom() {
    this.loadMore()
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '农药信息 - 药用植物助手',
      path: '/pages/pesticides/pesticides'
    }
  }
})