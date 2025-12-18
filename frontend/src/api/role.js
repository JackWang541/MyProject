import request from '@/utils/request'

// 获取所有角色列表
export function getRoles() {
  return request({
    url: '/role/list',
    method: 'get'
  })
}

// 获取角色的权限列表
export function getRolePermissions(role) {
  return request({
    url: `/role/permissions/${role}`,
    method: 'get'
  })
}

// 获取所有权限列表
export function getAllPermissions() {
  return request({
    url: '/role/allPermissions',
    method: 'get'
  })
}

// 更新角色权限
export function updateRolePermissions(role, permissionIds) {
  return request({
    url: `/role/permissions/${role}`,
    method: 'put',
    data: permissionIds
  })
}
