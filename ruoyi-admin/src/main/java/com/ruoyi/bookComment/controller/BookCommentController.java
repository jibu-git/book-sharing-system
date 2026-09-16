package com.ruoyi.bookComment.controller;

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
import com.ruoyi.bookComment.domain.BookComment;
import com.ruoyi.bookComment.service.IBookCommentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 图书评论Controller
 * * @author ruoyi
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/Comment/comment")
public class BookCommentController extends BaseController
{
    @Autowired
    private IBookCommentService bookCommentService;

    /**
     * 【Portal专用】获取书籍的树形评论列表
     * 该接口不需要分页，通常书籍评论会一次性加载根评论及回复
     */
    @GetMapping("/treeList/{bookId}")
    public AjaxResult getTreeList(@PathVariable("bookId") Long bookId)
    {
        return success(bookCommentService.selectBookCommentTreeByBookId(bookId));
    }

    /**
     * 查询图书评论列表 (后台管理用)
     */
    @PreAuthorize("@ss.hasPermi('Comment:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(BookComment bookComment)
    {
        startPage();
        List<BookComment> list = bookCommentService.selectBookCommentList(bookComment);
        return getDataTable(list);
    }

    /**
     * 新增图书评论
     */
    @Log(title = "图书评论", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BookComment bookComment)
    {
        // 从 SecurityUtils 获取当前登录用户信息
        // 强制设置，防止前端恶意传值
        bookComment.setUserId(SecurityUtils.getUserId());
        bookComment.setUserName(SecurityUtils.getUsername());
        bookComment.setAvatar(SecurityUtils.getLoginUser().getUser().getAvatar());
        bookComment.setCreateBy(SecurityUtils.getUsername());

        return toAjax(bookCommentService.insertBookComment(bookComment));
    }

    /**
     * 导出图书评论列表
     */
    @PreAuthorize("@ss.hasPermi('Comment:comment:export')")
    @Log(title = "图书评论", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BookComment bookComment)
    {
        List<BookComment> list = bookCommentService.selectBookCommentList(bookComment);
        ExcelUtil<BookComment> util = new ExcelUtil<BookComment>(BookComment.class);
        util.exportExcel(response, list, "图书评论数据");
    }

    /**
     * 获取图书评论详细信息
     */
    @PreAuthorize("@ss.hasPermi('Comment:comment:query')")
    @GetMapping(value = "/{commentId}")
    public AjaxResult getInfo(@PathVariable("commentId") Long commentId)
    {
        return success(bookCommentService.selectBookCommentByCommentId(commentId));
    }

    /**
     * 修改图书评论
     */
    @PreAuthorize("@ss.hasPermi('Comment:comment:edit')")
    @Log(title = "图书评论", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BookComment bookComment)
    {
        return toAjax(bookCommentService.updateBookComment(bookComment));
    }

    /**
     * 删除图书评论
     */
    @PreAuthorize("@ss.hasPermi('Comment:comment:remove')")
    @Log(title = "图书评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{commentIds}")
    public AjaxResult remove(@PathVariable Long[] commentIds)
    {
        return toAjax(bookCommentService.deleteBookCommentByCommentIds(commentIds));
    }
}