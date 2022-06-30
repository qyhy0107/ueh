import request from '@/utils/request'

// 获取用户详细信息
export function getProjectId() {
  return request({
    url: '/getProjectId',
    method: 'get'
  })
}

