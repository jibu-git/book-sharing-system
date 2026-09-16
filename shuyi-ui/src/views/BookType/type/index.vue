<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="图书标签名称" prop="bookTypeName">
        <el-input
          v-model="queryParams.bookTypeName"
          placeholder="请输入图书标签名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="标签状态" prop="bookTypeStatus">
        <el-select v-model="queryParams.bookTypeStatus" placeholder="请选择标签状态" clearable style="width: 150px;">
          <el-option
            v-for="dict in book_type_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['BookType:type:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['BookType:type:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['BookType:type:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['BookType:type:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="typeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="标签ID" align="center" prop="bookTypeId" />
      <el-table-column label="标签名称" align="center" prop="bookTypeName" />
      <el-table-column label="显示顺序" align="center" prop="bookTypeSort" />
      <el-table-column label="标签状态" align="center" prop="bookTypeStatus">
        <template #default="scope">
          <dict-tag :options="book_type_status" :value="scope.row.bookTypeStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['BookType:type:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['BookType:type:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="typeRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标签名称" prop="bookTypeName">
          <el-input v-model="form.bookTypeName" placeholder="请输入图书标签名称" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="bookTypeSort">
          <el-input-number v-model="form.bookTypeSort" controls-position="right" :min="0" />
        </el-form-item>
        <el-form-item label="标签状态" prop="bookTypeStatus">
          <el-radio-group v-model="form.bookTypeStatus">
            <el-radio
              v-for="dict in book_type_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Type">
import { listType, getType, delType, addType, updateType } from "@/api/BookType/type"
// 引入 userStore 模块
import useUserStore from '@/store/modules/user'

const { proxy } = getCurrentInstance()
const { book_type_status } = proxy.useDict('book_type_status')
const userStore = useUserStore()

const typeList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    bookTypeName: null,
    bookTypeSort: null,
    bookTypeStatus: null,
  },
  rules: {
    bookTypeName: [
      { required: true, message: "图书标签名称不能为空", trigger: "blur" }
    ],
    bookTypeSort: [
      { required: true, message: "显示顺序不能为空", trigger: "blur" }
    ],
    bookTypeStatus: [
      { required: true, message: "标签状态不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询图书标签列表 */
function getList() {
  loading.value = true
  listType(queryParams.value).then(response => {
    typeList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    bookTypeId: null,
    bookTypeName: null,
    bookTypeSort: 0,
    bookTypeStatus: "0",
    remark: null
  }
  proxy.resetForm("typeRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.bookTypeId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  // 约束逻辑：只有管理员可以新增
  if (userStore.id != 1) {
    proxy.$modal.msgError("只有管理员可以新增图书标签")
    return
  }
  reset()
  open.value = true
  title.value = "添加图书标签"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  // 约束逻辑：只有管理员可以修改
  if (userStore.id != 1) {
    proxy.$modal.msgError("只有管理员可以修改图书标签")
    return
  }
  reset()
  const _bookTypeId = row.bookTypeId || ids.value
  getType(_bookTypeId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改图书标签"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["typeRef"].validate(valid => {
    if (valid) {
      // 获取当前用户ID
      const user_id = userStore.id
      
      // 二次校验身份（双重保障）
      if (user_id != 1) {
        proxy.$modal.msgError("只有管理员可以保存图书标签数据")
        return
      }
      
      if (form.value.bookTypeId != null) {
        updateType(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addType(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _bookTypeIds = row.bookTypeId || ids.value
  
  // 约束逻辑：只有管理员可以删除
  if (userStore.id != 1) {
    proxy.$modal.msgError("只有管理员可以删除图书标签")
    return
  }

  proxy.$modal.confirm('是否确认删除图书标签编号为"' + _bookTypeIds + '"的数据项？').then(function() {
    return delType(_bookTypeIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('BookType/type/export', {
    ...queryParams.value
  }, `type_${new Date().getTime()}.xlsx`)
}

getList()
</script>