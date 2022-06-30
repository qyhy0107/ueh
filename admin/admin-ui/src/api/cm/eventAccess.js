import {dataLoad} from '@/api/dataservice/pageDataSet'


export function getEventProbeDefine(param){


  return dataLoad({
    //dcId:1201,
    start:param.start,
    limit:param.limit,
    dcName:'getEventProbeDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function addEventProbeDefine(param){
  return dataLoad({
    //dcId:1201,
    dcName:'addEventProbeDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function delEventProbeDefine(param){
  return dataLoad({
    //dcId:1201,
    dcName:'delEventProbeDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}

export function getAccessIdByApplicationName(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getAccessIdByApplicationName',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function getEventProbeDefineById(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getEventProbeDefineById',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function updateEventProbeDefine(param){
  return dataLoad({
    //dcId:1201,
    dcName:'updateEventProbeDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function getEventColumnDefineMapping(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getEventColumnDefineMapping',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function getEventSeverityDefineMapping(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getEventSeverityDefineMapping',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function getEventSourceTypeSelect(param) {
  return dataLoad({
    projectId:param.projectId,
    dcName: 'getEventSourceTypeSelect',
    filter: JSON.stringify(param)
  })
}
