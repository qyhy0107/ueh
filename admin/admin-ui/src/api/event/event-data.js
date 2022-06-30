import request from '@/utils/request'

// 查询参数列表
export function getEventList(loadParams) {
  return request({
    url: '/event/display/load',
    method: 'post',
    data: loadParams
  });
}

export function deleteEvent(loadParams) {
  return request({
    url: '/event/display/delete',
    method: 'post',
    data: loadParams
  });
}
export function acknoledgeEvent(loadParams) {
  return request({
    url: '/event/display/acknoledge',
    method: 'post',
    data: loadParams
  });
}
export function batchAcknoledgeEvent(loadParams) {
  return request({
    url: '/event/display/acknoledge',
    method: 'post',
    data: loadParams
  });
}
export function pushNotice(loadParams) {
  return request({
    url: '/event/tx/receive',
    method: 'post',
    data: loadParams
  });
}
