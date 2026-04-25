// 植物列表页面逻辑
const app = getApp()

Page({
  data: {
    loading: false,
    plants: [],
    searchKeyword: '',
    selectedPart: '',
    selectedPartText: '全部',
    dropdownOpen: false,
    sortBy: 'default',
    currentPage: 1,
    pageSize: 10,
    hasMore: true,
    showBackTop: false,
    medicinalParts: ['根', '茎', '叶', '花', '果实', '种子', '全草']
  },

  onLoad(options) {
    // 如果有搜索关键词，设置搜索条件
    if (options.keyword) {
      this.setData({
        searchKeyword: options.keyword
      })
    }
    this.loadPlants(true)
  },

  onShow() {
    // 页面显示时检查是否需要刷新
    if (this.data.plants.length === 0) {
      this.loadPlants(true)
    }
  },

  // 加载植物列表
  async loadPlants(reset = false) {
    if (this.data.loading) return

    try {
      this.setData({ loading: true })

      const page = reset ? 1 : this.data.currentPage
      const response = await app.request({
        url: '/normal/plants',
        data: {
          page: page,
          pageSize: this.data.pageSize,
          search: this.data.searchKeyword,
          medicinalPart: this.data.selectedPart,
          sortBy: this.data.sortBy
        }
      })

      const newPlants = response.data.list || []
      
      // 处理药用部位数组 - 英文转中文
      newPlants.forEach(plant => {
        if (plant.medicinalParts) {
          plant.medicinalPartsArray = plant.medicinalParts
            .split(',')
            .map(part => this.translateMedicinalPart(part.trim()))
        }
        // 格式化创建时间
        if (plant.createdAt) {
          plant.createdAt = this.formatDate(plant.createdAt)
        }
      })

      this.setData({
        plants: reset ? newPlants : [...this.data.plants, ...newPlants],
        currentPage: page,
        hasMore: newPlants.length === this.data.pageSize,
        loading: false
      })

    } catch (error) {
      console.error('加载植物列表失败:', error)
      this.setData({ loading: false })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    }
  },

  // 搜索输入
  onSearchInput(e) {
    this.setData({
      searchKeyword: e.detail.value
    })
  },

  // 执行搜索
  onSearch() {
    this.setData({
      currentPage: 1
    })
    this.loadPlants(true)
  },

  // 切换下拉框显示状态
  toggleDropdown(e) {
    e && e.stopPropagation && e.stopPropagation(); // 阻止事件冒泡

    this.setData({
      dropdownOpen: !this.data.dropdownOpen
    });
  },

  // 选择药用部位
  onPartSelect(e) {
    const { value, text } = e.currentTarget.dataset

    this.setData({
      selectedPart: value,
      selectedPartText: text,
      dropdownOpen: false,
      currentPage: 1
    })

    this.loadPlants(true)
  },

  // 关闭下拉框
  closeDropdown(e) {
    if (this.data.dropdownOpen) {
      this.setData({
        dropdownOpen: false
      });
    }
  },

  // 阻止事件冒泡
  stopPropagation() {
    // 阻止点击下拉选项区域时关闭下拉框
  },

  // 筛选变化（保留兼容性）
  onFilterChange(e) {
    const { type, value } = e.currentTarget.dataset

    if (type === 'part') {
      const text = value === '' ? '全部' : value
      this.setData({
        selectedPart: value,
        selectedPartText: text,
        currentPage: 1
      })
      this.loadPlants(true)
    }
  },

  // 排序变化
  onSortChange(e) {
    const value = e.currentTarget.dataset.value
    this.setData({
      sortBy: value,
      currentPage: 1
    })
    this.loadPlants(true)
  },

  // 加载更多
  loadMore() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({
        currentPage: this.data.currentPage + 1
      })
      this.loadPlants(false)
    }
  },

  // 跳转到详情页
  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/plant-detail/plant-detail?id=${id}`
    })
  },

  // 图片加载失败
  onImageError(e) {
    console.log('图片加载失败:', e);

    // 获取失败的图片信息
    const dataset = e.currentTarget.dataset || e.target.dataset;
    if (dataset && dataset.index !== undefined) {
      const index = dataset.index;
      const plants = this.data.plants;
      if (plants[index]) {
        console.log('原始图片URL:', plants[index].imageUrl);
        // 尝试重新处理图片URL
        const processedUrl = app.processImageUrl(plants[index].imageUrl);
        if (processedUrl !== plants[index].imageUrl) {
          console.log('重新处理图片URL:', processedUrl);
          plants[index].imageUrl = processedUrl;
          this.setData({ plants });
        }
      }
    }
  },

  // 返回顶部
  backToTop() {
    wx.pageScrollTo({
      scrollTop: 0,
      duration: 300
    })
  },

  // 页面滚动
  onPageScroll(e) {
    const showBackTop = e.scrollTop > 500
    if (showBackTop !== this.data.showBackTop) {
      this.setData({
        showBackTop: showBackTop
      })
    }
  },

  // 触底加载更多
  onReachBottom() {
    this.loadMore()
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.setData({
      currentPage: 1
    })
    this.loadPlants(true).then(() => {
      wx.stopPullDownRefresh()
    })
  },

  // 格式化日期
  formatDate(dateString) {
    if (!dateString) return ''
    
    const date = new Date(dateString)
    const now = new Date()
    const diff = now - date
    
    // 小于1天显示时间差
    if (diff < 24 * 60 * 60 * 1000) {
      const hours = Math.floor(diff / (60 * 60 * 1000))
      if (hours < 1) {
        const minutes = Math.floor(diff / (60 * 1000))
        return `${minutes}分钟前`
      }
      return `${hours}小时前`
    }
    
    // 大于1天显示日期
    const month = date.getMonth() + 1
    const day = date.getDate()
    return `${month}月${day}日`
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

  // 分享功能
  onShareAppMessage() {
    return {
      title: '药用植物大全 - 专业的植物知识库',
      path: '/pages/plants/plants'
    }
  }
})
