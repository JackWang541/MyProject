<template>
  <div class="system-role">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>角色管理</h3>
        </div>
      </template>
      
      <!-- 角色列表 -->
      <div class="role-list">
        <el-radio-group v-model="currentRole" @change="handleRoleChange">
          <el-radio-button label="root">系统管理员</el-radio-button>
          <el-radio-button label="admin">管理员</el-radio-button>
          <el-radio-button label="user">普通用户</el-radio-button>
        </el-radio-group>
      </div>
      
      <!-- 权限分配 -->
      <div class="permission-assign" style="margin-top: 20px;">
        <el-card title="权限分配">
          <el-checkbox-group v-model="checkedPermissions" @change="handlePermissionChange">
            <el-checkbox v-for="permission in permissionTree" :key="permission.id" :label="permission.id">
              {{ permission.permissionName }}
              <template v-if="permission.children && permission.children.length > 0">
                <el-divider direction="vertical" style="height: 20px;" />
                <el-checkbox-group v-model="checkedPermissions">
                  <el-checkbox 
                    v-for="child in permission.children" 
                    :key="child.id" 
                    :label="child.id"
                    style="margin-left: 20px;"
                  >
                    {{ child.permissionName }}
                  </el-checkbox>
                </el-checkbox-group>
              </template>
            </el-checkbox>
          </el-checkbox-group>
          <div style="margin-top: 20px; text-align: right;">
            <el-button type="primary" @click="savePermissions" :loading="saveLoading">保存权限</el-button>
          </div>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getRolePermissions, getAllPermissions, updateRolePermissions } from '@/api/role'

const currentRole = ref('root')
const saveLoading = ref(false)
const allPermissions = ref([])
const rolePermissions = ref([])
const checkedPermissions = ref([])
const permissionTree = ref([])

// 加载所有权限
const loadAllPermissions = () => {
  getAllPermissions()
    .then(res => {
      allPermissions.value = res
      // 构建权限树
      buildPermissionTree()
    })
    .catch(err => {
      console.error('获取所有权限失败:', err)
    })
}

// 加载角色权限
const loadRolePermissions = () => {
  getRolePermissions(currentRole.value)
    .then(res => {
      rolePermissions.value = res
      // 提取权限ID数组
      checkedPermissions.value = res.map(permission => permission.id)
    })
    .catch(err => {
      console.error('获取角色权限失败:', err)
    })
}

// 构建权限树
const buildPermissionTree = () => {
  // 先找出所有顶级权限
  const topLevelPermissions = allPermissions.value.filter(p => p.parentId === 0)
  
  // 递归构建子权限
  const buildChildren = (permission) => {
    const children = allPermissions.value.filter(p => p.parentId === permission.id)
    if (children.length > 0) {
      permission.children = children
      children.forEach(child => buildChildren(child))
    }
  }
  
  // 构建树结构
  topLevelPermissions.forEach(permission => buildChildren(permission))
  permissionTree.value = topLevelPermissions
}

// 角色切换
const handleRoleChange = () => {
  loadRolePermissions()
}

// 权限变更
const handlePermissionChange = () => {
  console.log('权限变更:', checkedPermissions.value)
}

// 保存权限
const savePermissions = () => {
  saveLoading.value = true
  updateRolePermissions(currentRole.value, checkedPermissions.value)
    .then(() => {
      ElMessage.success('权限保存成功')
    })
    .catch(err => {
      console.error('保存权限失败:', err)
    })
    .finally(() => {
      saveLoading.value = false
    })
}

// 生命周期钩子
onMounted(() => {
  loadAllPermissions()
  loadRolePermissions()
})
</script>

<style scoped>
.system-role {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.role-list {
  margin-bottom: 20px;
}
</style>
