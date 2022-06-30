import {dataLoad} from '@/api/dataservice/dataset'

export function getResourceAttributeList(param){
  return dataLoad({
    dcName:'getResourceAttributeList',
    start:param.start,
    limit:param.limit,
    projectId:param.projectId,
    engine:param.engine,
    filter:JSON.stringify(param)
  })
}
export function getSelectAttributeGroup(param) {
  return dataLoad({
    dcName: 'getSelectAttributeGroup',
    filter: JSON.stringify(param)
  })
}
export function addResourceAttribute(param){
  return dataLoad({
    dcName:'addResourceAttribute',
    projectId:param.projectId,
    engine:param.engine,
    filter:JSON.stringify(param)
  })
}
export function delResourceAttributeById(param){
  return dataLoad({
    dcName:'delResourceAttributeById',
    projectId:param.projectId,
    engine:param.engine,
    filter:JSON.stringify(param)
  })
}
export function getResourceAttributeById(param){
  return dataLoad({
    dcName:'getResourceAttributeByIdOrKey',
    projectId:param.projectId,
    engine:param.engine,
    filter:JSON.stringify(param)
  })
}
export function updateResourceAttribute(param){
  return dataLoad({
    dcName:'updateResourceAttribute',
    projectId:param.projectId,
    engine:param.engine,
    filter:JSON.stringify(param)
  })
}
//获取资源类别名称
export function getSelectResourceAttribute(param){
  return dataLoad({
    dcName:'getSelectResourceAttribute',
    filter:JSON.stringify(param)
  })
}
