package com.ruoyi.books.service;

import java.util.List;
import com.ruoyi.books.domain.Books;

/**
 * 图书Service接口
 * 
 * @author ruoyi
 * @date 2025-10-30
 */
public interface IBooksService 
{
    /**
     * 查询图书商品
     * 
     * @param booksId 图书主键
     * @return 图书
     */
    public Books selectBooksByBooksId(Long booksId);

    /**
     * 管获取所有数据
     *
     *
     * @return 图书集合
     */
    public List<Books> selectBooksList(Books books);

//    /**
//     * 查询图书列表
//     *
//     * @param user_id 图书
//     * @return 图书集合
//     */
//    public List<Books> selectBooksListByUid(Long user_id);

    List<Books> selectPortalListBooks(Books books);

    /**
     * 新增图书
     * 
     * @param books 图书
     * @return 结果
     */
    public int insertBooks(Books books);

    /**
     * 修改图书
     * 
     * @param books 图书
     * @return 结果
     */
    public int updateBooks(Books books);

    /**
     * 批量删除图书
     * 
     * @param booksIds 需要删除的图书主键集合
     * @return 结果
     */
    public int deleteBooksByBooksIds(Long[] booksIds);

    /**
     * 删除图书信息
     * 
     * @param booksId 图书主键
     * @return 结果
     */
    public int deleteBooksByBooksId(Long booksId);


}
