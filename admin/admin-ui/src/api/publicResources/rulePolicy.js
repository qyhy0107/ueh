import {dataLoad} from '@/api/dataservice/pageDataSet'


export function getEventTemDefine(param){

  return dataLoad({
    //dcId:1201,
    start:param.start,
    limit:param.limit,
    dcName:'getEventTemDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function addEventTemDefine(param){
  return dataLoad({
    //dcId:1201,
    dcName:'addEventTemDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function delEventTemDefine(param){
  return dataLoad({
    //dcId:1201,
    dcName:'delEventTemDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function updateEventSeverityDefine(param){
  return dataLoad({
    //dcId:1201,
    dcName:'updateEventSeverityDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function getEventTemDefineById(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getEventTemDefineById',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function updateEventTemDefine(param){
  return dataLoad({
    //dcId:1201,
    dcName:'updateEventTemDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
