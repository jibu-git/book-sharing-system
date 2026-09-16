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
      <el-form-item label="借阅者ID" prop="borrowerId">
        <el-input
          v-model="queryParams.borrowerId"
          placeholder="请输入借阅者ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="书主ID" prop="ownerId">
        <el-input
          v-model="queryParams.ownerId"
          placeholder="请输入书主ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="姓名" prop="orderRecvName">
        <el-input
          v-model="queryParams.orderRecvName"
          placeholder="请输入收货人姓名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="电话" prop="orderRecvPhone">
        <el-input
          v-model="queryParams.orderRecvPhone"
          placeholder="请输入收货人电话"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="省份" prop="orderRecvProvince">
        <el-input
          v-model="queryParams.orderRecvProvince"
          placeholder="请输入省份"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="城市" prop="orderRecvCity">
        <el-input
          v-model="queryParams.orderRecvCity"
          placeholder="请输入城市"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="详细地址" prop="orderRecvAddress">
        <el-input
          v-model="queryParams.orderRecvAddress"
          placeholder="请输入详细地址"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus" >
        <el-select v-model="queryParams.orderStatus" placeholder="请选择订单状态" clearable style="width: 150px;">
          <el-option
            v-for="dict in order_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
            
          />
        </el-select>
      </el-form-item>
      <el-form-item label="下单时间" prop="orderTime">
        <el-date-picker clearable
          v-model="queryParams.orderTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择下单时间">
        </el-date-picker>
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
          v-hasPermi="['Order:order:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['Order:order:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['Order:order:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['Order:order:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单ID" align="center" prop="orderId" />
      <el-table-column label="借阅者ID" align="center" prop="borrowerId" />
      <el-table-column label="书主ID" align="center" prop="ownerId" />
      <el-table-column label="收货人姓名" align="center" prop="orderRecvName" />
      <el-table-column label="收货人电话" align="center" prop="orderRecvPhone" />
      <el-table-column label="省份" align="center" prop="orderRecvProvince" />
      <el-table-column label="城市" align="center" prop="orderRecvCity" />
      <el-table-column label="详细地址" align="center" prop="orderRecvAddress" />
      <el-table-column label="订单状态" align="center" prop="orderStatus">
        <template #default="scope">
          <dict-tag :options="order_status" :value="scope.row.orderStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" align="center" prop="orderTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.orderTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['Order:order:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['Order:order:remove']">删除</el-button>
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

    <!-- 添加或修改借阅订单对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="orderRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="借阅者ID" prop="borrowerId">
          <el-input v-model="form.borrowerId" placeholder="请输入借阅者ID" />
        </el-form-item>
        <el-form-item label="书主ID" prop="ownerId">
          <el-input v-model="form.ownerId" placeholder="请输入书主ID" />
        </el-form-item>
        <el-form-item label="收货人姓名" prop="orderRecvName">
          <el-input v-model="form.orderRecvName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="收货人电话" prop="orderRecvPhone">
          <el-input v-model="form.orderRecvPhone" placeholder="请输入收货人电话" />
        </el-form-item>
        <el-form-item label="省份" prop="orderRecvProvince">
          <el-input v-model="form.orderRecvProvince" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市" prop="orderRecvCity">
          <el-input v-model="form.orderRecvCity" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="详细地址" prop="orderRecvAddress">
          <el-input v-model="form.orderRecvAddress" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="订单状态" prop="orderStatus">
          <el-radio-group v-model="form.orderStatus">
            <el-radio
              v-for="dict in order_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="下单时间" prop="orderTime">
          <el-date-picker clearable
            v-model="form.orderTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择下单时间">
          </el-date-picker>
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

<script setup name="Order">
import { listOrder, getOrder, delOrder, addOrder, updateOrder } from "@/api/Order/torder"

const { proxy } = getCurrentInstance()
const { order_status } = proxy.useDict('order_status')

const orderList = ref([])
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
    borrowerId: null,
    ownerId: null,
    orderRecvName: null,
    orderRecvPhone: null,
    orderRecvProvince: null,
    orderRecvCity: null,
    orderRecvAddress: null,
    orderStatus: null,
    orderTime: null,
  },
  rules: {
    borrowerId: [
      { required: true, message: "借阅者ID不能为空", trigger: "blur" }
    ],
    ownerId: [
      { required: true, message: "书主ID不能为空", trigger: "blur" }
    ],
    orderRecvName: [
      { required: true, message: "收货人姓名不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询借阅订单列表 */
function getList() {
  loading.value = true
  listOrder(queryParams.value).then(response => {
    orderList.value = response.rows
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
    orderId: null,
    borrowerId: null,
    ownerId: null,
    orderRecvName: null,
    orderRecvPhone: null,
    orderRecvProvince: null,
    orderRecvCity: null,
    orderRecvAddress: null,
    orderStatus: null,
    orderTime: null,
    createTime: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("orderRef")
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
  ids.value = selection.map(item => item.orderId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加借阅订单"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _orderId = row.orderId || ids.value
  getOrder(_orderId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改借阅订单"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["orderRef"].validate(valid => {
    if (valid) {
      if (form.value.orderId != null) {
        updateOrder(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addOrder(form.value).then(response => {
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
  const _orderIds = row.orderId || ids.value
  proxy.$modal.confirm('是否确认删除借阅订单编号为"' + _orderIds + '"的数据项？').then(function() {
    return delOrder(_orderIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('Order/order/export', {
    ...queryParams.value
  }, `order_${new Date().getTime()}.xlsx`)
}

getList()
</script>
