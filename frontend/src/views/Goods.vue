<template>
  <div class="app-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <el-input
            v-model="queryParams.goodsName"
            placeholder="请输入货物名称"
            clearable
            class="search-input"
            @keyup.enter="handleQuery"
          />
          <el-input
            v-model="queryParams.goodsCode"
            placeholder="请输入货物编码"
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
      <el-table v-loading="loading" :data="goodsList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="goodsCode" label="货物编码" width="120" align="center" />
        <el-table-column prop="goodsName" label="货物名称" width="150" align="center" />
        <el-table-column prop="goodsType" label="货物类型" width="120" align="center" />
        <el-table-column prop="unit" label="计量单位" width="100" align="center" />
        <el-table-column prop="price" label="单价" width="120" align="center">
          <template #default="scope">
            {{ scope.row.price.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="() => handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
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

    <!-- 添加/修改货物对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="open"
      width="500px"
      @close="handleClose"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="货物编码" prop="goodsCode">
          <el-input v-model="form.goodsCode" placeholder="请输入货物编码" />
        </el-form-item>
        <el-form-item label="货物名称" prop="goodsName">
          <el-input v-model="form.goodsName" placeholder="请输入货物名称" />
        </el-form-item>
        <el-form-item label="货物类型" prop="goodsType">
          <el-input v-model="form.goodsType" placeholder="请输入货物类型" />
        </el-form-item>
        <el-form-item label="计量单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入计量单位" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number
            v-model="form.price"
            :min="0"
            :precision="2"
            placeholder="请输入单价"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入货物描述"
            :rows="3"
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
import { listGoods, getGoods, addGoods, updateGoods, delGoods, enableGoods, disableGoods } from '@/api/goods'

// 表格数据
const goodsList = ref([])
const total = ref(0)
const loading = ref(false)
const open = ref(false)
const formRef = ref(null)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  goodsName: '',
  goodsCode: ''
})

// 表单数据
const form = reactive({
  id: null,
  goodsCode: '',
  goodsName: '',
  goodsType: '',
  unit: '',
  price: null,
  description: '',
  status: 1
})

// 表单校验规则
const rules = reactive({
  goodsCode: [
    { required: true, message: '货物编码不能为空', trigger: 'blur' },
    { min: 1, max: 50, message: '货物编码长度不能超过50个字符', trigger: 'blur' }
  ],
  goodsName: [
    { required: true, message: '货物名称不能为空', trigger: 'blur' },
    { min: 1, max: 100, message: '货物名称长度不能超过100个字符', trigger: 'blur' }
  ],
  unit: [
    { required: true, message: '计量单位不能为空', trigger: 'blur' },
    { min: 1, max: 20, message: '计量单位长度不能超过20个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '单价不能为空', trigger: 'blur' },
    { type: 'number', min: 0, message: '单价不能小于0', trigger: 'blur' }
  ]
})

// 对话框标题
const dialogTitle = computed(() => {
  return form.id ? '修改货物' : '新增货物'
})

// 初始化
onMounted(() => {
  getList()
})

// 获取货物列表
const getList = () => {
  loading.value = true
  listGoods(queryParams).then(response => {
    goodsList.value = response.records
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
    goodsName: '',
    goodsCode: ''
  })
  getList()
}

// 新增货物
const handleAdd = () => {
  Object.assign(form, {
    id: null,
    goodsCode: '',
    goodsName: '',
    goodsType: '',
    unit: '',
    price: null,
    description: '',
    status: 1
  })
  open.value = true
}

// 状态变更
const handleStatusChange = (row) => {
  // row.status为1表示启用，0表示禁用
  const request = row.status === 1 ? enableGoods(row.id) : disableGoods(row.id)
  
  request.then(() => {
    ElMessage.success('状态更新成功')
    getList() // 更新成功后刷新列表，确保数据准确
  }).catch(() => {
    getList() // 失败时刷新列表，恢复原状态
    ElMessage.error('状态更新失败')
  })
}

// 修改货物
const handleEdit = (row) => {
  getGoods(row.id).then(response => {
    Object.assign(form, response)
    open.value = true
  })
}

// 删除货物
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该货物吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delGoods(row.id)
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
        updateGoods(form).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        }).catch(() => {
          ElMessage.error('修改失败')
        })
      } else {
        addGoods(form).then(() => {
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
