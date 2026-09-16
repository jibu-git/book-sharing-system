package com.ruoyi.Order.mapper;

import java.util.List;
import com.ruoyi.Order.domain.TOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 借阅订单Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-23
 */
public interface TOrderMapper 
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

    /**
     * 根据用户ID查询订单ID列表
     *
     * @param userId 用户ID
     * @return 订单ID列表
     */
    public List<Long> selectOrderIdsByUserId(Long userId);

    /**
     * 根据用户ID查询订单及其订单项信息
     *
     * @param userId 用户ID
     * @return 订单及订单项信息列表
     */
//    public List<TOrderWithItems> selectOrderWithItemsByUserId(Long userId);


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
     * 删除借阅订单
     * 
     * @param orderId 借阅订单主键
     * @return 结果
     */
    public int deleteTOrderByOrderId(Long orderId);

    /**
     * 批量删除借阅订单
     * 
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTOrderByOrderIds(Long[] orderIds);

    /** 查询申请了某本书的其他待处理订单ID */
    public List<Long> selectOtherPendingOrderIdsByBookId(@Param("bookId") Long bookId, @Param("excludeOrderId") Long excludeOrderId);

    /** 批量更新订单状态 */
    public int updateOrderStatusBatch(@Param("orderIds") List<Long> orderIds, @Param("status") String status, @Param("remark") String remark);



    /** 查询用户对某本书是否已经借阅 */
        @Select("SELECT COUNT(1) FROM t_order o " +
                "JOIN t_order_item i ON o.order_id = i.order_id " +
                "WHERE o.borrower_id = #{userId} " +
                "AND i.book_id = #{booksId} " +
                "AND o.order_status IN ('0', '1')") // 0:申请中, 1:借阅中
        Integer countUserActiveOrderForBook(@Param("userId") Long userId, @Param("booksId") Long booksId);

    /**
     * 检查用户是否已有该书的活跃订单（待审核、借阅中、归还中）
     * @param borrowerId 借阅者ID
     * @param bookId 书籍ID
     * @return 匹配到的订单数量
     */
    public int checkUserHasBookOrder(@Param("borrowerId") Long borrowerId, @Param("bookId") Long bookId);

    /**
     * 根据图书ID查询其他正在申请中的订单
     * * @param bookId 图书ID
     * @param excludeOrderId 需要排除的当前成交订单ID
     * @return 订单列表
     */
    public List<TOrder> selectPendingOrdersByBookId(@Param("bookId") Long bookId, @Param("excludeOrderId") Long excludeOrderId);
}
