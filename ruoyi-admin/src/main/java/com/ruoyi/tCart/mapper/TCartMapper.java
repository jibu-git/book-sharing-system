package com.ruoyi.tCart.mapper;

import java.util.List;
import com.ruoyi.tCart.domain.TCart;

/**
 * 购物车Mapper接口
 * 
 * @author ruoyi
 * @date 2025-11-08
 */
public interface TCartMapper 
{
    /**
     * 查询购物车
     * 
     * @param cartId 购物车主键
     * @return 购物车
     */
    public TCart selectTCartByCartId(Long cartId);

    /**
     * 查询购物车列表
     * 
     * @param tCart 购物车
     * @return 购物车集合
     */
    public List<TCart> selectTCartList(TCart tCart);

    /**
     * 新增购物车
     * 
     * @param tCart 购物车
     * @return 结果
     */
    public int insertTCart(TCart tCart);

    /**
     * 修改购物车
     * 
     * @param tCart 购物车
     * @return 结果
     */
    public int updateTCart(TCart tCart);

    /**
     * 删除购物车
     * 
     * @param cartId 购物车主键
     * @return 结果
     */
    public int deleteTCartByCartId(Long cartId);

    /**
     * 批量删除购物车
     * 
     * @param cartIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTCartByCartIds(Long[] cartIds);

    /**
     * 根据ID列表查询购物车
     *
     * @param cartIds 购物车ID数组
     * @return 购物车列表
     */
    public List<TCart> selectTCartListByIds(Long[] cartIds);
}
