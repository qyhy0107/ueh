import { dataLoad } from '@/api/dataservice/dataset'
// 查询参数列表
export function listVendor(param) {
  return dataLoad({
    dcName: 'getVendorList',
    start: param.start,
    limit: param.limit,
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

// 查询参数列表
export function listCollectType(param) {
  return dataLoad({
    dcName: 'getCollectTypeList',
    start: param.start,
    limit: param.limit,
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

// 查询参数详细
export function getCollectType(collectTypeId) {
  return request({
    url: '/quota/collectType/' + collectTypeId,
    method: 'get'
  })
}

// 根据参数键名查询参数值
export function getCollectTypeKey(collectTypeKey) {
  return request({
    url: '/quota/collectType/collectTypeKey/' + collectTypeKey,
    method: 'get'
  })
}

// 新增参数配置
export function addCollectType(data) {
  return request({
    url: '/quota/collectType',
    method: 'post',
    data: data
  })
}

// 修改参数配置
export function updateCollectType(data) {
  return request({
    url: '/quota/collectType',
    method: 'put',
    data: data
  })
}

// 删除参数配置
export function delCollectType(collectTypeId) {
  return request({
    url: '/quota/collectType/' + collectTypeId,
    method: 'delete'
  })
}

// // 导出参数
// export function exportCollectType(query) {
//   return request({
//     url: '/quota/collectType/export',
//     method: 'get',
//     params: query
//   })
// }

export function getVendorList(param) {
  return dataLoad({
    dcName: 'getVendorList',
    start: param.start,
    limit: param.limit,
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function addVendor(param) {
  return dataLoad({
    dcName: 'addVendor',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function delVendor(param) {
  return dataLoad({
    dcName: 'delVendor',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function getVendorById(param) {
  return dataLoad({
    dcName: 'getVendorById',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function updateVendor(param) {
  return dataLoad({
    dcName: 'updateVendor',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

