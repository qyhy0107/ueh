import { dataLoad } from '@/api/dataservice/pageDataSet'

// 查询设备分类下拉
export function findCategorySelect(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'findCategorySelect',
    filter: JSON.stringify(params)
  })
}

// 查询分组下拉
export function findGroupSelect(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'findGroupSelect',
    filter: JSON.stringify(params)
  })
}

// 查询处理组列表
export function listGroups(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'findMonitoryGroupList',
    start: params.pageNum,
    limit: params.pageSize,
    filter: JSON.stringify(params)
  })
}
// 查询处理组id
export function findGroupIdByName(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'findGroupIdByName',
    filter: JSON.stringify(params)
  })
}
// 新增处理组和分类
export function addGroupCategory(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'addGroupCategory',
    filter: JSON.stringify(params)
  })
}

// 新增处理组
export function addGroup(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'addHandleGroup',
    filter: JSON.stringify(params)
  })
}
// 更新处理组
export function updateGroup(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'updateGroup',
    filter: JSON.stringify(params)
  })
}
// 删除处理组关系
export function deleteGroupCategory(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'deleteGroupCategory',
    filter: JSON.stringify(params)
  })
}
// 删除处理组
export function deleteGroup(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'deleteGroup',
    filter: JSON.stringify(params)
  })
}
// 更新处理组关系
export function updateGroupCategory(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'updateGroupCategory',
    filter: JSON.stringify(params)
  })
}

// 查询处理组
export function getGroupById(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'getGroupById',
    filter: JSON.stringify(params)
  })
}
// 查询关联分类id
export function getCategoryIds(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'getCategoryIds',
    filter: JSON.stringify(params)
  })
}
