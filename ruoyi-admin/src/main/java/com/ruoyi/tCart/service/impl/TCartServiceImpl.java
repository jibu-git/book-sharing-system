package com.ruoyi.tCart.service.impl;
import java.math.BigDecimal;
import java.util.*;

import com.ruoyi.Message.domain.TMessage;
import com.ruoyi.Message.service.ITMessageService;
import com.ruoyi.Order.domain.TOrder;
import com.ruoyi.Order.service.ITOrderService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.orderItem.domain.TOrderItem;
import com.ruoyi.orderItem.service.ITOrderItemService;
import com.ruoyi.pointsRecord.domain.PointsRecord;
import com.ruoyi.pointsRecord.mapper.PointsRecordMapper;
import com.ruoyi.pointsRecord.service.impl.PointsRecordServiceImpl;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.tAddress.domain.TAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.tCart.mapper.TCartMapper;
import com.ruoyi.tCart.domain.TCart;
import com.ruoyi.tCart.service.ITCartService;
import com.ruoyi.tAddress.service.ITAddressService;
import com.ruoyi.books.domain.Books;
import com.ruoyi.books.service.IBooksService;
import org.springframework.transaction.annotation.Transactional;


/**
 * 借阅车Service业务层处理
 *
 * @author ruoyi
 * @date 2025-11-08
 */
@Service
public class TCartServiceImpl implements ITCartService
{
    @Autowired
    private TCartMapper tCartMapper;
    @Autowired
    private ITAddressService tAddressService;

    @Autowired
    private IBooksService booksService;

    @Autowired
    private ITMessageService tMessageService;

    @Autowired
    private ITOrderService tOrderService;

    @Autowired
    private ITOrderItemService tOrderItemService;

    @Autowired
    private PointsRecordServiceImpl pointsRecordService;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询借阅车
     *
     * @param cartId 借阅车主键
     * @return 借阅车
     */
    @Override
    public TCart selectTCartByCartId(Long cartId)
    {
        return tCartMapper.selectTCartByCartId(cartId);
    }

    /**
     * 查询借阅车列表
     *
     * @param tCart 借阅车
     * @return 借阅车
     */
    @Override
    public List<TCart> selectTCartList(TCart tCart)
    {
        return tCartMapper.selectTCartList(tCart);
    }

    /**
     * 新增借阅车
     *
     * @param tCart 借阅车
     * @return 结果
     */
    @Override
    public int insertTCart(TCart tCart)
    {
        // 检查是否已有相同商品的借阅车记录
        TCart queryCart = new TCart();
        queryCart.setUserId(tCart.getUserId());
        queryCart.setCartBooksId(tCart.getCartBooksId());

        List<TCart> existingCarts = tCartMapper.selectTCartList(queryCart);


        if (!existingCarts.isEmpty()) {
            // 如果存在相同商品，更新数量
            TCart existingCart = existingCarts.get(0);
            // 累加数量（原数量 + 新数量）
            existingCart.setCartNum(existingCart.getCartNum() + tCart.getCartNum());
            existingCart.setUpdateTime(DateUtils.getNowDate());
            return tCartMapper.updateTCart(existingCart);
        } else {
            // 如果不存在，新增借阅车记录
            tCart.setCreateTime(DateUtils.getNowDate());
            return tCartMapper.insertTCart(tCart);
        }
    }

    /**
     * 修改借阅车
     *
     * @param tCart 借阅车
     * @return 结果
     */
    @Override
    public int updateTCart(TCart tCart)
    {
        return tCartMapper.updateTCart(tCart);
    }

    /**
     * 批量删除借阅车
     *
     * @param cartIds 需要删除的借阅车主键
     * @return 结果
     */
    @Override
    public int deleteTCartByCartIds(Long[] cartIds)
    {
        return tCartMapper.deleteTCartByCartIds(cartIds);
    }

    /**
     * 删除购物车信息
     *
     * @param cartId 购物车主键
     * @return 结果
     */
    @Override
    public int deleteTCartByCartId(Long cartId)
    {
        return tCartMapper.deleteTCartByCartId(cartId);
    }


    @Transactional(rollbackFor = Exception.class) // 必须加事务，保证订单创建、明细落库、积分扣减和消息发送一致
    @Override
    public boolean checkout(Long userId, Long[] cartIds)
    {
        // 1. 获取购物车选中的项
        List<TCart> cartItems = tCartMapper.selectTCartListByIds(cartIds);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new RuntimeException("借阅车内容为空");
        }

        // 2. 获取借阅者默认地址
        TAddress defaultAddress = tAddressService.getDefaultAddressByUserId(userId);
        if (defaultAddress == null) {
            throw new RuntimeException("请先设置默认收货地址");
        }

        // 3. 按书主 ID 分组，并进行前置库存/重复借阅校验，同时累计所需总积分
        Map<Long, List<TCart>> ownerGroup = new HashMap<>();
        Integer grandTotalRequired = 0; // 整个购物车结账所需冻结的总积分

        for (TCart item : cartItems) {
            Books book = booksService.selectBooksByBooksId(item.getCartBooksId());

            // --- 库存校验 ---
            if (book == null) throw new RuntimeException("书籍不存在");
            if (book.getAvailableStock() != null && book.getAvailableStock() <= 0) {
                throw new RuntimeException("书籍 [" + book.getBooksName() + "] 暂时没有库存了");
            }

            // --- 权限校验 ---
            if (userId.equals(book.getUserId())) {
                throw new RuntimeException("不能借阅自己发布的书: " + book.getBooksName());
            }

            // --- 去重校验 ---
            boolean hasActiveOrder = tOrderService.checkUserHasBookOrder(userId, book.getBooksId());
            if (hasActiveOrder) {
                throw new RuntimeException("您已申请或正在借阅 [" + book.getBooksName() + "]，请勿重复申请");
            }

            // --- 【新增】累计单本书所需的积分（押金10倍 + 租金固定1） ---
            Integer baseValue = book.getBookPointsValue() != null ? book.getBookPointsValue() : 0;
            if (baseValue <= 0) {
                throw new RuntimeException("书籍 [" + book.getBooksName() + "] 未设置有效积分价值，无法借阅");
            }
            Integer singlePledge = baseValue * 10; // 押金10倍

            Integer consumePoints = Math.max(1, (int) Math.ceil(baseValue));
            Integer rewardPoints = (int) Math.floor(consumePoints * 0.8);
            Integer totalRequiredPoints = singlePledge + consumePoints;
            grandTotalRequired += (singlePledge + consumePoints);

            Long ownerId = book.getUserId();
            ownerGroup.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(item);
        }

        // 4. 【新增】前置校验借阅者的可用积分是否足够一次性支付整个购物车
        SysUser borrower = sysUserMapper.selectUserById(userId);
        if (borrower == null) throw new RuntimeException("借阅用户信息异常");
        if (borrower.getPointsBalance() < grandTotalRequired) {
            throw new RuntimeException("您的可用积分不足，本次购物车合并结算共需 " + grandTotalRequired + " 积分");
        }

        // 5. 为每个书主生成一张订单，分别结算积分并发送消息通知
        for (Map.Entry<Long, List<TCart>> entry : ownerGroup.entrySet()) {
            Long ownerId = entry.getKey();
            List<TCart> itemsForThisOwner = entry.getValue();

            // 统计这笔订单（发给同一个书主）的总押金和总租金
            Integer orderPledgePoints = 0;
            Integer orderConsumePoints = 0;
            Integer orderRewardPoints = 0;

            List<String> bookNames = new ArrayList<>();
            List<TOrderItem> orderItemsToInsert = new ArrayList<>();

            // 收集图书明细，计算该单的积分
            for (TCart cartItem : itemsForThisOwner) {
                Books book = booksService.selectBooksByBooksId(cartItem.getCartBooksId());
                bookNames.add("《" + book.getBooksName() + "》");

                Integer baseValue = book.getBookPointsValue() != null ? book.getBookPointsValue() : 0;
                orderPledgePoints += (baseValue * 10);
                orderConsumePoints += 1; // 租金每本固定 1 积分
                orderRewardPoints += 1;  // 收益每本固定 1 积分
            }

            // 创建主订单（初始状态映射为 "1" - 已申请）
            TOrder order = createOrder(userId, ownerId, defaultAddress, "购物车批量申请");
            order.setPledgePoints(orderPledgePoints);
            order.setConsumePoints(orderConsumePoints);
            order.setRewardPoints(orderRewardPoints);
            tOrderService.insertTOrder(order); // 落库获取 orderId

            // 循环插入该订单下的明细项
            for (TCart cartItem : itemsForThisOwner) {
                Books book = booksService.selectBooksByBooksId(cartItem.getCartBooksId());
                TOrderItem orderItem = createOrderItem(order.getOrderId(), book, 1L);
                tOrderItemService.insertTOrderItem(orderItem);
            }

            // --- 【新增】调用统一流水方法，扣减可用、增加冻结积分 ---
            Integer orderTotalFrozen = orderPledgePoints + orderConsumePoints;
            if (orderTotalFrozen > 0) {
                PointsRecord record = new PointsRecord();
                record.setUserId(userId);
                record.setAmount(orderTotalFrozen); // 传入正数
                record.setTradeType(3); // 3-冻结押金(含租金)
                record.setRemark("购物车批量申请，冻结押金 " + orderPledgePoints + " 与租金 " + orderConsumePoints);
                record.setOrderId(order.getOrderId());
                // 此时insertPointsRecord会自动帮你扣可用积分、加冻结积分，不用再手调sysUserMapper
                pointsRecordService.insertPointsRecord(record);
            }

            // 拼接消息内容
            String contentNames = String.join("、", bookNames);
            if (contentNames.length() > 50) {
                contentNames = contentNames.substring(0, 50) + "...等";
            }

            String msgContent = "有书友希望借阅您的藏书 " + contentNames + "，系统已冻结其相应积分作为担保，请尽快前往【借出管理】审核处理。";
            createAndSendMessage(userId, ownerId, "1", "新的借阅申请", msgContent, order.getOrderId());


        }

        // 6. 清空已结算的购物车项
        tCartMapper.deleteTCartByCartIds(cartIds);

        return true;
    }

    /**
     * 辅助方法：构建并插入消息记录
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


    /**
     * 创建订单私有方法（适配最新表结构与最新的“已申请=1”的状态）
     */
    private TOrder createOrder(Long borrowerId, Long ownerId, TAddress address, String remark) {
        TOrder order = new TOrder();
        order.setBorrowerId(borrowerId); // 借阅者
        order.setOwnerId(ownerId);       // 书主
        order.setOrderRecvName(address.getName());
        order.setOrderRecvPhone(address.getPhone());
        order.setOrderRecvProvince(address.getProvinceName());
        order.setOrderRecvCity(address.getCityName());
        order.setOrderRecvAddress(address.getAddress());
        order.setOrderStatus("1");       // 核心修改：初始状态改为 "1" (已申请)
        order.setOrderTime(new Date());
        order.setCreateTime(new Date());
        order.setRemark(remark);
        return order;
    }

    /**
     * 创建订单明细（适配最新表结构）
     */
    private TOrderItem createOrderItem(Long orderId, Books book, Long num) {
        TOrderItem orderItem = new TOrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setBookId(book.getBooksId());
        orderItem.setBookTitle(book.getBooksName());
        orderItem.setBookImage(book.getBooksCover());
        orderItem.setBookNum(1L);
        orderItem.setCreateTime(new Date());
        return orderItem;
    }
}
