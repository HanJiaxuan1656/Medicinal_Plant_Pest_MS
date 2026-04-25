import request from '@/utils/request'

/**
 * 获取仪表盘数据
 * @returns {Promise}
 */
export function getDashboardData() {
  return request({
    url: '/dashboard',
    method: 'get'
  })
}

/**
 * 获取药用植物概览数据
 * @returns {Promise}
 */
export function getPlantsOverview() {
  return request({
    url: '/dashboard/plants/overview',
    method: 'get'
  })
}

/**
 * 获取病虫害概览数据
 * @returns {Promise}
 */
export function getPestDiseasesOverview() {
  return request({
    url: '/dashboard/pests/overview',
    method: 'get'
  })
}

/**
 * 获取农药概览数据
 * @returns {Promise}
 */
export function getPesticidesOverview() {
  return request({
    url: '/dashboard/pesticides/overview',
    method: 'get'
  })
}