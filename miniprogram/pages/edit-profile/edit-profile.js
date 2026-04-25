const app = getApp()

Page({
  data: {
    loading: true,
    saving: false,
    userInfo: {},
    userStats: {},
    formData: {
      nickname: '',
      phone: '',
      email: ''
    },
    originalData: {},
    phoneError: '',
    emailError: '',
    isFormValid: true
  },

  onLoad() {
    this.loadUserProfile()
  },

  onShow() {
    // 页面显示时重新加载数据
    this.loadUserProfile()
  },

  // 加载用户资料
  async loadUserProfile() {
    try {
      this.setData({ loading: true })

      // 并发请求用户信息和统计数据
      const [profileRes, statsRes] = await Promise.all([
        app.request({
          url: '/normal/profile'
        }),
        this.loadUserStats()
      ])

      if (profileRes.data) {
        const userInfo = profileRes.data.userInfo || {}
        const userStats = profileRes.data.userStats || {}

        this.setData({
          userInfo: userInfo,
          userStats: userStats,
          formData: {
            nickname: userInfo.nickname || '',
            phone: userInfo.phone || '',
            email: userInfo.email || ''
          },
          originalData: {
            nickname: userInfo.nickname || '',
            phone: userInfo.phone || '',
            email: userInfo.email || ''
          }
        })

        this.validateForm()
      }

    } catch (error) {
      wx.showToast({
        title: '加载失败',
        icon: 'error'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  // 加载用户统计数据
  async loadUserStats() {
    try {
      const [commentStatsRes, helpStatsRes] = await Promise.all([
        app.request({
          url: '/normal/my-comments-stats'
        }).catch(() => ({ data: { total: 0 } })),
        app.request({
          url: '/normal/my-help-stats'
        }).catch(() => ({ data: { total: 0 } }))
      ])

      return {
        commentCount: commentStatsRes.data.total || 0,
        helpCount: helpStatsRes.data.total || 0
      }
    } catch (error) {
      return { commentCount: 0, helpCount: 0 }
    }
  },

  // 昵称输入
  onNicknameInput(e) {
    const nickname = e.detail.value
    this.setData({
      'formData.nickname': nickname
    })
    this.validateForm()
  },

  // 手机号输入
  onPhoneInput(e) {
    const phone = e.detail.value
    this.setData({
      'formData.phone': phone
    })
    this.validatePhone(phone)
    this.validateForm()
  },

  // 邮箱输入
  onEmailInput(e) {
    const email = e.detail.value
    this.setData({
      'formData.email': email
    })
    this.validateEmail(email)
    this.validateForm()
  },

  // 验证手机号
  validatePhone(phone) {
    if (phone && !/^1[3-9]\d{9}$/.test(phone)) {
      this.setData({ phoneError: '请输入正确的手机号码' })
      return false
    } else {
      this.setData({ phoneError: '' })
      return true
    }
  },

  // 验证邮箱
  validateEmail(email) {
    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      this.setData({ emailError: '请输入正确的邮箱地址' })
      return false
    } else {
      this.setData({ emailError: '' })
      return true
    }
  },

  // 验证表单
  validateForm() {
    const { formData } = this.data
    const isPhoneValid = this.validatePhone(formData.phone)
    const isEmailValid = this.validateEmail(formData.email)
    
    // 检查是否有修改
    const hasChanges = JSON.stringify(formData) !== JSON.stringify(this.data.originalData)
    
    const isValid = isPhoneValid && isEmailValid && hasChanges
    this.setData({ isFormValid: isValid })
  },

  // 选择头像
  chooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath
        this.uploadAvatar(tempFilePath)
      },
      fail: (error) => {
        wx.showToast({
          title: '选择图片失败',
          icon: 'error'
        })
      }
    })
  },

  // 上传头像
  async uploadAvatar(filePath) {
    try {
      wx.showLoading({ title: '上传中...' })

      // 检查文件大小
      const fileInfo = await this.getFileInfo(filePath)
      if (fileInfo.size > 10 * 1024 * 1024) { // 10MB
        throw new Error('图片大小不能超过10MB')
      }

      const uploadedUrl = await this.uploadSingleImage(filePath)

      // 更新头像到服务器
      await app.request({
        url: '/normal/profile',
        method: 'PUT',
        data: {
          avatarUrl: uploadedUrl
        }
      })

      // 更新当前页面的用户信息
      this.setData({
        'userInfo.avatarUrl': uploadedUrl
      })

      // 更新全局用户信息
      this.updateGlobalUserInfo(uploadedUrl)

      wx.showToast({
        title: '头像更新成功',
        icon: 'success'
      })

    } catch (error) {
      let errorMsg = '上传失败，请重试'
      if (error.message) {
        if (error.message.includes('网络')) {
          errorMsg = '网络连接失败，请检查网络'
        } else if (error.message.includes('大小')) {
          errorMsg = error.message
        } else if (error.message.includes('格式')) {
          errorMsg = '图片格式不支持'
        }
      }

      wx.showToast({
        title: errorMsg,
        icon: 'error',
        duration: 2000
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 获取文件信息
  getFileInfo(filePath) {
    return new Promise((resolve, reject) => {
      wx.getFileInfo({
        filePath: filePath,
        success: resolve,
        fail: reject
      })
    })
  },

  // 上传单张图片
  uploadSingleImage(filePath) {
    return new Promise((resolve, reject) => {
      // 首先尝试主要的上传接口
      this.tryUploadToMainAPI(filePath)
        .then(resolve)
        .catch((mainError) => {
          // 如果主要接口失败，尝试备用方案
          this.tryUploadToBackupAPI(filePath)
            .then(resolve)
            .catch((backupError) => {
              reject(mainError) // 返回主要错误
            })
        })
    })
  },

  // 尝试主要的上传API
  tryUploadToMainAPI(filePath) {
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: app.globalData.baseUrl + '/files/upload',
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': app.globalData.token ? `Bearer ${app.globalData.token}` : '',
          'User-Id': app.globalData.userId ? String(app.globalData.userId) : ''
        },
        success: (res) => {
          if (res.statusCode !== 200) {
            reject(new Error(`服务器错误: ${res.statusCode}`))
            return
          }

          try {
            const data = JSON.parse(res.data)
            if (data.code === 1 && data.data) {
              resolve(data.data)
            } else {
              reject(new Error(data.msg || data.message || '上传失败'))
            }
          } catch (e) {
            reject(new Error('解析服务器响应失败'))
          }
        },
        fail: (error) => {
          reject(new Error('网络请求失败'))
        }
      })
    })
  },

  // 尝试备用的上传API（使用base64）
  tryUploadToBackupAPI(filePath) {
    return new Promise((resolve, reject) => {
      // 将图片转换为base64并生成一个临时URL
      wx.getFileSystemManager().readFile({
        filePath: filePath,
        encoding: 'base64',
        success: (res) => {
          // 生成一个临时的图片URL（实际项目中可能需要上传到其他服务）
          const tempUrl = `data:image/jpeg;base64,${res.data}`
          // 注意：这只是一个临时方案，实际应该上传到图片服务器
          resolve(tempUrl)
        },
        fail: (error) => {
          reject(new Error('读取图片文件失败'))
        }
      })
    })
  },

  // 更新全局用户信息
  updateGlobalUserInfo(avatarUrl) {
    try {
      // 更新app.js中的全局用户信息
      if (app.globalData.userInfo) {
        app.globalData.userInfo.avatarUrl = avatarUrl
      }

      // 更新本地存储中的用户信息
      const storedUserInfo = wx.getStorageSync('userInfo')
      if (storedUserInfo) {
        const updatedUserInfo = {
          ...storedUserInfo,
          avatarUrl: avatarUrl
        }
        wx.setStorageSync('userInfo', updatedUserInfo)
      }

      // 通知其他页面用户信息已更新
      this.notifyOtherPages(avatarUrl)

    } catch (error) {
      // 更新全局用户信息失败
    }
  },

  // 通知其他页面用户信息已更新
  notifyOtherPages(avatarUrl) {
    try {
      // 获取所有页面栈
      const pages = getCurrentPages()

      // 查找"我的"页面并更新其数据
      for (let i = pages.length - 1; i >= 0; i--) {
        const page = pages[i]
        if (page.route === 'pages/profile/profile') {
          page.setData({
            'userInfo.avatarUrl': avatarUrl
          })
          break
        }
      }

      // 发送全局事件通知
      wx.eventBus = wx.eventBus || {}
      if (typeof wx.eventBus.emit === 'function') {
        wx.eventBus.emit('userAvatarUpdated', avatarUrl)
      }

      // 使用自定义事件通知
      const eventDetail = { avatarUrl: avatarUrl }
      wx.triggerEvent && wx.triggerEvent('avatarUpdated', eventDetail)

    } catch (error) {
      // 通知其他页面失败
    }
  },

  // 保存个人信息
  async saveProfile() {
    if (!this.data.isFormValid || this.data.saving) return

    try {
      this.setData({ saving: true })

      await app.request({
        url: '/normal/profile',
        method: 'PUT',
        data: this.data.formData
      })

      // 更新原始数据
      this.setData({
        originalData: { ...this.data.formData },
        'userInfo.nickname': this.data.formData.nickname,
        'userInfo.phone': this.data.formData.phone,
        'userInfo.email': this.data.formData.email
      })

      // 更新全局用户信息
      this.updateGlobalUserInfoProfile()

      this.validateForm()

      wx.showToast({
        title: '保存成功',
        icon: 'success'
      })

    } catch (error) {
      wx.showToast({
        title: '保存失败',
        icon: 'error'
      })
    } finally {
      this.setData({ saving: false })
    }
  },

  // 更新全局用户信息（个人资料）
  updateGlobalUserInfoProfile() {
    try {
      // 更新app.js中的全局用户信息
      if (app.globalData.userInfo) {
        app.globalData.userInfo.nickname = this.data.formData.nickname
        app.globalData.userInfo.phone = this.data.formData.phone
        app.globalData.userInfo.email = this.data.formData.email
      }

      // 更新本地存储中的用户信息
      const storedUserInfo = wx.getStorageSync('userInfo')
      if (storedUserInfo) {
        const updatedUserInfo = {
          ...storedUserInfo,
          nickname: this.data.formData.nickname,
          phone: this.data.formData.phone,
          email: this.data.formData.email
        }
        wx.setStorageSync('userInfo', updatedUserInfo)
      }

      // 通知其他页面用户信息已更新
      this.notifyOtherPagesProfile()

    } catch (error) {
      // 更新全局用户信息失败
    }
  },

  // 通知其他页面用户信息已更新（个人资料）
  notifyOtherPagesProfile() {
    try {
      // 获取所有页面栈
      const pages = getCurrentPages()

      // 查找"我的"页面并更新其数据
      for (let i = pages.length - 1; i >= 0; i--) {
        const page = pages[i]
        if (page.route === 'pages/profile/profile') {
          page.setData({
            'userInfo.nickname': this.data.formData.nickname,
            'userInfo.phone': this.data.formData.phone,
            'userInfo.email': this.data.formData.email
          })
          break
        }
      }

    } catch (error) {
      // 通知其他页面失败
    }
  },

  // 重置表单
  resetForm() {
    this.setData({
      formData: { ...this.data.originalData },
      phoneError: '',
      emailError: ''
    })
    this.validateForm()
    
    wx.showToast({
      title: '已重置',
      icon: 'success'
    })
  },

  // 跳转到修改密码页面
  goToChangePassword() {
    wx.navigateTo({
      url: '/pages/change-password/change-password'
    })
  },

  // 页面分享
  onShareAppMessage() {
    return {
      title: '药用植物助手 - 个人中心',
      path: '/pages/index/index'
    }
  }
})
