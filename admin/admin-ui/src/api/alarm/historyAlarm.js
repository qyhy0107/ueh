import { dataLoad } from '@/api/dataservice/pageDataSet'

// 获取列表数据
export function getHistoryAlarmDetail(param) {
  return dataLoad({
    // dcId:1201,
    start: param.start,
    limit: param.end,
    dcName: 'getHistoryAlarmDetail',
    filter: JSON.stringify(param)
  })
}
// 根据时间获取列表数据
export function getHistoryTimeAlarmDetail(param) {
  return dataLoad({
    // dcId:1201,
    start: param.start,
    limit: param.end,
    dcName: 'getHistoryTimeAlarmDetail',
    filter: JSON.stringify(param)
  })
}
// 根据告警级别
export function getAlarmLevel(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getAlarmLevel',
    filter: JSON.stringify(param)
  })
}

