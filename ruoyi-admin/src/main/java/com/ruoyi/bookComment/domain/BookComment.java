package com.ruoyi.bookComment.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 图书评论对象 book_comment
 * 
 * @author ruoyi
 * @date 2026-03-31
 */
public class BookComment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long commentId;

    /** 书籍ID */
    @Excel(name = "书籍ID")
    private Long bookId;

    /** 评论者ID */
    @Excel(name = "评论者ID")
    private Long userId;

    /** 评论者昵称 */
    @Excel(name = "评论者昵称")
    private String userName;

    /** 评论者头像 */
    @Excel(name = "评论者头像")
    private String avatar;

    /** 评论内容 */
    @Excel(name = "评论内容")
    private String content;

    /** 根评论ID(一级评论为0) */
    @Excel(name = "根评论ID(一级评论为0)")
    private Long rootId;

    /** 父评论ID(直属上级) */
    @Excel(name = "父评论ID(直属上级)")
    private Long parentId;

    /** 被回复人ID */
    @Excel(name = "被回复人ID")
    private Long replyToId;

    /** 被回复人昵称 */
    @Excel(name = "被回复人昵称")
    private String replyToName;

    /** 点赞数 */
    @Excel(name = "点赞数")
    private Long likeCount;

    /** 状态(0正常 1停用) */
    @Excel(name = "状态(0正常 1停用)")
    private String status;

    /** 删除标志(0代表存在 2代表删除) */
    private String delFlag;

    /** 删除标志(0代表存在 2代表删除) */
    private List<BookComment> children = new ArrayList<>();

    public List<BookComment> getChildren() {
        return children;
    }

    public void setChildren(List<BookComment> children) {
        this.children = children;
    }

    public void setCommentId(Long commentId) 
    {
        this.commentId = commentId;
    }

    public Long getCommentId() 
    {
        return commentId;
    }

    public void setBookId(Long bookId) 
    {
        this.bookId = bookId;
    }

    public Long getBookId() 
    {
        return bookId;
    }

    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }

    public void setAvatar(String avatar) 
    {
        this.avatar = avatar;
    }

    public String getAvatar() 
    {
        return avatar;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setRootId(Long rootId) 
    {
        this.rootId = rootId;
    }

    public Long getRootId() 
    {
        return rootId;
    }

    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }

    public void setReplyToId(Long replyToId) 
    {
        this.replyToId = replyToId;
    }

    public Long getReplyToId() 
    {
        return replyToId;
    }

    public void setReplyToName(String replyToName) 
    {
        this.replyToName = replyToName;
    }

    public String getReplyToName() 
    {
        return replyToName;
    }

    public void setLikeCount(Long likeCount) 
    {
        this.likeCount = likeCount;
    }

    public Long getLikeCount() 
    {
        return likeCount;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("commentId", getCommentId())
            .append("bookId", getBookId())
            .append("userId", getUserId())
            .append("userName", getUserName())
            .append("avatar", getAvatar())
            .append("content", getContent())
            .append("rootId", getRootId())
            .append("parentId", getParentId())
            .append("replyToId", getReplyToId())
            .append("replyToName", getReplyToName())
            .append("likeCount", getLikeCount())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
