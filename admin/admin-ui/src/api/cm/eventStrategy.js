import { dataLoad } from '@/api/dataservice/pageDataSet'

export function getHandlerRulesDefine(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getHandlerRulesDefine',
    projectId: param.projectId,
    name: param.name,
    ruleType: param.ruleType,
    excludeRuleType: param.excludeRuleType,
    start: param.start,
    limit: param.limit,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

export function getResourceListMP(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getResourceListMP',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}

export function addHandlerRulesDefine(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'addHandlerRulesDefine',
    projectId: param.project_id,
    filter: JSON.stringify(param)
  })
}

export function deleteHandlerRulesDefine(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'deleteHandlerRulesDefine',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getManagerSelect(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getManagerSelect',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getPhysicalCategory(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getPhysicalCategory',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getPhysicalCategory1(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getPhysicalCategory1',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getAccessIdByApplicationName(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getAccessIdByApplicationName',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getEventProbeDefineById(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getEventProbeDefineById',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function updateEventProbeDefine(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'updateEventProbeDefine',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getEventColumnDefineMapping(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getEventColumnDefineMapping',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getEventSeverityDefineMapping(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getEventSeverityDefineMapping',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getResByCategoryName(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getResByCategoryName',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getEventStrategyById(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getEventStrategyById',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function updateHandlerRulesDefine(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'updateHandlerRulesDefine',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
export function getProbeSourceType(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getProbeSourceType',
    projectId: param.projectId,
    engine: param.engine,
    filter: JSON.stringify(param)
  })
}

export function setRoleDataScope(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'setRoleDataScope',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}

export function getSelectDictData(param) {
  return dataLoad({
    // dcId:1201,
    dcName: 'getSelectDictData',
    projectId: param.projectId,
    filter: JSON.stringify(param)
  })
}
