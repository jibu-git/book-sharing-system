package com.ruoyi.tAddress.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 收货地址对象 t_address
 * 
 * @author ruoyi
 * @date 2025-11-04
 */
public class TAddress extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 收货地址id */
    private Long aid;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 收货人姓名 */
    @Excel(name = "收货人姓名")
    private String name;

    /** 省-名称 */
    @Excel(name = "省-名称")
    private String provinceName;

    /** 市-名称 */
    @Excel(name = "市-名称")
    private String cityName;

    /** 详细地址 */
    @Excel(name = "详细地址")
    private String address;

    /** 手机 */
    @Excel(name = "手机")
    private String phone;

    /** 标签 */
    @Excel(name = "标签")
    private String tag;

    /** 是否默认：0-不默认，1-默认 */
    @Excel(name = "是否默认：0-不默认，1-默认")
    private Long isDefault;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdTime;



    /** 修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date modifiedTime;

    public void setAid(Long aid) 
    {
        this.aid = aid;
    }

    public Long getAid() 
    {
        return aid;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setProvinceName(String provinceName) 
    {
        this.provinceName = provinceName;
    }

    public String getProvinceName() 
    {
        return provinceName;
    }

    public void setCityName(String cityName) 
    {
        this.cityName = cityName;
    }

    public String getCityName() 
    {
        return cityName;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }

    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }

    public void setTag(String tag) 
    {
        this.tag = tag;
    }

    public String getTag() 
    {
        return tag;
    }

    public void setIsDefault(Long isDefault) 
    {
        this.isDefault = isDefault;
    }

    public Long getIsDefault() 
    {
        return isDefault;
    }

    public void setCreatedTime(Date createdTime) 
    {
        this.createdTime = createdTime;
    }

    public Date getCreatedTime() 
    {
        return createdTime;
    }

    public void setModifiedTime(Date modifiedTime) 
    {
        this.modifiedTime = modifiedTime;
    }

    public Date getModifiedTime() 
    {
        return modifiedTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("aid", getAid())
            .append("userId", getUserId())
            .append("name", getName())
            .append("provinceName", getProvinceName())
            .append("cityName", getCityName())
            .append("address", getAddress())
            .append("phone", getPhone())
            .append("tag", getTag())
            .append("isDefault", getIsDefault())
            .append("createdTime", getCreatedTime())

            .append("modifiedTime", getModifiedTime())
            .toString();
    }
}
