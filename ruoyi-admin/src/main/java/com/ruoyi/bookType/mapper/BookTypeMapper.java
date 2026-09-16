package com.ruoyi.bookType.mapper;

import java.util.List;
import com.ruoyi.bookType.domain.BookType;

/**
 * 图书标签Mapper接口
 * 
 * @author ruoyi
 * @date 2025-10-30
 */
public interface BookTypeMapper 
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
     * 删除图书标签
     * 
     * @param bookTypeId 图书标签主键
     * @return 结果
     */
    public int deleteBookTypeByBookTypeId(Long bookTypeId);

    /**
     * 批量删除图书标签
     * 
     * @param bookTypeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBookTypeByBookTypeIds(Long[] bookTypeIds);
}
