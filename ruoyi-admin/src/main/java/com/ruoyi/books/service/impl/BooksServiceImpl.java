package com.ruoyi.books.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.tAddress.domain.TAddress;
import com.ruoyi.tAddress.service.ITAddressService;
import com.ruoyi.tAddress.service.impl.TAddressServiceImpl;
import com.ruoyi.tCart.domain.TCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.books.mapper.BooksMapper;
import com.ruoyi.books.domain.Books;
import com.ruoyi.books.service.IBooksService;
import com.ruoyi.system.mapper.SysUserRoleMapper;

/**
 * 图书商品Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-30
 */
@Service
public class BooksServiceImpl implements IBooksService
{
    @Autowired
    private BooksMapper booksMapper;

//    @Autowired
//    private ITAddressService tAddressService;

//    @Autowired
//    private TOrderMapper tOrderMapper;
//
//    @Autowired
//    private TOrderItemMapper tOrderItemService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 查询图书商品
     *
     * @param booksId 图书商品主键
     * @return 图书商品
     */
    @Override
    public Books selectBooksByBooksId(Long booksId)
    {

        return booksMapper.selectBooksByBooksId(booksId);
    }

    /**
     * 查询图书商品列表
     *
     *
     * @return 图书商品
     */
    @Override
    public List<Books> selectBooksList(Books books)
    {
        if (books.getUserId() == 1){
            books.setUserId(null);
            return booksMapper.selectBooksList(books);
        }

        return booksMapper.selectBooksList(books);
    }


    /**
     * 前台查询图书商品列表
     *
     *
     * @return 图书商品
     */
    @Override
    public List<Books> selectPortalListBooks(Books books)
    {
        return booksMapper.selectBooksListByBooksStatus(books);
    }
//    /**
//     * 查询图书商品列表
//     *
//     * @param user_id 图书商品
//     * @return 图书商品
//     */
//    @Override
//    public List<Books> selectBooksListByUid(Long user_id) {
//        if (user_id == 1){
//            return booksMapper.selectBooksList();
//        }else {
//            return booksMapper.selectBooksListByUid(user_id);
//        }
//    }

    /**
     * 新增图书
     *
     * @param books 图书
     * @return 结果
     */
    @Override
    @Transactional
    public int insertBooks(Books books)
    {
        books.setCreateTime(DateUtils.getNowDate());
        int result = booksMapper.insertBooks(books);
        return result;
    }

    /**
     * 修改图书商品
     * 
     * @param books 图书商品
     * @return 结果
     */
    @Override
    public int updateBooks(Books books)
    {
        books.setUpdateTime(DateUtils.getNowDate());
        return booksMapper.updateBooks(books);
    }

    /**
     * 批量删除图书商品
     * 
     * @param booksIds 需要删除的图书商品主键
     * @return 结果
     */
    @Override
    public int deleteBooksByBooksIds(Long[] booksIds)
    {
        return booksMapper.deleteBooksByBooksIds(booksIds);
    }

    /**
     * 删除图书商品信息
     * 
     * @param booksId 图书商品主键
     * @return 结果
     */
    @Override
    public int deleteBooksByBooksId(Long booksId)
    {
        return booksMapper.deleteBooksByBooksId(booksId);
    }



}
