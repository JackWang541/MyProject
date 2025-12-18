<template>
  <div class="warehouse-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-box">
            <el-input
              v-model="queryParams.warehouseName"
              placeholder="请输入仓库名称"
              clearable
              class="search-input"
              @keyup.enter="handleQuery"
            />
            <el-input
              v-model="queryParams.manager"
              placeholder="请输入仓库管理员"
              clearable
              class="search-input"
              @keyup.enter="handleQuery"
            />
            <el-button type="primary" @click="handleQuery">
              <el-icon><search /></el-icon>
              搜索
            </el-button>
            <el-button @click="resetQuery">
              <el-icon><refresh /></el-icon>
              重置
            </el-button>
          </div>
          <el-button type="primary" @click="openAddDialog">
            <el-icon><plus /></el-icon>
            添加仓库
          </el-button>
        </div>
      </template>

      <el-table
        :data="warehouseList"
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="warehouseName" label="仓库名称" width="150" />
        <el-table-column prop="address" label="仓库地址" min-width="200" />
        <el-table-column prop="manager" label="仓库管理员" width="120" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
              {{ scope.row.status === 0 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openEditDialog(scope.row)">
              <el-icon><edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">
              <el-icon><delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          :total="total"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="warehouseFormRef"
        :model="warehouseForm"
        :rules="warehouseRules"
        label-width="120px"
      >
        <el-form-item label="仓库名称" prop="warehouseName">
          <el-input v-model="warehouseForm.warehouseName" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item label="仓库地址" prop="address">
          <el-input v-model="warehouseForm.address" placeholder="请输入仓库地址" />
        </el-form-item>
        <el-form-item label="仓库管理员" prop="manager">
          <el-input v-model="warehouseForm.manager" placeholder="请输入仓库管理员" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="warehouseForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="warehouseForm.status">
            <el-radio :label="0">启用</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue'
import * as warehouseApi from '../api/warehouse'

// 数据加载状态
const loading = ref(false)

// 仓库列表
const warehouseList = ref([])

// 分页数据
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 查询参数
const queryParams = reactive({
  warehouseName: '',
  manager: ''
})

// 选中的行
const selectedRows = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const warehouseFormRef = ref(null)

// 表单数据
const warehouseForm = reactive({
  id: null,
  warehouseName: '',
  address: '',
  manager: '',
  phone: '',
  status: 0
})

// 表单验证规则
const warehouseRules = {
  warehouseName: [
    { required: true, message: '请输入仓库名称', trigger: 'blur' },
    { min: 2, max: 50, message: '仓库名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入仓库地址', trigger: 'blur' }
  ],
  manager: [
    { required: true, message: '请输入仓库管理员', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 生命周期
onMounted(() => {
  getWarehouseList()
})

// 获取仓库列表
const getWarehouseList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      ...queryParams
    }
    const response = await warehouseApi.getWarehouseList(params)
    warehouseList.value = response.records
    total.value = response.total
  } catch (error) {
    console.error('获取仓库列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  currentPage.value = 1
  getWarehouseList()
}

// 重置
const resetQuery = () => {
  Object.assign(queryParams, {
    warehouseName: '',
    manager: ''
  })
  currentPage.value = 1
  getWarehouseList()
}

// 打开添加对话框
const openAddDialog = () => {
  dialogTitle.value = '添加仓库'
  resetForm()
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (row) => {
  dialogTitle.value = '编辑仓库'
  // 深拷贝对象
  Object.assign(warehouseForm, JSON.parse(JSON.stringify(row)))
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  if (warehouseFormRef.value) {
    warehouseFormRef.value.resetFields()
  }
  Object.assign(warehouseForm, {
    id: null,
    warehouseName: '',
    address: '',
    manager: '',
    phone: '',
    status: 0
  })
}

// 处理表单提交
const handleSubmit = async () => {
  if (!warehouseFormRef.value) return
  await warehouseFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (warehouseForm.id) {
          // 更新仓库
          await warehouseApi.updateWarehouse(warehouseForm)
          ElMessage.success('仓库更新成功')
        } else {
          // 创建仓库
          await warehouseApi.createWarehouse(warehouseForm)
          ElMessage.success('仓库添加成功')
        }
        dialogVisible.value = false
        getWarehouseList()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

// 处理删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该仓库吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    loading.value = true
    try {
      await warehouseApi.deleteWarehouse(row.id)
      ElMessage.success('仓库删除成功')
      getWarehouseList()
    } catch (error) {
      console.error('删除仓库失败:', error)
      ElMessage.error('删除失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }).catch(() => {
    // 取消删除
  })
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  getWarehouseList()
}

// 处理分页页码变化
const handleCurrentChange = (page) => {
  currentPage.value = page
  getWarehouseList()
}
</script>

<style lang="scss" scoped>
.warehouse-container {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .search-box {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .search-input {
      width: 200px;
    }
  }

  .pagination-container {
    margin-top: 20px;
    text-align: right;
  }
}
</style>
