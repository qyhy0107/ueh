import {dataLoad} from '@/api/dataservice/pageDataSet'


export function getEventColumnDefine(param){

  return dataLoad({
    //dcId:1201,
    start:param.start,
    limit:param.limit,
    dcName:'getEventColumnDefine',
    projectId:param.projectId,
    engine:param.engine,
    filter:JSON.stringify(param)
  })
}

/*export function getEventColumnDefines(param){

  return dataLoad({
    //dcId:1201
    dcName:'getEventColumnDefine',
    projectId:param.projectId,
    engine:param.engine,
    filter:JSON.stringify(param)
  })
}*/
export function getStandData(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getStandardRecord',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function addEventColumnDefine(param){
  return dataLoad({
    //dcId:1201,
    dcName:'addEventColumnDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function delEventColumnDefine(param){
  return dataLoad({
    //dcId:1201,
    dcName:'delEventColumnDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function updateEventColumnDefine(param){
  return dataLoad({
    //dcId:1201,
    dcName:'updateEventColumnDefine',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function getEventColumnDefineById(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getEventColumnDefineById',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function updateSyncEventField(param){
  return dataLoad({
    //dcId:1201,
    dcName:'updateSyncEventField',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
