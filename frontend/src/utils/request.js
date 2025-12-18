import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 可以在这里添加token等认证信息
    return config
  },
  error => {
    // 处理请求错误
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 统一处理响应格式
    if (res.code === 200) {
      return res.data
    } else {
      // 处理业务错误
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    // 处理响应错误
    let message = ''
    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response
      switch (status) {
        case 400:
          message = data.message || '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          // 可以在这里跳转到登录页面
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = `请求地址不存在: ${error.response.config.url}`
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `请求失败: ${status}`
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      message = '请求超时，请稍后重试'
    } else {
      // 请求配置出错
      message = `请求错误: ${error.message}`
    }
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
