import request from '@/utils/request'
import { dataLoad } from '@/api/dataservice/dataset'
// 下拉列表
export function getPhysicalCategories(param) {
  return dataLoad({
    // start:param.start,
    // limit:param.limit,
    dcName: 'getPhysicalCategory',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

// 查询参数列表
/* export function listPhysicalCategory(param) {
  return dataLoad({
    dcName:'',
    projectId:param.projectId,
    engine:param.engine,
    filter:JSON.stringify(param)
  })
} */

// 查询参数详细
export function getPhysicalCategory(param) {
  return dataLoad({
    dcName: 'getPhysicalCategoryByIdOrKey',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 获取当前角色所有可访问数据类型列表
export function getDataTypeOptions(param) {
  return dataLoad({
    dcName: 'getDataTypeOptions',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 获取当前角色所有可访问数据类型列表
export function getParentDataTypeOptions(param) {
  return dataLoad({
    dcName: 'getParentDataTypeOptions',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 获取当前角色所有可访问数据列表
export function getDataOptions(param) {
  return dataLoad({
    dcName: 'getDataOptions',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 删除当前角色可访问数据列表
export function deleteRoleData(param) {
  return dataLoad({
    dcName: 'deleteRoleData',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
/* export function deleteRoleDataCategory(param) {
  return dataLoad({
    dcName: 'deleteRoleDataCategory',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
} */
// 新增当前角色已选可访问数据列表
export function insertRoleData(param) {
  return dataLoad({
    dcName: 'insertRoleData',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
/* export function insertRoleDataCategory(param) {
  return dataLoad({
    dcName: 'insertRoleDataCategory',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
} */
// 删除当前角色可访问数据类型
export function deleteRoleDataTypes(param) {
  return dataLoad({
    dcName: 'deleteRoleDataTypes',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 新增当前角色已选可访问数据类型
export function insertRoleDataTypes(param) {
  return dataLoad({
    dcName: 'insertRoleDataTypes',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 获取当前角色已选数据列表
export function getRoleData(param) {
  return dataLoad({
    dcName: 'getRoleData',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 获取当前角色已选数据类型列表
export function getRoleDataType(param) {
  return dataLoad({
    dcName: 'getRoleDataType',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 获取当前角色已选数据类型列表
/* export function getRoleDataCategory(param) {
  return dataLoad({
    dcName: 'getRoleDataCategory',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
} */

/* export function getPhysicalCategoryByName(name) {
  return request({
    url: '/quota/physicalCategory/name/'+name,
    method: 'get'
  })
}

// 根据参数键名查询参数值
export function getPhysicalCategoryKey(physicalCategoryKey) {
  return request({
    url: '/quota/physicalCategory/physicalCategoryKey/' + physicalCategoryKey,
    method: 'get'
  })
} */

// 新增参数配置
export function addPhysicalCategory(param) {
  return dataLoad({
    dcName: 'addPhysicalCategory',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

// 修改参数配置
export function updatePhysicalCategory(param) {
  return dataLoad({
    dcName: 'updatePhysicalCategory',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

// 删除参数配置
export function delPhysicalCategory(param) {
  return dataLoad({
    dcName: 'deletePhysicalCategory',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
