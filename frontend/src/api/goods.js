import request from '@/utils/request'

// 查询货物列表
export function listGoods(query) {
  return request({
    url: '/goods',
    method: 'get',
    params: query
  })
}

// 查询货物详细
export function getGoods(id) {
  return request({
    url: '/goods/' + id,
    method: 'get'
  })
}

// 新增货物
export function addGoods(data) {
  return request({
    url: '/goods',
    method: 'post',
    data: data
  })
}

// 修改货物
export function updateGoods(data) {
  return request({
    url: '/goods',
    method: 'put',
    data: data
  })
}

// 删除货物
export function delGoods(id) {
  return request({
    url: '/goods/' + id,
    method: 'delete'
  })
}

// 启用货物
export function enableGoods(id) {
  return request({
    url: '/goods/enable/' + id,
    method: 'put'
  })
}

// 禁用货物
export function disableGoods(id) {
  return request({
    url: '/goods/disable/' + id,
    method: 'put'
  })
}
