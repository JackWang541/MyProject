<template>
  <div class="app-container">
    <el-container class="main-container">
      <el-aside width="200px" class="sidebar-container">
        <div class="logo">
          <h3>物流库存管理系统</h3>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          background-color="#001529"
          text-color="#fff"
          active-text-color="#409EFF"
          router
        >
          <!-- 仓库管理：root和admin可以访问 -->
          <el-menu-item v-if="hasPermission(['root', 'admin'])" index="/warehouse">
            <template #title>
              <el-icon><location /></el-icon>
              <span>仓库管理</span>
            </template>
          </el-menu-item>
          <!-- 货物管理：root和admin可以访问 -->
          <el-menu-item v-if="hasPermission(['root', 'admin'])" index="/goods">
            <template #title>
              <el-icon><goods /></el-icon>
              <span>货物管理</span>
            </template>
          </el-menu-item>
          <!-- 库存管理：root和admin可以访问 -->
          <el-menu-item v-if="hasPermission(['root', 'admin'])" index="/inventory">
            <template #title>
              <el-icon><data-analysis /></el-icon>
              <span>库存管理</span>
            </template>
          </el-menu-item>
          <!-- 入库管理：所有角色可以访问 -->
          <el-menu-item v-if="hasPermission(['root', 'admin', 'user'])" index="/inbound">
            <template #title>
              <el-icon><upload /></el-icon>
              <span>入库管理</span>
            </template>
          </el-menu-item>
          <!-- 出库管理：所有角色可以访问 -->
          <el-menu-item v-if="hasPermission(['root', 'admin', 'user'])" index="/outbound">
            <template #title>
              <el-icon><download /></el-icon>
              <span>出库管理</span>
            </template>
          </el-menu-item>
          <!-- 系统管理：只有root可以访问 -->
          <el-sub-menu v-if="hasPermission(['root'])" index="system">
            <template #title>
              <el-icon><setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/user">
              <template #title>
                <el-icon><user /></el-icon>
                <span>用户管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/system/role">
              <template #title>
                <el-icon><user-filled /></el-icon>
                <span>角色管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/system/log">
              <template #title>
                <el-icon><document /></el-icon>
                <span>登录日志</span>
              </template>
            </el-menu-item>
          </el-sub-menu>
          
          <!-- 个人中心：所有角色可以访问 -->
          <el-menu-item v-if="hasPermission(['root', 'admin', 'user'])" index="/profile">
            <template #title>
              <el-icon><setting /></el-icon>
              <span>个人中心</span>
            </template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header-container">
          <div class="header-title">货物物流库存管理系统</div>
          <div class="header-user">
            <el-dropdown>
              <span class="user-info">
                <el-icon><user /></el-icon>
                <span>{{ userInfo.nickname || userInfo.username || '未知用户' }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="navigateToProfile">
                    <el-icon><setting /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location, Goods, DataAnalysis, Upload, Download, User, Setting, SwitchButton, UserFilled, Document } from '@element-plus/icons-vue'
import { logout } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const activeMenu = computed(() => route.path)

// 用户信息
const userInfo = ref({})

// 获取用户信息
const getUserInfo = () => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
}

// 检查权限
const hasPermission = (roles) => {
  if (!userInfo.value.role) {
    return false
  }
  return roles.includes(userInfo.value.role)
}

// 跳转到个人中心
const navigateToProfile = () => {
  router.push('/profile')
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    logout()
      .then(() => {
        localStorage.removeItem('user')
        router.push('/login')
        ElMessage.success('退出登录成功')
      })
      .catch(() => {
        // 无论API调用成功与否，都清除本地存储并跳转到登录页
        localStorage.removeItem('user')
        router.push('/login')
        ElMessage.success('退出登录成功')
      })
  }).catch(() => {
    // 取消退出
  })
}

// 初始化用户信息
onMounted(() => {
  getUserInfo()
  
  // 监听路由变化，更新用户信息
  router.beforeEach((to, from, next) => {
    getUserInfo()
    next()
  })
})
</script>

<style lang="scss" scoped>
.app-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

.main-container {
  height: 100%;
  background-color: #f0f2f5;
}

.sidebar-container {
  height: 100%;
  background-color: #001529;
  .logo {
    height: 60px;
    line-height: 60px;
    text-align: center;
    color: #fff;
    font-weight: bold;
    border-bottom: 1px solid #1890ff;
  }
  .sidebar-menu {
    height: calc(100% - 60px);
    overflow-y: auto;
  }
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #e8e8e8;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
  .header-title {
    font-size: 18px;
    font-weight: bold;
    margin-left: 20px;
  }
  .header-user {
    margin-right: 20px;
    .user-info {
      cursor: pointer;
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }
}

.main-content {
  padding: 20px;
  overflow-y: auto;
}
</style>
