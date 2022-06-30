// import request from '@/utils/request'
import { dataLoad } from '@/api/dataservice/dataset'
// 获取全部告警数量
export function getAllAlarmCount(param) {
  return dataLoad({
    // dcId:1200,
    dcName: 'getAllAlarmCount',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 获取全部未确认数量
export function getConfirmAll(param) {
  return dataLoad({
    // dcId:1200,
    dcName: 'getConfirmAll',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

export function getSixTypeCount(param) {
  return dataLoad({
    // dcId:1200,
    dcName: 'getSixTypeCount',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

export function getAlarmTypeCount(param) {
  return dataLoad({
    // dcId:1200,
    dcName: 'getAlarmTypeCount',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

export function getAlarmCategoryCount(param) {
  return dataLoad({
    // dcId:1200,
    dcName: 'getAlarmCategoryCount',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

export function getAlarmInfoTable(param) {
  return dataLoad({
    // dcId:1200,
    dcName: 'getAlarmInfoTable',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

export function getMonthsAlarmInfo(param) {
  return dataLoad({
    // dcId:1200,
    dcName: 'getMonthsAlarmInfo',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

export function turnHost(param) {
  return dataLoad({
    // dcId:1200,
    dcName: 'turnHost',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

// 获取时间周期
export function getAlarmTimeToSet(param) {
  return dataLoad({
    // dcId:1200,
    dcName: 'getAlarmTimeToSet',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

export function isTurnHost(param) {
  return dataLoad({
    // dcId:1200,
    dcName: 'isTurnHost',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

