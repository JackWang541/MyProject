import request from '../utils/request'

// 获取所有仓库
export function getWarehouseList(params) {
  return request({
    url: '/warehouse',
    method: 'get',
    params
  })
}

// 根据ID获取仓库
export function getWarehouseById(id) {
  return request({
    url: `/warehouse/${id}`,
    method: 'get'
  })
}

// 创建仓库
export function createWarehouse(data) {
  return request({
    url: '/warehouse',
    method: 'post',
    data
  })
}

// 更新仓库
export function updateWarehouse(data) {
  return request({
    url: '/warehouse',
    method: 'put',
    data
  })
}

// 删除仓库
export function deleteWarehouse(id) {
  return request({
    url: `/warehouse/${id}`,
    method: 'delete'
  })
}

// 根据仓库名称查询
export function getWarehouseByName(name) {
  return request({
    url: `/warehouse/name/${name}`,
    method: 'get'
  })
}
