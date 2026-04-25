// 药用植物病虫害管理系统 - 小程序入口文件
App({
  globalData: {
    userInfo: null,
    token: null,
    baseUrl: 'http://localhost:8080/api', // 后端API地址
    isLogin: false,
    userId: null,
    userType: 'normal'
  },

  onLaunch() {
    this.checkLogin()
    this.initApp()
  },

  // 检查登录状态
  checkLogin() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')

    if (token && userInfo) {
      this.globalData.token = token
      this.globalData.userInfo = userInfo
      this.globalData.isLogin = true
      this.globalData.userId = userInfo.id
    }
  },

  // 初始化应用
  initApp() {
    // 获取系统信息
    wx.getSystemInfo({
      success: (res) => {
        this.globalData.systemInfo = res
        this.globalData.statusBarHeight = res.statusBarHeight
        this.globalData.navBarHeight = res.statusBarHeight + 44
      }
    })
  },

  // 登录方法
  login(userInfo) {
    this.globalData.userInfo = userInfo
    this.globalData.isLogin = true
    this.globalData.userId = userInfo.id
    this.globalData.token = userInfo.token

    // 存储到本地
    wx.setStorageSync('userInfo', userInfo)
    wx.setStorageSync('token', userInfo.token)
  },

  // 退出登录
  logout() {
    // 清除全局数据
    this.globalData.userInfo = null
    this.globalData.isLogin = false
    this.globalData.userId = null
    this.globalData.token = null

    // 清除本地存储
    wx.removeStorageSync('userInfo')
    wx.removeStorageSync('token')

    // 跳转到登录页
    wx.reLaunch({
      url: '/pages/login/login'
    })
  },

  // 网络请求封装
  request(options) {
    return new Promise((resolve, reject) => {
      wx.request({
        url: this.globalData.baseUrl + options.url,
        method: options.method || 'GET',
        data: options.data || {},
        header: {
          'Content-Type': 'application/json',
          'Authorization': this.globalData.token ? `Bearer ${this.globalData.token}` : '',
          'User-Id': this.globalData.userId || '',
          ...options.header
        },
        success: (res) => {
          if (res.statusCode === 200) {
            // 无论成功还是失败都返回数据，让调用方自己处理
            resolve(res.data)
          } else {
            wx.showToast({
              title: '网络请求失败',
              icon: 'none'
            })
            reject(res)
          }
        },
        fail: (err) => {
          wx.showToast({
            title: '网络连接失败',
            icon: 'none'
          })
          reject(err)
        }
      })
    })
  },

  // 显示加载提示
  showLoading(title = '加载中...') {
    wx.showLoading({
      title: title,
      mask: true
    })
  },

  // 隐藏加载提示
  hideLoading() {
    wx.hideLoading()
  },

  // 显示成功提示
  showSuccess(title) {
    wx.showToast({
      title: title,
      icon: 'success'
    })
  },

  // 显示错误提示
  showError(title) {
    wx.showToast({
      title: title,
      icon: 'none'
    })
  },

  // 处理图片URL的通用方法
  processImageUrl(imageUrl) {
    if (!imageUrl) {
      return '';
    }

    // 如果已经是完整的URL，直接返回
    if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
      return imageUrl;
    }

    // 如果是相对路径，需要拼接完整的URL
    let processedUrl = '';
    if (imageUrl.startsWith('/api/')) {
      processedUrl = 'http://localhost:8080' + imageUrl;
    } else if (imageUrl.startsWith('/files/')) {
      processedUrl = 'http://localhost:8080/api' + imageUrl;
    } else if (imageUrl.startsWith('files/')) {
      processedUrl = 'http://localhost:8080/api/' + imageUrl;
    } else {
      // 默认情况下，假设是相对于API根路径的
      processedUrl = this.globalData.baseUrl + '/' + imageUrl;
    }

    return processedUrl;
  }
})
