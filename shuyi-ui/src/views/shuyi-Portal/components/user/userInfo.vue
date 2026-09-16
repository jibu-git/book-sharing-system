<template>
    <el-form ref="userRef" :model="form" :rules="rules" label-width="80px" style="padding: 20px 0;">
      <el-form-item label="用户昵称" prop="nickName">
        <el-input v-model="form.nickName" maxlength="30" placeholder="请输入用户昵称" />
      </el-form-item>
      <el-form-item label="手机号码" prop="phonenumber">
        <el-input v-model="form.phonenumber" maxlength="11" placeholder="请输入手机号码" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" maxlength="50" placeholder="请输入用户邮箱" />
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.sex">
          <el-radio value="0">男</el-radio>
          <el-radio value="1">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit" round>保存修改</el-button>
      </el-form-item>
    </el-form>
  </template>
  
  <script setup>
  import { ref, watch, getCurrentInstance } from "vue";
  import { updateUserProfile } from "@/api/system/user";
  
  const props = defineProps({
    user: {
      type: Object
    }
  });
  
  const { proxy } = getCurrentInstance();
  const form = ref({});
  const rules = ref({
    nickName: [{ required: true, message: "用户昵称不能为空", trigger: "blur" }],
    email: [
      { required: true, message: "邮箱地址不能为空", trigger: "blur" }, 
      { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }
    ],
    phonenumber: [
      { required: true, message: "手机号码不能为空", trigger: "blur" }, 
      { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }
    ],
  });
  
  /** 提交按钮 */
  function submit() {
    proxy.$refs.userRef.validate(valid => {
      if (valid) {
        updateUserProfile(form.value).then(response => {
          // 使用原版提示
          proxy.$modal.msgSuccess("修改成功");
          // 本地数据同步回显
          props.user.phonenumber = form.value.phonenumber;
          props.user.email = form.value.email;
          props.user.nickName = form.value.nickName;
          props.user.sex = form.value.sex;
        });
      }
    });
  }
  
  // 核心回显逻辑：监听父组件传来的 user 数据
  watch(() => props.user, user => {
    if (user) {
      form.value = { 
        nickName: user.nickName, 
        phonenumber: user.phonenumber, 
        email: user.email, 
        sex: user.sex 
      };
    }
  }, { immediate: true });
  </script>