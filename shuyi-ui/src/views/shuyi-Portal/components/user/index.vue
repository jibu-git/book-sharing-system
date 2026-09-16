<template>
    <div class="portal-profile-container">
      <el-row :gutter="20">
        <el-col :span="8" :xs="24">
          <el-card class="box-card user-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>个人信息</span>
              </div>
            </template>
            <div>
              <div class="text-center avatar-box">
                <userAvatar />
              </div>
              <ul class="list-group list-group-striped">
                <li class="list-group-item">
                  <div class="item-label"><el-icon><User /></el-icon> 用户名称</div>
                  <div class="pull-right">{{ state.user.userName }}</div>
                </li>
                <li class="list-group-item">
                  <div class="item-label"><el-icon><Phone /></el-icon> 手机号码</div>
                  <div class="pull-right">{{ state.user.phonenumber }}</div>
                </li>
                <li class="list-group-item">
                  <div class="item-label"><el-icon><Message /></el-icon> 用户邮箱</div>
                  <div class="pull-right">{{ state.user.email }}</div>
                </li>
                <li class="list-group-item" v-if="state.user.dept">
                  <div class="item-label"><el-icon><OfficeBuilding /></el-icon> 所属部门</div>
                  <div class="pull-right">{{ state.user.dept.deptName }} / {{ state.postGroup }}</div>
                </li>
                <li class="list-group-item">
                  <div class="item-label"><el-icon><CollectionTag /></el-icon> 所属角色</div>
                  <div class="pull-right">{{ state.roleGroup }}</div>
                </li>
                <li class="list-group-item">
                  <div class="item-label"><el-icon><Calendar /></el-icon> 创建日期</div>
                  <div class="pull-right">{{ state.user.createTime }}</div>
                </li>
              </ul>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="16" :xs="24">
          <el-card class="box-card tabs-card" shadow="hover">
            <template #header>
              <div class="card-header">
                <span>账号设置</span>
              </div>
            </template>
            <el-tabs v-model="selectedTab" class="custom-tabs">
              <el-tab-pane label="基本资料" name="userinfo">
                <userInfo :user="state.user" />
              </el-tab-pane>
              <el-tab-pane label="修改密码" name="resetPwd">
                <resetPwd />
              </el-tab-pane>
            </el-tabs>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </template>
  
  <script setup name="PortalProfile">
  import { ref, reactive, onMounted } from "vue";
  import { useRoute } from "vue-router";
  import { getUserProfile } from "@/api/system/user";
  import userAvatar from "./userAvatar.vue";
  import userInfo from "./userInfo.vue";
  import resetPwd from "./resetPwd.vue";
  import { User, Phone, Message, OfficeBuilding, CollectionTag, Calendar } from "@element-plus/icons-vue";
  
  const route = useRoute();
  const selectedTab = ref("userinfo");
  
  // 完全使用若依的数据流转模型
  const state = reactive({
    user: {},
    roleGroup: {},
    postGroup: {}
  });
  
  function getUser() {
    getUserProfile().then(response => {
      state.user = response.data;
      state.roleGroup = response.roleGroup;
      state.postGroup = response.postGroup;
    });
  }
  
  onMounted(() => {
    const activeTab = route.params && route.params.activeTab;
    if (activeTab) {
      selectedTab.value = activeTab;
    }
    getUser();
  });
  </script>
  
  <style scoped>
  .portal-profile-container {
    max-width: 1200px;
    margin: 30px auto;
    padding: 0 20px;
  }
  .box-card {
    border-radius: 12px;
    border: none;
    margin-bottom: 20px;
  }
  .card-header {
    font-weight: bold;
    font-size: 16px;
    color: #303133;
  }
  .avatar-box {
    padding: 20px 0 30px;
  }
  .list-group {
    list-style: none;
    padding: 0;
    margin: 0;
  }
  .list-group-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #f0f2f5;
    font-size: 14px;
  }
  .item-label {
    display: flex;
    align-items: center;
    gap: 8px;
    color: #606266;
  }
  .pull-right {
    color: #303133;
    font-weight: 500;
  }
  .text-center { text-align: center; }
  </style>