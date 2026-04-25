const app = getApp()

Page({
  data: {
    loading: false,
    changing: false,
    passwordForm: {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    },
    showCurrentPassword: false,
    showNewPassword: false,
    showConfirmPassword: false,
    newPasswordError: '',
    confirmPasswordError: '',
    isFormValid: false,
    // 密码强度相关
    strengthLevel: 'weak',
    strengthText: '弱',
    hasLength: false,
    hasNumber: false,
    hasLetter: false
  },

  // 当前密码输入
  onCurrentPasswordInput(e) {
    this.setData({
      'passwordForm.currentPassword': e.detail.value
    })
    this.validateForm()
  },

  // 新密码输入
  onNewPasswordInput(e) {
    const newPassword = e.detail.value
    this.setData({
      'passwordForm.newPassword': newPassword
    })
    this.validateNewPassword(newPassword)
    this.checkPasswordStrength(newPassword)
    this.validateForm()
  },

  // 确认密码输入
  onConfirmPasswordInput(e) {
    const confirmPassword = e.detail.value
    this.setData({
      'passwordForm.confirmPassword': confirmPassword
    })
    this.validateConfirmPassword(confirmPassword)
    this.validateForm()
  },

  // 切换当前密码显示
  toggleCurrentPassword() {
    this.setData({
      showCurrentPassword: !this.data.showCurrentPassword
    })
  },

  // 切换新密码显示
  toggleNewPassword() {
    this.setData({
      showNewPassword: !this.data.showNewPassword
    })
  },

  // 切换确认密码显示
  toggleConfirmPassword() {
    this.setData({
      showConfirmPassword: !this.data.showConfirmPassword
    })
  },

  // 验证新密码
  validateNewPassword(password) {
    if (password.length < 6) {
      this.setData({ newPasswordError: '密码长度至少为6个字符' })
      return false
    } else {
      this.setData({ newPasswordError: '' })
      return true
    }
  },

  // 验证确认密码
  validateConfirmPassword(confirmPassword) {
    if (confirmPassword !== this.data.passwordForm.newPassword) {
      this.setData({ confirmPasswordError: '两次输入的密码不一致' })
      return false
    } else {
      this.setData({ confirmPasswordError: '' })
      return true
    }
  },

  // 检查密码强度
  checkPasswordStrength(password) {
    const hasLength = password.length >= 6
    const hasNumber = /\d/.test(password)
    const hasLetter = /[a-zA-Z]/.test(password)
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(password)

    this.setData({
      hasLength,
      hasNumber,
      hasLetter
    })

    let strengthLevel = 'weak'
    let strengthText = '弱'

    if (hasLength && hasNumber && hasLetter) {
      if (hasSpecial && password.length >= 8) {
        strengthLevel = 'strong'
        strengthText = '强'
      } else {
        strengthLevel = 'medium'
        strengthText = '中'
      }
    }

    this.setData({
      strengthLevel,
      strengthText
    })
  },

  // 验证表单
  validateForm() {
    const { passwordForm } = this.data
    const hasCurrentPassword = passwordForm.currentPassword.length > 0
    const isNewPasswordValid = this.validateNewPassword(passwordForm.newPassword)
    const isConfirmPasswordValid = this.validateConfirmPassword(passwordForm.confirmPassword)

    const isValid = hasCurrentPassword && isNewPasswordValid && isConfirmPasswordValid
    this.setData({ isFormValid: isValid })
  },

  // 修改密码
  async changePassword() {
    if (!this.data.isFormValid || this.data.changing) return

    try {
      this.setData({ changing: true })

      const { passwordForm } = this.data
      const response = await app.request({
        url: '/normal/change-password',
        method: 'PUT',
        data: {
          oldPassword: passwordForm.currentPassword,
          newPassword: passwordForm.newPassword
        }
      })

      // 检查响应状态
      if (response.code === 1) {
        // 修改成功
        wx.showModal({
          title: '修改成功',
          content: '密码修改成功，请重新登录',
          showCancel: false,
          success: () => {
            // 调用app的logout方法，完整清除登录状态
            app.logout()
          }
        })
      } else {
        // 修改失败，显示错误信息
        const errorMsg = response.msg || '密码修改失败'
        wx.showToast({
          title: errorMsg,
          icon: 'error',
          duration: 2000
        })
      }

    } catch (error) {

      let errorMsg = '修改密码失败'
      if (error.data && error.data.msg) {
        errorMsg = error.data.msg
      } else if (error.msg) {
        errorMsg = error.msg
      } else if (error.message) {
        errorMsg = error.message
      }

      wx.showToast({
        title: errorMsg,
        icon: 'error',
        duration: 2000
      })
    } finally {
      this.setData({ changing: false })
    }
  },

  // 页面分享
  onShareAppMessage() {
    return {
      title: '药用植物助手',
      path: '/pages/index/index'
    }
  }
})
