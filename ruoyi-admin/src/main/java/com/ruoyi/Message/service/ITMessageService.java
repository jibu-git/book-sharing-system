package com.ruoyi.Message.service;

import java.util.List;
import com.ruoyi.Message.domain.TMessage;

/**
 * 消息提醒Service接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface ITMessageService 
{
    /**
     * 查询消息提醒
     * 
     * @param msgId 消息提醒主键
     * @return 消息提醒
     */
    public TMessage selectTMessageByMsgId(Long msgId);

    /**
     * 查询消息提醒列表
     * 
     * @param tMessage 消息提醒
     * @return 消息提醒集合
     */
    public List<TMessage> selectTMessageList(TMessage tMessage);

    /**
     * 新增消息提醒
     * 
     * @param tMessage 消息提醒
     * @return 结果
     */
    public int insertTMessage(TMessage tMessage);

    /**
     * 修改消息提醒
     * 
     * @param tMessage 消息提醒
     * @return 结果
     */
    public int updateTMessage(TMessage tMessage);

    /**
     * 批量删除消息提醒
     * 
     * @param msgIds 需要删除的消息提醒主键集合
     * @return 结果
     */
    public int deleteTMessageByMsgIds(Long[] msgIds);

    /**
     * 删除消息提醒信息
     * 
     * @param msgId 消息提醒主键
     * @return 结果
     */
    public int deleteTMessageByMsgId(Long msgId);
}
