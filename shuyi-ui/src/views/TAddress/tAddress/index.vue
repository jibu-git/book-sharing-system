<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="收货人" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入收货人姓名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="省-名称" prop="provinceName">
        <el-input
          v-model="queryParams.provinceName"
          placeholder="请输入省-名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="市-名称" prop="cityName">
        <el-input
          v-model="queryParams.cityName"
          placeholder="请输入市-名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="详细地址" prop="address">
        <el-input
          v-model="queryParams.address"
          placeholder="请输入详细地址"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="标签" prop="tag">
        <el-input
          v-model="queryParams.tag"
          placeholder="请输入标签"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      
      <el-form-item label="创建时间" prop="createdTime">
        <el-date-picker clearable
          v-model="queryParams.createdTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择创建时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="修改人" prop="modifiedUser">
        <el-input
          v-model="queryParams.modifiedUser"
          placeholder="请输入修改人"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="修改时间" prop="modifiedTime">
        <el-date-picker clearable
          v-model="queryParams.modifiedTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择修改时间">
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
          v-hasPermi="['TAddress:tAddress:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['TAddress:tAddress:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['TAddress:tAddress:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['TAddress:tAddress:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tAddressList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="收货地址id" align="center" prop="aid" />
      <el-table-column label="收货人姓名" align="center" prop="name" />
      <el-table-column label="省-名称" align="center" prop="provinceName" />
      <el-table-column label="市-名称" align="center" prop="cityName" />
      <el-table-column label="详细地址" align="center" prop="address" />
      <el-table-column label="手机" align="center" prop="phone" />
      <el-table-column label="标签" align="center" prop="tag" />
      <el-table-column label="是否默认" align="center" prop="isDefault">
        <template #default="scope">
          <dict-tag :options="is_default" :value="scope.row.isDefault"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createdTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="修改时间" align="center" prop="modifiedTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.modifiedTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['TAddress:tAddress:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['TAddress:tAddress:remove']">删除</el-button>
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

    <!-- 添加或修改收货地址对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="tAddressRef" :model="form" :rules="rules" label-width="80px">
        
        <el-form-item label="收货人" prop="name">
          <el-input v-model="form.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="省-名称" prop="provinceName">
          <el-input v-model="form.provinceName" placeholder="请输入省-名称" />
        </el-form-item>
        <el-form-item label="市-名称" prop="cityName">
          <el-input v-model="form.cityName" placeholder="请输入市-名称" />
        </el-form-item>
        <el-form-item label="详细地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="手机" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机" />
        </el-form-item>
        <el-form-item label="标签" prop="tag">
          <el-input v-model="form.tag" placeholder="请输入标签" />
        </el-form-item>
        <el-form-item label="是否默认" prop="isDefault">
          <el-radio-group v-model="form.isDefault">
            <el-radio
              v-for="dict in is_default"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{dict.label}}</el-radio>
          </el-radio-group>
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

<script setup name="TAddress">
import { listTAddress, getTAddress, delTAddress, addTAddress, updateTAddress } from "@/api/TAddress/tAddress"

const { proxy } = getCurrentInstance()
const { is_default } = proxy.useDict('is_default')

const tAddressList = ref([])
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
    userId: null,
    name: null,
    provinceName: null,
    cityName: null,
    address: null,
    phone: null,
    tag: null,
    isDefault: null,
    createdTime: null,
    modifiedTime: null
  },
  rules: {
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询收货地址列表 */
function getList() {
  loading.value = true
  listTAddress(queryParams.value).then(response => {
    tAddressList.value = response.rows
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
    aid: null,
    userId: null,
    name: null,
    provinceName: null,
    cityName: null,
    address: null,
    phone: null,
    tag: null,
    isDefault: null,
    createdTime: null,
    modifiedTime: null
  }
  proxy.resetForm("tAddressRef")
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
  ids.value = selection.map(item => item.aid)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加收货地址"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _aid = row.aid || ids.value
  getTAddress(_aid).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改收货地址"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["tAddressRef"].validate(valid => {
    if (valid) {
      if (form.value.aid != null) {
        updateTAddress(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addTAddress(form.value).then(response => {
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
  const _aids = row.aid || ids.value
  proxy.$modal.confirm('是否确认删除收货地址编号为"' + _aids + '"的数据项？').then(function() {
    return delTAddress(_aids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('TAddress/tAddress/export', {
    ...queryParams.value
  }, `tAddress_${new Date().getTime()}.xlsx`)
}

getList()
</script>
