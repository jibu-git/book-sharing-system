<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="订单ID" prop="orderId">
        <el-input
          v-model="queryParams.orderId"
          placeholder="请输入订单ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="书籍ID" prop="bookId">
        <el-input
          v-model="queryParams.bookId"
          placeholder="请输入书籍ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="书籍标题" prop="bookTitle">
        <el-input
          v-model="queryParams.bookTitle"
          placeholder="请输入书籍标题"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数量" prop="bookNum">
        <el-input
          v-model="queryParams.bookNum"
          placeholder="请输入数量"
          clearable
          @keyup.enter="handleQuery"
        />
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
          v-hasPermi="['orderItem:item:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['orderItem:item:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['orderItem:item:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['orderItem:item:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="itemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单明细ID" align="center" prop="orderItemId" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="书籍ID" align="center" prop="bookId" />
      <el-table-column label="书籍标题" align="center" prop="bookTitle" />
      <el-table-column label="书籍图片" align="center" prop="bookImage" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.bookImage" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="数量" align="center" prop="bookNum" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['orderItem:item:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['orderItem:item:remove']">删除</el-button>
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

    <!-- 添加或修改借阅订单明细对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="itemRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="订单ID" prop="orderId">
          <el-input v-model="form.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="书籍ID" prop="bookId">
          <el-input v-model="form.bookId" placeholder="请输入书籍ID" />
        </el-form-item>
        <el-form-item label="书籍标题" prop="bookTitle">
          <el-input v-model="form.bookTitle" placeholder="请输入书籍标题" />
        </el-form-item>
        <el-form-item label="书籍图片" prop="bookImage">
          <image-upload v-model="form.bookImage"/>
        </el-form-item>
        <el-form-item label="数量" prop="bookNum">
          <el-input v-model="form.bookNum" placeholder="请输入数量" />
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

<script setup name="Item">
import { listItem, getItem, delItem, addItem, updateItem } from "@/api/orderItem/item"

const { proxy } = getCurrentInstance()

const itemList = ref([])
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
    orderId: null,
    bookId: null,
    bookTitle: null,
    bookImage: null,
    bookNum: null,
  },
  rules: {
    orderId: [
      { required: true, message: "订单ID不能为空", trigger: "blur" }
    ],
    bookId: [
      { required: true, message: "书籍ID不能为空", trigger: "blur" }
    ],
    bookTitle: [
      { required: true, message: "书籍标题不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询借阅订单明细列表 */
function getList() {
  loading.value = true
  listItem(queryParams.value).then(response => {
    itemList.value = response.rows
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
    orderItemId: null,
    orderId: null,
    bookId: null,
    bookTitle: null,
    bookImage: null,
    bookNum: null,
    createTime: null,
    remark: null
  }
  proxy.resetForm("itemRef")
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
  ids.value = selection.map(item => item.orderItemId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加借阅订单明细"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _orderItemId = row.orderItemId || ids.value
  getItem(_orderItemId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改借阅订单明细"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["itemRef"].validate(valid => {
    if (valid) {
      if (form.value.orderItemId != null) {
        updateItem(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addItem(form.value).then(response => {
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
  const _orderItemIds = row.orderItemId || ids.value
  proxy.$modal.confirm('是否确认删除借阅订单明细编号为"' + _orderItemIds + '"的数据项？').then(function() {
    return delItem(_orderItemIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('orderItem/item/export', {
    ...queryParams.value
  }, `item_${new Date().getTime()}.xlsx`)
}

getList()
</script>
