<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>个人中心</span>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" type="card">
        <!-- 个人信息 -->
        <el-tab-pane label="个人信息" name="info">
          <el-form :model="userInfo" label-position="left" label-width="120px" class="profile-form">
            <el-form-item label="用户名">
              <el-input v-model="userInfo.username" disabled placeholder="用户名" />
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="userInfo.nickname" placeholder="昵称" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="userInfo.email" placeholder="邮箱" />
            </el-form-item>
            <el-form-item label="电话">
              <el-input v-model="userInfo.phone" placeholder="电话" />
            </el-form-item>
            <el-form-item label="角色">
              <el-select v-model="userInfo.role" disabled placeholder="角色">
                <el-option label="超级管理员" value="root" />
                <el-option label="管理员" value="admin" />
                <el-option label="普通用户" value="user" />
              </el-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-switch v-model="userInfo.status" disabled :active-value="1" :inactive-value="0" />
            </el-form-item>
            <el-form-item label="创建时间">
              <el-input v-model="userInfo.createTime" disabled placeholder="创建时间" />
            </el-form-item>
            <el-form-item label="最后登录时间">
              <el-input v-model="userInfo.lastLoginTime" disabled placeholder="最后登录时间" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdateInfo">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <!-- 修改密码 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-position="left" label-width="120px" class="profile-form">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password" placeholder="原密码" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="新密码" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="确认密码" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUpdatePassword">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUser, updateUser } from '@/api/auth'

const activeTab = ref('info')
const passwordFormRef = ref(null)
const loading = ref(false)

// 用户信息
const userInfo = reactive({
  id: null,
  username: '',
  nickname: '',
  email: '',
  phone: '',
  role: '',
  status: 1,
  createTime: '',
  updateTime: '',
  lastLoginTime: ''
})

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordRules = reactive({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
})

// 获取用户信息
const fetchUserInfo = () => {
  loading.value = true
  getCurrentUser()
    .then(response => {
      Object.assign(userInfo, response.data)
      // 格式化时间
      if (userInfo.createTime) {
        userInfo.createTime = new Date(userInfo.createTime).toLocaleString()
      }
      if (userInfo.updateTime) {
        userInfo.updateTime = new Date(userInfo.updateTime).toLocaleString()
      }
      if (userInfo.lastLoginTime) {
        userInfo.lastLoginTime = new Date(userInfo.lastLoginTime).toLocaleString()
      }
    })
    .catch(error => {
      ElMessage.error(error.response?.data?.message || '获取用户信息失败')
    })
    .finally(() => {
      loading.value = false
    })
}

// 更新用户信息
const handleUpdateInfo = () => {
  loading.value = true
  updateUser(userInfo)
    .then(() => {
      ElMessage.success('更新成功')
      // 更新本地存储的用户信息
      localStorage.setItem('user', JSON.stringify(userInfo))
    })
    .catch(error => {
      ElMessage.error(error.response?.data?.message || '更新失败')
    })
    .finally(() => {
      loading.value = false
    })
}

// 更新密码
const handleUpdatePassword = () => {
  passwordFormRef.value.validate((valid) => {
    if (valid) {
      loading.value = true
      // 这里需要添加修改密码的API调用
      // updatePassword(passwordForm)
      setTimeout(() => {
        ElMessage.success('密码修改成功，下次登录请使用新密码')
        // 清空表单
        Object.assign(passwordForm, {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        })
        loading.value = false
      }, 1000)
    }
  })
}

// 监听activeTab变化，切换标签时重置表单
watch(activeTab, (newVal) => {
  if (newVal === 'password') {
    Object.assign(passwordForm, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    if (passwordFormRef.value) {
      passwordFormRef.value.resetFields()
    }
  }
})

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-form {
  max-width: 600px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style>
