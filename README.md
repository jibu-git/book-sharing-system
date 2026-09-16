# 图书分享系统（book-sharing-system）

📚 面向校园的二手图书 C2C 借阅流转平台：支持藏书发布、借阅订单全生命周期、积分体系与多维筛选检索，形成完整的图书闲置共享业务闭环。

> 本项目基于 [RuoYi 若依框架](https://gitee.com/y_project/RuoYi)（MIT 协议）二次开发，前端工程为 `shuyi-ui`。

## 功能特性

**用户端**
- 图书浏览 / 多维筛选检索
- 收藏 / 购物车
- 借阅下单（订单状态机：待借阅 → 借阅中 → 待归还 → 已完成）
- 积分体系与积分流水
- 图书评论、站内消息、收货地址管理

**管理端（若依自带）**
- 用户 / 角色 / 菜单 / 字典 等权限管理
- 图书、订单、分类管理
- 操作日志 / 登录日志 / 缓存监控

## 技术栈

| 端 | 技术 |
|---|---|
| 后端 | Java / Spring Boot / Spring Security + JWT / MyBatis / MySQL / Redis |
| 前端 | Vue / Element UI / Axios |
| 工程 | Maven / Docker / Nginx |

## 项目结构

```
├── ruoyi-admin       # 启动模块 + Web 控制器
├── ruoyi-common      # 公共模块（工具类、常量、统一返回）
├── ruoyi-framework   # 框架核心（安全认证、配置）
├── ruoyi-system      # 系统业务（用户、角色、菜单）
├── ruoyi-generator   # 代码生成器
├── ruoyi-quartz      # 定时任务
├── shuyi-ui          # 前端工程
└── sql               # 数据库初始化脚本
```

## 快速开始

### 环境要求
- JDK 8+、Maven 3.6+
- MySQL 5.7+、Redis
- Node.js（前端构建）

### 1. 初始化数据库
使用 `sql/` 目录下的脚本，在 MySQL 中新建数据库并导入建表语句与初始化数据。

### 2. 启动后端
1. 修改 `ruoyi-admin/src/main/resources/application-druid.yml` 中的数据库账号密码。
2. 启动 `RuoYiApplication.java`，默认端口 `8080`。

### 3. 启动前端
```bash
cd shuyi-ui
npm install
npm run dev
```
浏览器访问前端地址，默认管理员账号 `admin / 123456`（首次登录请修改）。

> ⚠️ 启动细节可能因本地环境而异，如遇问题欢迎提 Issue。



## 开源协议

本项目基于 [RuoYi（MIT）](https://gitee.com/y_project/RuoYi) 二次开发，代码遵循 **MIT License**，使用前请保留版权声明。
