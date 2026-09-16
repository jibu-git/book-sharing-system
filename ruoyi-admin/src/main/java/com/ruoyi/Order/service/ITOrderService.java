package com.ruoyi.Order.service;

import java.util.List;
import com.ruoyi.Order.domain.TOrder;
import org.springframework.transaction.annotation.Transactional;

/**
 * 借阅订单Service接口
 * 
 * @author ruoyi
 * @date 2026-03-23
 */
public interface ITOrderService 
{
    /**
     * 查询借阅订单
     * 
     * @param orderId 借阅订单主键
     * @return 借阅订单
     */
    public TOrder selectTOrderByOrderId(Long orderId);

    /**
     * 查询借阅订单列表
     * 
     * @param tOrder 借阅订单
     * @return 借阅订单集合
     */
    public List<TOrder> selectTOrderList(TOrder tOrder);

    List<Long> selectOrderIdsByUserId(Long userId);

//    List<TOrderWithItems> selectOrderWithItemsByUserId(Long userId);

    /**
     * 新增借阅订单
     * 
     * @param tOrder 借阅订单
     * @return 结果
     */
    public int insertTOrder(TOrder tOrder);

    /**
     * 修改借阅订单
     * 
     * @param tOrder 借阅订单
     * @return 结果
     */
    public int updateTOrder(TOrder tOrder);

    /**
     * 批量删除借阅订单
     * 
     * @param orderIds 需要删除的借阅订单主键集合
     * @return 结果
     */
    public int deleteTOrderByOrderIds(Long[] orderIds);

    /**
     * 删除借阅订单信息
     * 
     * @param orderId 借阅订单主键
     * @return 结果
     */
    public int deleteTOrderByOrderId(Long orderId);

    /**
     * 同意借阅申请（排他性处理）
     * @param orderId 订单ID
     * @return 结果
     */
    public int agreeLendOrder(Long orderId);

    @Transactional(rollbackFor = Exception.class)
    boolean cancelOrder(Long orderId, Long userId);

    public int returnOrder(Long orderId);
    public int confirmReceipt(Long orderId);



    @Transactional(rollbackFor = Exception.class)
    boolean quickCheckout(Long userId, Long booksId, Long num);

    boolean checkUserHasBookOrder(Long borrowerId, Long bookId);
}
