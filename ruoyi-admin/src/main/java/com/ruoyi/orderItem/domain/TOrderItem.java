package com.ruoyi.orderItem.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 借阅订单明细对象 t_order_item
 * 
 * @author ruoyi
 * @date 2026-03-23
 */
public class TOrderItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单明细ID */
    private Long orderItemId;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;



    /** 订单id列表（用于查询条件）*/
    private List<Long> orderItemOrderIdList;

    /** 书籍ID */
    @Excel(name = "书籍ID")
    private Long bookId;

    /** 书籍标题 */
    @Excel(name = "书籍标题")
    private String bookTitle;

    /** 书籍图片 */
    @Excel(name = "书籍图片")
    private String bookImage;

    /** 数量 */
    @Excel(name = "数量")
    private Long bookNum;

    public void setOrderItemId(Long orderItemId) 
    {
        this.orderItemId = orderItemId;
    }

    public Long getOrderItemId() 
    {
        return orderItemId;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setBookId(Long bookId) 
    {
        this.bookId = bookId;
    }

    public Long getBookId() 
    {
        return bookId;
    }

    public void setBookTitle(String bookTitle) 
    {
        this.bookTitle = bookTitle;
    }

    public String getBookTitle() 
    {
        return bookTitle;
    }

    public void setBookImage(String bookImage) 
    {
        this.bookImage = bookImage;
    }

    public String getBookImage() 
    {
        return bookImage;
    }

    public void setBookNum(Long bookNum) 
    {
        this.bookNum = bookNum;
    }

    public Long getBookNum() 
    {
        return bookNum;
    }

    public List<Long> getOrderItemOrderIdList() {
        return orderItemOrderIdList;
    }

    public void setOrderItemOrderIdList(List<Long> orderItemOrderIdList) {
        this.orderItemOrderIdList = orderItemOrderIdList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderItemId", getOrderItemId())
            .append("orderId", getOrderId())
            .append("orderItemOrderIdList", getOrderItemOrderIdList())
            .append("bookId", getBookId())
            .append("bookTitle", getBookTitle())
            .append("bookImage", getBookImage())
            .append("bookNum", getBookNum())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
