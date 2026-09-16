<template>
    <div class="address-container">
      <div class="page-header">
        <h2 class="title">我的收货地址</h2>
        <el-button type="primary" icon="Plus" @click="handleAdd" round>新增地址</el-button>
      </div>
  
      <div v-loading="loading" class="address-list">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8" v-for="item in tAddressList" :key="item.aid">
            <el-card :class="['address-card', { 'is-default': item.isDefault === 1 }]" shadow="hover">
              <div class="card-header">
                <span class="user-name">{{ item.name }}</span>
                <span class="tag" v-if="item.tag">{{ item.tag }}</span>
                <el-tag v-if="item.isDefault === 1" type="danger" size="small" effect="dark">默认</el-tag>
              </div>
              
              <div class="card-content">
                <p class="phone"><el-icon><Phone /></el-icon>{{ item.phone }}</p>
                <p class="area">
                  <el-icon><Location /></el-icon>
                  {{ item.provinceName }} {{ item.cityName }}
                </p>
                <p class="detail">{{ item.address }}</p>
              </div>
  
              <div class="card-actions">
                <el-button link type="primary" @click="handleUpdate(item)">修改</el-button>
                <el-divider direction="vertical" />
                <el-button link type="danger" @click="handleDelete(item)">删除</el-button>
                <el-button 
                  v-if="item.isDefault !== 1" 
                  link 
                  type="info" 
                  @click="handleSetDefault(item)"
                >设为默认</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
  
        <el-empty v-if="!loading && tAddressList.length === 0" description="暂无收货地址，点击右上角添加" />
      </div>
  
      <el-dialog 
        :title="title" 
        v-model="open" 
        width="500px" 
        append-to-body
        destroy-on-close
        class="address-dialog"
      >
        <el-form ref="tAddressRef" :model="form" :rules="rules" label-position="top">
          <el-row :gutter="15">
            <el-col :span="12">
              <el-form-item label="收货人姓名" prop="name">
                <el-input v-model="form.name" placeholder="请填写姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="手机号码" prop="phone">
                <el-input v-model="form.phone" placeholder="请填写手机号" />
              </el-form-item>
            </el-col>
          </el-row>
  
          <el-row :gutter="15">
            <el-col :span="12">
              <el-form-item label="省份" prop="provinceName">
                <el-input v-model="form.provinceName" placeholder="如：广西壮族自治区" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="城市" prop="cityName">
                <el-input v-model="form.cityName" placeholder="如：桂林市" />
              </el-form-item>
            </el-col>
          </el-row>
  
          <el-form-item label="详细地址" prop="address">
            <el-input 
              v-model="form.address" 
              type="textarea" 
              :rows="2" 
              placeholder="街道、楼牌号等" 
            />
          </el-form-item>
  
          <el-row :gutter="15">
            <el-col :span="12">
              <el-form-item label="地址标签" prop="tag">
                <el-select v-model="form.tag" placeholder="请选择标签" clearable>
                  <el-option label="家" value="家" />
                  <el-option label="学校" value="学校" />
                  <el-option label="公司" value="公司" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="设为默认" prop="isDefault">
                <el-switch 
                  v-model="form.isDefault" 
                  :active-value="1" 
                  :inactive-value="0" 
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="cancel">取 消</el-button>
            <el-button type="primary" @click="submitForm" :loading="submitLoading">保 存</el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </template>
  
  <script setup name="UserAddress">
  import { ref, reactive, onMounted, getCurrentInstance } from 'vue'
  import { Phone, Location } from '@element-plus/icons-vue'
  import { listTAddress, getTAddress, delTAddress, addTAddress, updateTAddress } from "@/api/TAddress/tAddress"
  import { ElMessage, ElMessageBox } from 'element-plus'
  
  const { proxy } = getCurrentInstance()
  
  // 响应式数据
  const tAddressList = ref([])
  const loading = ref(true)
  const open = ref(false)
  const title = ref("")
  const submitLoading = ref(false)
  
  const data = reactive({
    form: {},
    rules: {
      name: [{ required: true, message: "收货人姓名不能为空", trigger: "blur" }],
      phone: [
        { required: true, message: "手机号码不能为空", trigger: "blur" },
        { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
      ],
      provinceName: [{ required: true, message: "省份不能为空", trigger: "blur" }],
      cityName: [{ required: true, message: "城市不能为空", trigger: "blur" }],
      address: [{ required: true, message: "详细地址不能为空", trigger: "blur" }]
    }
  })
  
  const { form, rules } = toRefs(data)
  
  /** 获取地址列表 */
  const getList = async () => {
    loading.value = true
    try {
      const res = await listTAddress({ pageNum: 1, pageSize: 50 })
      tAddressList.value = res.rows
    } finally {
      loading.value = false
    }
  }
  
  /** 重置表单 */
  const reset = () => {
    form.value = {
      aid: null,
      name: null,
      provinceName: null,
      cityName: null,
      address: null,
      phone: null,
      tag: null,
      isDefault: 0
    }
  }
  
  /** 新增 */
  const handleAdd = () => {
    reset()
    open.value = true
    title.value = "新增收货地址"
  }
  
  /** 修改 */
  const handleUpdate = (row) => {
    reset()
    getTAddress(row.aid).then(response => {
      form.value = response.data
      open.value = true
      title.value = "修改收货地址"
    })
  }
  
  /** 设置默认 */
  const handleSetDefault = (row) => {
    const data = { ...row, isDefault: 1 }
    updateTAddress(data).then(() => {
      ElMessage.success("设置默认地址成功")
      getList()
    })
  }
  
  /** 提交表单 */
  const submitForm = () => {
    proxy.$refs["tAddressRef"].validate(async (valid) => {
      if (valid) {
        submitLoading.value = true
        try {
          if (form.value.aid != null) {
            await updateTAddress(form.value)
            ElMessage.success("修改成功")
          } else {
            await addTAddress(form.value)
            ElMessage.success("新增成功")
          }
          open.value = false
          getList()
        } finally {
          submitLoading.value = false
        }
      }
    })
  }
  
  /** 删除 */
  const handleDelete = (row) => {
    ElMessageBox.confirm('确定要删除这个地址吗？', '提示', { type: 'warning' })
      .then(() => delTAddress(row.aid))
      .then(() => {
        ElMessage.success("删除成功")
        getList()
      })
      .catch(() => {})
  }
  
  const cancel = () => {
    open.value = false
  }
  
  onMounted(() => {
    getList()
  })
  </script>
  
  <style scoped>
  .address-container {
    max-width: 1000px;
    margin: 30px auto;
    padding: 0 20px;
  }
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
  }
  
  .title {
    font-size: 1.5rem;
    font-weight: bold;
    color: #333;
  }
  
  .address-list {
    min-height: 400px;
  }
  
  .address-card {
    margin-bottom: 20px;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
  }
  
  .address-card.is-default {
    border-color: var(--el-color-primary-light-3);
    background-color: var(--el-color-primary-light-9);
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 15px;
  }
  
  .user-name {
    font-size: 1.1rem;
    font-weight: 600;
    color: #1e293b;
  }
  
  .tag {
    font-size: 12px;
    padding: 2px 8px;
    background: #f1f5f9;
    color: #64748b;
    border-radius: 4px;
  }
  
  .card-content {
    font-size: 0.95rem;
    color: #475569;
    line-height: 1.6;
  }
  
  .card-content p {
    display: flex;
    align-items: center;
    gap: 8px;
    margin: 8px 0;
  }
  
  .card-content .detail {
    margin-top: 10px;
    color: #1e293b;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  
  .card-actions {
    margin-top: 20px;
    padding-top: 15px;
    border-top: 1px solid #f1f5f9;
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
  
  /* 响应式调整 */
  @media (max-width: 768px) {
    .address-container { margin: 15px auto; }
    .page-header { flex-direction: column; gap: 15px; align-items: flex-start; }
  }
  </style>