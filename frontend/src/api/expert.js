import axios from 'axios'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '',
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 直接返回整个响应数据，让业务代码处理
    return response
  },
  error => {
    return Promise.reject(error)
  }
)

/**
 * 获取专家用户个人信息
 */
export function getExpertProfile() {
  return service({
    url: '/expert/profile',
    method: 'get'
  })
}

/**
 * 更新专家用户个人信息
 */
export function updateExpertProfile(data) {
  return service({
    url: '/expert/profile',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 */
export function updatePassword(data) {
  return service({
    url: '/expert/password',
    method: 'put',
    data
  })
}

/**
 * 获取专家用户统计信息
 */
export function getExpertStats() {
  return service({
    url: '/expert/stats',
    method: 'get'
  })
}

/**
 * 获取求助列表（专家）
 */
export function getExpertHelpRequests(params) {
  return service({
    url: '/expert/help-requests',
    method: 'get',
    params
  })
}

/**
 * 获取求助回复列表
 */
export function getHelpReplies(helpId) {
  return service({
    url: `/expert/help-requests/${helpId}/replies`,
    method: 'get'
  })
}

/**
 * 创建回复
 */
export function createHelpReply(helpId, data) {
  // 优先从真实登录信息中获取专家ID

  // 1. 优先使用真实登录的专家信息
  const userInfoStr = localStorage.getItem('userInfo')

  let expertId = 1 // 默认值

  if (userInfoStr) {
    try {
      const userInfo = JSON.parse(userInfoStr)
      if (userInfo.id) {
        expertId = userInfo.id
      }
    } catch (e) {
      console.error('解析userInfo失败:', e)
    }
  }

  // 2. 如果没有登录信息，尝试从expertInfo获取（测试用）
  if (expertId === 1) {
    const expertInfoStr = localStorage.getItem('expertInfo')

    if (expertInfoStr) {
      try {
        const expertInfo = JSON.parse(expertInfoStr)
        if (expertInfo.id) {
          expertId = expertInfo.id
        }
      } catch (e) {
        console.error('解析expertInfo失败:', e)
      }
    }
  }


  return service({
    url: `/expert/help-requests/${helpId}/replies`,
    method: 'post',
    data,
    headers: {
      'Expert-Id': expertId
    }
  })
}

/**
 * 获取求助详情（专家）
 */
export function getExpertHelpRequestDetail(id) {
  return service({
    url: `/expert/help-requests/${id}`,
    method: 'get'
  })
}
