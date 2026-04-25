// 首页逻辑
const app = getApp()

Page({
  data: {
    loading: true,
    hotPlants: [],
    recentPests: [],
    recentPesticides: [],
    recentHelps: []
  },

  onLoad() {
    this.loadHomeData()
  },

  onShow() {
    // 每次显示页面时刷新数据
    this.loadHomeData()
  },

  // 加载首页数据
  async loadHomeData() {
    try {
      this.setData({ loading: true })

      // 并发请求多个接口
      const [plantsRes, pestsRes, pesticidesRes, helpsRes] = await Promise.all([
        this.getHotPlants(),
        this.getRecentPests(),
        this.getRecentPesticides(),
        this.getRecentHelps()
      ])

      // 处理热门植物的图片URL
      const hotPlants = (plantsRes.data.list || []).map(plant => ({
        ...plant,
        imageUrl: app.processImageUrl(plant.imageUrl)
      }));

      // 处理病虫害的图片URL
      const recentPests = (pestsRes.data.list || []).map(pest => ({
        ...pest,
        imageUrl: app.processImageUrl(pest.imageUrl)
      }));

      // 处理农药的图片URL和描述
      const recentPesticides = (pesticidesRes.data.list || []).map(pesticide => ({
        ...pesticide,
        imageUrl: app.processImageUrl(pesticide.imageUrl),
        usageInstructions: pesticide.usageInstructions && pesticide.usageInstructions.length > 40
          ? pesticide.usageInstructions.substring(0, 40) + '...'
          : pesticide.usageInstructions
      }));

      this.setData({
        hotPlants: hotPlants,
        recentPests: recentPests,
        recentPesticides: recentPesticides,
        recentHelps: helpsRes.data.list || [],
        loading: false
      })
    } catch (error) {
      console.error('加载首页数据失败:', error)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  // 获取热门植物
  getHotPlants() {
    return app.request({
      url: '/normal/plants',
      data: {
        page: 1,
        pageSize: 6,
        sortBy: 'viewCount'
      }
    })
  },

  // 获取最新病虫害
  getRecentPests() {
    return app.request({
      url: '/normal/pest-diseases',
      data: {
        page: 1,
        pageSize: 4,
        sortBy: 'latest'
      }
    })
  },

  // 获取推荐农药
  getRecentPesticides() {
    return app.request({
      url: '/normal/pesticides',
      data: {
        page: 1,
        pageSize: 4,
        sortBy: 'latest'
      }
    })
  },

  // 获取最新求助
  getRecentHelps() {
    return app.request({
      url: '/normal/help-requests',
      data: {
        page: 1,
        pageSize: 3,
        status: 'approved'
      }
    })
  },

  // 跳转到植物列表
  goToPlants() {
    wx.switchTab({
      url: '/pages/plants/plants'
    })
  },

  // 跳转到病虫害列表
  goToPests() {
    wx.switchTab({
      url: '/pages/pests/pests'
    })
  },

  // 跳转到农药列表
  goToPesticides() {
    wx.navigateTo({
      url: '/pages/pesticides/pesticides'
    })
  },

  // 跳转到求助中心
  goToHelp() {
    wx.switchTab({
      url: '/pages/help/help'
    })
  },

  // 跳转到植物详情
  goToPlantDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/plant-detail/plant-detail?id=${id}`
    })
  },

  // 跳转到病虫害详情
  goToPestDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/pest-detail/pest-detail?id=${id}`
    })
  },

  // 跳转到农药详情
  goToPesticideDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/pesticide-detail/pesticide-detail?id=${id}`
    })
  },

  // 跳转到求助详情
  goToHelpDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/help-detail/help-detail?id=${id}`
    })
  },

  // 跳转到创建求助
  goToHelpCreate() {
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

  // 下拉刷新
  onPullDownRefresh() {
    this.loadHomeData().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '药用植物助手 - 专业的植物病虫害管理平台',
      path: '/pages/index/index'
    }
  },

  onShareTimeline() {
    return {
      title: '药用植物助手 - 专业的植物病虫害管理平台'
    }
  }
})
