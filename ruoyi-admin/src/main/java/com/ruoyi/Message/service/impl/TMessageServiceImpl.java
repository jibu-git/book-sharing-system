package com.ruoyi.Message.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.Message.mapper.TMessageMapper;
import com.ruoyi.Message.domain.TMessage;
import com.ruoyi.Message.service.ITMessageService;

/**
 * 消息提醒Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@Service
public class TMessageServiceImpl implements ITMessageService 
{
    @Autowired
    private TMessageMapper tMessageMapper;

    /**
     * 查询消息提醒
     * 
     * @param msgId 消息提醒主键
     * @return 消息提醒
     */
    @Override
    public TMessage selectTMessageByMsgId(Long msgId)
    {
        return tMessageMapper.selectTMessageByMsgId(msgId);
    }

    /**
     * 查询消息提醒列表
     * 
     * @param tMessage 消息提醒
     * @return 消息提醒
     */
    @Override
    public List<TMessage> selectTMessageList(TMessage tMessage)
    {
        return tMessageMapper.selectTMessageList(tMessage);
    }


    /**
     * 新增消息提醒
     * 
     * @param tMessage 消息提醒
     * @return 结果
     */
    @Override
    public int insertTMessage(TMessage tMessage)
    {
        tMessage.setCreateTime(DateUtils.getNowDate());
        tMessage.setIsRead("0"); // 默认未读
        return tMessageMapper.insertTMessage(tMessage);
    }

    /**
     * 修改消息提醒
     * 
     * @param tMessage 消息提醒
     * @return 结果
     */
    @Override
    public int updateTMessage(TMessage tMessage)
    {
        return tMessageMapper.updateTMessage(tMessage);
    }

    /**
     * 批量删除消息提醒
     * 
     * @param msgIds 需要删除的消息提醒主键
     * @return 结果
     */
    @Override
    public int deleteTMessageByMsgIds(Long[] msgIds)
    {
        return tMessageMapper.deleteTMessageByMsgIds(msgIds);
    }

    /**
     * 删除消息提醒信息
     * 
     * @param msgId 消息提醒主键
     * @return 结果
     */
    @Override
    public int deleteTMessageByMsgId(Long msgId)
    {
        return tMessageMapper.deleteTMessageByMsgId(msgId);
    }
}
