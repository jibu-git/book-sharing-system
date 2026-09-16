<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="图书名称" prop="booksName">
        <el-input
          v-model="queryParams.booksName"
          placeholder="请输入图书名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="图书作者" prop="booksAuthor">
        <el-input
          v-model="queryParams.booksAuthor"
          placeholder="请输入图书作者"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="出版社" prop="booksPublisher">
        <el-input
          v-model="queryParams.booksPublisher"
          placeholder="请输入出版社"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="标签ID" prop="booksTypeId">
        <el-input
          v-model="queryParams.booksTypeId"
          placeholder="请输入图书标签ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="图书状态" prop="booksStatus" >
        <el-select v-model="queryParams.booksStatus" placeholder="请选择图书状态" clearable style="width: 150px;">
          <el-option
            v-for="dict in books_status"
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
          v-hasPermi="['Books:books:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['Books:books:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['Books:books:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['Books:books:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="booksList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="图书ID" align="center" prop="booksId" />
      <el-table-column label="图书名称" align="center" prop="booksName" />
      <el-table-column label="图书作者" align="center" prop="booksAuthor" />
      <el-table-column label="出版社" align="center" prop="booksPublisher" />
      <el-table-column label="图书标签ID" align="center" prop="booksTypeId" />
      <el-table-column label="图书封面" align="center" prop="booksCover" width="100">
        <template #default="scope">
          <image-preview :src="scope.row.booksCover" :width="50" :height="50"/>
        </template>
      </el-table-column>
      <el-table-column label="图书描述" align="center" prop="booksDescription" />
      <el-table-column label="图书状态" align="center" prop="booksStatus">
        <template #default="scope">
          <dict-tag :options="books_status" :value="scope.row.booksStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="备注" align="center" prop="remark" />
      
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['Books:books:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['Books:books:remove']">删除</el-button>
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

    <!-- 添加或修改图书商品对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="booksRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="图书名称" prop="booksName">
          <el-input v-model="form.booksName" placeholder="请输入图书名称" />
        </el-form-item>
        <el-form-item label="图书作者" prop="booksAuthor">
          <el-input v-model="form.booksAuthor" placeholder="请输入图书作者" />
        </el-form-item>
        <el-form-item label="出版社" prop="booksPublisher">
          <el-input v-model="form.booksPublisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="图书标签ID" prop="booksTypeId">
          <el-input v-model="form.booksTypeId" placeholder="请输入图书标签ID" />
        </el-form-item>
        <el-form-item label="图书封面" prop="booksCover">
          <image-upload v-model="form.booksCover"/>
        </el-form-item>
        <el-form-item label="图书描述" prop="booksDescription">
          <el-input v-model="form.booksDescription" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="图书状态" prop="booksStatus">
          <el-radio-group v-model="form.booksStatus">
            <el-radio
              v-for="dict in books_status"
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

<script setup name="Books">
import { listBooks, getBooks, delBooks, addBooks, updateBooks } from "@/api/Books/books"
import useUserStore from '@/store/modules/user'



const { proxy } = getCurrentInstance()
const { books_condition, books_status } = proxy.useDict('books_condition', 'books_status')


const booksList = ref([])
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
    booksName: null,
    booksAuthor: null,
    booksPublisher: null,
    booksTypeId: null,
    booksCover: null,
    booksDescription: null,
    booksStatus: 0,
    userId: null,
  },
  
  rules: {
    booksName: [
      { required: true, message: "图书名称不能为空", trigger: "blur" }
    ],
    booksAuthor: [
      { required: true, message: "图书作者不能为空", trigger: "blur" }
    ],
    booksPublisher: [
      { required: true, message: "出版社不能为空", trigger: "blur" }
    ],
    booksTypeId: [
      { required: true, message: "图书标签ID不能为空", trigger: "blur" }
    ],
    booksStatus: [
      { required: true, message: "图书状态不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询图书商品列表 */
function getList() {
  loading.value = true
  listBooks(queryParams.value).then(response => {
    booksList.value = response.rows
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
    booksId: null,
    booksName: null,
    booksAuthor: null,
    booksPublisher: null,
    booksTypeId: null,
    booksCover: null,
    booksDescription: null,
    booksStatus: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("booksRef")
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
  ids.value = selection.map(item => item.booksId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加图书商品"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _booksId = row.booksId || ids.value
  getBooks(_booksId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改图书商品"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["booksRef"].validate(valid => {
      // 从store中获取user_id
      const userStore = useUserStore()
      const user_id = userStore.id
      
      // 检查权限：user_id不为1且尝试修改/新增上架状态(booksStatus=0)
      if (user_id != 1 && form.value.booksStatus == 0) {
        proxy.$modal.msgError("只有管理员可以上架图书")
        return
      }
      
      if (form.value.booksId != null) {
        updateBooks(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addBooks(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    })
  }


/** 删除按钮操作 */
function handleDelete(row) {
  const _booksIds = row.booksId || ids.value
  proxy.$modal.confirm('是否确认删除图书商品编号为"' + _booksIds + '"的数据项？').then(function() {
    return delBooks(_booksIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('Books/books/export', {
    ...queryParams.value
  }, `books_${new Date().getTime()}.xlsx`)
}

getList()
</script>
