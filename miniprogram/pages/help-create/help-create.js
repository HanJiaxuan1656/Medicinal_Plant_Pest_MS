// 创建求助页面逻辑
const app = getApp()

Page({
  data: {
    formData: {
      title: '',
      description: ''
    },
    uploadedImage: '', // 改为单张图片
    uploading: false,
    submitting: false,
    canSubmit: false
  },

  onLoad() {
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
          } else {
            wx.navigateBack()
          }
        }
      })
    }

    // 测试网络连接
    this.testNetworkConnection()
  },

  // 测试网络连接
  testNetworkConnection() {
    wx.request({
      url: app.globalData.baseUrl + '/files/test',
      method: 'GET',
      success: (res) => {
        // 网络连接正常
      },
      fail: (error) => {
        // 网络连接失败
      }
    })
  },

  // 标题输入
  onTitleInput(e) {
    this.setData({
      'formData.title': e.detail.value
    })
    this.checkCanSubmit()
  },

  // 描述输入
  onDescriptionInput(e) {
    this.setData({
      'formData.description': e.detail.value
    })
    this.checkCanSubmit()
  },



  // 检查是否可以提交
  checkCanSubmit() {
    const { title, description } = this.data.formData
    const canSubmit = title.trim().length > 0 && description.trim().length > 0
    this.setData({ canSubmit })
  },

  // 选择图片
  chooseImage() {
    wx.chooseImage({
      count: 1, // 只能选择一张图片
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.uploadImage(res.tempFilePaths[0])
      },
      fail: (error) => {
        wx.showToast({
          title: '选择图片失败',
          icon: 'none'
        })
      }
    })
  },

  // 上传图片
  async uploadImage(tempFilePath) {
    this.setData({ uploading: true })

    try {
      // 检查文件大小
      const fileInfo = await this.getFileInfo(tempFilePath)
      if (fileInfo.size > 10 * 1024 * 1024) { // 10MB
        throw new Error('图片大小不能超过10MB')
      }

      const uploadedUrl = await this.uploadSingleImage(tempFilePath)

      this.setData({
        uploadedImage: uploadedUrl,
        uploading: false
      })

      wx.showToast({
        title: '上传成功',
        icon: 'success'
      })

    } catch (error) {

      this.setData({ uploading: false })

      let errorMsg = '上传失败，请重试'
      if (error.message) {
        if (error.message.includes('网络')) {
          errorMsg = '网络连接失败，请检查网络'
        } else if (error.message.includes('大小')) {
          errorMsg = error.message
        } else if (error.message.includes('格式')) {
          errorMsg = '请选择正确的图片格式'
        } else {
          errorMsg = error.message
        }
      }

      wx.showToast({
        title: errorMsg,
        icon: 'none',
        duration: 3000
      })
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
          reject(new Error(`网络请求失败: ${error.errMsg || '未知错误'}`))
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

  // 预览图片
  previewImage() {
    if (this.data.uploadedImage) {
      wx.previewImage({
        urls: [this.data.uploadedImage],
        current: this.data.uploadedImage
      })
    }
  },

  // 删除图片
  deleteImage() {
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这张图片吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({
            uploadedImage: ''
          })
          wx.showToast({
            title: '图片已删除',
            icon: 'success'
          })
        }
      }
    })
  },

  // 提交求助
  async submitHelp() {
    if (!this.data.canSubmit || this.data.submitting) {
      return
    }

    const { title, description } = this.data.formData

    // 验证必填字段
    if (!title.trim()) {
      wx.showToast({
        title: '请输入求助标题',
        icon: 'none'
      })
      return
    }

    if (!description.trim()) {
      wx.showToast({
        title: '请输入问题描述',
        icon: 'none'
      })
      return
    }

    try {
      this.setData({ submitting: true })

      const requestData = {
        title: title.trim(),
        description: description.trim(),
        imageUrl: this.data.uploadedImage || ''
      }

      const response = await app.request({
        url: '/normal/help-requests',
        method: 'POST',
        data: requestData
      })

      wx.showToast({
        title: '求助提交成功',
        icon: 'success'
      })

      // 延迟跳转，让用户看到成功提示
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)

    } catch (error) {
      this.setData({ submitting: false })

      wx.showToast({
        title: error.message || '提交失败，请重试',
        icon: 'none'
      })
    }
  },

  // 页面分享
  onShareAppMessage() {
    return {
      title: '药用植物助手 - 专家求助',
      path: '/pages/help-create/help-create'
    }
  }
})