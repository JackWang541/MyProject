import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  },
  {
    path: '/warehouse',
    name: 'Warehouse',
    component: () => import('../views/Warehouse.vue'),
    meta: {
      title: '仓库管理',
      requiresAuth: true,
      roles: ['root', 'admin']
    }
  },
  {
    path: '/goods',
    name: 'Goods',
    component: () => import('../views/Goods.vue'),
    meta: {
      title: '货物管理',
      requiresAuth: true,
      roles: ['root', 'admin']
    }
  },
  {
    path: '/inventory',
    name: 'Inventory',
    component: () => import('../views/Inventory.vue'),
    meta: {
      title: '库存管理',
      requiresAuth: true,
      roles: ['root', 'admin']
    }
  },
  {
    path: '/inbound',
    name: 'Inbound',
    component: () => import('../views/Inbound.vue'),
    meta: {
      title: '入库管理',
      requiresAuth: true,
      roles: ['root', 'admin', 'user']
    }
  },
  {
    path: '/outbound',
    name: 'Outbound',
    component: () => import('../views/Outbound.vue'),
    meta: {
      title: '出库管理',
      requiresAuth: true,
      roles: ['root', 'admin', 'user']
    }
  },
  {  
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: {
      title: '个人中心',
      requiresAuth: true,
      roles: ['root', 'admin', 'user']
    }
  },
  {  
    path: '/system',
    name: 'System',
    component: () => import('../views/System.vue'),
    meta: {
      title: '系统管理',
      requiresAuth: true,
      roles: ['root']
    },
    children: [
      {  
        path: 'user',
        name: 'User',
        component: () => import('../views/system/User.vue'),
        meta: {
          title: '用户管理',
          requiresAuth: true,
          roles: ['root']
        }
      },
      {  
        path: 'role',
        name: 'Role',
        component: () => import('../views/system/Role.vue'),
        meta: {
          title: '角色管理',
          requiresAuth: true,
          roles: ['root']
        }
      },
      {  
        path: 'log',
        name: 'LoginLog',
        component: () => import('../views/system/LoginLog.vue'),
        meta: {
          title: '登录日志',
          requiresAuth: true,
          roles: ['root']
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }

  // 不需要认证的页面直接放行
  if (!to.meta.requiresAuth) {
    next()
    return
  }

  // 检查是否已登录
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    next('/login')
    return
  }

  const user = JSON.parse(userStr)
  // 检查角色权限
  if (to.meta.roles && to.meta.roles.length > 0) {
    if (!to.meta.roles.includes(user.role)) {
      // 无权限，跳转到首页或显示无权限页面
      next(from.path || '/warehouse')
      return
    }
  }

  next()
})

export default router
