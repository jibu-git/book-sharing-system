package com.ruoyi.bookType.service;

import java.util.List;
import com.ruoyi.bookType.domain.BookType;

/**
 * 图书标签Service接口
 * 
 * @author ruoyi
 * @date 2025-10-30
 */
public interface IBookTypeService 
{
    /**
     * 查询图书标签
     * 
     * @param bookTypeId 图书标签主键
     * @return 图书标签
     */
    public BookType selectBookTypeByBookTypeId(Long bookTypeId);

    /**
     * 查询图书标签列表
     * 
     * @param bookType 图书标签
     * @return 图书标签集合
     */
    public List<BookType> selectBookTypeList(BookType bookType);

    /**
     * 新增图书标签
     * 
     * @param bookType 图书标签
     * @return 结果
     */
    public int insertBookType(BookType bookType);

    /**
     * 修改图书标签
     * 
     * @param bookType 图书标签
     * @return 结果
     */
    public int updateBookType(BookType bookType);

    /**
     * 批量删除图书标签
     * 
     * @param bookTypeIds 需要删除的图书标签主键集合
     * @return 结果
     */
    public int deleteBookTypeByBookTypeIds(Long[] bookTypeIds);

    /**
     * 删除图书标签信息
     * 
     * @param bookTypeId 图书标签主键
     * @return 结果
     */
    public int deleteBookTypeByBookTypeId(Long bookTypeId);
}
