// 个人中心页面逻辑
const app = getApp()

Page({
  data: {
    userInfo: null,
    stats: {
      commentCount: 0,
      helpCount: 0
    }
  },

  onLoad() {
    this.loadUserData()
  },

  onShow() {
    // 每次显示页面时刷新用户数据
    this.loadUserData()

    // 强制刷新用户信息（特别是从编辑页面返回时）
    this.refreshUserInfo()
  },

  // 加载用户数据
  loadUserData() {
    const userInfo = app.globalData.userInfo

    this.setData({
      userInfo: userInfo
    })

    // 如果已登录，加载统计数据
    if (userInfo && app.globalData.isLogin) {
      this.loadUserStats()
    }
  },

  // 强制刷新用户信息
  refreshUserInfo() {
    try {
      // 从本地存储重新读取用户信息
      const storedUserInfo = wx.getStorageSync('userInfo')
      if (storedUserInfo) {
        // 更新全局用户信息
        app.globalData.userInfo = storedUserInfo

        // 更新页面显示
        this.setData({
          userInfo: storedUserInfo
        })
      }

      // 也检查全局用户信息
      if (app.globalData.userInfo) {
        this.setData({
          userInfo: app.globalData.userInfo
        })
      }

    } catch (error) {
      // 刷新用户信息失败
    }
  },

  // 加载用户统计数据
  async loadUserStats() {
    try {
      const app = getApp()

      // 并发请求评论统计和求助统计
      const [commentStatsRes, helpStatsRes] = await Promise.all([
        // 获取我的评论统计
        app.request({
          url: '/normal/my-comments-stats'
        }).catch(error => {
          return { data: { total: 0 } }
        }),

        // 获取我的求助统计
        app.request({
          url: '/normal/my-help-stats'
        }).catch(error => {
          return { data: { total: 0 } }
        })
      ])

      const stats = {
        commentCount: commentStatsRes.data.total || 0,
        helpCount: helpStatsRes.data.total || 0
      }

      this.setData({ stats })

    } catch (error) {
      // 出错时使用默认值
      this.setData({
        stats: {
          commentCount: 0,
          helpCount: 0
        }
      })
    }
  },

  // 跳转到登录页
  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  // 编辑个人资料
  editProfile() {
    if (!this.checkLogin()) {
      return
    }

    wx.navigateTo({
      url: '/pages/edit-profile/edit-profile',
      fail: (error) => {
        wx.showToast({
          title: '跳转失败',
          icon: 'error'
        })
      }
    })
  },

  // 跳转到我的评论
  goToMyComments() {
    if (!this.checkLogin()) return

    wx.navigateTo({
      url: '/pages/my-comments/my-comments'
    })
  },

  // 跳转到编辑个人信息页面
  goToEditProfile() {
    if (!this.checkLogin()) {
      return
    }

    wx.navigateTo({
      url: '/pages/edit-profile/edit-profile',
      fail: (error) => {
        wx.showToast({
          title: '跳转失败',
          icon: 'error'
        })
      }
    })
  },

  // 跳转到我的求助
  goToMyHelp() {
    if (!this.checkLogin()) {
      return
    }

    // 因为help页面在tabBar中，使用switchTab而不是navigateTo
    wx.switchTab({
      url: '/pages/help/help',
      fail: (error) => {
        wx.showToast({
          title: '跳转失败',
          icon: 'none'
        })
      }
    })
  },

  // 退出登录
  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.logout()
          this.setData({
            userInfo: null,
            stats: {
              commentCount: 0,
              helpCount: 0
            }
          })
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          })
        }
      }
    })
  },

  // 检查登录状态
  checkLogin() {
    // 优先检查页面的用户信息，其次检查全局用户信息
    const hasUserInfo = this.data.userInfo || app.globalData.userInfo

    if (!hasUserInfo) {
      wx.showModal({
        title: '提示',
        content: '请先登录后再使用此功能',
        confirmText: '去登录',
        success: (res) => {
          if (res.confirm) {
            this.goToLogin()
          }
        }
      })
      return false
    }

    return true
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadUserData()
    wx.stopPullDownRefresh()
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '药用植物助手 - 专业的植物病虫害管理平台',
      path: '/pages/index/index'
    }
  }
})