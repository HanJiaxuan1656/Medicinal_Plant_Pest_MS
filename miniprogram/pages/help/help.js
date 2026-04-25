// 求助中心页面逻辑
const app = getApp()

Page({
  data: {
    loading: false,
    helpRequests: [],
    selectedStatus: '',
    currentPage: 1,
    pageSize: 10,
    hasMore: true
  },

  onLoad() {
    this.loadHelpRequests(true)
  },

  onShow() {
    // 每次显示页面时刷新数据
    this.loadHelpRequests(true)
  },

  // 加载求助列表
  async loadHelpRequests(reset = false) {
    if (this.data.loading) return

    try {
      this.setData({ loading: true })

      const page = reset ? 1 : this.data.currentPage
      const response = await app.request({
        url: '/normal/help-requests',
        data: {
          page: page,
          pageSize: this.data.pageSize,
          status: this.data.selectedStatus
        }
      })

      const newHelps = response.data.list || []

      // 格式化数据
      newHelps.forEach(help => {
        if (help.createdAt) {
          help.createdAt = this.formatDate(help.createdAt)
        }
      })

      this.setData({
        helpRequests: reset ? newHelps : [...this.data.helpRequests, ...newHelps],
        currentPage: page,
        hasMore: newHelps.length === this.data.pageSize,
        loading: false
      })

    } catch (error) {
      console.error('加载求助列表失败:', error)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  // 筛选变化
  onFilterChange(e) {
    const value = e.currentTarget.dataset.value
    this.setData({
      selectedStatus: value,
      currentPage: 1
    })
    this.loadHelpRequests(true)
  },

  // 跳转到求助详情
  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/help-detail/help-detail?id=${id}`
    })
  },

  // 跳转到创建求助
  goToCreateHelp() {
    // 检查登录状态
    if (!app.globalData.isLogin) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再发起求助',
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login'
            })
          }
        }
      })
      return
    }

    wx.navigateTo({
      url: '/pages/help-create/help-create'
    })
  },

  // 预览图片
  previewImage(e) {
    const url = e.currentTarget.dataset.url
    wx.previewImage({
      urls: [url],
      current: url
    })
  },

  // 加载更多
  loadMore() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({
        currentPage: this.data.currentPage + 1
      })
      this.loadHelpRequests(false)
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
    this.loadHelpRequests(true).then(() => {
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
      title: '专家求助 - 药用植物问题专业解答',
      path: '/pages/help/help'
    }
  }
})