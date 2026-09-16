<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="发送者ID" prop="senderId">
        <el-input
          v-model="queryParams.senderId"
          placeholder="请输入发送者ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="接收者ID" prop="receiverId">
        <el-input
          v-model="queryParams.receiverId"
          placeholder="请输入接收者ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="消息标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入消息标题"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联业务ID" prop="relatedId">
        <el-input
          v-model="queryParams.relatedId"
          placeholder="请输入关联业务ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="阅读状态" prop="isRead">
        <el-input
          v-model="queryParams.isRead"
          placeholder="请输入阅读状态"
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
          v-hasPermi="['message:message:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['message:message:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['message:message:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['message:message:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="messageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="消息主键" align="center" prop="msgId" />
      <el-table-column label="发送者ID" align="center" prop="senderId" />
      <el-table-column label="接收者ID " align="center" prop="receiverId" />
      <el-table-column label="消息类型 " align="center" prop="msgType" />
      <el-table-column label="消息标题 " align="center" prop="title" />
      <el-table-column label="消息正文" align="center" prop="content" />
      <el-table-column label="关联业务ID" align="center" prop="relatedId" />
      <el-table-column label="阅读状态" align="center" prop="isRead" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['message:message:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['message:message:remove']">删除</el-button>
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

    <!-- 添加或修改消息提醒对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="messageRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="发送者ID " prop="senderId">
          <el-input v-model="form.senderId" placeholder="请输入发送者ID" />
        </el-form-item>
        <el-form-item label="接收者ID (通知给谁)" prop="receiverId">
          <el-input v-model="form.receiverId" placeholder="请输入接收者ID " />
        </el-form-item>
        <el-form-item label="消息标题 (例如：新的借阅申请)" prop="title">
          <el-input v-model="form.title" placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="消息正文">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="关联业务ID " prop="relatedId">
          <el-input v-model="form.relatedId" placeholder="请输入关联业务ID" />
        </el-form-item>
        <el-form-item label="阅读状态 (0:未读 1:已读)" prop="isRead">
          <el-input v-model="form.isRead" placeholder="请输入阅读状态 " />
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

<script setup name="Message">
import { listMessage, getMessage, delMessage, addMessage, updateMessage } from "@/api/message/message"

const { proxy } = getCurrentInstance()

const messageList = ref([])
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
    senderId: null,
    receiverId: null,
    msgType: null,
    title: null,
    content: null,
    relatedId: null,
    isRead: null,
  },
  rules: {
    receiverId: [
      { required: true, message: "接收者ID不能为空", trigger: "blur" }
    ],
    msgType: [
      { required: true, message: "消息类型 不能为空", trigger: "change" }
    ],
    title: [
      { required: true, message: "消息标题 不能为空", trigger: "blur" }
    ],
    content: [
      { required: true, message: "消息正文 不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询消息提醒列表 */
function getList() {
  loading.value = true
  listMessage(queryParams.value).then(response => {
    messageList.value = response.rows
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
    msgId: null,
    senderId: null,
    receiverId: null,
    msgType: null,
    title: null,
    content: null,
    relatedId: null,
    isRead: null,
    createTime: null
  }
  proxy.resetForm("messageRef")
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
  ids.value = selection.map(item => item.msgId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加消息提醒"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _msgId = row.msgId || ids.value
  getMessage(_msgId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改消息提醒"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["messageRef"].validate(valid => {
    if (valid) {
      if (form.value.msgId != null) {
        updateMessage(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addMessage(form.value).then(response => {
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
  const _msgIds = row.msgId || ids.value
  proxy.$modal.confirm('是否确认删除消息提醒编号为"' + _msgIds + '"的数据项？').then(function() {
    return delMessage(_msgIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('message/message/export', {
    ...queryParams.value
  }, `message_${new Date().getTime()}.xlsx`)
}

getList()
</script>
