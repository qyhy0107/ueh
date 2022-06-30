import { dataLoad } from '@/api/dataservice/pageDataSet'

export function getEventSeverityDefine(param) {
  return dataLoad({
    // dcId:1201,
    start: param.start,
    limit: param.limit,
    dcName: 'getEventSeverityDefine',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getEventSeverityDefineAll(param) {
  return dataLoad({
    dcName: 'getEventSeverityDefine',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function addEventSeverityDefine(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'addEventSeverityDefine',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function delEventSeverityDefine(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'delEventSeverityDefine',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function updateEventSeverityDefine(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'updateEventSeverityDefine',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getEventSeverityDefineById(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getEventSeverityDefineById',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function updateSyncEventLevel(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'updateSyncEventLevel',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
