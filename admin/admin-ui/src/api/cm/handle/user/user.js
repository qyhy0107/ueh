import { dataLoad } from '@/api/dataservice/pageDataSet'

// 查询设备下拉
export function findResourceSelect(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'findResourceSelect',
    filter: JSON.stringify(params)
  })
}
// 查询设备下拉
export function findResourceIdSelect(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'findResourceIdSelect',
    filter: JSON.stringify(params)
  })
}
// 查询设备下拉
export function findResourceparentIdSelect(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'findResourceparentIdSelect',
    filter: JSON.stringify(params)
  })
}

// 查询处理人
export function findHandleUserByDomainName(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'findHandleUserByDomainName',
    filter: JSON.stringify(params)
  })
}
// 查询处理人
export function findHandleUserById(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'findHandleUserById',
    filter: JSON.stringify(params)
  })
}
// 查询处理人
export function findMonitoryUserList(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'findMonitoryUserList',
    start: params.pageNum,
    limit: params.pageSize,
    filter: JSON.stringify(params)
  })
}
// 查询处理人和组关系
export function getUserAndGroup(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'getUserAndGroup',
    filter: JSON.stringify(params)
  })
}
// 查询处理人和资源关系
export function getUserAndResource(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'getUserAndResource',
    filter: JSON.stringify(params)
  })
}
// 查询处理人和资源关系
export function getUserAndResourceClass(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'getUserAndResourceClass',
    filter: JSON.stringify(params)
  })
}

// 添加处理用户
export function addHandleUser(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'addHandleUser',
    filter: JSON.stringify(params)
  })
}
// 新增处理人和处理组关系
export function addHandleUserAndGroup(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'addHandleUserAndGroup',
    filter: JSON.stringify(params)
  })
}
// 新增处理人和资源关系
export function addHandleUserAndResource(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'addHandleUserAndResource',
    filter: JSON.stringify(params)
  })
}
// 修改处理人
export function updateHandleUser(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'updateHandleUser',
    filter: JSON.stringify(params)
  })
}
// 删除处理人和处理组关系
export function deleteUserGroup(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'deleteUserGroup',
    filter: JSON.stringify(params)
  })
}
// 删除处理人和资源关系
export function deleteUserResource(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'deleteUserResource',
    filter: JSON.stringify(params)
  })
}
// 删除处理人
export function deleteUser(params) {
  return dataLoad({
    projectId: params.projectId,
    dcName: 'deleteUser',
    filter: JSON.stringify(params)
  })
}
