<template>
  <div class="publish-container">
    <div class="publish-card">
      <header class="publish-header">
        <h1 class="title">分享新藏书</h1>
        <p class="subtitle">填写书籍信息，开启知识共享之旅</p>
      </header>

      <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="modern-form">

          <el-form-item prop="booksCover" label="书籍封面">
              <div class="cover-upload-wrapper">
              <el-upload
                  class="cover-uploader"
                  :action="uploadImgUrl"
                  :headers="headers"
                  :show-file-list="false"
                  :on-success="handleUploadSuccess"
                  :before-upload="beforeUpload"
              >
                  <img v-if="form.booksCover" :src="getRealImagePath(form.booksCover)" class="preview-img" />
                  <div v-else class="upload-placeholder">
                  <el-icon class="upload-icon"><Plus /></el-icon>
                  <span>点击上传封面</span>
                  <p class="upload-hint">支持 JPG/PNG 格式</p>
                  </div>
              </el-upload>
              </div>
          </el-form-item>

        <div class="form-row">
          <el-form-item label="书籍名称" prop="booksName" class="flex-1">
            <el-input v-model="form.booksName" placeholder="请输入完整书名" clearable />
          </el-form-item>
          <el-form-item label="作者" prop="booksAuthor" class="flex-1">
            <el-input v-model="form.booksAuthor" placeholder="作者名" clearable />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="所属分类" prop="booksTypeId" class="flex-1">
            <el-select v-model="form.booksTypeId" placeholder="选择分类" class="full-width">
              <el-option 
                v-for="item in tagList" 
                :key="item.bookTypeId" 
                :label="item.bookTypeName" 
                :value="item.bookTypeId" 
              />
            </el-select>
          </el-form-item>
          <el-form-item label="出版社" prop="booksPublisher" class="flex-1">
            <el-input v-model="form.booksPublisher" placeholder="出版社名称" clearable />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="提供数量 (库存)" prop="totalStock" class="flex-1">
            <el-input-number 
              v-model="form.totalStock" 
              :min="1" 
              :max="99" 
              controls-position="right"
              class="full-width-number" 
            />
          </el-form-item>
          <div class="flex-1"></div> 
        </div>

        <el-form-item label="书籍简介 / 分享寄语" prop="booksDescription">
          <el-input 
            v-model="form.booksDescription" 
            type="textarea" 
            :rows="5" 
            placeholder="简要介绍一下这本书的内容..." 
            maxlength="300"
            show-word-limit
          />
        </el-form-item>

        <div class="submit-bar">
          <el-button type="primary" size="large" @click="submitForm" :loading="submitting" class="submit-btn">
            提交审核申请
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { listType } from "@/api/BookType/type"
import { addBooks } from "@/api/books/books"
import { getToken } from "@/utils/auth"

const router = useRouter()
const formRef = ref(null)
const tagList = ref([])
const submitting = ref(false)

const uploadImgUrl = import.meta.env.VITE_APP_BASE_API + "/common/upload"
const headers = { Authorization: "Bearer " + getToken() }

const form = ref({
  booksName: '',
  booksTypeId: '',
  booksAuthor: '',
  booksPublisher: '',
  booksCover: '',
  booksDescription: '',
  totalStock: 1, // 默认 1 本
  availableStock: 1, // 初始可用库存 = 总库存
  booksStatus: '3'
})

const rules = {
  booksName: [{ required: true, message: '请填写书名', trigger: 'blur' }],
  booksAuthor: [{ required: true, message: '请填写作者', trigger: 'blur' }],
  booksPublisher: [{ required: true, message: '请填写出版社', trigger: 'blur' }],
  booksTypeId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  booksCover: [{ required: true, message: '请上传书籍封面', trigger: 'change' }],
  totalStock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}

const getRealImagePath = (url) => {
  if (!url) return ''
  return url.startsWith('http') ? url : import.meta.env.VITE_APP_BASE_API + url
}

const handleUploadSuccess = (res) => {
  form.value.booksCover = res.fileName
  ElMessage.success('封面上传成功')
}
const beforeUpload = (file) => {
  const isTypeOk = ['image/jpeg', 'image/png', 'image/webp'].includes(file.type);
  if (!isTypeOk) ElMessage.error('仅支持 JPG/PNG/WEBP 格式');
  return isTypeOk;
}
const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  // 提交前同步：初始可用库存必须等于总库存
  form.value.availableStock = form.value.totalStock;
  try {
    await addBooks(form.value)
    ElMessage.success('申请提交成功，请耐心等待管理员审核')
    router.push('/user/mySharedBooks')
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  const res = await listType({ pageSize: 100 })
  tagList.value = res.rows
})
</script>

<style scoped>
/* 保持你原有的所有样式不变 */
.publish-container { background: #f8fafc; min-height: calc(100vh - 64px); padding: 40px 20px; }
.publish-card { max-width: 750px; margin: 0 auto; background: #fff; border-radius: 20px; padding: 40px 50px; box-shadow: 0 4px 20px rgba(0,0,0,0.02); }
.publish-header { text-align: center; margin-bottom: 30px; }
.title { font-size: 1.6rem; color: #1e293b; letter-spacing: 2px; }
.subtitle { color: #94a3b8; font-size: 0.9rem; margin-top: 8px; }

.form-row { display: flex; gap: 20px; }
.flex-1 { flex: 1; }
.full-width { width: 100%; }

/* 新增：让数字输入框撑满父容器，保持对齐 */
.full-width-number { width: 100%; }
:deep(.el-input-number .el-input__wrapper) { text-align: left; }

.cover-upload-wrapper { display: flex; justify-content: center; margin-bottom: 10px; }
.cover-uploader { 
  width: 200px; 
  aspect-ratio: 3 / 4; 
  border: 2px dashed #e2e8f0; 
  border-radius: 16px; 
  overflow: hidden; 
  transition: all 0.4s ease; 
  background: #fcfdfe; 
  cursor: pointer; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
}
.cover-uploader:hover { border-color: #6366f1; background: #f8fafc; }
.preview-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.upload-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20px; color: #94a3b8; }
.upload-hint { font-size: 12px; margin-top: 8px; opacity: 0.7; }

:deep(.el-upload) { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; }
.submit-bar { margin-top: 30px; display: flex; justify-content: center; }
.submit-btn { padding: 0 60px; height: 46px; border-radius: 23px; background: #1e293b; border: none; }

@media (max-width: 600px) {
  .form-row { flex-direction: column; gap: 0; }
  .publish-card { padding: 30px 20px; }
}
</style>