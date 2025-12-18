import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 登出
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 获取当前登录用户信息
export function getCurrentUser() {
  return request({
    url: '/auth/getCurrentUser',
    method: 'post'
  })
}

// 获取用户列表
export function getUsers(params) {
  return request({
    url: '/user',
    method: 'get',
    params
  })
}

// 获取用户详情
export function getUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data) {
  return request({
    url: '/user',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

// 重置用户密码
export function resetPassword(id, newPassword) {
  return request({
    url: `/user/resetPassword/${id}`,
    method: 'put',
    params: { newPassword }
  })
}
