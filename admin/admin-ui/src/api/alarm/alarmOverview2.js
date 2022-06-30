import {dataLoad} from '@/api/dataservice/pageDataSet'


//获取告警分析列表
export function getAlarmDetailList(param){
  return dataLoad({
    //dcId:1201,
    start:param.start,
    limit:param.end,
    dcName:'getAlarmDetailList',
    filter:JSON.stringify(param)
  })
}
