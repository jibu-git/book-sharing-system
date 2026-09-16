package com.ruoyi.Message.mapper;

import java.util.List;
import com.ruoyi.Message.domain.TMessage;

/**
 * 消息提醒Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
public interface TMessageMapper 
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
     * 删除消息提醒
     * 
     * @param msgId 消息提醒主键
     * @return 结果
     */
    public int deleteTMessageByMsgId(Long msgId);

    /**
     * 批量删除消息提醒
     * 
     * @param msgIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTMessageByMsgIds(Long[] msgIds);


}
