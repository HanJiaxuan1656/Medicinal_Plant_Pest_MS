// 病虫害详情页面
Page({
  data: {
    loading: true,
    pestInfo: null,
    relatedPesticides: [],
    comments: [],
    commentContent: '',
    isCommentValid: false,
    userInfo: null
  },



  onLoad(options) {
    const pestId = options.id
    if (pestId) {
      this.loadPestDetail(pestId)
      this.loadComments(pestId)
    }

    // 获取用户信息
    this.setData({
      userInfo: getApp().globalData.userInfo
    })
  },

  // 加载病虫害详情
  async loadPestDetail(id) {
    try {
      this.setData({ loading: true })

      const app = getApp()
      const response = await app.request({
        url: `/normal/pest-diseases/${id}`
      })

      const pestData = response.data

      // 处理图片URL
      if (pestData.pestDisease.imageUrl) {
        pestData.pestDisease.imageUrl = app.processImageUrl(pestData.pestDisease.imageUrl)
      }

      // 处理相关农药数据
      if (pestData.relatedPesticides && pestData.relatedPesticides.length > 0) {
        pestData.relatedPesticides.forEach(pesticide => {
          // 处理图片URL
          if (pesticide.imageUrl) {
            pesticide.imageUrl = app.processImageUrl(pesticide.imageUrl)
          }

          // 处理使用说明长度，如果太长则截取
          if (pesticide.description && pesticide.description.length > 100) {
            pesticide.description = pesticide.description.substring(0, 100) + '...'
          }

          // 处理用法用量长度
          if (pesticide.dosage && pesticide.dosage.length > 80) {
            pesticide.dosage = pesticide.dosage.substring(0, 80) + '...'
          }

          // 处理施用方法长度
          if (pesticide.applicationMethod && pesticide.applicationMethod.length > 60) {
            pesticide.applicationMethod = pesticide.applicationMethod.substring(0, 60) + '...'
          }

          // 处理备注长度
          if (pesticide.notes && pesticide.notes.length > 60) {
            pesticide.notes = pesticide.notes.substring(0, 60) + '...'
          }
        });
      }

      // 处理危害程度文本
      const severityMap = {
        'low': '轻微',
        'medium': '中等',
        'high': '严重'
      }
      if (pestData.pestDisease.severityLevel) {
        pestData.pestDisease.severityText = severityMap[pestData.pestDisease.severityLevel] || pestData.pestDisease.severityLevel
      }

      this.setData({
        pestInfo: pestData.pestDisease,
        relatedPesticides: pestData.relatedPesticides || [],
        loading: false
      })

    } catch (error) {
      console.error('加载病虫害详情失败:', error)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  // 加载评论
  async loadComments(pestId) {
    try {
      const app = getApp()
      const response = await app.request({
        url: '/normal/comments',
        data: {
          targetType: 'pest_disease',
          targetId: pestId,
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
          targetType: 'pest_disease',
          targetId: this.data.pestInfo.id,
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
      this.loadComments(this.data.pestInfo.id)

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
    if (this.data.pestInfo && this.data.pestInfo.imageUrl) {
      wx.previewImage({
        urls: [this.data.pestInfo.imageUrl],
        current: this.data.pestInfo.imageUrl
      })
    }
  },

  // 跳转到农药详情
  goToPesticideDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/pesticide-detail/pesticide-detail?id=${id}`
    })
  },

  // 农药图片加载错误处理
  onPesticideImageError(e) {
    const index = e.currentTarget.dataset.index
    console.log('农药图片加载失败:', index, this.data.relatedPesticides[index])

    // 清空错误的图片路径，显示占位符
    const relatedPesticides = this.data.relatedPesticides
    if (relatedPesticides[index]) {
      relatedPesticides[index].imageUrl = ''
      this.setData({
        relatedPesticides: relatedPesticides
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
    const pestInfo = this.data.pestInfo
    return {
      title: `${pestInfo.name} - 病虫害详情`,
      path: `/pages/pest-detail/pest-detail?id=${pestInfo.id}`,
      imageUrl: pestInfo.imageUrl
    }
  }
})