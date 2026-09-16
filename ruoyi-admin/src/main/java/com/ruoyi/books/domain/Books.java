package com.ruoyi.books.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 图书商品对象 books
 * 
 * @author ruoyi
 * @date 2025-10-30
 */
public class Books extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 图书ID */
    private Long booksId;

    /** 图书名称 */
    @Excel(name = "图书名称")
    private String booksName;

    /** 图书作者 */
    @Excel(name = "图书作者")
    private String booksAuthor;

    /** 出版社 */
    @Excel(name = "出版社")
    private String booksPublisher;

    /** 图书标签ID */
    @Excel(name = "图书标签ID")
    private Long booksTypeId;


    /** 图书封面 */
    @Excel(name = "图书封面")
    private String booksCover;

    /** 图书描述 */
    @Excel(name = "图书描述")
    private String booksDescription;

    /** 图书状态（0上架 1下架） */
    @Excel(name = "图书状态", readConverterExp = "0=上架,1=下架")
    private String booksStatus;

    /** 卖家用户ID */
    @Excel(name = "卖家用户ID")
    private Long userId;

    /** 总库存数量 **/
    @Excel(name = "总库存数量")
    private Integer totalStock;

    /** 当前可借库存数量 **/
    @Excel(name = "当前可借库存数量")
    private Integer availableStock;

    public Integer getBookPointsValue() {
        return bookPointsValue;
    }

    public void setBookPointsValue(Integer bookPointsValue) {
        this.bookPointsValue = bookPointsValue;
    }

    /** 图书借阅所需积分 **/
    @Excel(name = "图书借阅所需积分")
    private Integer bookPointsValue;

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }



    public void setBooksId(Long booksId) 
    {
        this.booksId = booksId;
    }

    public Long getBooksId() 
    {
        return booksId;
    }

    public void setBooksName(String booksName) 
    {
        this.booksName = booksName;
    }

    public String getBooksName() 
    {
        return booksName;
    }

    public void setBooksAuthor(String booksAuthor) 
    {
        this.booksAuthor = booksAuthor;
    }

    public String getBooksAuthor() 
    {
        return booksAuthor;
    }

    public void setBooksPublisher(String booksPublisher) 
    {
        this.booksPublisher = booksPublisher;
    }

    public String getBooksPublisher() 
    {
        return booksPublisher;
    }

    public void setBooksTypeId(Long booksTypeId) 
    {
        this.booksTypeId = booksTypeId;
    }

    public Long getBooksTypeId() 
    {
        return booksTypeId;
    }

    public void setBooksCover(String booksCover) 
    {
        this.booksCover = booksCover;
    }

    public String getBooksCover() 
    {
        return booksCover;
    }

    public void setBooksDescription(String booksDescription) 
    {
        this.booksDescription = booksDescription;
    }

    public String getBooksDescription() 
    {
        return booksDescription;
    }

    public void setBooksStatus(String booksStatus) 
    {
        this.booksStatus = booksStatus;
    }

    public String getBooksStatus() 
    {
        return booksStatus;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("booksId", getBooksId())
            .append("booksName", getBooksName())
            .append("booksAuthor", getBooksAuthor())
            .append("totalStock", getTotalStock())
            .append("availableStock", getAvailableStock())
            .append("booksPublisher", getBooksPublisher())
            .append("booksTypeId", getBooksTypeId())
            .append("booksCover", getBooksCover())
            .append("booksDescription", getBooksDescription())
            .append("booksStatus", getBooksStatus())
            .append("userId", getUserId())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("bookPointsValue", getBookPointsValue())
            .toString();
    }
}
