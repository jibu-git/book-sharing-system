<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="书籍ID" prop="bookId">
        <el-input
          v-model="queryParams.bookId"
          placeholder="请输入书籍ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评论者ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入评论者ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评论者昵称" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入评论者昵称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="根评论ID(一级评论为0)" prop="rootId">
        <el-input
          v-model="queryParams.rootId"
          placeholder="请输入根评论ID(一级评论为0)"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="父评论ID(直属上级)" prop="parentId">
        <el-input
          v-model="queryParams.parentId"
          placeholder="请输入父评论ID(直属上级)"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="被回复人ID" prop="replyToId">
        <el-input
          v-model="queryParams.replyToId"
          placeholder="请输入被回复人ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="被回复人昵称" prop="replyToName">
        <el-input
          v-model="queryParams.replyToName"
          placeholder="请输入被回复人昵称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="点赞数" prop="likeCount">
        <el-input
          v-model="queryParams.likeCount"
          placeholder="请输入点赞数"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态(0正常 1停用)" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态(0正常 1停用)" clearable>
          <el-option
            v-for="dict in book_comment_status"
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
          v-hasPermi="['Comment:comment:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['Comment:comment:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['Comment:comment:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['Comment:comment:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="评论ID" align="center" prop="commentId" />
      <el-table-column label="书籍ID" align="center" prop="bookId" />
      <el-table-column label="评论者ID" align="center" prop="userId" />
      <el-table-column label="评论者昵称" align="center" prop="userName" />
      <el-table-column label="评论者头像" align="center" prop="avatar" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.avatar" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="评论内容" align="center" prop="content" />
      <el-table-column label="根评论ID(一级评论为0)" align="center" prop="rootId" />
      <el-table-column label="父评论ID(直属上级)" align="center" prop="parentId" />
      <el-table-column label="被回复人ID" align="center" prop="replyToId" />
      <el-table-column label="被回复人昵称" align="center" prop="replyToName" />
      <el-table-column label="点赞数" align="center" prop="likeCount" />
      <el-table-column label="状态(0正常 1停用)" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="book_comment_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['Comment:comment:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['Comment:comment:remove']">删除</el-button>
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

    <!-- 添加或修改图书评论对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="commentRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="书籍ID" prop="bookId">
          <el-input v-model="form.bookId" placeholder="请输入书籍ID" />
        </el-form-item>
        <el-form-item label="评论者ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入评论者ID" />
        </el-form-item>
        <el-form-item label="评论者昵称" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入评论者昵称" />
        </el-form-item>
        <el-form-item label="评论内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="根评论ID(一级评论为0)" prop="rootId">
          <el-input v-model="form.rootId" placeholder="请输入根评论ID(一级评论为0)" />
        </el-form-item>
        <el-form-item label="父评论ID(直属上级)" prop="parentId">
          <el-input v-model="form.parentId" placeholder="请输入父评论ID(直属上级)" />
        </el-form-item>
        <el-form-item label="被回复人ID" prop="replyToId">
          <el-input v-model="form.replyToId" placeholder="请输入被回复人ID" />
        </el-form-item>
        <el-form-item label="被回复人昵称" prop="replyToName">
          <el-input v-model="form.replyToName" placeholder="请输入被回复人昵称" />
        </el-form-item>
        <el-form-item label="点赞数" prop="likeCount">
          <el-input v-model="form.likeCount" placeholder="请输入点赞数" />
        </el-form-item>
        <el-form-item label="状态(0正常 1停用)" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in book_comment_status"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="删除标志(0代表存在 2代表删除)" prop="delFlag">
          <el-radio-group v-model="form.delFlag">
            <el-radio
              v-for="dict in book_comment_del_flag"
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

<script setup name="Comment">
import { listComment, getComment, delComment, addComment, updateComment } from "@/api/Comment/comment"

const { proxy } = getCurrentInstance()
const { book_comment_del_flag, book_comment_status } = proxy.useDict('book_comment_del_flag', 'book_comment_status')

const commentList = ref([])
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
    bookId: null,
    userId: null,
    userName: null,
    avatar: null,
    content: null,
    rootId: null,
    parentId: null,
    replyToId: null,
    replyToName: null,
    likeCount: null,
    status: null,
  },
  rules: {
    bookId: [
      { required: true, message: "书籍ID不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "评论者ID不能为空", trigger: "blur" }
    ],
    content: [
      { required: true, message: "评论内容不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询图书评论列表 */
function getList() {
  loading.value = true
  listComment(queryParams.value).then(response => {
    commentList.value = response.rows
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
    commentId: null,
    bookId: null,
    userId: null,
    userName: null,
    avatar: null,
    content: null,
    rootId: null,
    parentId: null,
    replyToId: null,
    replyToName: null,
    likeCount: null,
    status: null,
    delFlag: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("commentRef")
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
  ids.value = selection.map(item => item.commentId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加图书评论"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _commentId = row.commentId || ids.value
  getComment(_commentId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改图书评论"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["commentRef"].validate(valid => {
    if (valid) {
      if (form.value.commentId != null) {
        updateComment(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addComment(form.value).then(response => {
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
  const _commentIds = row.commentId || ids.value
  proxy.$modal.confirm('是否确认删除图书评论编号为"' + _commentIds + '"的数据项？').then(function() {
    return delComment(_commentIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('Comment/comment/export', {
    ...queryParams.value
  }, `comment_${new Date().getTime()}.xlsx`)
}

getList()
</script>
