import request from '@/utils/request'

// 分页查询登录日志
export function getLoginLogs(params) {
  return request({
    url: '/log/page',
    method: 'get',
    params
  })
}
