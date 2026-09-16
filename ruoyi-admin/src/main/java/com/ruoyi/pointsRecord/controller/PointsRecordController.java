package com.ruoyi.pointsRecord.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pointsRecord.domain.PointsRecord;
import com.ruoyi.pointsRecord.service.IPointsRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户积分流水明细Controller
 * 
 * @author ruoyi
 * @date 2026-05-21
 */
@RestController
@RequestMapping("/Record/record")
public class PointsRecordController extends BaseController
{
    @Autowired
    private IPointsRecordService pointsRecordService;

    /**
     * 查询用户积分流水明细列表
     */
    @PreAuthorize("@ss.hasPermi('Record:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(PointsRecord pointsRecord)
    {
        Long user_id = SecurityUtils.getLoginUser().getUserId();
        pointsRecord.setUserId(user_id);
        startPage();
        List<PointsRecord> list = pointsRecordService.selectPointsRecordList(pointsRecord);
        return getDataTable(list);
    }

    /**
     * 查询用户积分流水明细列表
     */
    @PreAuthorize("@ss.hasPermi('Record:record:list')")
    @GetMapping("/current/points")
    public AjaxResult getCurrentUserPoints()
    {
        Long user_id = SecurityUtils.getLoginUser().getUserId();

        // 2. 控制层只调用业务层，直接返回业务层组装好的结果
        return success(pointsRecordService.getCurrentUserPoints(user_id));
    }



    /**
     * 导出用户积分流水明细列表
     */
    @PreAuthorize("@ss.hasPermi('Record:record:export')")
    @Log(title = "用户积分流水明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PointsRecord pointsRecord)
    {
        List<PointsRecord> list = pointsRecordService.selectPointsRecordList(pointsRecord);
        ExcelUtil<PointsRecord> util = new ExcelUtil<PointsRecord>(PointsRecord.class);
        util.exportExcel(response, list, "用户积分流水明细数据");
    }

    /**
     * 获取用户积分流水明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('Record:record:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(pointsRecordService.selectPointsRecordByRecordId(recordId));
    }

    /**
     * 新增用户积分流水明细
     */
    @PreAuthorize("@ss.hasPermi('Record:record:add')")
    @Log(title = "用户积分流水明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PointsRecord pointsRecord)
    {
        Long user_id = SecurityUtils.getLoginUser().getUserId();
        pointsRecord.setUserId(user_id);
        return toAjax(pointsRecordService.insertPointsRecord(pointsRecord));
    }

    /**
     * 修改用户积分流水明细
     */
    @PreAuthorize("@ss.hasPermi('Record:record:edit')")
    @Log(title = "用户积分流水明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PointsRecord pointsRecord)
    {
        return toAjax(pointsRecordService.updatePointsRecord(pointsRecord));
    }

    /**
     * 删除用户积分流水明细
     */
    @PreAuthorize("@ss.hasPermi('Record:record:remove')")
    @Log(title = "用户积分流水明细", businessType = BusinessType.DELETE)
	@DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(pointsRecordService.deletePointsRecordByRecordIds(recordIds));
    }
}
