import request from '@/utils/request'
import {dataLoad} from '@/api/dataservice/pageDataSet'

export function addProperty(param){

  return dataLoad({
    dcName:'addProperty',
   filter:JSON.stringify(param)
  })
}zabDelProperty

export function zabProperty(query) {
  return request({
    url: '/zabbix/monitor',
    method: 'post',
    params: query
  })
}

export function zabDelProperty(query) {
  return request({
    url: '/zabbix/unmonitor',
    method: 'post',
    params: query
  })
}

export function updateProperty(param){

  return dataLoad({
    dcName: 'updateProperty',
    filter: JSON.stringify(param)
  })
}
export function getProperty(param){

  return dataLoad({
    dcName: 'getProperty',
    filter: JSON.stringify(param)
  })
}

export function  deleteProperty(param) {

  return dataLoad({
    dcName: 'deleteProperty',
    filter: JSON.stringify(param)
  })
}
export function  getList(param) {
  return dataLoad({
    start:param.start,
    limit:param.limit,
    dcName: 'getMoneyManageDatas',
    filter: JSON.stringify(param)
  })
}

export function getVendor(param){
  return dataLoad({
    dcName:'getVendor',
    filter:JSON.stringify(param)
  })
}


