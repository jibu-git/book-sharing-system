package com.ruoyi.orderItem.service.impl;

import java.util.List;

import com.ruoyi.Order.service.ITOrderService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.orderItem.mapper.TOrderItemMapper;
import com.ruoyi.orderItem.domain.TOrderItem;
import com.ruoyi.orderItem.service.ITOrderItemService;

/**
 * 借阅订单明细Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-23
 */
@Service
public class TOrderItemServiceImpl implements ITOrderItemService 
{
    @Autowired
    private TOrderItemMapper tOrderItemMapper;

    @Autowired
    private ITOrderService tOrderService;
    /**
     * 查询借阅订单明细
     * 
     * @param orderItemId 借阅订单明细主键
     * @return 借阅订单明细
     */
    @Override
    public TOrderItem selectTOrderItemByOrderItemId(Long orderItemId)
    {
        return tOrderItemMapper.selectTOrderItemByOrderItemId(orderItemId);
    }

    /**
     * 查询借阅订单明细列表
     * 
     * @param tOrderItem 借阅订单明细
     * @return 借阅订单明细
     */
    @Override
    public List<TOrderItem> selectTOrderItemList(TOrderItem tOrderItem)
    {
        // 根据用户ID查询订单ID列表
        Long userId = SecurityUtils.getLoginUser().getUserId();
        List<Long> orderIds = tOrderService.selectOrderIdsByUserId(userId);

        // 如果有订单ID，则根据订单ID列表查询订单商品
        if (orderIds != null && !orderIds.isEmpty()) {
            // 设置查询条件为这些订单ID
            tOrderItem.setOrderItemOrderIdList(orderIds);
        } else {
            // 如果没有订单，则返回空列表
            throw new IllegalArgumentException("订单id为空");
        }

        return tOrderItemMapper.selectTOrderItemList(tOrderItem);
    }

    /**
     * 新增借阅订单明细
     * 
     * @param tOrderItem 借阅订单明细
     * @return 结果
     */
    @Override
    public int insertTOrderItem(TOrderItem tOrderItem)
    {
        tOrderItem.setCreateTime(DateUtils.getNowDate());
        return tOrderItemMapper.insertTOrderItem(tOrderItem);
    }

    /**
     * 修改借阅订单明细
     * 
     * @param tOrderItem 借阅订单明细
     * @return 结果
     */
    @Override
    public int updateTOrderItem(TOrderItem tOrderItem)
    {
        return tOrderItemMapper.updateTOrderItem(tOrderItem);
    }

    /**
     * 批量删除借阅订单明细
     * 
     * @param orderItemIds 需要删除的借阅订单明细主键
     * @return 结果
     */
    @Override
    public int deleteTOrderItemByOrderItemIds(Long[] orderItemIds)
    {
        return tOrderItemMapper.deleteTOrderItemByOrderItemIds(orderItemIds);
    }

    /**
     * 删除借阅订单明细信息
     * 
     * @param orderItemId 借阅订单明细主键
     * @return 结果
     */
    @Override
    public int deleteTOrderItemByOrderItemId(Long orderItemId)
    {
        return tOrderItemMapper.deleteTOrderItemByOrderItemId(orderItemId);
    }
}
