import request from '@/utils/request'

// 查询出库记录列表
export function listOutbound(query) {
  return request({
    url: '/outbound',
    method: 'get',
    params: query
  })
}

// 查询出库记录详细
export function getOutbound(id) {
  return request({
    url: '/outbound/' + id,
    method: 'get'
  })
}

// 新增出库记录
export function addOutbound(data) {
  return request({
    url: '/outbound',
    method: 'post',
    data: data
  })
}

// 修改出库记录
export function updateOutbound(data) {
  return request({
    url: '/outbound',
    method: 'put',
    data: data
  })
}

// 删除出库记录
export function delOutbound(id) {
  return request({
    url: '/outbound/' + id,
    method: 'delete'
  })
}
