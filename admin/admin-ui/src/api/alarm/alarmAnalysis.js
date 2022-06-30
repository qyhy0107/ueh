import {dataLoad} from '@/api/dataservice/pageDataSet'

//获取历史告警数
export function getAlarmCount(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getAlarmCount',
    filter:JSON.stringify(param)
  })
}
//获取告警折线图
export function getAlarmLada(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getAlarmLada',
    filter:JSON.stringify(param)
  })
}
//获取告警分析列表
export function getAlarmListData(param){
  return dataLoad({
    //dcId:1201,
    dcName:'getAlarmListData',
    filter:JSON.stringify(param)
  })
}





