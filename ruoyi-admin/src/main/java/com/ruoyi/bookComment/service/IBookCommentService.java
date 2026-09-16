package com.ruoyi.bookComment.service;

import java.util.List;
import com.ruoyi.bookComment.domain.BookComment;

/**
 * 图书评论Service接口
 * 
 * @author ruoyi
 * @date 2026-03-31
 */
public interface IBookCommentService 
{
    /**
     * 查询图书评论
     * 
     * @param commentId 图书评论主键
     * @return 图书评论
     */
    public BookComment selectBookCommentByCommentId(Long commentId);

    /**
     * 查询图书评论列表
     * 
     * @param bookComment 图书评论
     * @return 图书评论集合
     */
    public List<BookComment> selectBookCommentList(BookComment bookComment);

    /**
     * 新增图书评论
     * 
     * @param bookComment 图书评论
     * @return 结果
     */
    public int insertBookComment(BookComment bookComment);

    /**
     * 修改图书评论
     * 
     * @param bookComment 图书评论
     * @return 结果
     */
    public int updateBookComment(BookComment bookComment);

    /**
     * 批量删除图书评论
     * 
     * @param commentIds 需要删除的图书评论主键集合
     * @return 结果
     */
    public int deleteBookCommentByCommentIds(Long[] commentIds);

    /**
     * 删除图书评论信息
     * 
     * @param commentId 图书评论主键
     * @return 结果
     */
    public int deleteBookCommentByCommentId(Long commentId);

    /**
     * 查询图书评论树形列表
     *
     * @param commentId 图书评论主键
     * @return 结果
     */
    public List<BookComment> selectBookCommentTreeByBookId(Long bookId);
}
