// 农药详情页面
Page({
  data: {
    loading: true,
    pesticide: null,
    comments: [],
    commentContent: '',
    isCommentValid: false,
    userInfo: null
  },

  onLoad(options) {
    const pesticideId = options.id
    if (pesticideId) {
      this.loadPesticideDetail(pesticideId)
      this.loadComments(pesticideId)
    }

    // 获取用户信息
    this.setData({
      userInfo: getApp().globalData.userInfo
    })
  },

  // 加载农药详情
  async loadPesticideDetail(id) {
    try {
      this.setData({ loading: true })

      const app = getApp()
      const response = await app.request({
        url: `/normal/pesticides/${id}`
      })

      // 后端返回的数据结构是 {pesticide: {...}}
      const pesticideData = response.data.pesticide || response.data

      // 处理图片URL
      if (pesticideData.imageUrl) {
        pesticideData.imageUrl = app.processImageUrl(pesticideData.imageUrl)
      }

      // 处理创建时间
      if (pesticideData.createdAt) {
        pesticideData.createdAt = this.formatDate(pesticideData.createdAt)
      }

      // 处理安全等级文本
      const safetyLevelMap = {
        'low': '低毒',
        'medium': '中毒',
        'high': '高毒'
      }
      if (pesticideData.safetyLevel) {
        pesticideData.safetyLevelText = safetyLevelMap[pesticideData.safetyLevel] || pesticideData.safetyLevel
      }

      // 处理使用方法数据（如果后端返回）
      if (pesticideData.applicationMethods) {
        pesticideData.applicationMethods.forEach(method => {
          if (method.description && method.description.length > 200) {
            method.description = method.description.substring(0, 200) + '...'
          }
        })
      }

      // 确保基本字段存在
      if (!pesticideData.name) {
        pesticideData.name = '未知农药'
      }
      if (!pesticideData.category) {
        pesticideData.category = '未分类'
      }

      this.setData({
        pesticide: pesticideData,
        loading: false
      })

      // 设置页面标题
      wx.setNavigationBarTitle({
        title: pesticideData.name
      })

    } catch (error) {
      console.error('加载农药详情失败:', error)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  // 加载评论
  async loadComments(pesticideId) {
    try {
      const app = getApp()
      const response = await app.request({
        url: '/normal/comments',
        data: {
          targetType: 'pesticide',
          targetId: pesticideId,
          page: 1,
          pageSize: 20
        }
      })

      const comments = response.data.list || []

      // 格式化评论时间
      comments.forEach(comment => {
        comment.createdAt = this.formatDate(comment.createdAt)
      })

      this.setData({
        comments: comments
      })

    } catch (error) {
      console.error('加载评论失败:', error)
    }
  },

  // 评论输入
  onCommentInput(e) {
    const content = e.detail.value
    this.setData({
      commentContent: content,
      isCommentValid: content.trim().length > 0
    })
  },

  // 提交评论
  async submitComment() {
    if (!this.data.commentContent.trim()) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none'
      })
      return
    }

    const app = getApp()
    if (!app.globalData.isLogin) {
      this.goToLogin()
      return
    }

    try {
      wx.showLoading({ title: '发表中...' })

      await app.request({
        url: '/normal/comments',
        method: 'POST',
        data: {
          targetType: 'pesticide',
          targetId: this.data.pesticide.id,
          content: this.data.commentContent.trim()
        }
      })

      wx.hideLoading()
      wx.showToast({
        title: '评论提交成功，等待审核',
        icon: 'success'
      })

      // 清空输入框
      this.setData({
        commentContent: '',
        isCommentValid: false
      })

      // 重新加载评论
      this.loadComments(this.data.pesticide.id)

    } catch (error) {
      wx.hideLoading()
      console.error('提交评论失败:', error)
      wx.showToast({
        title: '提交失败',
        icon: 'none'
      })
    }
  },

  // 预览图片
  previewImage() {
    if (this.data.pesticide && this.data.pesticide.imageUrl) {
      wx.previewImage({
        urls: [this.data.pesticide.imageUrl],
        current: this.data.pesticide.imageUrl
      })
    }
  },

  // 图片加载错误处理
  onImageError(e) {
    const pesticide = this.data.pesticide
    if (pesticide) {
      pesticide.imageUrl = ''
      this.setData({
        pesticide: pesticide
      })
    }
  },

  // 跳转到登录页
  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  // 添加收藏
  addToFavorites() {
    wx.showToast({
      title: '收藏功能开发中',
      icon: 'none'
    })
  },

  // 分享给朋友
  shareToFriend() {
    // 触发分享
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
    const pesticide = this.data.pesticide
    return {
      title: `${pesticide.name} - 农药详情`,
      path: `/pages/pesticide-detail/pesticide-detail?id=${pesticide.id}`,
      imageUrl: pesticide.imageUrl
    }
  }
})