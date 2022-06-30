import request from '@/utils/request'

// 查询参数列表
export function listPageDataSource(query) {
  return request({
    url: '/dataservice/pageDataSource/list',
    method: 'get',
    params: query
  })
}

// 查询参数详细
export function getPageDataSource(pageDataSourceId) {
  return request({
    url: '/dataservice/pageDataSource/' + pageDataSourceId,
    method: 'get'
  })
}

// 根据参数键名查询参数值
export function getPageDataSourceKey(pageDataSourceKey) {
  return request({
    url: '/dataservice/pageDataSource/pageDataSourceKey/' + pageDataSourceKey,
    method: 'get'
  })
}

// 新增参数配置
export function addPageDataSource(data) {
  return request({
    url: '/dataservice/pageDataSource',
    method: 'post',
    data: data
  })
}

// 修改参数配置
export function updatePageDataSource(data) {
  return request({
    url: '/dataservice/pageDataSource',
    method: 'put',
    data: data
  })
}

// 删除参数配置
export function delPageDataSource(pageDataSourceId) {
  return request({
    url: '/dataservice/pageDataSource/' + pageDataSourceId,
    method: 'delete'
  })
}

// 导出参数
// export function exportPageDataSource(query) {
//   return request({
//     url: '/seecloudCfet/pageDataSource/export',
//     method: 'get',
//     params: query
//   })
// }
