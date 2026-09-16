package com.ruoyi.pointsRecord.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户积分流水明细对象 points_record
 * 
 * @author ruoyi
 * @date 2026-05-21
 */
public class PointsRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 流水主键ID */
    @Excel(name = "流水主键ID")
    private Long recordId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 交易类型 */
    @Excel(name = "交易类型")
    private Integer tradeType;

    /** 变动数额 */
    @Excel(name = "变动数额")
    private Integer amount;

    /** 关联的业务ID */
    @Excel(name = "关联的业务ID")
    private Long orderId;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setTradeType(Integer tradeType)
    {
        this.tradeType = tradeType;
    }

    public Integer getTradeType()
    {
        return tradeType;
    }

    public void setAmount(Integer amount)
    {
        this.amount = amount;
    }

    public Integer getAmount()
    {
        return amount;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("userId", getUserId())
            .append("tradeType", getTradeType())
            .append("amount", getAmount())
            .append("orderId", getOrderId())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
