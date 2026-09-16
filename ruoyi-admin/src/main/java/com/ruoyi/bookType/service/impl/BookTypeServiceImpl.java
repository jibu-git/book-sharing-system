package com.ruoyi.bookType.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.bookType.mapper.BookTypeMapper;
import com.ruoyi.bookType.domain.BookType;
import com.ruoyi.bookType.service.IBookTypeService;

/**
 * 图书标签Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-10-30
 */
@Service
public class BookTypeServiceImpl implements IBookTypeService 
{
    @Autowired
    private BookTypeMapper bookTypeMapper;

    /**
     * 查询图书标签
     * 
     * @param bookTypeId 图书标签主键
     * @return 图书标签
     */
    @Override
    public BookType selectBookTypeByBookTypeId(Long bookTypeId)
    {
        return bookTypeMapper.selectBookTypeByBookTypeId(bookTypeId);
    }

    /**
     * 查询图书标签列表
     * 
     * @param bookType 图书标签
     * @return 图书标签
     */
    @Override
    public List<BookType> selectBookTypeList(BookType bookType)
    {
        return bookTypeMapper.selectBookTypeList(bookType);
    }

    /**
     * 新增图书标签
     * 
     * @param bookType 图书标签
     * @return 结果
     */
    @Override
    public int insertBookType(BookType bookType)
    {
        bookType.setCreateTime(DateUtils.getNowDate());
        return bookTypeMapper.insertBookType(bookType);
    }

    /**
     * 修改图书标签
     * 
     * @param bookType 图书标签
     * @return 结果
     */
    @Override
    public int updateBookType(BookType bookType)
    {
        bookType.setUpdateTime(DateUtils.getNowDate());
        return bookTypeMapper.updateBookType(bookType);
    }

    /**
     * 批量删除图书标签
     * 
     * @param bookTypeIds 需要删除的图书标签主键
     * @return 结果
     */
    @Override
    public int deleteBookTypeByBookTypeIds(Long[] bookTypeIds)
    {
        return bookTypeMapper.deleteBookTypeByBookTypeIds(bookTypeIds);
    }

    /**
     * 删除图书标签信息
     * 
     * @param bookTypeId 图书标签主键
     * @return 结果
     */
    @Override
    public int deleteBookTypeByBookTypeId(Long bookTypeId)
    {
        return bookTypeMapper.deleteBookTypeByBookTypeId(bookTypeId);
    }
}
