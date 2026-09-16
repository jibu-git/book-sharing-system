package com.ruoyi.Message.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 消息提醒对象 t_message
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public class TMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息主键 */
    private Long msgId;

    /** 发送者ID (0代表系统通知) */
    @Excel(name = "发送者ID (0代表系统通知)")
    private Long senderId;

    /** 接收者ID (通知给谁) */
    @Excel(name = "接收者ID (通知给谁)")
    private Long receiverId;

    /** 消息类型 (1:订单通知 2:系统公告 3:互动提醒) */
    @Excel(name = "消息类型 (1:订单通知 2:系统公告 3:互动提醒)")
    private String msgType;

    /** 消息标题 (例如：新的借阅申请) */
    @Excel(name = "消息标题 (例如：新的借阅申请)")
    private String title;

    /** 消息正文 (例如：用户XX申请借阅您的《老子的海》) */
    @Excel(name = "消息正文 (例如：用户XX申请借阅您的《老子的海》)")
    private String content;

    /** 关联业务ID (例如存入 order_id，方便前端点击跳转到详情) */
    @Excel(name = "关联业务ID (例如存入 order_id，方便前端点击跳转到详情)")
    private Long relatedId;

    /** 阅读状态 (0:未读 1:已读) */
    @Excel(name = "阅读状态 (0:未读 1:已读)")
    private String isRead;

    public void setMsgId(Long msgId) 
    {
        this.msgId = msgId;
    }

    public Long getMsgId() 
    {
        return msgId;
    }

    public void setSenderId(Long senderId) 
    {
        this.senderId = senderId;
    }

    public Long getSenderId() 
    {
        return senderId;
    }

    public void setReceiverId(Long receiverId) 
    {
        this.receiverId = receiverId;
    }

    public Long getReceiverId() 
    {
        return receiverId;
    }

    public void setMsgType(String msgType) 
    {
        this.msgType = msgType;
    }

    public String getMsgType() 
    {
        return msgType;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setRelatedId(Long relatedId) 
    {
        this.relatedId = relatedId;
    }

    public Long getRelatedId() 
    {
        return relatedId;
    }

    public void setIsRead(String isRead) 
    {
        this.isRead = isRead;
    }

    public String getIsRead() 
    {
        return isRead;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("msgId", getMsgId())
            .append("senderId", getSenderId())
            .append("receiverId", getReceiverId())
            .append("msgType", getMsgType())
            .append("title", getTitle())
            .append("content", getContent())
            .append("relatedId", getRelatedId())
            .append("isRead", getIsRead())
            .append("createTime", getCreateTime())
            .toString();
    }
}
