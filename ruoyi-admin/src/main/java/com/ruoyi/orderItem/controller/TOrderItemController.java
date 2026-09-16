package com.ruoyi.orderItem.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.orderItem.domain.TOrderItem;
import com.ruoyi.orderItem.service.ITOrderItemService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 借阅订单明细Controller
 * 
 * @author ruoyi
 * @date 2026-03-23
 */
@RestController
@RequestMapping("/orderItem/item")
public class TOrderItemController extends BaseController
{
    @Autowired
    private ITOrderItemService tOrderItemService;

    /**
     * 查询借阅订单明细列表
     */
    @PreAuthorize("@ss.hasPermi('orderItem:item:list')")
    @GetMapping("/list")
    public TableDataInfo list(TOrderItem tOrderItem)
    {
        startPage();
        List<TOrderItem> list = tOrderItemService.selectTOrderItemList(tOrderItem);
        return getDataTable(list);
    }

    /**
     * 导出借阅订单明细列表
     */
    @PreAuthorize("@ss.hasPermi('orderItem:item:export')")
    @Log(title = "借阅订单明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TOrderItem tOrderItem)
    {
        List<TOrderItem> list = tOrderItemService.selectTOrderItemList(tOrderItem);
        ExcelUtil<TOrderItem> util = new ExcelUtil<TOrderItem>(TOrderItem.class);
        util.exportExcel(response, list, "借阅订单明细数据");
    }

    /**
     * 获取借阅订单明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('orderItem:item:query')")
    @GetMapping(value = "/{orderItemId}")
    public AjaxResult getInfo(@PathVariable("orderItemId") Long orderItemId)
    {
        return success(tOrderItemService.selectTOrderItemByOrderItemId(orderItemId));
    }

    /**
     * 新增借阅订单明细
     */
    @PreAuthorize("@ss.hasPermi('orderItem:item:add')")
    @Log(title = "借阅订单明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TOrderItem tOrderItem)
    {
        return toAjax(tOrderItemService.insertTOrderItem(tOrderItem));
    }

    /**
     * 修改借阅订单明细
     */
    @PreAuthorize("@ss.hasPermi('orderItem:item:edit')")
    @Log(title = "借阅订单明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TOrderItem tOrderItem)
    {
        return toAjax(tOrderItemService.updateTOrderItem(tOrderItem));
    }

    /**
     * 删除借阅订单明细
     */
    @PreAuthorize("@ss.hasPermi('orderItem:item:remove')")
    @Log(title = "借阅订单明细", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderItemIds}")
    public AjaxResult remove(@PathVariable Long[] orderItemIds)
    {
        return toAjax(tOrderItemService.deleteTOrderItemByOrderItemIds(orderItemIds));
    }
}
