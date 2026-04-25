// 登录页面逻辑
const app = getApp()

Page({
  data: {
    loginForm: {
      username: '',
      password: ''
    },
    showPassword: false,
    logging: false,
    canLogin: false
  },

  onLoad(options) {
    // 如果从注册页面跳转过来，自动填充用户名
    if (options.username) {
      this.setData({
        'loginForm.username': decodeURIComponent(options.username)
      })
      this.checkCanLogin()

      // 显示注册成功提示
      wx.showToast({
        title: '注册成功，请登录',
        icon: 'success',
        duration: 2000
      })
    }
  },

  onShow() {
    // 每次显示页面时检查登录状态
    // 如果已经登录且不是从修改密码页面跳转过来，直接返回
    if (app.globalData.isLogin && app.globalData.userInfo) {
      this.handleLoginSuccess()
      return
    }
  },

  // 用户名输入
  onUsernameInput(e) {
    this.setData({
      'loginForm.username': e.detail.value
    })
    this.checkCanLogin()
  },

  // 密码输入
  onPasswordInput(e) {
    this.setData({
      'loginForm.password': e.detail.value
    })
    this.checkCanLogin()
  },

  // 切换密码显示
  togglePassword() {
    this.setData({
      showPassword: !this.data.showPassword
    })
  },

  // 检查是否可以登录
  checkCanLogin() {
    const { username, password } = this.data.loginForm
    const canLogin = username.trim().length > 0 && password.trim().length > 0
    this.setData({ canLogin })
  },



  // 表单登录
  async onFormLogin() {
    if (!this.data.canLogin || this.data.logging) {
      return
    }

    const { username, password } = this.data.loginForm

    try {
      this.setData({ logging: true })

      const response = await app.request({
        url: '/auth/normal/login',
        method: 'POST',
        data: {
          username: username.trim(),
          password: password.trim()
        }
      })

      if (response.code === 1) {
        // 登录成功
        const userData = response.data
        app.login(userData)

        wx.showToast({
          title: '登录成功',
          icon: 'success'
        })

        setTimeout(() => {
          this.handleLoginSuccess()
        }, 1500)
      } else {
        throw new Error(response.message || '登录失败')
      }

    } catch (error) {
      console.error('登录失败:', error)
      wx.showToast({
        title: error.message || '用户名或密码错误',
        icon: 'none'
      })
    } finally {
      this.setData({ logging: false })
    }
  },

  // 处理登录成功后的跳转
  handleLoginSuccess() {
    // 获取页面栈
    const pages = getCurrentPages()

    // 如果页面栈中有多个页面，说明是从其他页面跳转过来的
    if (pages.length > 1) {
      // 检查上一个页面是否是首页或主要功能页面
      const prevPage = pages[pages.length - 2]
      const prevRoute = prevPage.route

      // 如果上一个页面是首页、植物、病虫害等主要页面，则返回
      if (prevRoute.includes('index') ||
          prevRoute.includes('plants') ||
          prevRoute.includes('pests') ||
          prevRoute.includes('help') ||
          prevRoute.includes('profile')) {
        wx.navigateBack()
        return
      }
    }

    // 否则跳转到首页
    wx.switchTab({
      url: '/pages/index/index'
    })
  },

  // 跳转到注册页
  goToRegister() {
    wx.navigateTo({
      url: '/pages/register/register'
    })
  },

  // 游客模式
  enterAsGuest() {
    // 游客模式也使用相同的跳转逻辑
    this.handleLoginSuccess()
  },

  // 显示隐私政策
  showPrivacy() {
    wx.showModal({
      title: '用户协议与隐私政策',
      content: '感谢您使用药用植物助手。我们重视您的隐私，请仔细阅读我们的用户协议和隐私政策。',
      showCancel: false,
      confirmText: '我知道了'
    })
  },



  // 分享功能
  onShareAppMessage() {
    return {
      title: '药用植物助手 - 专业的植物病虫害管理平台',
      path: '/pages/index/index'
    }
  }
})