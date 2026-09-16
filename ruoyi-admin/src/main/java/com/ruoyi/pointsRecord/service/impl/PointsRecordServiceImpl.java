package com.ruoyi.pointsRecord.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pointsRecord.mapper.PointsRecordMapper;
import com.ruoyi.pointsRecord.domain.PointsRecord;
import com.ruoyi.pointsRecord.service.IPointsRecordService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户积分流水明细Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-05-21
 */
@Service
public class PointsRecordServiceImpl implements IPointsRecordService 
{
    @Autowired
    private PointsRecordMapper pointsRecordMapper;

    @Autowired
    private SysUserMapper sysUserMapper;
    /**
     * 查询用户积分流水明细
     * 
     * @param recordId 用户积分流水明细主键
     * @return 用户积分流水明细
     */
    @Override
    public PointsRecord selectPointsRecordByRecordId(Long recordId)
    {
        return pointsRecordMapper.selectPointsRecordByRecordId(recordId);
    }

    /**
     * 查询用户积分流水明细列表
     * 
     * @param pointsRecord 用户积分流水明细
     * @return 用户积分流水明细
     */
    @Override
    public List<PointsRecord> selectPointsRecordList(PointsRecord pointsRecord)
    {
        return pointsRecordMapper.selectPointsRecordList(pointsRecord);
    }


    /**
     * 新增用户积分流水明细并联动更新用户余额
     * * @param pointsRecord 用户积分流水明细
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class) // 建议加上事务注解，确保流水和余额同时成功或失败
    public int insertPointsRecord(PointsRecord pointsRecord)
    {
        System.out.println("进入了新增积分流水...");
        // 1. 安全校验：保证所有传进来的变动额度都是正数，增减逻辑由 tradeType 决定
        if (pointsRecord.getAmount() == null || pointsRecord.getAmount() <= 0) {
            throw new ServiceException("操作失败：变动积分额度必须大于 0");
        }
        if (pointsRecord.getUserId() == null) {
            throw new ServiceException("操作失败：用户 ID 不能为空");
        }

        // 2. 设置流水产生时间
        pointsRecord.setCreateTime(DateUtils.getNowDate());

        // 3. 往流水明细表插入记录
        int rows = pointsRecordMapper.insertPointsRecord(pointsRecord);
        System.out.println("流水表新增行数: " + rows);

        // 4. 联动更新用户表的积分余额
        if (rows > 0) {
            Long userId = pointsRecord.getUserId();
            Integer amount = pointsRecord.getAmount(); // 这里一定是个正数
            int updateRows = 0;

            switch (pointsRecord.getTradeType()) {
                case 1: // 1-充值：增加可用积分
                    updateRows = sysUserMapper.updateUserPointsBalance(userId, amount);
                    break;

                case 2: // 2-支付租金：扣除可用积分
                    updateRows = sysUserMapper.updateUserPointsBalance(userId, -amount);
                    break;

                case 3: // 3-冻结押金(含租金)：扣除可用积分，增加冻结积分
                    sysUserMapper.updateUserPointsBalance(userId, -amount);
                    updateRows = sysUserMapper.updateUserPointsFrozen(userId, amount);
                    break;

                case 4: // 4-解冻押金(含租金)：增加可用积分，扣除冻结积分
                    sysUserMapper.updateUserPointsBalance(userId, amount);
                    updateRows = sysUserMapper.updateUserPointsFrozen(userId, -amount);
                    break;

                case 5: // 5-获得共享收益：增加可用积分
                    updateRows = sysUserMapper.updateUserPointsBalance(userId, amount);
                    break;

                case 6: // 6-违约扣除押金：直接扣除已冻结的积分（可用积分不变）
                    updateRows = sysUserMapper.updateUserPointsFrozen(userId, -amount);
                    break;

                default:
                    throw new ServiceException("未知的流水交易类型: " + pointsRecord.getTradeType());
            }

            if (updateRows == 0) {
                throw new ServiceException("积分更新失败：当前操作的用户不存在或余额不足");
            }
        }

        return rows;
    }

    /**
     * 修改用户积分流水明细
     * 
     * @param pointsRecord 用户积分流水明细
     * @return 结果
     */
    @Override
    public int updatePointsRecord(PointsRecord pointsRecord)
    {
        return pointsRecordMapper.updatePointsRecord(pointsRecord);
    }

    /**
     * 批量删除用户积分流水明细
     * 
     * @param recordIds 需要删除的用户积分流水明细主键
     * @return 结果
     */
    @Override
    public int deletePointsRecordByRecordIds(Long[] recordIds)
    {
        return pointsRecordMapper.deletePointsRecordByRecordIds(recordIds);
    }

    /**
     * 删除用户积分流水明细信息
     * 
     * @param recordId 用户积分流水明细主键
     * @return 结果
     */
    @Override
    public int deletePointsRecordByRecordId(Long recordId)
    {
        return pointsRecordMapper.deletePointsRecordByRecordId(recordId);
    }

    @Override
    public Map<String, Object> getCurrentUserPoints(Long user_id) {
        // 1. 查询当前用户信息
        SysUser user = sysUserMapper.selectUserById(user_id);
        if (user == null) {
            throw new ServiceException("获取积分失败：当前用户不存在或已被删除");
        }

        // 2. 组装安全的积分数据返回（防止前端直接拿到完整的 SysUser 隐私敏感信息）
        Map<String, Object> pointsData = new HashMap<>();

        // 提取可用积分（若为空则兜底返回0）
        pointsData.put("pointsBalance", user.getPointsBalance() != null ? user.getPointsBalance() : 0);
        // 提取冻结积分（若为空则兜底返回0）
        pointsData.put("pointsFrozen", user.getPointsFrozen() != null ? user.getPointsFrozen() : 0);

        return pointsData;
    }
}
