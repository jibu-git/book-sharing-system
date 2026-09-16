package com.ruoyi.Order.controller;

import java.util.List;
import java.util.Map;
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
import com.ruoyi.Order.domain.TOrder;
import com.ruoyi.Order.service.ITOrderService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 借阅订单Controller
 * 
 * @author ruoyi
 * @date 2026-03-23
 */
@RestController
@RequestMapping("/Order/order")
public class TOrderController extends BaseController
{
    @Autowired
    private ITOrderService tOrderService;

    /**
     * 查询借阅订单列表
     */
    @PreAuthorize("@ss.hasPermi('Order:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(TOrder tOrder)
    {
        startPage();
        Long currentUserId = SecurityUtils.getUserId();
        System.out.println("压根没进来");
        // 使用 role 字段来判断
        if ("borrower".equals(tOrder.getRole())) {
            // 我是借书人，查 borrower_id
            System.out.println("进入借书人");
            tOrder.setBorrowerId(currentUserId);
        }
        else if ("owner".equals(tOrder.getRole())) {
            // 我是书主，查 owner_id
            System.out.println("进入书主");
            tOrder.setOwnerId(currentUserId);
        }

        List<TOrder> list = tOrderService.selectTOrderList(tOrder);
        return getDataTable(list);
    }

    /**
     * 导出借阅订单列表
     */
    @PreAuthorize("@ss.hasPermi('Order:order:export')")
    @Log(title = "借阅订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TOrder tOrder)
    {
        List<TOrder> list = tOrderService.selectTOrderList(tOrder);
        ExcelUtil<TOrder> util = new ExcelUtil<TOrder>(TOrder.class);
        util.exportExcel(response, list, "借阅订单数据");
    }

    /**
     * 获取借阅订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('Order:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(tOrderService.selectTOrderByOrderId(orderId));
    }

    /**
     * 新增借阅订单
     */
    @PreAuthorize("@ss.hasPermi('Order:order:add')")
    @Log(title = "借阅订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TOrder tOrder)
    {
        // 1. 下单人强制设为借阅者 (Borrower)
        tOrder.setBorrowerId(SecurityUtils.getLoginUser().getUserId());
        return toAjax(tOrderService.insertTOrder(tOrder));
    }

    /**
     * 书主同意借阅申请（同意当前订单，自动拒绝其他同图书订单）
     */
    @PreAuthorize("@ss.hasPermi('Order:order:edit')")
    @Log(title = "订单管理-同意借阅", businessType = BusinessType.UPDATE)
    @PutMapping("/agreeLend/{orderId}")
    public AjaxResult agreeLend(@PathVariable("orderId") Long orderId)
    {
        // 调用 Service 层处理带有事务的排他性借阅逻辑
        return toAjax(tOrderService.agreeLendOrder(orderId));
    }



    /**
     * 修改借阅订单
     */
    @PreAuthorize("@ss.hasPermi('Order:order:edit')")
    @Log(title = "借阅订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TOrder tOrder)
    {
        if (tOrder.getOrderId() == null) {
            return AjaxResult.error("订单ID不能为空");
        }
        return toAjax(tOrderService.updateTOrder(tOrder));
    }

    /**
     * 删除借阅订单
     */
    @PreAuthorize("@ss.hasPermi('Order:order:remove')")
    @Log(title = "借阅订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(tOrderService.deleteTOrderByOrderIds(orderIds));
    }

    /**
     * 借书人：归还图书（将状态由 2已同意 改为 3已归还）
     */
    @PreAuthorize("@ss.hasPermi('Order:order:edit')")
    @Log(title = "订单管理-归还图书", businessType = BusinessType.UPDATE)
    @PutMapping("/returnOrder/{orderId}")
    public AjaxResult returnOrder(@PathVariable("orderId") Long orderId) {
        return toAjax(tOrderService.returnOrder(orderId));
    }

    /**
     * 书主：确认收到归还（将状态由 3已归还 改为 4已完成，并自动将图书设为 0上架）
     */
    @PreAuthorize("@ss.hasPermi('Order:order:edit')")
    @Log(title = "订单管理-确认收到归还", businessType = BusinessType.UPDATE)
    @PutMapping("/confirmReceipt/{orderId}")
    public AjaxResult confirmReceipt(@PathVariable("orderId") Long orderId) {
        return toAjax(tOrderService.confirmReceipt(orderId));
    }



    /**
     * 立即申请借阅
     */
    @PreAuthorize("@ss.hasPermi('TCart:cart:add')")
    @Log(title = "立即申请借阅", businessType = BusinessType.INSERT)
    @PostMapping("/quickCheckout")
    public AjaxResult quickCheckout(@RequestBody Map<String, Object> params)
    {
        Long userId = SecurityUtils.getLoginUser().getUserId();
        Long booksId = Long.valueOf(params.get("booksId").toString());
        Long num = params.get("num") != null ? Integer.parseInt(params.get("num").toString()) : 1L;
        return success(tOrderService.quickCheckout(userId, booksId, num));
    }

    /**
     * 取消申请借阅
     */
    @PreAuthorize("@ss.hasPermi('TCart:cart:remove')")
    @Log(title = "取消申请借阅", businessType = BusinessType.INSERT)
    @PutMapping("/cancel/{orderId}")
    public AjaxResult cancelOrder(@PathVariable("orderId") Long orderId)
    {
        Long userId = SecurityUtils.getLoginUser().getUserId();
        return success(tOrderService.cancelOrder(orderId,userId));
    }
}
