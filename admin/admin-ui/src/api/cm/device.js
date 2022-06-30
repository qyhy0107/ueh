import { dataLoad } from '@/api/dataservice/dataset'
import request from '@/utils/request'
// 批量启停
export function startOrStopDevice(param) {
  return dataLoad({
    dcName: 'startOrStopDevice',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 设备信息操作
export function getResourceList(param) {
  return dataLoad({
    dcName: 'getResourceList',
    start: param.start,
    limit: param.limit,
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function getResourceById(param) {
  return dataLoad({
    dcName: 'getResourceById',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function addResource(param) {
  return dataLoad({
    dcName: 'addResource',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function updateResource(param) {
  return dataLoad({
    dcName: 'updateResource',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function delResourceById(param) {
  return dataLoad({
    dcName: 'delResourceById',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
// 设备属性操作
export function getResourceAttributeValueList(param) {
  return dataLoad({
    dcName: 'getResourceAttributeValueList',
    start: param.start,
    limit: param.limit,
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function getResourceAttributeValue(param) {
  return dataLoad({
    dcName: 'getResourceAttributeValueByIdOrKey',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function addResourceAttributeValue(param) {
  return dataLoad({
    dcName: 'addResourceAttributeValue',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function canAdd(param) {
  return dataLoad({
    dcName: 'canAdd',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function updateResourceAttributeValue(param) {
  return dataLoad({
    dcName: 'updateResourceAttributeValue',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}
export function delResourceAttributeValue(param) {
  return dataLoad({
    dcName: 'deleteResourceAttributeValue',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

// 其他的不知道干啥用的
export function getSelectCabinet(param) {
  return dataLoad({
    dcName: 'getSelectCabinet',
    filter: JSON.stringify(param)
  })
}
export function getSelectCabinetLayer(param) {
  return dataLoad({
    dcName: 'getSelectCabinetLayer',
    filter: JSON.stringify(param)
  })
}
export function getDefaultAttribute(param) {
  return dataLoad({
    dcName: 'getDefaultAttribute',
    filter: JSON.stringify(param)
  })
}
export function addBasicResourceAttr(param) {
  return dataLoad({
    dcName: 'addBasicResourceAttr',
    filter: JSON.stringify(param)
  })
}
export function getBasicAttribute(param) {
  return dataLoad({
    dcName: 'getBasicAttribute',
    filter: JSON.stringify(param)
  })
}
export function updateBasicResourceAttr(param) {
  return dataLoad({
    dcName: 'updateBasicResourceAttr',
    filter: JSON.stringify(param)
  })
}
export function getAttributeGroup(param) {
  return dataLoad({
    dcName: 'getAttributeGroup',
    filter: JSON.stringify(param)
  })
}
export function addResourceOtherAttribute(param) {
  return dataLoad({
    dcName: 'addResourceOtherAttribute',
    filter: JSON.stringify(param)
  })
}
export function getAotherResourceAttrbuteList(param) {
  return dataLoad({
    dcName: 'getAotherResourceAttrbuteList',
    filter: JSON.stringify(param)
  })
}
export function deleteResourceOtherAttribute(param) {
  return dataLoad({
    dcName: 'deleteResourceOtherAttribute',
    filter: JSON.stringify(param)
  })
}

export function getCabinetLayer(param) {
  return dataLoad({
    dcName: 'getCabinetLayer',
    filter: JSON.stringify(param)
  })
}

export function delResourceAttrbuiteByResourceId(param) {
  return dataLoad({
    dcName: 'delResourceAttrbuiteByResourceId',
    filter: JSON.stringify(param)
  })
}
// 获取属性名

export function getAttributeName(param) {
  return dataLoad({
    dcName: 'getAttributeName',
    filter: JSON.stringify(param)
  })
}
export function exportDevice(param) {
  return request({
    url: '/event/exportDevice',
    method: 'post',
    data: JSON.stringify(param)
  })
}
export function importDeviceTemplate(param) {
  return request({
    url: '/event/importDeviceTemplate',
    method: 'post',
    data: JSON.stringify(param)
  })
}

export function getZabbixHostGroups(param) {
  return dataLoad({
    dcName: 'getZabbixHostGroups',
    projectId: param.projectId
  })
}
export function getZabbixHost(param) {
  return dataLoad({
    dcName: 'getZabbixHost',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getHostRelation(param) {
  return dataLoad({
    dcName: 'getHostRelation',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}

export function startMonitor(param) {
  return request({
    url: '/zabbix/monitor/host',
    method: 'post',
    data: JSON.stringify(param)
  })
}
export function stopMonitor(param) {
  return request({
    url: '/zabbix/unmonitor/host',
    method: 'post',
    data: JSON.stringify(param)
  })
}
