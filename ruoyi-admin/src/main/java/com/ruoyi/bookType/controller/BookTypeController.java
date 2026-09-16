package com.ruoyi.bookType.controller;

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
import com.ruoyi.bookType.domain.BookType;
import com.ruoyi.bookType.service.IBookTypeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 图书标签Controller
 * 
 * @author ruoyi
 * @date 2025-10-30
 */
@RestController
@RequestMapping("/BookType/type")
public class BookTypeController extends BaseController
{
    @Autowired
    private IBookTypeService bookTypeService;

    /**
     * 查询图书标签列表
     */
    @PreAuthorize("@ss.hasPermi('BookType:type:list')")
    @GetMapping("/list")
    public TableDataInfo list(BookType bookType)
    {
        startPage();
        List<BookType> list = bookTypeService.selectBookTypeList(bookType);
        return getDataTable(list);
    }

    /**
     * 导出图书标签列表
     */
    @PreAuthorize("@ss.hasPermi('BookType:type:export')")
    @Log(title = "图书标签", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BookType bookType)
    {
        List<BookType> list = bookTypeService.selectBookTypeList(bookType);
        ExcelUtil<BookType> util = new ExcelUtil<BookType>(BookType.class);
        util.exportExcel(response, list, "图书标签数据");
    }

    /**
     * 获取图书标签详细信息
     */
    @PreAuthorize("@ss.hasPermi('BookType:type:query')")
    @GetMapping(value = "/{bookTypeId}")
    public AjaxResult getInfo(@PathVariable("bookTypeId") Long bookTypeId)
    {
        return success(bookTypeService.selectBookTypeByBookTypeId(bookTypeId));
    }

    /**
     * 新增图书标签
     */
    @PreAuthorize("@ss.hasPermi('BookType:type:add')")
    @Log(title = "图书标签", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BookType bookType)
    {
        return toAjax(bookTypeService.insertBookType(bookType));
    }

    /**
     * 修改图书标签
     */
    @PreAuthorize("@ss.hasPermi('BookType:type:edit')")
    @Log(title = "图书标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BookType bookType)
    {
        return toAjax(bookTypeService.updateBookType(bookType));
    }

    /**
     * 删除图书标签
     */
    @PreAuthorize("@ss.hasPermi('BookType:type:remove')")
    @Log(title = "图书标签", businessType = BusinessType.DELETE)
	@DeleteMapping("/{bookTypeIds}")
    public AjaxResult remove(@PathVariable Long[] bookTypeIds)
    {
        return toAjax(bookTypeService.deleteBookTypeByBookTypeIds(bookTypeIds));
    }
}
