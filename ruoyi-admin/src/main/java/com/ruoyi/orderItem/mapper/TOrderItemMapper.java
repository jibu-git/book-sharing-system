package com.ruoyi.orderItem.mapper;

import java.util.List;
import com.ruoyi.orderItem.domain.TOrderItem;

/**
 * 借阅订单明细Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-23
 */
public interface TOrderItemMapper 
{
    /**
     * 查询借阅订单明细
     * 
     * @param orderItemId 借阅订单明细主键
     * @return 借阅订单明细
     */
    public TOrderItem selectTOrderItemByOrderItemId(Long orderItemId);

    /**
     * 查询借阅订单明细列表
     * 
     * @param tOrderItem 借阅订单明细
     * @return 借阅订单明细集合
     */
    public List<TOrderItem> selectTOrderItemList(TOrderItem tOrderItem);

    /**
     * 新增借阅订单明细
     * 
     * @param tOrderItem 借阅订单明细
     * @return 结果
     */
    public int insertTOrderItem(TOrderItem tOrderItem);

    /**
     * 修改借阅订单明细
     * 
     * @param tOrderItem 借阅订单明细
     * @return 结果
     */
    public int updateTOrderItem(TOrderItem tOrderItem);

    /**
     * 删除借阅订单明细
     * 
     * @param orderItemId 借阅订单明细主键
     * @return 结果
     */
    public int deleteTOrderItemByOrderItemId(Long orderItemId);

    /**
     * 批量删除借阅订单明细
     * 
     * @param orderItemIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTOrderItemByOrderItemIds(Long[] orderItemIds);
}
