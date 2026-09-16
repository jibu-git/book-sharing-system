package com.ruoyi.pointsRecord.mapper;

import java.util.List;
import com.ruoyi.pointsRecord.domain.PointsRecord;

/**
 * 用户积分流水明细Mapper接口
 * 
 * @author ruoyi
 * @date 2026-05-21
 */
public interface PointsRecordMapper 
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
     * 删除用户积分流水明细
     * 
     * @param recordId 用户积分流水明细主键
     * @return 结果
     */
    public int deletePointsRecordByRecordId(Long recordId);

    /**
     * 批量删除用户积分流水明细
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePointsRecordByRecordIds(Long[] recordIds);
}
