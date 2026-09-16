package com.ruoyi.Order.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 借阅订单对象 t_order
 * 
 * @author ruoyi
 * @date 2026-03-23
 */
public class TOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 借阅者ID */
    @Excel(name = "借阅者ID")
    private Long borrowerId;

    /** 书主ID */
    @Excel(name = "书主ID")
    private Long ownerId;

    /** 收货人姓名 */
    @Excel(name = "收货人姓名")
    private String orderRecvName;

    /** 收货人电话 */
    @Excel(name = "收货人电话")
    private String orderRecvPhone;

    /** 省份 */
    @Excel(name = "省份")
    private String orderRecvProvince;

    /** 城市 */
    @Excel(name = "城市")
    private String orderRecvCity;

    /** 详细地址 */
    @Excel(name = "详细地址")
    private String orderRecvAddress;

    /** 订单状态 */
    @Excel(name = "订单状态")
    private String orderStatus;

    /** 下单时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "下单时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date orderTime;



    private String role;

   /* 本次借阅消耗的租金积分(如书价的10%) */
    private Integer consumePoints;
    /* 本次借阅冻结的押金积分(如书价的100%) */
    private Integer pledgePoints;

    public Integer getConsumePoints() {
        return consumePoints;
    }

    public void setConsumePoints(Integer consumePoints) {
        this.consumePoints = consumePoints;
    }

    public Integer getPledgePoints() {
        return pledgePoints;
    }

    public void setPledgePoints(Integer pledgePoints) {
        this.pledgePoints = pledgePoints;
    }

    public Integer getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(Integer rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    /* 图书提供者预计可获得的收益积分(如租金的80%) */
    private Integer rewardPoints;

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setBorrowerId(Long borrowerId) 
    {
        this.borrowerId = borrowerId;
    }

    public Long getBorrowerId() 
    {
        return borrowerId;
    }

    public void setOwnerId(Long ownerId) 
    {
        this.ownerId = ownerId;
    }

    public Long getOwnerId() 
    {
        return ownerId;
    }

    public void setOrderRecvName(String orderRecvName) 
    {
        this.orderRecvName = orderRecvName;
    }

    public String getOrderRecvName() 
    {
        return orderRecvName;
    }

    public void setOrderRecvPhone(String orderRecvPhone) 
    {
        this.orderRecvPhone = orderRecvPhone;
    }

    public String getOrderRecvPhone() 
    {
        return orderRecvPhone;
    }

    public void setOrderRecvProvince(String orderRecvProvince) 
    {
        this.orderRecvProvince = orderRecvProvince;
    }

    public String getOrderRecvProvince() 
    {
        return orderRecvProvince;
    }

    public void setOrderRecvCity(String orderRecvCity) 
    {
        this.orderRecvCity = orderRecvCity;
    }

    public String getOrderRecvCity() 
    {
        return orderRecvCity;
    }

    public void setOrderRecvAddress(String orderRecvAddress) 
    {
        this.orderRecvAddress = orderRecvAddress;
    }

    public String getOrderRecvAddress() 
    {
        return orderRecvAddress;
    }

    public void setOrderStatus(String orderStatus) 
    {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() 
    {
        return orderStatus;
    }

    public void setOrderTime(Date orderTime) 
    {
        this.orderTime = orderTime;
    }

    public Date getOrderTime() 
    {
        return orderTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("borrowerId", getBorrowerId())
            .append("ownerId", getOwnerId())
            .append("orderRecvName", getOrderRecvName())
            .append("orderRecvPhone", getOrderRecvPhone())
            .append("orderRecvProvince", getOrderRecvProvince())
            .append("orderRecvCity", getOrderRecvCity())
            .append("orderRecvAddress", getOrderRecvAddress())
            .append("orderStatus", getOrderStatus())
            .append("orderTime", getOrderTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("consumePoints", getConsumePoints())
            .append("pledgePoints", getPledgePoints())
            .append("rewardPoints", getRewardPoints())
            .toString();
    }
}
