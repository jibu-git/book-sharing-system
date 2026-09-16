package com.ruoyi.pointsRecord.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.pointsRecord.domain.PointsRecord;

/**
 * 用户积分流水明细Service接口
 * 
 * @author ruoyi
 * @date 2026-05-21
 */
public interface IPointsRecordService 
{
    /**
     * 查询用户积分流水明细
     * 
     * @param recordId 用户积分流水明细主键
     * @return 用户积分流水明细
     */
    public PointsRecord selectPointsRecordByRecordId(Long recordId);

    /**
     * 查询用户积分流水明细列表
     * 
     * @param pointsRecord 用户积分流水明细
     * @return 用户积分流水明细集合
     */
    public List<PointsRecord> selectPointsRecordList(PointsRecord pointsRecord);

    /**
     * 新增用户积分流水明细
     * 
     * @param pointsRecord 用户积分流水明细
     * @return 结果
     */
    public int insertPointsRecord(PointsRecord pointsRecord);

    /**
     * 修改用户积分流水明细
     * 
     * @param pointsRecord 用户积分流水明细
     * @return 结果
     */
    public int updatePointsRecord(PointsRecord pointsRecord);

    /**
     * 批量删除用户积分流水明细
     * 
     * @param recordIds 需要删除的用户积分流水明细主键集合
     * @return 结果
     */
    public int deletePointsRecordByRecordIds(Long[] recordIds);

    /**
     * 删除用户积分流水明细信息
     * 
     * @param recordId 用户积分流水明细主键
     * @return 结果
     */
    public int deletePointsRecordByRecordId(Long recordId);

    /**
     * 获取当前登录用户的积分信息
     * * @param userId 用户ID
     * @return 积分数据（包含可用积分与冻结积分）
     */
    public Map<String, Object> getCurrentUserPoints(Long user_id);
}
