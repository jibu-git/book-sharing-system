package com.ruoyi.tAddress.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

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
import com.ruoyi.tAddress.domain.TAddress;
import com.ruoyi.tAddress.service.ITAddressService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 收货地址Controller
 * 
 * @author ruoyi
 * @date 2025-11-04
 */
@RestController
@RequestMapping("/TAddress/tAddress")
public class TAddressController extends BaseController
{
    @Autowired
    private ITAddressService tAddressService;

    /**
     * 查询收货地址列表
     */
    @PreAuthorize("@ss.hasPermi('TAddress:tAddress:list')")
    @GetMapping("/list")
    public TableDataInfo list(TAddress tAddress)
    {
        startPage();
        Long user_id = SecurityUtils.getLoginUser().getUserId();    //获取用户id
        tAddress.setUserId(user_id);   //赋值user_id
        List<TAddress> list = tAddressService.selectTAddressList(tAddress);
        return getDataTable(list);
    }

    /**
     * 导出收货地址列表
     */
    @PreAuthorize("@ss.hasPermi('TAddress:tAddress:export')")
    @Log(title = "收货地址", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TAddress tAddress)
    {
        Long user_id = SecurityUtils.getLoginUser().getUserId();    //获取用户id
        tAddress.setUserId(user_id);   //赋值user_id
        List<TAddress> list = tAddressService.selectTAddressList(tAddress);
        ExcelUtil<TAddress> util = new ExcelUtil<TAddress>(TAddress.class);
        util.exportExcel(response, list, "收货地址数据");
    }

    /**
     * 获取收货地址详细信息
     */
    @PreAuthorize("@ss.hasPermi('TAddress:tAddress:query')")
    @GetMapping(value = "/{aid}")
    public AjaxResult getInfo(@PathVariable("aid") Long aid)
    {

        return success(tAddressService.selectTAddressByAid(aid));
    }

    /**
     * 新增收货地址
     */
    @PreAuthorize("@ss.hasPermi('TAddress:tAddress:add')")
    @Log(title = "收货地址", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TAddress tAddress)
    {
        Long user_id = SecurityUtils.getLoginUser().getUserId();    //获取用户id
        tAddress.setUserId(user_id);   //赋值user_id
        return toAjax(tAddressService.insertTAddress(tAddress));
    }

    /**
     * 修改收货地址
     */
    @PreAuthorize("@ss.hasPermi('TAddress:tAddress:edit')")
    @Log(title = "收货地址", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TAddress tAddress)
    {
        Long user_id = SecurityUtils.getLoginUser().getUserId();    //获取用户id
        tAddress.setUserId(user_id);   //赋值user_id
        return toAjax(tAddressService.updateTAddress(tAddress));
    }

    /**
     * 删除收货地址
     */
    @PreAuthorize("@ss.hasPermi('TAddress:tAddress:remove')")
    @Log(title = "收货地址", businessType = BusinessType.DELETE)
	@DeleteMapping("/{aids}")
    public AjaxResult remove(@PathVariable Long[] aids)
    {
        return toAjax(tAddressService.deleteTAddressByAids(aids));
    }
}
