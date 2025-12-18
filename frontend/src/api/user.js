import request from '@/utils/request'

// 分页查询用户列表
export function getUsers(params) {
  return request({
    url: '/user/page',
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
