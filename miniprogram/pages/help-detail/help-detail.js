// 求助详情页面
const app = getApp()

Page({
  data: {
    loading: true,
    helpRequest: null,
    replies: [],
    isMyHelp: false,
    resolving: false,
    deleting: false,
    helpId: null
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ helpId: options.id })
      this.loadHelpDetail()
      this.loadReplies()
    } else {
      wx.showToast({
        title: '参数错误',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }
  },

  // 加载求助详情
  async loadHelpDetail() {
    try {
      this.setData({ loading: true })

      const response = await app.request({
        url: `/normal/help-requests/${this.data.helpId}`
      })

      const helpRequest = response.data

      // 处理图片URL
      if (helpRequest.imageUrl) {
        helpRequest.imageUrl = app.processImageUrl(helpRequest.imageUrl)
      }

      // 添加状态文本和时间文本
      helpRequest.statusText = this.getStatusText(helpRequest.status)
      helpRequest.timeText = this.formatTime(helpRequest.createdAt)

      // 检查是否是当前用户的求助
      const isMyHelp = app.globalData.userId &&
                      (helpRequest.userId === app.globalData.userId ||
                       helpRequest.authorId === app.globalData.userId)

      this.setData({
        helpRequest: helpRequest,
        isMyHelp: isMyHelp,
        loading: false
      })

      // 设置页面标题
      wx.setNavigationBarTitle({
        title: helpRequest.title.length > 10 ?
               helpRequest.title.substring(0, 10) + '...' :
               helpRequest.title
      })

    } catch (error) {
      console.error('加载求助详情失败:', error)
      this.setData({ loading: false })
      wx.showToast({
        title: error.message || '加载失败',
        icon: 'none'
      })
    }
  },

  // 加载专家回复
  async loadReplies() {
    try {
      const response = await app.request({
        url: `/normal/help-requests/${this.data.helpId}/replies`
      })

      let replies = response.data.list || response.data || []

      // 处理回复中的图片URL和时间文本
      replies = replies.map(reply => ({
        ...reply,
        imageUrl: reply.imageUrl ? app.processImageUrl(reply.imageUrl) : '',
        expertAvatar: reply.expertAvatar ? app.processImageUrl(reply.expertAvatar) : '',
        timeText: this.formatTime(reply.createdAt)
      }))

      this.setData({ replies: replies })

    } catch (error) {
      console.error('加载回复失败:', error)
      // 回复加载失败不影响主要内容显示
    }
  },

  // 刷新回复
  refreshReplies() {
    wx.showToast({
      title: '刷新中...',
      icon: 'loading'
    })
    this.loadReplies().then(() => {
      wx.hideToast()
      wx.showToast({
        title: '刷新成功',
        icon: 'success'
      })
    })
  },

  // 预览图片
  previewImage(e) {
    const url = e.currentTarget.dataset.url
    if (url) {
      wx.previewImage({
        urls: [url],
        current: url
      })
    }
  },

  // 专家头像加载错误处理
  onExpertAvatarError(e) {
    const index = e.currentTarget.dataset.index
    const replies = this.data.replies
    if (replies[index]) {
      replies[index].expertAvatar = ''
      this.setData({
        replies: replies
      })
    }
  },

  // 感谢专家回复
  async thankReply(e) {
    const replyId = e.currentTarget.dataset.id

    try {
      // 这里可以调用感谢API
      // await app.request({
      //   url: `/normal/help-replies/${replyId}/thank`,
      //   method: 'POST'
      // })

      // 更新本地状态
      const replies = this.data.replies.map(reply => {
        if (reply.id === replyId) {
          return { ...reply, isThanked: true }
        }
        return reply
      })

      this.setData({ replies })

      wx.showToast({
        title: '感谢已发送',
        icon: 'success'
      })

    } catch (error) {
      console.error('感谢失败:', error)
      wx.showToast({
        title: '感谢失败',
        icon: 'none'
      })
    }
  },

  // 标记为已解决
  async markAsResolved() {
    try {
      await wx.showModal({
        title: '确认操作',
        content: '确定要标记这个求助为已解决吗？',
        confirmText: '确定',
        cancelText: '取消'
      })

      this.setData({ resolving: true })

      // 调用标记为已解决的API
      await app.request({
        url: `/normal/help-requests/${this.data.helpId}/resolve`,
        method: 'POST'
      })

      // 更新本地状态
      this.setData({
        'helpRequest.status': 'resolved',
        resolving: false
      })

      wx.showToast({
        title: '已标记为解决',
        icon: 'success'
      })

    } catch (error) {
      this.setData({ resolving: false })
      if (error.errMsg !== 'showModal:fail cancel') {
        console.error('标记失败:', error)
        wx.showToast({
          title: '标记失败',
          icon: 'none'
        })
      }
    }
  },

  // 删除求助
  async deleteHelp() {
    try {
      await wx.showModal({
        title: '确认删除',
        content: '确定要删除这条求助吗？删除后无法恢复。',
        confirmText: '删除',
        cancelText: '取消',
        confirmColor: '#ff4757'
      })

      this.setData({ deleting: true })

      await app.request({
        url: `/normal/help-requests/${this.data.helpId}`,
        method: 'DELETE'
      })

      wx.showToast({
        title: '删除成功',
        icon: 'success'
      })

      setTimeout(() => {
        wx.navigateBack()
      }, 1500)

    } catch (error) {
      this.setData({ deleting: false })
      if (error.errMsg !== 'showModal:fail cancel') {
        console.error('删除失败:', error)
        wx.showToast({
          title: '删除失败',
          icon: 'none'
        })
      }
    }
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'pending': '待审核',
      'approved': '已审核',
      'rejected': '已拒绝',
      'replied': '已回复',
      'resolved': '已解决'
    }
    return statusMap[status] || '未知状态'
  },

  // 格式化时间
  formatTime(timeStr) {
    if (!timeStr) return ''

    const time = new Date(timeStr)
    const now = new Date()
    const diff = now - time

    // 小于1分钟
    if (diff < 60000) {
      return '刚刚'
    }

    // 小于1小时
    if (diff < 3600000) {
      return Math.floor(diff / 60000) + '分钟前'
    }

    // 小于1天
    if (diff < 86400000) {
      return Math.floor(diff / 3600000) + '小时前'
    }

    // 小于7天
    if (diff < 604800000) {
      return Math.floor(diff / 86400000) + '天前'
    }

    // 超过7天显示具体日期
    return time.toLocaleDateString()
  },

  // 下拉刷新
  onPullDownRefresh() {
    Promise.all([
      this.loadHelpDetail(),
      this.loadReplies()
    ]).finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: this.data.helpRequest ? this.data.helpRequest.title : '求助详情',
      path: `/pages/help-detail/help-detail?id=${this.data.helpId}`,
      imageUrl: this.data.helpRequest && this.data.helpRequest.imageUrl ?
                this.data.helpRequest.imageUrl : ''
    }
  }
})