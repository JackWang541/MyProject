import request from '@/utils/request'

// 查询入库记录列表
export function listInbound(query) {
  return request({
    url: '/inbound',
    method: 'get',
    params: query
  })
}

// 查询入库记录详细
export function getInbound(id) {
  return request({
    url: '/inbound/' + id,
    method: 'get'
  })
}

// 新增入库记录
export function addInbound(data) {
  return request({
    url: '/inbound',
    method: 'post',
    data: data
  })
}

// 修改入库记录
export function updateInbound(data) {
  return request({
    url: '/inbound',
    method: 'put',
    data: data
  })
}

// 删除入库记录
export function delInbound(id) {
  return request({
    url: '/inbound/' + id,
    method: 'delete'
  })
}
