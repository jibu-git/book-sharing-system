package com.ruoyi.tCart.controller;

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
import com.ruoyi.tCart.domain.TCart;
import com.ruoyi.tCart.service.ITCartService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 购物车Controller
 * 
 * @author ruoyi
 * @date 2025-11-08
 */
@RestController
@RequestMapping("/TCart/cart")
public class TCartController extends BaseController
{
    @Autowired
    private ITCartService tCartService;

    /**
     * 查询购物车列表
     */
    @PreAuthorize("@ss.hasPermi('TCart:cart:list')")
    @GetMapping("/list")
    public TableDataInfo list(TCart tCart)
    {
        startPage();
        tCart.setUserId(SecurityUtils.getLoginUser().getUserId());
        List<TCart> list = tCartService.selectTCartList(tCart);
        return getDataTable(list);
    }

    /**
     * 导出购物车列表
     */
    @PreAuthorize("@ss.hasPermi('TCart:cart:export')")
    @Log(title = "购物车", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TCart tCart)
    {
        List<TCart> list = tCartService.selectTCartList(tCart);
        ExcelUtil<TCart> util = new ExcelUtil<TCart>(TCart.class);
        util.exportExcel(response, list, "购物车数据");
    }

    /**
     * 获取购物车详细信息
     */
    @PreAuthorize("@ss.hasPermi('TCart:cart:query')")
    @GetMapping(value = "/{cartId}")
    public AjaxResult getInfo(@PathVariable("cartId") Long cartId)
    {
        return success(tCartService.selectTCartByCartId(cartId));
    }

    /**
     * 新增购物车
     */
    @PreAuthorize("@ss.hasPermi('TCart:cart:add')")
    @Log(title = "购物车", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TCart tCart )
    {
        tCart.setUserId(SecurityUtils.getLoginUser().getUserId());
        return toAjax(tCartService.insertTCart(tCart));
    }

    /**
     * 修改购物车
     */
    @PreAuthorize("@ss.hasPermi('TCart:cart:edit')")
    @Log(title = "购物车", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TCart tCart)
    {
        tCart.setUserId(SecurityUtils.getLoginUser().getUserId());
        return toAjax(tCartService.updateTCart(tCart));
    }

    /**
     * 删除购物车
     */
    @PreAuthorize("@ss.hasPermi('TCart:cart:remove')")
    @Log(title = "购物车", businessType = BusinessType.DELETE)
	@DeleteMapping("/{cartIds}")
    public AjaxResult remove(@PathVariable Long[] cartIds)
    {
        return toAjax(tCartService.deleteTCartByCartIds(cartIds));
    }

    /**
     * 购物车下单
     */
    @PreAuthorize("@ss.hasPermi('TCart:cart:add')")
    @Log(title = "购物车下单", businessType = BusinessType.INSERT)
    @PostMapping("/checkout")
    public AjaxResult checkout(@RequestBody Long[] cartIds)
    {
        System.out.println("进入下单功能");
        Long userId = SecurityUtils.getLoginUser().getUserId();
        return success(tCartService.checkout(userId, cartIds));
    }
}
