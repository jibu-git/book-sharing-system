package com.ruoyi.Message.controller;

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
import com.ruoyi.Message.domain.TMessage;
import com.ruoyi.Message.service.ITMessageService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 消息提醒Controller
 * 
 * @author ruoyi
 * @date 2026-03-27
 */
@RestController
@RequestMapping("/message/message")
public class TMessageController extends BaseController
{
    @Autowired
    private ITMessageService tMessageService;

    /**
     * 查询消息提醒列表
     */
    @PreAuthorize("@ss.hasPermi('message:message:list')")
    @GetMapping("/list")
    public TableDataInfo list(TMessage tMessage)
    {
        startPage(); // 开启分页

        Long currentUserId = getUserId();

        if (currentUserId == 1L) {
            // 如果是管理员，不做 receiverId 的硬性限制
            // 管理员也可以根据前端传来的参数过滤特定用户的消息，但不强制锁定为自己
            System.out.println("管理员访问：查看全局消息记录");
        } else {
            // 普通用户：强制锁定只能查询发送给自己的消息
            tMessage.setReceiverId(currentUserId);
            System.out.println("普通用户访问：锁定 receiverId = " + currentUserId);
        }

        List<TMessage> list = tMessageService.selectTMessageList(tMessage);
        return getDataTable(list);
    }


    /**
     * 获取未读总数（同样区分管理员）
     */
    @PreAuthorize("@ss.hasPermi('message:message:list')")
    @GetMapping("/unreadCount")
    public AjaxResult getUnreadCount() {
        TMessage query = new TMessage();
        query.setIsRead("0"); // 只查未读

        Long currentUserId = getUserId();
        if (currentUserId != 1L) {
            query.setReceiverId(currentUserId);
        }

        List<TMessage> list = tMessageService.selectTMessageList(query);
        return AjaxResult.success(list.size());
    }


    /**
     * 导出消息提醒列表
     */
    @PreAuthorize("@ss.hasPermi('message:message:export')")
    @Log(title = "消息提醒", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TMessage tMessage)
    {
        List<TMessage> list = tMessageService.selectTMessageList(tMessage);
        ExcelUtil<TMessage> util = new ExcelUtil<TMessage>(TMessage.class);
        util.exportExcel(response, list, "消息提醒数据");
    }

    /**
     * 获取消息提醒详细信息
     */
    @PreAuthorize("@ss.hasPermi('message:message:query')")
    @GetMapping(value = "/{msgId}")
    public AjaxResult getInfo(@PathVariable("msgId") Long msgId)
    {
        return success(tMessageService.selectTMessageByMsgId(msgId));
    }

    /**
     * 新增消息提醒
     */
    @PreAuthorize("@ss.hasPermi('message:message:add')")
    @Log(title = "消息提醒", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TMessage tMessage)
    {
        return toAjax(tMessageService.insertTMessage(tMessage));
    }

    /**
     * 修改消息提醒
     */
    @PreAuthorize("@ss.hasPermi('message:message:edit')")
    @Log(title = "消息提醒", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TMessage tMessage)
    {
        // 安全校验：只能修改发给自己的消息
        TMessage dbMessage = tMessageService.selectTMessageByMsgId(tMessage.getMsgId());
        if (dbMessage == null || !dbMessage.getReceiverId().equals(getUserId())) {
            return AjaxResult.error("无权操作此消息");
        }
        return toAjax(tMessageService.updateTMessage(tMessage));
    }

    /**
     * 删除消息提醒
     */
    @PreAuthorize("@ss.hasPermi('message:message:remove')")
    @Log(title = "消息提醒", businessType = BusinessType.DELETE)
	@DeleteMapping("/{msgIds}")
    public AjaxResult remove(@PathVariable Long[] msgIds)
    {
        return toAjax(tMessageService.deleteTMessageByMsgIds(msgIds));
    }
}
