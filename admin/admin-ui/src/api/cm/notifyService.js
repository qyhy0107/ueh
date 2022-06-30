import { dataLoad } from '@/api/dataservice/pageDataSet'

export function getHandlerRulesDefine(param) {
  return dataLoad({
    // dcId:1201,
    start: param.start,
    limit: param.limit,
    dcName: 'getHandlerRulesDefine',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
/* export function addNotificationConfiguration(param){
  return dataLoad({
    //dcId:1201,
    dcName:'addNotificationConfiguration',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}

export function getPermiRole(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getPermiRole',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function getUserByRoleId(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getUserByRoleId',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function delNotificationConfiguration(param){
  return dataLoad({
    //dcId:1201,
    dcName:'delNotificationConfiguration',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function getEventNotificationConfigurationById(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getEventNotificationConfigurationById',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}*/
export function getNotifyServices(param) {
  return dataLoad({
    // dcId:1201,
    start: param.start,
    limit: param.limit,
    dcName: 'notifySettingSelect',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function updateNotifyService(param) {
  return dataLoad({
    dcName: 'updateNotifyService',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function addNotifyService(param) {
  return dataLoad({
    dcName: 'addNotifyService',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
/* export function getEventNotificationService(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getEventNotificationService',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}
export function updateEventNotificationService(param){
  return dataLoad({
    //dcId:1201,
    dcName:'updateEventNotificationService',
    projectId:param.projectId,
    filter:JSON.stringify(param)
  })
}*/
