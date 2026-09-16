package com.ruoyi.tCart.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 购物车对象 t_cart
 * 
 * @author ruoyi
 * @date 2025-11-08
 */
public class TCart extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 购物车数据id */
    private Long cartId;

    /** 用户id */
    @Excel(name = "用户id")
    private Long userId;

    /** 商品id */
    @Excel(name = "商品id")
    private Long cartBooksId;



    /** 商品数量 */
    @Excel(name = "商品数量")
    private Long cartNum;

    public void setCartId(Long cartId) 
    {
        this.cartId = cartId;
    }

    public Long getCartId() 
    {
        return cartId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setCartBooksId(Long cartBooksId) 
    {
        this.cartBooksId = cartBooksId;
    }

    public Long getCartBooksId() 
    {
        return cartBooksId;
    }


    public void setCartNum(Long cartNum) 
    {
        this.cartNum = cartNum;
    }

    public Long getCartNum() 
    {
        return cartNum;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("cartId", getCartId())
            .append("userId", getUserId())
            .append("cartBooksId", getCartBooksId())
            .append("cartNum", getCartNum())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
