package com.ruoyi.books.controller;

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
import com.ruoyi.books.domain.Books;
import com.ruoyi.books.service.IBooksService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 图书商品Controller
 * 
 * @author ruoyi
 * @date 2025-10-30
 */
@RestController
@RequestMapping("/Books/books")
public class BooksController extends BaseController
{
    @Autowired
    private IBooksService booksService;

    /**
     * 查询图书商品列表
     */
    @PreAuthorize("@ss.hasPermi('Books:books:list')")
    @GetMapping("/list")
    public TableDataInfo list(Books books)
    {
        startPage();
        Long user_id = SecurityUtils.getLoginUser().getUserId();
        books.setUserId(user_id);
        List<Books> list = booksService.selectBooksList(books);
        return getDataTable(list);
    }

    /**
     * 前台查询图书商品列表
     */
    @PreAuthorize("@ss.hasPermi('Books:books:list')")
    @GetMapping("/list/portal")
    public TableDataInfo portalListBooks(Books books)
    {
//        System.out.println("调用了前台的获取图书列表");
        startPage();
        List<Books> list = booksService.selectPortalListBooks(books);
        return getDataTable(list);
    }

    /**
     * 导出图书商品列表
     */
    @PreAuthorize("@ss.hasPermi('Books:books:export')")
    @Log(title = "图书商品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response,Books books)
    {

        List<Books> list = booksService.selectBooksList(books);
        ExcelUtil<Books> util = new ExcelUtil<Books>(Books.class);
        util.exportExcel(response, list, "图书商品数据");
    }


    /**
     * 获取图书商品详细信息
     */
    @PreAuthorize("@ss.hasPermi('Books:books:query')")
    @GetMapping(value = "/{booksId}")
    public AjaxResult getInfo(@PathVariable("booksId") Long booksId)
    {

        return success(booksService.selectBooksByBooksId(booksId));
    }

    /**
     * 新增图书
     */
    @PreAuthorize("@ss.hasPermi('Books:books:add')")
    @Log(title = "图书", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Books books)
    {
        books.setUserId(SecurityUtils.getLoginUser().getUserId());
        return toAjax(booksService.insertBooks(books));
    }

    /**
     * 修改图书商品
     */
    @PreAuthorize("@ss.hasPermi('Books:books:edit')")
    @Log(title = "图书商品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Books books)
    {
        return toAjax(booksService.updateBooks(books));
    }

    /**
     * 删除图书商品
     */
    @PreAuthorize("@ss.hasPermi('Books:books:remove')")
    @Log(title = "图书商品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{booksIds}")
    public AjaxResult remove(@PathVariable Long[] booksIds)
    {
        return toAjax(booksService.deleteBooksByBooksIds(booksIds));
    }
}
