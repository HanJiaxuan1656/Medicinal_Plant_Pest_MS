// 植物详情页面逻辑
const app = getApp()

Page({
  data: {
    loading: true,
    plant: null,
    relatedDiseases: [],
    comments: [],
    commentContent: '',
    isCommentValid: false,
    userInfo: null
  },

  onLoad(options) {
    const plantId = options.id
    if (plantId) {
      this.loadPlantDetail(plantId)
      this.loadComments(plantId)
    }

    // 获取用户信息
    this.setData({
      userInfo: app.globalData.userInfo
    })
  },

  // 加载植物详情
  async loadPlantDetail(id) {
    try {
      this.setData({ loading: true })

      const response = await app.request({
        url: `/normal/plants/${id}`
      })

      const plantData = response.data

      // 处理药用部位 - 英文转中文
      if (plantData.plant.medicinalParts) {
        plantData.plant.medicinalPartsArray = plantData.plant.medicinalParts
          .split(',')
          .map(part => this.translateMedicinalPart(part.trim()))
      }

      // 处理相关病虫害的图片路径
      if (plantData.relatedDiseases && plantData.relatedDiseases.length > 0) {
        plantData.relatedDiseases.forEach(disease => {
          disease.imageUrl = app.processImageUrl(disease.imageUrl);
        });
      }

      this.setData({
        plant: plantData.plant,
        relatedDiseases: plantData.relatedDiseases || [],
        loading: false
      })

    } catch (error) {
      console.error('加载植物详情失败:', error)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  /**
   * 翻译药用部位从英文到中文
   */
  translateMedicinalPart(englishPart) {
    const partMap = {
      'root': '根',
      'stem': '茎',
      'leaf': '叶',
      'flower': '花',
      'fruit': '果实',
      'seed': '种子',
      'whole': '全草',
      'bark': '皮',
      'rhizome': '根茎',
      'tuber': '块茎',
      'bulb': '鳞茎'
    };

    return partMap[englishPart] || englishPart; // 如果找不到对应翻译，返回原文
  },

  /**
   * 相关病虫害图片加载错误处理
   */
  onDiseaseImageError(e) {
    const index = e.currentTarget.dataset.index;
    console.log('病虫害图片加载失败:', index, this.data.relatedDiseases[index]);

    // 可以在这里设置默认图片或者隐藏图片
    const relatedDiseases = this.data.relatedDiseases;
    if (relatedDiseases[index]) {
      relatedDiseases[index].imageUrl = ''; // 清空错误的图片路径，显示占位符
      this.setData({
        relatedDiseases: relatedDiseases
      });
    }
  },

  // 加载评论
  async loadComments(plantId) {
    try {
      const response = await app.request({
        url: '/normal/comments',
        data: {
          targetType: 'plant',
          targetId: plantId,
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
          targetType: 'plant',
          targetId: this.data.plant.id,
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
      this.loadComments(this.data.plant.id)

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
    if (this.data.plant && this.data.plant.imageUrl) {
      wx.previewImage({
        urls: [this.data.plant.imageUrl],
        current: this.data.plant.imageUrl
      })
    }
  },

  // 跳转到病虫害详情
  goToPestDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/pest-detail/pest-detail?id=${id}`
    })
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
    const plant = this.data.plant
    return {
      title: `${plant.name} - 药用植物详情`,
      path: `/pages/plant-detail/plant-detail?id=${plant.id}`,
      imageUrl: plant.imageUrl
    }
  }
})