// 我的评论页面
const app = getApp()

Page({
  data: {
    loading: false,
    comments: [],
    stats: {
      total: 0,
      pending: 0,
      approved: 0,
      rejected: 0
    },

    // 筛选条件
    selectedType: '',
    selectedTypeText: '全部类型',
    selectedStatus: '',
    selectedStatusText: '全部状态',
    typeDropdownOpen: false,
    statusDropdownOpen: false,

    // 分页
    currentPage: 1,
    pageSize: 10,
    hasMore: true,

    // 编辑评论
    editModalVisible: false,
    editContent: '',
    editingComment: null,
    updating: false
  },

  onLoad() {
    this.loadComments(1, true)
    this.loadStats()
  },

  onShow() {
    // 页面显示时刷新数据
    this.loadComments(1, true)
    this.loadStats()
  },

  // 加载评论列表
  async loadComments(page = 1, reset = false) {
    if (this.data.loading) return

    try {
      this.setData({ loading: true })

      const response = await app.request({
        url: '/normal/my-comments',
        data: {
          page: page,
          pageSize: this.data.pageSize,
          type: this.data.selectedType,
          status: this.data.selectedStatus
        }
      })

      const newComments = response.data.list || []

      // 处理评论数据
      newComments.forEach(comment => {
        // 格式化时间
        comment.createdAt = this.formatDate(comment.createdAt)

        // 设置类型文本
        comment.typeText = this.getTypeText(comment.type)

        // 设置状态文本
        comment.statusText = this.getStatusText(comment.status)
      })

      this.setData({
        comments: reset ? newComments : [...this.data.comments, ...newComments],
        currentPage: page,
        hasMore: newComments.length === this.data.pageSize,
        loading: false
      })

    } catch (error) {
      console.error('加载评论列表失败:', error)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  // 加载统计数据
  async loadStats() {
    try {
      const response = await app.request({
        url: '/normal/my-comments-stats'
      })

      this.setData({
        stats: response.data || {
          total: 0,
          pending: 0,
          approved: 0,
          rejected: 0
        }
      })

    } catch (error) {
      console.error('加载统计数据失败:', error)
    }
  },

  // 切换类型下拉框
  toggleTypeDropdown() {
    this.setData({
      typeDropdownOpen: !this.data.typeDropdownOpen,
      statusDropdownOpen: false
    })
  },

  // 切换状态下拉框
  toggleStatusDropdown() {
    this.setData({
      statusDropdownOpen: !this.data.statusDropdownOpen,
      typeDropdownOpen: false
    })
  },

  // 选择评论类型
  onTypeSelect(e) {
    const { value, text } = e.currentTarget.dataset
    this.setData({
      selectedType: value,
      selectedTypeText: text,
      typeDropdownOpen: false,
      currentPage: 1
    })
    this.loadComments(1, true)
  },

  // 选择审核状态
  onStatusSelect(e) {
    const { value, text } = e.currentTarget.dataset
    this.setData({
      selectedStatus: value,
      selectedStatusText: text,
      statusDropdownOpen: false,
      currentPage: 1
    })
    this.loadComments(1, true)
  },

  // 关闭下拉框
  closeDropdowns() {
    this.setData({
      typeDropdownOpen: false,
      statusDropdownOpen: false
    })
  },

  // 阻止事件冒泡
  stopPropagation() {
    // 阻止点击下拉选项区域时关闭下拉框
  },

  // 查看评论对象
  viewTarget(e) {
    const item = e.currentTarget.dataset.item
    const routeMap = {
      'plant': `/pages/plant-detail/plant-detail?id=${item.targetId}`,
      'pest_disease': `/pages/pest-detail/pest-detail?id=${item.targetId}`,
      'pesticide': `/pages/pesticide-detail/pesticide-detail?id=${item.targetId}`
    }

    const route = routeMap[item.type]
    if (route) {
      wx.navigateTo({
        url: route
      })
    }
  },

  // 编辑评论
  editComment(e) {
    const item = e.currentTarget.dataset.item
    this.setData({
      editModalVisible: true,
      editContent: item.content,
      editingComment: item
    })
  },

  // 关闭编辑弹窗
  closeEditModal() {
    this.setData({
      editModalVisible: false,
      editContent: '',
      editingComment: null
    })
  },

  // 编辑内容输入
  onEditInput(e) {
    this.setData({
      editContent: e.detail.value
    })
  },

  // 更新评论
  async updateComment() {
    if (!this.data.editContent.trim()) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none'
      })
      return
    }

    if (this.data.updating) return

    try {
      this.setData({ updating: true })

      await app.request({
        url: `/normal/comments/${this.data.editingComment.id}`,
        method: 'PUT',
        data: {
          content: this.data.editContent.trim()
        }
      })

      wx.showToast({
        title: '修改成功',
        icon: 'success'
      })

      this.closeEditModal()
      this.loadComments(1, true)
      this.loadStats()

    } catch (error) {
      console.error('更新评论失败:', error)
      wx.showToast({
        title: '修改失败',
        icon: 'none'
      })
    } finally {
      this.setData({ updating: false })
    }
  },

  // 删除评论
  deleteComment(e) {
    const item = e.currentTarget.dataset.item

    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条评论吗？删除后无法恢复。',
      success: async (res) => {
        if (res.confirm) {
          try {
            await app.request({
              url: `/normal/comments/${item.id}`,
              method: 'DELETE'
            })

            wx.showToast({
              title: '删除成功',
              icon: 'success'
            })

            this.loadComments(1, true)
            this.loadStats()

          } catch (error) {
            console.error('删除评论失败:', error)
            wx.showToast({
              title: '删除失败',
              icon: 'none'
            })
          }
        }
      }
    })
  },

  // 跳转到植物页面
  goToPlants() {
    wx.switchTab({
      url: '/pages/plants/plants'
    })
  },

  // 加载更多
  loadMore() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadComments(this.data.currentPage + 1, false)
    }
  },

  // 获取类型文本
  getTypeText(type) {
    const typeMap = {
      'plant': '植物',
      'pest_disease': '病虫害',
      'pesticide': '农药'
    }
    return typeMap[type] || '未知'
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'pending': '审核中',
      'approved': '已通过',
      'rejected': '已拒绝'
    }
    return statusMap[status] || '未知'
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

  // 下拉刷新
  onPullDownRefresh() {
    this.loadComments(1, true).then(() => {
      wx.stopPullDownRefresh()
    })
    this.loadStats()
  },

  // 上拉加载更多
  onReachBottom() {
    this.loadMore()
  },

  // 分享功能
  onShareAppMessage() {
    return {
      title: '我的评论 - 药用植物助手',
      path: '/pages/my-comments/my-comments'
    }
  }
})