package com.ruoyi.Order.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ruoyi.Message.domain.TMessage;
import com.ruoyi.Message.service.ITMessageService;
import com.ruoyi.books.domain.Books;
import com.ruoyi.books.mapper.BooksMapper;
import com.ruoyi.books.service.impl.BooksServiceImpl;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.orderItem.domain.TOrderItem;
import com.ruoyi.orderItem.service.ITOrderItemService;
import com.ruoyi.pointsRecord.domain.PointsRecord;
import com.ruoyi.pointsRecord.mapper.PointsRecordMapper;
import com.ruoyi.pointsRecord.service.impl.PointsRecordServiceImpl;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.tAddress.domain.TAddress;
import com.ruoyi.tAddress.service.ITAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.Order.mapper.TOrderMapper;
import com.ruoyi.Order.domain.TOrder;
import com.ruoyi.Order.service.ITOrderService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 借阅订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-23
 */
@Service
public class TOrderServiceImpl implements ITOrderService 
{
    @Autowired
    private TOrderMapper tOrderMapper;

    @Autowired
    private ITMessageService tMessageService;

    @Autowired
    private BooksServiceImpl booksService;

    @Autowired
    private ITAddressService tAddressService;

    @Autowired
    private ITOrderItemService tOrderItemService;

    @Autowired
    private BooksMapper booksMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PointsRecordServiceImpl pointsRecordService;
    /**
     * 查询借阅订单
     * 
     * @param orderId 借阅订单主键
     * @return 借阅订单
     */
    @Override
    public TOrder selectTOrderByOrderId(Long orderId)
    {
        return tOrderMapper.selectTOrderByOrderId(orderId);
    }

    /**
     * 查询借阅订单列表
     * 
     * @param tOrder 借阅订单
     * @return 借阅订单
     */
    @Override
    public List<TOrder> selectTOrderList(TOrder tOrder)
    {
        Long currentUserId = SecurityUtils.getUserId();


        // 如果是管理员，我们直接跳过 ID 设置，Mapper 就会查出所有数据
        if (currentUserId != null && currentUserId == 1) {

        }
        else {
            // 非管理员逻辑：根据 Controller 传过来的字段进行过滤
            if (tOrder.getBorrowerId() == null && tOrder.getOwnerId() == null) {
                tOrder.setBorrowerId(currentUserId);
            }
        }
        return tOrderMapper.selectTOrderList(tOrder);
    }

    /**
     * 根据用户ID查询订单ID列表
     *
     * @param userId 用户ID
     * @return 订单ID列表
     */
    @Override
    public List<Long> selectOrderIdsByUserId(Long userId)
    {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return tOrderMapper.selectOrderIdsByUserId(userId);
    }

    /**
     * 根据用户ID查询订单及其订单项信息
     *
     * @param userId 用户ID
     * @return 订单及订单项信息列表
     */
//    @Override
//    public List<TOrderWithItems> selectOrderWithItemsByUserId(Long userId)
//    {
//        if (userId == null) {
//            throw new IllegalArgumentException("用户ID不能为空");
//        }
//        return tOrderMapper.selectOrderWithItemsByUserId(userId);
//    }

    /**
     * 新增借阅订单
     * 
     * @param tOrder 借阅订单
     * @return 结果
     */
    @Override
    public int insertTOrder(TOrder tOrder)
    {
        tOrder.setCreateTime(DateUtils.getNowDate());
        return tOrderMapper.insertTOrder(tOrder);
    }

    /**
     * 修改借阅订单
     * 
     * @param tOrder 借阅订单
     * @return 结果
     */
    @Override
    public int updateTOrder(TOrder tOrder)
    {
        tOrder.setUpdateTime(DateUtils.getNowDate());

        return tOrderMapper.updateTOrder(tOrder);
    }

    /**
     * 批量删除借阅订单
     * 
     * @param orderIds 需要删除的借阅订单主键
     * @return 结果
     */
    @Override
    public int deleteTOrderByOrderIds(Long[] orderIds)
    {
        return tOrderMapper.deleteTOrderByOrderIds(orderIds);
    }

    /**
     * 删除借阅订单信息
     * 
     * @param orderId 借阅订单主键
     * @return 结果
     */
    @Override
    public int deleteTOrderByOrderId(Long orderId)
    {
        return tOrderMapper.deleteTOrderByOrderId(orderId);
    }

    /**
     * 借阅者主动取消订单（仅限待处理状态，全额退还积分）
     * * @param orderId 订单ID
     * @param userId 当前操作人(借阅者)的ID，用于权限校验
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId) {
        TOrder order = tOrderMapper.selectTOrderByOrderId(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!order.getBorrowerId().equals(userId)) throw new RuntimeException("无权操作他人的订单");
        if (!"1".equals(order.getOrderStatus())) throw new RuntimeException("当前订单状态无法取消");

        Integer pledgePoints = order.getPledgePoints() != null ? order.getPledgePoints() : 0;
        Integer consumePoints = order.getConsumePoints() != null ? order.getConsumePoints() : 0;
        Integer totalRefund = pledgePoints + consumePoints;

        // ================= 核心修改：统一调用流水解冻 =================
        if (totalRefund > 0) {
            PointsRecord refundRecord = new PointsRecord();
            refundRecord.setUserId(userId);
            refundRecord.setAmount(totalRefund); // 传入正数
            refundRecord.setTradeType(4); // 4-解冻押金
            refundRecord.setRemark("主动取消借阅申请，全额解冻并退回积分");
            refundRecord.setOrderId(orderId);
            pointsRecordService.insertPointsRecord(refundRecord);
        }

        order.setOrderStatus("2");
        order.setUpdateTime(new Date());
        order.setRemark("借阅者主动取消申请");
        int result = tOrderMapper.updateTOrder(order);

        if (result > 0) {
            String msgContent = "借友已主动取消借阅申请，该订单已关闭。";
            createAndSendMessage(userId, order.getOwnerId(), "1", "借阅申请已取消", msgContent, orderId);
        }

        return result > 0;
    }


    /**
     * 借书人发起归还申请
     * 逻辑：修改订单状态为 3，并通知书主确认
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int returnOrder(Long orderId) {
        // 1. 获取订单最新信息
        TOrder oldOrder = tOrderMapper.selectTOrderByOrderId(orderId);

        // 2. 严格校验逻辑
        if (oldOrder == null) {
            throw new RuntimeException("订单不存在");
        }

        if (!"0".equals(oldOrder.getOrderStatus())) {
            throw new RuntimeException("当前订单状态不可发起归还（只有借阅中的书籍可归还）");
        }

        // 3. 执行状态变更
        TOrder order = new TOrder();
        order.setOrderId(orderId);
        order.setOrderStatus("3"); // 状态改为：3 (待书主确认归还)
        order.setUpdateTime(new Date());
        int result = tOrderMapper.updateTOrder(order);

        // 4. 发送消息通知给书主
        if (result > 0) {
            // 获取该订单下的图书明细，用于拼装消息内容
            TOrderItem queryItem = new TOrderItem();
            queryItem.setOrderId(orderId);
            List<TOrderItem> items = tOrderItemService.selectTOrderItemList(queryItem);

            List<String> bookNames = new ArrayList<>();
            for (TOrderItem item : items) {
                bookNames.add("《" + item.getBookTitle() + "》");
            }

            String contentNames = String.join("、", bookNames);
            if (contentNames.length() > 50) {
                contentNames = contentNames.substring(0, 50) + "...等";
            }

            // 发送给书主 (receiverId = oldOrder.getOwnerId())
            String msgContent = "书友已归还您的藏书 " + contentNames + "。如果您已收到实体书，请前往【借出管理】点击“确认收到”，系统将自动恢复图书库存。";

            createAndSendMessage(
                    oldOrder.getBorrowerId(), // 发送者：借书人
                    oldOrder.getOwnerId(),     // 接收者：书主
                    "1",                       // 消息类型：书主订单通知
                    "图书归还提醒",             // 标题
                    msgContent,                // 内容
                    orderId                    // 关联订单ID
            );
        }

        return result;
    }



    /**
     * 书主确认收到归还
     * 逻辑：
     * 1. 订单状态 -> 4 已完成
     * 2. 核心：原子恢复可用库存 (available_stock + 1)
     * 3. 联动：如果恢复后库存 > 0，确保图书状态为 0 (正常上架)
     * 4. 新增：通知借书人，告知流程已圆满结束
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmReceipt(Long orderId) {
        TOrder order = tOrderMapper.selectTOrderByOrderId(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!"3".equals(order.getOrderStatus())) throw new RuntimeException("当前订单状态非待确认归还状态，无法操作");

        // 1. 获取订单中所有的图书项
        TOrderItem queryItem = new TOrderItem();
        queryItem.setOrderId(orderId);
        List<TOrderItem> items = tOrderItemService.selectTOrderItemList(queryItem);

        // 2. 遍历所有订单项，循环更新每本书的库存和状态
        if (items != null && !items.isEmpty()) {
            for (TOrderItem item : items) {
                Long bookId = item.getBookId();
                Books book = booksMapper.selectBooksByBooksId(bookId);
                if (book != null) {
                    // 恢复库存
                    book.setAvailableStock(book.getAvailableStock() + 1);
                    // 确保状态为 0 (正常上架)
                    book.setBooksStatus("0");
                    booksMapper.updateBooks(book);
                }
            }
        }

        Integer pledgePoints = order.getPledgePoints() != null ? order.getPledgePoints() : 0;
        Integer consumePoints = order.getConsumePoints() != null ? order.getConsumePoints() : 0;
        Integer rewardPoints = order.getRewardPoints() != null ? order.getRewardPoints() : 0;
        Integer totalFrozen = pledgePoints + consumePoints;

        Long borrowerId = order.getBorrowerId();
        Long ownerId = order.getOwnerId();

        // 3. 积分处理逻辑保持不变...
        if (totalFrozen > 0) {
            PointsRecord unfreezeRecord = new PointsRecord();
            unfreezeRecord.setUserId(borrowerId);
            unfreezeRecord.setAmount(totalFrozen);
            unfreezeRecord.setTradeType(4);
            unfreezeRecord.setRemark("图书归还，全额解冻担保积分");
            unfreezeRecord.setOrderId(orderId);
            pointsRecordService.insertPointsRecord(unfreezeRecord);
        }

        if (consumePoints > 0) {
            PointsRecord payRentRecord = new PointsRecord();
            payRentRecord.setUserId(borrowerId);
            payRentRecord.setAmount(consumePoints);
            payRentRecord.setTradeType(2);
            payRentRecord.setRemark("扣除单次借阅租金");
            payRentRecord.setOrderId(orderId);
            pointsRecordService.insertPointsRecord(payRentRecord);
        }

        if (rewardPoints > 0) {
            PointsRecord incomeRecord = new PointsRecord();
            incomeRecord.setUserId(ownerId);
            incomeRecord.setAmount(rewardPoints);
            incomeRecord.setTradeType(5);
            incomeRecord.setRemark("书友成功归还，获得借阅分成收益");
            incomeRecord.setOrderId(orderId);
            pointsRecordService.insertPointsRecord(incomeRecord);
        }

        // 4. 更新订单状态
        order.setOrderStatus("4");
        order.setUpdateTime(new Date());
        int result = tOrderMapper.updateTOrder(order);

        // 5. 发送消息通知
        if (result > 0) {
            // 这里为了提示语清晰，如果多本书可以显示“所借图书”
            String bookSummary = items.size() > 1 ? "您借阅的全部图书" : ("《" + items.get(0).getBookTitle() + "》");
            String msgContent = "书主已确认收到您归还的 " + bookSummary + "。本次借阅已顺利完结，系统已将押金 " + pledgePoints + " 积分退回您的账户，祝您生活愉快！";
            createAndSendMessage(ownerId, borrowerId, "4", "图书归还成功", msgContent, orderId);
        }

        return result;
    }



    /**
     * 书主同意借阅申请（基于库存的多副本处理 + 自动化消息通知）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int agreeLendOrder(Long orderId) {
        // 1. 获取当前订单信息
        TOrder currentOrder = tOrderMapper.selectTOrderByOrderId(orderId);
        if (currentOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"1".equals(currentOrder.getOrderStatus())) {
            throw new RuntimeException("订单状态非待处理，无法同意");
        }

        // 获取订单明细
        TOrderItem queryItem = new TOrderItem();
        queryItem.setOrderId(orderId);
        List<TOrderItem> items = tOrderItemService.selectTOrderItemList(queryItem);

        if (items == null || items.isEmpty()) {
            throw new RuntimeException("订单明细为空");
        }

        // 用于记录当前申请通过的书名，用于发消息
        List<String> approvedBookNames = new ArrayList<>();

        // 2. 核心：遍历明细并尝试扣减库存
        for (TOrderItem item : items) {
            Long bookId = item.getBookId();
            approvedBookNames.add("《" + item.getBookTitle() + "》");

            // 【原子操作】执行数据库扣减
            int updateRows = booksMapper.decreaseStock(bookId);

            if (updateRows == 0) {
                throw new RuntimeException("库存不足，无法同意此申请");
            }

            // 3. 检查扣减后的实时库存状态
            Books bookInfo = booksService.selectBooksByBooksId(bookId);

            // 如果库存归零，则同步修改图书状态为“2”（表示已被借完）
            if (bookInfo != null && bookInfo.getAvailableStock() <= 0) {
                Books updateBook = new Books();
                updateBook.setBooksId(bookId);
                updateBook.setBooksStatus("2");
                booksService.updateBooks(updateBook);

                // 库存空了，批量拒绝其他还在申请这本 bookId 的订单
                List<TOrder> otherPendingOrders = tOrderMapper.selectPendingOrdersByBookId(bookId, orderId);

                if (otherPendingOrders != null && !otherPendingOrders.isEmpty()) {
                    List<Long> otherOrderIds = otherPendingOrders.stream().map(TOrder::getOrderId).collect(Collectors.toList());
                    tOrderMapper.updateOrderStatusBatch(otherOrderIds, "2", "抱歉，该图书库存已耗尽，系统自动取消申请");

                    for (TOrder rejectedOrder : otherPendingOrders) {
                        Integer pledge = rejectedOrder.getPledgePoints() != null ? rejectedOrder.getPledgePoints() : 0;
                        Integer consume = rejectedOrder.getConsumePoints() != null ? rejectedOrder.getConsumePoints() : 0;
                        Integer refundAmount = pledge + consume;

                        if (refundAmount > 0) {
                            // ================= 核心修改：调用统一流水接口解冻 =================
                            PointsRecord refundRecord = new PointsRecord();
                            refundRecord.setUserId(rejectedOrder.getBorrowerId());
                            refundRecord.setAmount(refundAmount); // 传入正数
                            refundRecord.setTradeType(4); // 4-解冻押金
                            refundRecord.setRemark("由于库存不足订单自动取消，全额解冻押金与租金");
                            refundRecord.setOrderId(rejectedOrder.getOrderId());

                            pointsRecordService.insertPointsRecord(refundRecord); // 统一接管加回可用、扣除冻结
                        }

                        createAndSendMessage(0L, rejectedOrder.getBorrowerId(), "4", "申请自动取消提醒", "很抱歉，由于库存已借完，系统已自动取消申请，积分已全额退回。", rejectedOrder.getOrderId());
                    }
                }
            }
        }

        // 4. 更新当前订单状态为 "0" (已通过/借阅中)
        currentOrder.setOrderStatus("0");
        currentOrder.setUpdateTime(new Date());
        int result = tOrderMapper.updateTOrder(currentOrder);

        if (result > 0) {
            String contentNames = String.join("、", approvedBookNames);
            String msgContent = "好消息！书主已同意了您关于 " + contentNames + " 的借阅申请。请保持联系，确认线下交接或快递单号。";

            createAndSendMessage(
                    currentOrder.getOwnerId(),     // 发送者：书主
                    currentOrder.getBorrowerId(),  // 接收者：借书人
                    "4",                           // 消息类型：订单通知
                    "借阅申请已通过",               // 标题
                    msgContent,                    // 内容
                    orderId                        // 关联订单ID
            );
        }

        return result;
    }


    /**
     * 立即借阅下单（单本直接下单 + 库存校验 + 消息通知）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quickCheckout(Long userId, Long booksId, Long num) {
        // 1. 获取图书信息并校验基本状态
        Books book = booksService.selectBooksByBooksId(booksId);
        if (book == null) throw new RuntimeException("图书不存在");
        if (book.getAvailableStock() == null || book.getAvailableStock() <= 0) throw new RuntimeException("手慢了，该书当前已无库存可借");
        if (userId.equals(book.getUserId())) throw new RuntimeException("不能借阅自己发布的图书");

        Integer activeCount = tOrderMapper.countUserActiveOrderForBook(userId, booksId);
        if (activeCount != null && activeCount > 0) throw new RuntimeException("您已申请或正在借阅此书，每人限借一本哦");

        // 2. 积分计算与校验
        Integer baseValue = book.getBookPointsValue();
        if (baseValue == null || baseValue <= 0) throw new RuntimeException("该图书未设置有效的积分价值，无法借阅");

        // 押金 = 基础价值 * 10
        Integer pledgePoints = baseValue * 10;
        // 租金
        Integer consumePoints = Math.max(1, (int) Math.ceil(baseValue));
        Integer rewardPoints = (int) Math.floor(consumePoints * 0.8);
        Integer totalRequiredPoints = pledgePoints + consumePoints;

        SysUser user = sysUserMapper.selectUserById(userId);
        if (user == null) throw new RuntimeException("借阅用户信息异常");
        if (user.getPointsBalance() < totalRequiredPoints) {
            throw new RuntimeException("您的可用积分不足（需押金 " + pledgePoints + " + 租金 " + consumePoints + " 积分）");
        }

        // 3. 获取地址并创建订单 (先落库订单拿到 orderId，方便流水直接绑定)
        TAddress defaultAddress = tAddressService.getDefaultAddressByUserId(userId);
        if (defaultAddress == null) throw new RuntimeException("请先设置默认收货地址");

        TOrder order = createOrder(userId, book.getUserId(), defaultAddress, "立即借阅申请");
        order.setPledgePoints(pledgePoints);
        order.setConsumePoints(consumePoints);
        order.setRewardPoints(rewardPoints);
        tOrderMapper.insertTOrder(order);

        // 4. ================= 核心修改：调用流水统一接口冻结积分 =================
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setAmount(totalRequiredPoints); // 传入正数
        record.setTradeType(3); // 3-冻结押金
        record.setRemark("发起借阅申请，冻结押金 " + pledgePoints + " 与租金 " + consumePoints);
        record.setOrderId(order.getOrderId());
        pointsRecordService.insertPointsRecord(record); // 统一接管扣减可用、增加冻结

        // 5. 创建订单明细
        TOrderItem orderItem = createOrderItem(order.getOrderId(), book, 1L);
        tOrderItemService.insertTOrderItem(orderItem);

        // 6. 发送消息通知给书主
        String msgContent = "书友申请借阅您的《" + book.getBooksName() + "》，系统已冻结其相应积分作为担保，请尽快前往【借出管理】审核处理。";
        createAndSendMessage(userId, book.getUserId(), "1", "新的借阅申请", msgContent, order.getOrderId());



        return true;
    }

    /**
     * 创建订单方法
     */
    private TOrder createOrder(Long borrowerId, Long ownerId, TAddress address, String remark) {
        TOrder order = new TOrder();
        // 关键修改：设置双角色 ID
        order.setBorrowerId(borrowerId);
        order.setOwnerId(ownerId);

        // 设置收货信息
        order.setOrderRecvName(address.getName());
        order.setOrderRecvPhone(address.getPhone());
        order.setOrderRecvProvince(address.getProvinceName());
        order.setOrderRecvCity(address.getCityName());
        order.setOrderRecvAddress(address.getAddress());

        // 初始化状态
        order.setOrderStatus("1"); // 待处理
        order.setOrderTime(new Date());
        order.setCreateTime(new Date());
        order.setRemark(remark);
        return order;
    }

    /**
     * 创建订单项方法
     */
    private TOrderItem createOrderItem(Long orderId, Books book, Long num) {
        TOrderItem orderItem = new TOrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setBookId(book.getBooksId());
        orderItem.setBookTitle(book.getBooksName());
        orderItem.setBookImage(book.getBooksCover());
        // 无论前端传什么，这里强制 1L，符合一人一本规则
        orderItem.setBookNum(1L);
        orderItem.setCreateTime(new Date());
        return orderItem;
    }

    /**
     * 辅助方法：构建并插入消息记录 (建议放在 BaseService 或当前 Service 中复用)
     */
    private void createAndSendMessage(Long senderId, Long receiverId, String msgType, String title, String content, Long relatedId) {
        TMessage message = new TMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setMsgType(msgType);
        message.setTitle(title);
        message.setContent(content);
        message.setRelatedId(relatedId);
        message.setIsRead("0");
        message.setCreateTime(new Date());
        tMessageService.insertTMessage(message);
    }



    @Override
    public boolean checkUserHasBookOrder(Long borrowerId, Long bookId) {
        int count = tOrderMapper.checkUserHasBookOrder(borrowerId, bookId);
        return count > 0;
    }
}

