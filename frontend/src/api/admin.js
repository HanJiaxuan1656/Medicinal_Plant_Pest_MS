import request from '@/utils/request'

// 获取求助列表（管理员）
export function getAdminHelpRequests(params) {
  return request({
    url: '/admin/help-requests',
    method: 'get',
    params
  })
}

// 审核通过求助
export function approveHelpRequest(id) {
  return request({
    url: `/admin/help-requests/${id}/approve`,
    method: 'post'
  })
}

// 审核拒绝求助
export function rejectHelpRequest(id) {
  return request({
    url: `/admin/help-requests/${id}/reject`,
    method: 'post'
  })
}

// 批量审核通过
export function batchApproveHelpRequests(ids) {
  return request({
    url: '/admin/help-requests/batch-approve',
    method: 'post',
    data: { ids }
  })
}

// 批量审核拒绝
export function batchRejectHelpRequests(ids) {
  return request({
    url: '/admin/help-requests/batch-reject',
    method: 'post',
    data: { ids }
  })
}

// 获取求助详情（管理员）
export function getHelpRequestDetail(id) {
  return request({
    url: `/admin/help-requests/${id}`,
    method: 'get'
  })
}

// 获取用户列表
export function getUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

// 获取专家列表
export function getExperts(params) {
  return request({
    url: '/admin/experts',
    method: 'get',
    params
  })
}

// 获取评论列表
export function getComments(params) {
  return request({
    url: '/admin/comments',
    method: 'get',
    params
  })
}

// 审核通过评论
export function approveComment(id) {
  return request({
    url: `/admin/comments/${id}/approve`,
    method: 'post'
  })
}

// 审核拒绝评论
export function rejectComment(id) {
  return request({
    url: `/admin/comments/${id}/reject`,
    method: 'post'
  })
}

// 批量审核通过评论
export function batchApproveComments(ids) {
  return request({
    url: '/admin/comments/batch-approve',
    method: 'post',
    data: { ids }
  })
}

// 批量审核拒绝评论
export function batchRejectComments(ids) {
  return request({
    url: '/admin/comments/batch-reject',
    method: 'post',
    data: { ids }
  })
}

// 获取管理员统计数据
export function getAdminStats() {
  return request({
    url: '/admin/stats',
    method: 'get'
  })
}

// 专家用户管理扩展功能
export function addExpert(data) {
  return request({
    url: '/admin/experts',
    method: 'post',
    data
  })
}

export function updateExpert(id, data) {
  return request({
    url: `/admin/experts/${id}`,
    method: 'put',
    data
  })
}

export function updateExpertStatus(id, data) {
  return request({
    url: `/admin/experts/${id}/status`,
    method: 'put',
    data
  })
}

export function deleteExpert(id) {
  return request({
    url: `/admin/experts/${id}`,
    method: 'delete'
  })
}

export function resetExpertPassword(id) {
  return request({
    url: `/admin/experts/${id}/reset-password`,
    method: 'put'
  })
}

// 普通用户管理扩展功能
export function addUser(data) {
  return request({
    url: '/admin/users',
    method: 'post',
    data
  })
}

export function updateUser(id, data) {
  return request({
    url: `/admin/users/${id}`,
    method: 'put',
    data
  })
}

export function updateUserStatus(id, data) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

export function resetUserPassword(id) {
  return request({
    url: `/admin/users/${id}/reset-password`,
    method: 'put'
  })
}

// 数据分析相关API
export function getAnalyticsData() {
  return request({
    url: '/admin/analytics',
    method: 'get'
  })
}
