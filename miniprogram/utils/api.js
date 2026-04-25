// API 接口封装
const app = getApp()

/**
 * 植物相关API
 */
export const plantApi = {
  // 获取植物列表
  getPlants(params = {}) {
    return app.request({
      url: '/normal/plants',
      data: params
    })
  },

  // 获取植物详情
  getPlantDetail(id) {
    return app.request({
      url: `/normal/plants/${id}`
    })
  },

  // 搜索植物
  searchPlants(keyword) {
    return app.request({
      url: '/normal/plants',
      data: { search: keyword }
    })
  }
}

/**
 * 病虫害相关API
 */
export const pestApi = {
  // 获取病虫害列表
  getPestDiseases(params = {}) {
    return app.request({
      url: '/normal/pest-diseases',
      data: params
    })
  },

  // 获取病虫害详情
  getPestDetail(id) {
    return app.request({
      url: `/normal/pest-diseases/${id}`
    })
  }
}

/**
 * 农药相关API
 */
export const pesticideApi = {
  // 获取农药列表
  getPesticides(params = {}) {
    return app.request({
      url: '/normal/pesticides',
      data: params
    })
  },

  // 获取农药详情
  getPesticideDetail(id) {
    return app.request({
      url: `/normal/pesticides/${id}`
    })
  }
}

/**
 * 求助相关API
 */
export const helpApi = {
  // 获取求助列表
  getHelpRequests(params = {}) {
    return app.request({
      url: '/normal/help-requests',
      data: params
    })
  },

  // 获取求助详情
  getHelpDetail(id) {
    return app.request({
      url: `/normal/help-requests/${id}`
    })
  },

  // 创建求助
  createHelp(data) {
    return app.request({
      url: '/normal/help-requests',
      method: 'POST',
      data: data
    })
  },

  // 获取我的求助
  getMyHelps(params = {}) {
    return app.request({
      url: '/normal/my-help-requests',
      data: params
    })
  }
}

/**
 * 评论相关API
 */
export const commentApi = {
  // 获取评论列表
  getComments(targetType, targetId, params = {}) {
    return app.request({
      url: '/normal/comments',
      data: {
        targetType,
        targetId,
        ...params
      }
    })
  },

  // 添加评论
  addComment(data) {
    return app.request({
      url: '/normal/comments',
      method: 'POST',
      data: data
    })
  },

  // 获取我的评论
  getMyComments(params = {}) {
    return app.request({
      url: '/normal/my-comments',
      data: params
    })
  },

  // 删除评论
  deleteComment(id) {
    return app.request({
      url: `/normal/comments/${id}`,
      method: 'DELETE'
    })
  }
}

/**
 * 用户相关API
 */
export const userApi = {
  // 登录
  login(data) {
    return app.request({
      url: '/auth/normal/login',
      method: 'POST',
      data: data
    })
  },

  // 注册
  register(data) {
    return app.request({
      url: '/auth/normal/register',
      method: 'POST',
      data: data
    })
  },

  // 获取用户信息
  getProfile() {
    return app.request({
      url: '/normal/profile'
    })
  },

  // 更新用户信息
  updateProfile(data) {
    return app.request({
      url: '/normal/profile',
      method: 'PUT',
      data: data
    })
  }
}

/**
 * 文件上传API
 */
export const fileApi = {
  // 上传文件
  uploadFile(filePath) {
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: app.globalData.baseUrl + '/files/upload',
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': app.globalData.token ? `Bearer ${app.globalData.token}` : '',
          'User-Id': app.globalData.userId || ''
        },
        success: (res) => {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 1) {
              resolve(data.data)
            } else {
              reject(new Error(data.message || '上传失败'))
            }
          } catch (e) {
            reject(new Error('解析响应失败'))
          }
        },
        fail: reject
      })
    })
  }
}
