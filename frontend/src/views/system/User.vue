<template>
  <div class="system-user">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>用户管理</h3>
          <el-button type="primary" @click="handleAdd" :icon="Plus">新增用户</el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="searchForm.nickname" placeholder="请输入昵称" clearable />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="请选择角色" clearable>
            <el-option label="系统管理员" value="root" />
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="resetSearch" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 用户列表 -->
      <el-table v-loading="loading" :data="userList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="nickname" label="昵称" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="电话" width="150" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag v-if="scope.row.role === 'root'" type="danger">系统管理员</el-tag>
            <el-tag v-else-if="scope.row.role === 'admin'" type="warning">管理员</el-tag>
            <el-tag v-else type="success">普通用户</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录时间" width="200" />
        <el-table-column prop="createTime" label="创建时间" width="200" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)" :icon="Edit">编辑</el-button>
            <el-button type="success" size="small" @click="handleResetPassword(scope.row)" :icon="Key">重置密码</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form ref="userFormRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item prop="username" label="用户名">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item prop="password" label="密码" v-if="!formData.id">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item prop="nickname" label="昵称">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item prop="email" label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item prop="phone" label="电话">
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item prop="role" label="角色">
          <el-select v-model="formData.role" placeholder="请选择角色">
            <el-option label="系统管理员" value="root" />
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item prop="status" label="状态">
          <el-select v-model="formData.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 重置密码对话框 -->
    <el-dialog
      v-model="resetPasswordVisible"
      title="重置密码"
      width="400px"
    >
      <el-form ref="resetPasswordFormRef" :model="resetPasswordForm" :rules="resetPasswordRules" label-width="100px">
        <el-form-item prop="newPassword" label="新密码">
          <el-input v-model="resetPasswordForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item prop="confirmPassword" label="确认密码">
          <el-input v-model="resetPasswordForm.confirmPassword" type="password" placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="resetPasswordVisible = false">取消</el-button>
          <el-button type="primary" @click="handleResetPasswordSubmit" :loading="resetPasswordLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh, Key } from '@element-plus/icons-vue'
import { getUsers, addUser, updateUser, deleteUser, resetPassword } from '@/api/user'

const userList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const resetPasswordVisible = ref(false)
const submitLoading = ref(false)
const resetPasswordLoading = ref(false)
const userFormRef = ref(null)
const resetPasswordFormRef = ref(null)

// 搜索表单
const searchForm = reactive({
  username: '',
  nickname: '',
  role: '',
  status: ''
})

// 分页配置
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表单数据
const formData = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  role: '',
  status: 1
})

// 重置密码表单
const resetPasswordForm = reactive({
  userId: null,
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const formRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 确认密码验证
const confirmPasswordValidator = (rule, value, callback) => {
  if (value !== resetPasswordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 重置密码验证规则
const resetPasswordRules = reactive({
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: confirmPasswordValidator, trigger: 'blur' }
  ]
})

// 加载用户列表
const loadUserList = () => {
  loading.value = true
  getUsers({
    ...searchForm,
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize
  })
    .then(res => {
      userList.value = res.records
      pagination.total = res.total
    })
    .catch(err => {
      console.error('获取用户列表失败:', err)
    })
    .finally(() => {
      loading.value = false
    })
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadUserList()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.pageNum = 1
  loadUserList()
}

// 新增用户
const handleAdd = () => {
  dialogVisible.value = true
  // 重置表单
  Object.keys(formData).forEach(key => {
    formData[key] = ''
  })
  formData.status = 1
}

// 编辑用户
const handleEdit = (row) => {
  dialogVisible.value = true
  // 复制行数据到表单
  Object.assign(formData, row)
}

// 提交表单
const handleSubmit = () => {
  userFormRef.value.validate((valid) => {
    if (valid) {
      submitLoading.value = true
      const request = formData.id ? updateUser(formData) : addUser(formData)
      request
        .then(() => {
          ElMessage.success(formData.id ? '更新成功' : '新增成功')
          dialogVisible.value = false
          loadUserList()
        })
        .catch(err => {
          console.error('操作失败:', err)
        })
        .finally(() => {
          submitLoading.value = false
        })
    }
  })
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户"${row.username}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      deleteUser(row.id)
        .then(() => {
          ElMessage.success('删除成功')
          loadUserList()
        })
        .catch(err => {
          console.error('删除失败:', err)
        })
    })
    .catch(() => {
      // 取消删除
    })
}

// 重置密码
const handleResetPassword = (row) => {
  resetPasswordVisible.value = true
  resetPasswordForm.userId = row.id
  resetPasswordForm.newPassword = ''
  resetPasswordForm.confirmPassword = ''
}

// 提交重置密码
const handleResetPasswordSubmit = () => {
  resetPasswordFormRef.value.validate((valid) => {
    if (valid) {
      resetPasswordLoading.value = true
      resetPassword(resetPasswordForm.userId, resetPasswordForm.newPassword)
        .then(() => {
          ElMessage.success('密码重置成功')
          resetPasswordVisible.value = false
        })
        .catch(err => {
          console.error('密码重置失败:', err)
        })
        .finally(() => {
          resetPasswordLoading.value = false
        })
    }
  })
}

// 状态变更
const handleStatusChange = (row) => {
  updateUser(row)
    .then(() => {
      ElMessage.success('状态更新成功')
    })
    .catch(err => {
      console.error('状态更新失败:', err)
      // 恢复原有状态
      loadUserList()
    })
}

// 分页大小变更
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadUserList()
}

// 页码变更
const handleCurrentChange = (current) => {
  pagination.pageNum = current
  loadUserList()
}

// 生命周期钩子
onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.system-user {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
