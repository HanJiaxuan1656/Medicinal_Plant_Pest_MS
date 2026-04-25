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
    console.error(error)
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
    console.error('请求错误: ', error)
    return Promise.reject(error)
  }
)

// 登录接口
export function login(userType, data) {
  return service({
    url: `/auth/${userType}/login`,
    method: 'post',
    data
  })
}

// 注册接口
export function register(userType, data) {
  return service({
    url: `/auth/${userType}/register`,
    method: 'post',
    data
  })
}

// 检查用户名是否存在
export function checkUsername(username, userType) {
  return service({
    url: '/auth/check-username',
    method: 'get',
    params: { username, userType }
  })
}

export default service 