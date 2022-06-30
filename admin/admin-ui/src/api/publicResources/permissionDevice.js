import {dataLoad} from '@/api/dataservice/pageDataSet'

// SQLEngine:{}
export function getHandlerPermissionDevice(param){

  return dataLoad({
    //dcId:1201,
    start:param.start,
    limit:param.limit,
    dcName:'getHandlerPermissionDevice',
    projectId:param.projectId,
    filter:JSON.stringify(param),
  })
}
export function getRoles(param){

  return dataLoad({
    //dcId:1201,
    dcName:'getRoles',
    projectId:param.projectId,
    filter:JSON.stringify(param),
  })
}
export function getResPhysicalCategory(param){

  return dataLoad({
    //dcId:1201,
    dcName:'getResPhysicalCategory',
    projectId:param.projectId,
    filter:JSON.stringify(param),
  })
}

