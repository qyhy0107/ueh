import request from '@/utils/request'

// 数据分发
export function dataLoad(data) {
  return request({
    url: '/data/load',
    method: 'post',
    data: data
  })
}
