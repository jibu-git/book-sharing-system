package com.ruoyi.bookType.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 图书标签对象 book_type
 * 
 * @author ruoyi
 * @date 2025-10-30
 */
public class BookType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 图书标签ID */
    private Long bookTypeId;

    /** 图书标签名称 */
    @Excel(name = "图书标签名称")
    private String bookTypeName;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    private Long bookTypeSort;

    /** 标签状态（0正常 1停用） */
    @Excel(name = "标签状态", readConverterExp = "0=正常,1=停用")
    private String bookTypeStatus;

    public void setBookTypeId(Long bookTypeId) 
    {
        this.bookTypeId = bookTypeId;
    }

    public Long getBookTypeId() 
    {
        return bookTypeId;
    }

    public void setBookTypeName(String bookTypeName) 
    {
        this.bookTypeName = bookTypeName;
    }

    public String getBookTypeName() 
    {
        return bookTypeName;
    }

    public void setBookTypeSort(Long bookTypeSort) 
    {
        this.bookTypeSort = bookTypeSort;
    }

    public Long getBookTypeSort() 
    {
        return bookTypeSort;
    }

    public void setBookTypeStatus(String bookTypeStatus) 
    {
        this.bookTypeStatus = bookTypeStatus;
    }

    public String getBookTypeStatus() 
    {
        return bookTypeStatus;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("bookTypeId", getBookTypeId())
            .append("bookTypeName", getBookTypeName())
            .append("bookTypeSort", getBookTypeSort())
            .append("bookTypeStatus", getBookTypeStatus())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
