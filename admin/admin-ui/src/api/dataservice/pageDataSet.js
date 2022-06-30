import request from '@/utils/request'

// 查询参数列表
export function listPageDataSet(query) {
  return request({
    url: '/dataservice/pageDataSet/list',
    method: 'get',
    params: query
  })
}

export function optionselect() {
  return request({
    url: '/dataservice/pageDataSet/optionselect',
    method: 'get'
  })
}
// 查询参数详细
export function getPageDataSet(pageDataSetId) {
  return request({
    url: '/dataservice/pageDataSet/' + pageDataSetId,
    method: 'get'
  })
}

// 校验数据集
export function checkPageDataSetKeyUnique(data) {
  return request({
    url: '/dataservice/pageDataSet/checkPageDataSetKeyUnique/',
    method: 'post',
    data: data
  })
}

// 根据参数键名查询参数值
export function getPageDataSetKey(pageDataSetKey) {
  return request({
    url: '/dataservice/pageDataSet/pageDataSetKey/' + pageDataSetKey,
    method: 'get'
  })
}

// 新增参数配置
export function addPageDataSet(data) {
  return request({
    url: '/dataservice/pageDataSet',
    method: 'post',
    data: data
  })
}

// 修改参数配置
export function updatePageDataSet(data) {
  return request({
    url: '/dataservice/pageDataSet',
    method: 'put',
    data: data
  })
}

// 删除参数配置
export function delPageDataSet(pageDataSetId) {
  return request({
    url: '/dataservice/pageDataSet/' + pageDataSetId,
    method: 'delete'
  })
}


// 数据分发
export function dataLoad(data) {
  return request({
    url: '/data/load',
    method: 'post',
    data: data
  })
}

// // 导出参数
// export function exportPageDataSet(query) {
//   return request({
//     url: '/seecloudCfet/pageDataSet/export',
//     method: 'get',
//     params: query
//   })
// }



