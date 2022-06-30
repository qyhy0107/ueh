import { getEventList, deleteEvent, acknoledgeEvent, pushNotice } from '@/api/event/event-data'
import { dataLoad } from '@/api/dataservice/dataset'
import request from '@/utils/request'

// 获取关联策略
export function getRulesName(param) {
  return dataLoad({
    dcName: 'getRulesName',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
// 获取跳转信息
export function alarmTurnToHost(param) {
  return dataLoad({
    dcName: 'alarmTurnToHost',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
// 获取权限组
export function getAlarmsCategory(param) {
  return dataLoad({
    dcName: 'getAlarmsCategory',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
// 获取权限组
export function getAlarmsCategory1(param) {
  return dataLoad({
    dcName: 'getAlarmsCategory1',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
// 获取权限设备
export function getAlarmKeys(param) {
  return dataLoad({
    dcName: 'getAlarmKeys',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getEvents(param) {
  return getEventList(JSON.stringify({
    start: param.start,
    limit: param.limit,
    engine: param.engine,
    dcName: 'getEvents',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  }))
}
export function deleteEvents(param) {
  return deleteEvent(JSON.stringify({
    engine: param.engine,
    dcName: 'deleteEvents',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  }))
}

export function acknoledgeEvents(param) {
  return acknoledgeEvent(JSON.stringify({
    start: param.start,
    limit: param.limit,
    engine: param.engine,
    dcName: 'acknoledgeEvents',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  }))
}

export function batchAcknoledgeEvents(param) {
  return acknoledgeEvent(JSON.stringify({
    start: param.start,
    limit: param.limit,
    engine: param.engine,
    dcName: 'acknoledgeEvents',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  }))
}
export function batchCloseEvents(param) {
  return dataLoad({
    dcName: 'batchCloseEvents',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}

export function getPerson(param) {
  return dataLoad({
    dcName: 'getPerson',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
// 获取告警来源
export function getManager(param) {
  return dataLoad({
    dcName: 'getManager',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}

export function getPersonShow(param) {
  return dataLoad({
    dcName: 'getPersonShow',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}

export function getEventsForWaitRecover(param) {
  return dataLoad({
    start: param.start,
    limit: param.limit,
    dcName: 'getEventsForWaitRecover',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}

export function getEventsForRealtime(param) {
  return dataLoad({
    start: param.start,
    limit: param.limit,
    dcName: 'getEventsForRealtime',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function pushEventToNotifyService(param) {
  return pushNotice(JSON.stringify({
    projectId: param.projectId,
    filter: JSON.stringify(param)
  }))
}
export function getEventsForHistory(param) {
  return dataLoad({
    start: param.start,
    limit: param.limit,
    dcName: 'getEventsForHistory',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function batchDeleteHistoryEvents(param) {
  return dataLoad({
    dcName: 'deleteEventsForHistory',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}

export function batchCloseEventsForWeb(param) {
  return dataLoad({
    dcName: 'batchCloseEventsForWeb',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}

export function exportHistory(param) {
  return request({
    url: '/event/exportHistory',
    method: 'post',
    data: JSON.stringify(param)
  })
}

export function exportStatus(param) {
  return request({
    url: '/event/exportStatus',
    method: 'post',
    data: JSON.stringify(param)
  })
}

export function exportWaitRecover(param) {
  return request({
    url: '/event/exportWaitRecover',
    method: 'post',
    data: JSON.stringify(param)
  })
}

