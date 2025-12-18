<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <el-input
            v-model="queryParams.warehouseName"
            placeholder="请输入仓库名称"
            clearable
            class="search-input"
            @keyup.enter="handleQuery"
          />
          <el-input
            v-model="queryParams.goodsName"
            placeholder="请输入货物名称"
            clearable
            class="search-input"
            @keyup.enter="handleQuery"
          />
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
      </template>
      <el-table v-loading="loading" :data="inventoryList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="warehouseName" label="仓库名称" width="150" align="center" />
        <el-table-column prop="goodsName" label="货物名称" width="150" align="center" />
        <el-table-column prop="quantity" label="库存数量" width="120" align="center" />
        <el-table-column prop="safeQuantity" label="安全库存" width="120" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 添加/修改库存对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="open"
      width="500px"
      @close="handleClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="仓库ID" prop="warehouseId">
          <el-input-number
            v-model="form.warehouseId"
            :min="1"
            placeholder="请输入仓库ID"
          />
        </el-form-item>
        <el-form-item label="货物ID" prop="goodsId">
          <el-input-number
            v-model="form.goodsId"
            :min="1"
            placeholder="请输入货物ID"
          />
        </el-form-item>
        <el-form-item label="库存数量" prop="quantity">
          <el-input-number
            v-model="form.quantity"
            :min="0"
            placeholder="请输入库存数量"
          />
        </el-form-item>
        <el-form-item label="安全库存" prop="safeQuantity">
          <el-input-number
            v-model="form.safeQuantity"
            :min="0"
            placeholder="请输入安全库存"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="open = false">取消</el-button>
          <el-button type="primary" @click="submitForm">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listInventory, getInventory, addInventory, updateInventory, delInventory } from '@/api/inventory'

// 表格数据
const inventoryList = ref([])
const total = ref(0)
const loading = ref(false)
const open = ref(false)
const formRef = ref(null)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  warehouseName: '',
  goodsName: ''
})

// 表单数据
const form = reactive({
  id: null,
  warehouseId: null,
  goodsId: null,
  quantity: 0,
  safeQuantity: 0
})

// 表单校验规则
const rules = reactive({
  warehouseId: [
    { required: true, message: '仓库ID不能为空', trigger: 'blur' },
    { type: 'number', min: 1, message: '仓库ID必须大于0', trigger: 'blur' }
  ],
  goodsId: [
    { required: true, message: '货物ID不能为空', trigger: 'blur' },
    { type: 'number', min: 1, message: '货物ID必须大于0', trigger: 'blur' }
  ],
  quantity: [
    { required: true, message: '库存数量不能为空', trigger: 'blur' },
    { type: 'number', min: 0, message: '库存数量不能小于0', trigger: 'blur' }
  ],
  safeQuantity: [
    { required: true, message: '安全库存不能为空', trigger: 'blur' },
    { type: 'number', min: 0, message: '安全库存不能小于0', trigger: 'blur' }
  ]
})

// 对话框标题
const dialogTitle = computed(() => {
  return form.id ? '修改库存' : '新增库存'
})

// 初始化
onMounted(() => {
  getList()
})

// 获取库存列表
const getList = () => {
  loading.value = true
  listInventory(queryParams).then(response => {
    inventoryList.value = response.records
    total.value = response.total
    loading.value = false
  })
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  Object.assign(queryParams, {
    warehouseName: '',
    goodsName: ''
  })
  getList()
}

// 新增库存
const handleAdd = () => {
  Object.assign(form, {
    id: null,
    warehouseId: null,
    goodsId: null,
    quantity: 0,
    safeQuantity: 0
  })
  open.value = true
}

// 修改库存
const handleEdit = (row) => {
  getInventory(row.id).then(response => {
    Object.assign(form, response)
    open.value = true
  })
}

// 删除库存
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该库存记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delInventory(row.id)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 提交表单
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (form.id) {
        updateInventory(form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        }).catch(() => {
          ElMessage.error('修改失败')
        })
      } else {
        addInventory(form).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        }).catch(() => {
          ElMessage.error('新增失败')
        })
      }
    }
  })
}

// 关闭对话框
const handleClose = () => {
  formRef.value.resetFields()
}

// 分页处理
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.search-input {
  width: 200px;
  margin-right: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
