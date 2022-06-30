
import { dataLoad } from '@/api/dataservice/dataset'

export function getResourceAttributeGroupList(param) {
  return dataLoad({
    dcName: 'getResourceAttributeGroupList',
    start: param.start,
    limit: param.limit,
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function addResourceAttributeGroup(param) {
  return dataLoad({
    dcName: 'addResourceAttributeGroup',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function delResourceAttributeGroup(param) {
  return dataLoad({
    dcName: 'delResourceAttributeGroup',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function getResourceAttributeGroupById(param) {
  return dataLoad({
    dcName: 'getResourceAttributeGroupByIdOrKey',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function updateResourceAttributeGroup(param) {
  return dataLoad({
    dcName: 'updateResourceAttributeGroup',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
