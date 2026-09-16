package com.ruoyi.bookComment.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruoyi.Message.domain.TMessage;
import com.ruoyi.Message.service.ITMessageService;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.bookComment.mapper.BookCommentMapper;
import com.ruoyi.bookComment.domain.BookComment;
import com.ruoyi.bookComment.service.IBookCommentService;

/**
 * 图书评论Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-31
 */
@Service
public class BookCommentServiceImpl implements IBookCommentService 
{
    @Autowired
    private BookCommentMapper bookCommentMapper;

    @Autowired
    private ITMessageService messageService; // 注入消息服务

    /**
     * 高效获取树形评论列表
     */
    @Override
    public List<BookComment> selectBookCommentTreeByBookId(Long bookId) {
        // 1. 获取该书籍下的所有评论（不分页，一次性取出，依靠 book_id 索引非常快）
        BookComment query = new BookComment();
        query.setBookId(bookId);
        List<BookComment> allComments = bookCommentMapper.selectBookCommentList(query);

        if (allComments == null || allComments.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 将所有评论存入 Map，Key 为 ID，方便快速查找
        Map<Long, BookComment> map = allComments.stream()
                .collect(Collectors.toMap(BookComment::getCommentId, c -> c));

        List<BookComment> rootComments = new ArrayList<>();

        // 3. 遍历列表，将子评论归位
        for (BookComment comment : allComments) {
            Long rootId = comment.getRootId();
            if (rootId == null || rootId == 0) {
                // 如果是顶级评论，加入结果集
                rootComments.add(comment);
            } else {
                // 如果是回复，找到它的“根评论”，放入其 children 列表中
                // 注意：这里为了前端展示简洁，我们将二级、三级等所有子回复都挂在根评论下（扁平化展示）
                BookComment root = map.get(rootId);
                if (root != null) {
                    if (root.getChildren() == null) {
                        root.setChildren(new ArrayList<>());
                    }
                    root.getChildren().add(comment);
                }
            }
        }
        return rootComments;
    }


    /**
     * 查询图书评论
     * 
     * @param commentId 图书评论主键
     * @return 图书评论
     */
    @Override
    public BookComment selectBookCommentByCommentId(Long commentId)
    {
        return bookCommentMapper.selectBookCommentByCommentId(commentId);
    }

    /**
     * 查询图书评论列表
     * 
     * @param bookComment 图书评论
     * @return 图书评论
     */
    @Override
    public List<BookComment> selectBookCommentList(BookComment bookComment)
    {
        return bookCommentMapper.selectBookCommentList(bookComment);
    }

    /**
     * 新增图书评论并发送消息提醒
     */
    @Override
    public int insertBookComment(BookComment bookComment) {
        bookComment.setCreateTime(DateUtils.getNowDate());

        // 1. 保存评论到数据库
        int rows = bookCommentMapper.insertBookComment(bookComment);

        // 2. 发送消息提醒（逻辑联动）
        if (rows > 0 && bookComment.getReplyToId() != null && bookComment.getReplyToId() != 0) {
            // 只有是“回复他人”时才发提醒，自己回复自己不提醒（可选）
            if (!bookComment.getUserId().equals(bookComment.getReplyToId())) {
                sendReplyMessage(bookComment);
            }
        }
        return rows;
    }

    /**
     * 构建并发送消息
     */
    private void sendReplyMessage(BookComment comment) {
        try {
            TMessage msg = new TMessage();
            msg.setSenderId(comment.getUserId());
            msg.setReceiverId(comment.getReplyToId());
            msg.setMsgType("5"); // 5 代表评论回复消息
            msg.setTitle("有人回复了你的评论");

            // 截取评论内容预览
            String contentPreview = comment.getContent();
            if (contentPreview.length() > 20) {
                contentPreview = contentPreview.substring(0, 20) + "...";
            }

            msg.setContent(comment.getUserName() + " 回复了你：" + contentPreview);
            msg.setRelatedId(comment.getBookId()); // 关联 ID 设为书籍 ID，方便点击跳转到书籍详情页
            msg.setIsRead("0");
            msg.setCreateTime(DateUtils.getNowDate());

            messageService.insertTMessage(msg); // 调用你已有的消息插入接口
        } catch (Exception e) {
            // 消息发送失败不应影响评论提交，只记录日志
            System.err.println("发送回复消息失败: " + e.getMessage());
        }
    }

    /**
     * 修改图书评论
     * 
     * @param bookComment 图书评论
     * @return 结果
     */
    @Override
    public int updateBookComment(BookComment bookComment)
    {
        bookComment.setUpdateTime(DateUtils.getNowDate());
        return bookCommentMapper.updateBookComment(bookComment);
    }

    /**
     * 批量删除图书评论
     * 
     * @param commentIds 需要删除的图书评论主键
     * @return 结果
     */
    @Override
    public int deleteBookCommentByCommentIds(Long[] commentIds)
    {
        return bookCommentMapper.deleteBookCommentByCommentIds(commentIds);
    }

    /**
     * 删除图书评论信息
     * 
     * @param commentId 图书评论主键
     * @return 结果
     */
    @Override
    public int deleteBookCommentByCommentId(Long commentId)
    {
        return bookCommentMapper.deleteBookCommentByCommentId(commentId);
    }
}
