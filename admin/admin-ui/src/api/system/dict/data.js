import request from '@/utils/request'
import { dataLoad } from '@/api/dataservice/dataset'

// 查询字典数据列表
export function listData(query) {
  return request({
    url: '/system/dict/data/list',
    method: 'get',
    params: query
  })
}

// 查询字典数据详细
export function getData(dictCode) {
  return request({
    url: '/system/dict/data/' + dictCode,
    method: 'get'
  })
}

// 根据字典类型查询字典数据信息
export function getDicts(dictType) {
  return request({
    url: '/system/dict/data/dictType/' + dictType,
    method: 'get'
  })
}

// 新增字典数据
export function addData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data: data
  })
}

// 修改字典数据
export function updateData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data: data
  })
}

// 修改跳转菜单
export function updateRouter(data) {
  return dataLoad({
    projectId: data.projectId,
    dcName: 'updateRouter',
    filter: JSON.stringify(data)
  })
}

// 删除字典数据
export function delData(dictCode) {
  return request({
    url: '/system/dict/data/' + dictCode,
    method: 'delete'
  })
}

// 导出字典数据
export function exportData(query) {
  return request({
    url: '/system/dict/data/export',
    method: 'get',
    params: query
  })
}

export function changeDictLabelStatus(param) {
  return dataLoad({
    // dcId:1222,
    dcName: 'changeDictLabelStatus',
    filter: JSON.stringify(param)
  })
}
export function getBaseDictLabel(param) {
  return dataLoad({
    projectId: param.projectId,
    dcName: 'getBaseDictLabel',
    filter: JSON.stringify(param)
  })
}
export function getCountDictLabel(param) {
  return dataLoad({
    // dcId:1222,
    dcName: 'getCountDictLabel',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function updateDataJudge(param) {
  return dataLoad({
    // dcId:1222,
    dcName: 'updateDataJudge',
    filter: JSON.stringify(param)
  })
}
