import request from '@/utils/request'

// 获取药用植物列表
export function getPlants(params) {
  return request({
    url: '/normal/plants',
    method: 'get',
    params
  })
}

// 获取植物详情
export function getPlantDetail(id) {
  return request({
    url: `/normal/plants/${id}`,
    method: 'get'
  })
}

// 获取病虫害列表
export function getPestDiseases(params) {
  return request({
    url: '/normal/pest-diseases',
    method: 'get',
    params
  })
}

// 获取病虫害详情
export function getPestDiseaseDetail(id) {
  return request({
    url: `/normal/pest-diseases/${id}`,
    method: 'get'
  })
}

// 获取农药列表
export function getPesticides(params) {
  return request({
    url: '/normal/pesticides',
    method: 'get',
    params
  })
}

// 获取农药详情
export function getPesticideDetail(id) {
  return request({
    url: `/normal/pesticides/${id}`,
    method: 'get'
  })
}

// 获取评论列表
export function getComments(targetType, targetId, params = {}) {
  return request({
    url: '/normal/comments',
    method: 'get',
    params: {
      targetType,
      targetId,
      ...params
    }
  })
}

// 添加评论
export function addComment(data) {
  // 获取当前用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1

  return request({
    url: '/normal/comments',
    method: 'post',
    data,
    headers: {
      'User-Id': userId
    }
  })
}

// 获取我的评论
export function getMyComments(params) {
  // 获取当前用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1

  return request({
    url: '/normal/my-comments',
    method: 'get',
    params,
    headers: {
      'User-Id': userId
    }
  })
}

// 删除评论
export function deleteComment(id) {
  // 获取当前用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1

  return request({
    url: `/normal/comments/${id}`,
    method: 'delete',
    headers: {
      'User-Id': userId
    }
  })
}

// 更新评论
export function updateComment(id, data) {
  // 获取当前用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1

  return request({
    url: `/normal/comments/${id}`,
    method: 'put',
    data,
    headers: {
      'User-Id': userId
    }
  })
}

// 获取求助列表（只返回当前用户的求助）
export function getHelpRequests(params) {
  // 获取当前用户ID（从localStorage或其他地方）
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1 // 默认为1，实际应该从登录信息获取

  return request({
    url: '/normal/help-requests',
    method: 'get',
    params,
    headers: {
      'User-Id': userId
    }
  })
}

// 获取求助详情
export function getHelpRequestDetail(id) {
  return request({
    url: `/normal/help-requests/${id}`,
    method: 'get'
  })
}

// 创建求助
export function createHelpRequest(data) {
  // 获取当前用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1

  return request({
    url: '/normal/help-requests',
    method: 'post',
    data,
    headers: {
      'User-Id': userId
    }
  })
}

// 获取我的求助
export function getMyHelpRequests(params) {
  return request({
    url: '/normal/my-help-requests',
    method: 'get',
    params
  })
}

// 更新求助
export function updateHelpRequest(id, data) {
  return request({
    url: `/normal/help-requests/${id}`,
    method: 'put',
    data
  })
}

// 删除求助
export function deleteHelpRequest(id) {
  // 获取当前用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1


  return request({
    url: `/normal/help-requests/${id}`,
    method: 'delete',
    headers: {
      'User-Id': userId
    }
  })
}

// 获取求助回复
export function getHelpReplies(helpId, params) {
  return request({
    url: `/normal/help-requests/${helpId}/replies`,
    method: 'get',
    params
  })
}

// 上传图片
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/files/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 获取个人信息
export function getNormalProfile() {
  // 获取当前用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1

  return request({
    url: '/normal/profile',
    method: 'get',
    headers: {
      'User-Id': userId
    }
  })
}

// 更新个人信息
export function updateNormalProfile(data) {
  // 获取当前用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1

  return request({
    url: '/normal/profile',
    method: 'put',
    data,
    headers: {
      'User-Id': userId
    }
  })
}

// 修改密码
export function changePassword(data) {
  // 获取当前用户ID
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.id || 1

  return request({
    url: '/normal/change-password',
    method: 'put',
    data,
    headers: {
      'User-Id': userId
    }
  })
}