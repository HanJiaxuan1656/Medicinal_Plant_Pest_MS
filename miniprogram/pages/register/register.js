// 注册页面逻辑
const app = getApp()

Page({
  data: {
    registerForm: {
      username: '',
      password: '',
      confirmPassword: '',
      email: ''
    },
    showPassword: false,
    showConfirmPassword: false,
    registering: false,
    canRegister: false,
    passwordStrength: 0,
    passwordStrengthText: '',
    formErrors: []
  },

  onLoad() {
    // 如果已经登录，直接返回
    if (app.globalData.isLogin) {
      wx.navigateBack()
    }
  },

  // 用户名输入
  onUsernameInput(e) {
    this.setData({
      'registerForm.username': e.detail.value
    })
    this.validateForm()
  },

  // 密码输入
  onPasswordInput(e) {
    const password = e.detail.value
    this.setData({
      'registerForm.password': password
    })
    this.checkPasswordStrength(password)
    this.validateForm()
  },

  // 确认密码输入
  onConfirmPasswordInput(e) {
    this.setData({
      'registerForm.confirmPassword': e.detail.value
    })
    this.validateForm()
  },

  // 邮箱输入
  onEmailInput(e) {
    this.setData({
      'registerForm.email': e.detail.value
    })
    this.validateForm()
  },

  // 切换密码显示
  togglePassword() {
    this.setData({
      showPassword: !this.data.showPassword
    })
  },

  // 切换确认密码显示
  toggleConfirmPassword() {
    this.setData({
      showConfirmPassword: !this.data.showConfirmPassword
    })
  },

  // 检查密码强度
  checkPasswordStrength(password) {
    let strength = 0
    let strengthText = ''

    if (password.length >= 6) {
      strength++
      
      // 包含数字
      if (/\d/.test(password)) {
        strength++
      }
      
      // 包含字母
      if (/[a-zA-Z]/.test(password)) {
        strength++
      }
      
      // 包含特殊字符
      if (/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
        strength = Math.min(strength + 1, 3)
      }
    }

    switch (strength) {
      case 0:
      case 1:
        strengthText = '弱'
        break
      case 2:
        strengthText = '中'
        break
      case 3:
        strengthText = '强'
        break
    }

    this.setData({
      passwordStrength: strength,
      passwordStrengthText: strengthText
    })
  },

  // 表单验证
  validateForm() {
    const { username, password, confirmPassword, email } = this.data.registerForm
    const errors = []

    // 用户名验证
    if (username.length > 0 && username.length < 3) {
      errors.push('用户名至少需要3个字符')
    }
    if (username.length > 20) {
      errors.push('用户名不能超过20个字符')
    }
    if (username.length > 0 && !/^[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(username)) {
      errors.push('用户名只能包含字母、数字、下划线和中文')
    }

    // 密码验证
    if (password.length > 0 && password.length < 6) {
      errors.push('密码至少需要6个字符')
    }
    if (password.length > 20) {
      errors.push('密码不能超过20个字符')
    }

    // 确认密码验证
    if (confirmPassword.length > 0 && password !== confirmPassword) {
      errors.push('两次输入的密码不一致')
    }

    // 邮箱验证（如果填写了）
    if (email.length > 0 && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      errors.push('邮箱格式不正确')
    }

    // 检查是否可以注册
    const canRegister = username.length >= 3 && 
                       password.length >= 6 && 
                       password === confirmPassword && 
                       errors.length === 0

    this.setData({
      formErrors: errors,
      canRegister: canRegister
    })
  },

  // 注册
  async onRegister() {
    if (!this.data.canRegister || this.data.registering) {
      return
    }

    const { username, password, email } = this.data.registerForm

    try {
      this.setData({ registering: true })

      const response = await app.request({
        url: '/auth/normal/register',
        method: 'POST',
        data: {
          username: username.trim(),
          password: password.trim(),
          email: email.trim() || null
        }
      })

      if (response.code === 1) {
        // 注册成功
        wx.showToast({
          title: '注册成功',
          icon: 'success'
        })

        setTimeout(() => {
          // 跳转到登录页面，并传递注册成功的用户名
          wx.redirectTo({
            url: `/pages/login/login?username=${encodeURIComponent(username.trim())}`
          })
        }, 1500)
      } else {
        throw new Error(response.message || '注册失败')
      }

    } catch (error) {
      wx.showToast({
        title: error.message || '注册失败，请重试',
        icon: 'none'
      })
    } finally {
      this.setData({ registering: false })
    }
  },

  // 跳转到登录页
  goToLogin() {
    wx.navigateBack()
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
