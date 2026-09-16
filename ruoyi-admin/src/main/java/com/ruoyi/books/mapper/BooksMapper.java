package com.ruoyi.books.mapper;

import java.awt.print.Book;
import java.util.List;
import com.ruoyi.books.domain.Books;

/**
 * 图书商品Mapper接口
 * 
 * @author ruoyi
 * @date 2025-10-30
 */
public interface BooksMapper 
{
    /**
     * 查询图书商品
     * 
     * @param booksId 图书商品主键
     * @return 图书商品
     */
    public Books selectBooksByBooksId(Long booksId);

    /**
     * 查询所有图书商品列表
     *
     *@param books 图书列表
     * @return 图书商品集合
     */
    public List<Books> selectBooksList(Books books);


    /**
     * 查询所有上架的图书商品列表
     *
     *@param books 图书列表
     * @return 图书商品集合
     */
    public List<Books> selectBooksListByBooksStatus(Books books);

//    /**
//     * 查询图书商品列表
//     *
//     * @param user_id 用户(卖家)ID
//     * @return 图书商品集合
//     */
//    public List<Books> selectBooksListByUid(Long user_id);

    /**
     * 新增图书商品
     * 
     * @param books 图书商品
     * @return 结果
     */
    public int insertBooks(Books books);

    /**
     * 修改图书商品
     * 
     * @param books 图书商品
     * @return 结果
     */
    public int updateBooks(Books books);

    /**
     * 删除图书商品
     * 
     * @param booksId 图书商品主键
     * @return 结果
     */
    public int deleteBooksByBooksId(Long booksId);

    /**
     * 批量删除图书商品
     * 
     * @param booksIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBooksByBooksIds(Long[] booksIds);

    /**
     * 安全扣减可用库存
     * @param booksId 图书ID
     * @return 受影响行数（1: 成功, 0: 库存不足或图书不存在）
     */
    public int decreaseStock(Long booksId);

    /**
     * 安全恢复可用库存（归还时使用）
     * @param booksId 图书ID
     * @return 受影响行数
     */
    public int increaseStock(Long booksId);

}
