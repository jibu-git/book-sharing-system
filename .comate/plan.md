# 订单创建功能增强计划

## 背景
需在 `TOrderItemServiceImpl` 中实现订单创建完整逻辑，包括：创建订单商品列表、调用新增订单方法、查询默认地址、设置状态等。

## 执行步骤

### 1. 注入依赖服务
- **文件**: `ruoyi-admin/src/main/java/com/ruoyi/tOrderItem/service/impl/TOrderItemServiceImpl.java`
- **操作**: 添加 `@Autowired` 注解注入以下服务：
  - `ITOrderService tOrderService` (创建订单)
  - `ITAddressService tAddressService` (查询默认地址)
  - `IBooksService booksService` (查询商品信息)
- **预期**: 服务注入成功，无编译错误。

### 2. 修改新增订单商品方法
- **文件**: `TOrderItemServiceImpl.java` 的 `insertTOrderItem` 方法
- **操作**:
  - 调用 `tOrderService.insertTOrder` 创建主订单
  - 调用 `tAddressService.selectDefaultAddressByUserId` 查询默认地址
  - 设置订单状态为 "未支付"
  - 处理商品 ID 数组，调用 `booksService.selectBooksByIds` 获取商品数据
  - 设置 `order_item_is_shipped = 0`
- **预期**: 订单与商品信息关联成功，状态设置正确。

### 3. 调整控制器逻辑（若需）
- **文件**: `ruoyi-admin/src/main/java/com/ruoyi/tOrderItem/controller/TOrderItemController.java`
- **操作**: 确保 `add` 方法正确传递 `user_id` 和商品 ID 数组
- **预期**: 前端请求能正确触发完整订单创建流程。

## 验证步骤
1. 调用新增订单接口，检查数据库中订单与商品记录是否正确关联
2. 验证默认地址是否被正确获取
3. 确认订单状态和发货状态设置正确

## 依赖说明
- 需确保 `TOrderService`、`TAddressService`、`BooksService` 已正确实现相关方法
- 若 `selectBooksByIds` 方法不存在，需先在 `IBooksService` 中定义并实现