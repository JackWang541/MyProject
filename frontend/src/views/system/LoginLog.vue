<template>
  <div class="system-login-log">
    <el-card>
      <template #header>
        <div class="card-header">
          <h3>登录日志</h3>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :icon="Search">搜索</el-button>
          <el-button @click="resetSearch" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
      
      <!-- 日志列表 -->
      <el-table v-loading="loading" :data="logList" style="width: 100%" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="ip" label="登录IP" width="150" />
        <el-table-column prop="userAgent" label="浏览器信息" min-width="200">
          <template #default="scope">
            <el-tooltip :content="scope.row.userAgent" placement="top">
              <span class="user-agent-text">{{ truncateText(scope.row.userAgent, 50) }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 1" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="消息" min-width="200" />
        <el-table-column prop="loginTime" label="登录时间" width="200" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getLoginLogs } from '@/api/log'

const logList = ref([])
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  username: '',
  status: ''
})

// 分页配置
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 加载登录日志
const loadLoginLogs = () => {
  loading.value = true
  getLoginLogs({
    ...searchForm,
    pageNum: pagination.pageNum,
    pageSize: pagination.pageSize
  })
    .then(res => {
      logList.value = res.records
      pagination.total = res.total
    })
    .catch(err => {
      console.error('获取登录日志失败:', err)
    })
    .finally(() => {
      loading.value = false
    })
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  loadLoginLogs()
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.pageNum = 1
  loadLoginLogs()
}

// 分页大小变更
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadLoginLogs()
}

// 页码变更
const handleCurrentChange = (current) => {
  pagination.pageNum = current
  loadLoginLogs()
}

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

// 生命周期钩子
onMounted(() => {
  loadLoginLogs()
})
</script>

<style scoped>
.system-login-log {
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

.user-agent-text {
  cursor: help;
  color: #409eff;
}
</style>
