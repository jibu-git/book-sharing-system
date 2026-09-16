<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="商品id" prop="cartBooksId">
        <el-input
          v-model="queryParams.cartBooksId"
          placeholder="请输入商品id"
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
          v-hasPermi="['TCart:cart:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['TCart:cart:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['TCart:cart:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['TCart:cart:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Shopping"
          @click="handleOrder"
          :disabled="multiple"
        >下单</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="cartList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="借阅车数据id" align="center" prop="cartId" />
      <el-table-column label="用户id" align="center" prop="userId" />
      <el-table-column label="商品id" align="center" prop="cartBooksId" />
      <el-table-column label="加入时商品单价" align="center" prop="cartPrice" />
      <el-table-column label="商品数量" align="center" prop="cartNum" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['TCart:cart:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['TCart:cart:remove']">删除</el-button>
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

    <!-- 添加或修改借阅车对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="cartRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户id" />
        </el-form-item>
        <el-form-item label="商品id" prop="cartBooksId">
          <el-input v-model="form.cartBooksId" placeholder="请输入商品id" />
        </el-form-item>
        <el-form-item label="加入时商品单价" prop="cartPrice">
          <el-input v-model="form.cartPrice" placeholder="请输入加入时商品单价" />
        </el-form-item>
        <el-form-item label="商品数量" prop="cartNum">
          <el-input v-model="form.cartNum" placeholder="请输入商品数量" />
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

<script setup name="Cart">
import { listCart, getCart, delCart, addCart, updateCart, orderCart } from "@/api/TCart/cart"

const { proxy } = getCurrentInstance()

const cartList = ref([])
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
    cartBooksId: null,
  },
  rules: {
    userId: [
      { required: true, message: "用户id不能为空", trigger: "blur" }
    ],
    cartBooksId: [
      { required: true, message: "商品id不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询借阅车列表 */
function getList() {
  loading.value = true
  listCart(queryParams.value).then(response => {
    cartList.value = response.rows
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
    cartId: null,
    userId: null,
    cartBooksId: null,
    cartPrice: null,
    cartNum: null,
    createTime: null,
    remark: null
  }
  proxy.resetForm("cartRef")
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
  ids.value = selection.map(item => item.cartId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加借阅车"
}



/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _cartId = row.cartId || ids.value
  getCart(_cartId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改借阅车"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["cartRef"].validate(valid => {
    if (valid) {
      if (form.value.cartId != null) {
        updateCart(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCart(form.value).then(response => {
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
  const _cartIds = row.cartId || ids.value
  proxy.$modal.confirm('是否确认删除借阅车编号为"' + _cartIds + '"的数据项？').then(function() {
    return delCart(_cartIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('TCart/cart/export', {
    ...queryParams.value
  }, `cart_${new Date().getTime()}.xlsx`)
}

getList()

/** 下单按钮操作 */
function handleOrder() {
  if (ids.value.length === 0) {
    proxy.$modal.msgWarning("请选择要下单的商品")
    return
  }
  
  // 直接调用TCart的下单API，传入借阅车ID数组
  orderCart(ids.value).then(response => {
    proxy.$modal.msgSuccess("下单成功")
    // 刷新借阅车列表（下单成功后商品会自动从借阅车移除）
    getList()
  }).catch(error => {
    proxy.$modal.msgError("下单失败：" + (error.message || "网络错误"))
  })
}
</script>
