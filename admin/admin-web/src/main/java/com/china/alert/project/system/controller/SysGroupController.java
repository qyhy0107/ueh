package com.china.alert.project.system.controller;

import com.china.alert.common.constant.UserConstants;
import com.china.alert.project.system.domain.SysGroup;
import com.china.alert.project.system.service.ISysGroupService;
import com.china.alert.framework.aspectj.lang.annotation.Log;
import com.china.alert.framework.aspectj.lang.enums.BusinessType;
import com.china.alert.framework.web.controller.BaseController;
import com.china.alert.framework.web.domain.AjaxResult;
import com.china.alert.framework.web.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: weijingyu
 * @description: 分组管理
 * @date: create in 2019/12/5 0005 18:09
 */
@RestController
@RequestMapping("/system/group")
public class SysGroupController extends BaseController {

    @Autowired
    ISysGroupService sysGroupService;

    /**
     * 获取分组管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:group:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysGroup group) {
        startPage();
        List<SysGroup> list = sysGroupService.selectGroupList(group);
        return getDataTable(list);
    }

    /**
     * 根据分组编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:group:query')")
    @GetMapping(value = "/{groupId}")
    public AjaxResult getInfo(@PathVariable Long groupId) {
        return AjaxResult.success(sysGroupService.selectGroupById(groupId));
    }

    /**
     * 根据参数键名查询分组管理
     */
    @PreAuthorize("@ss.hasPermi('system:group:query')")
    @GetMapping(value = "/groupKey/{groupName}")
    public AjaxResult getGroupKey(@PathVariable String groupName) {
        return AjaxResult.success(sysGroupService.selectGroupByKey(groupName));
    }

    /**
     * 新增分组管理
     */
    @PreAuthorize("@ss.hasPermi('system:group:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysGroup group) {
        if (UserConstants.NOT_UNIQUE.equals(sysGroupService.checkGroupKeyUnique(group))) {
            return AjaxResult.error("新增参数'" + group.getGroupName() + "'失败，参数键名已存在");
        }
        return toAjax(sysGroupService.insertGroup(group));
    }

    /**
     * 修改分组管理
     */
    @PreAuthorize("@ss.hasPermi('system:group:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysGroup group) {
        if (UserConstants.NOT_UNIQUE.equals(sysGroupService.checkGroupKeyUnique(group))) {
            return AjaxResult.error("修改参数'" + group.getGroupName() + "'失败，参数键名已存在");
        }
        return toAjax(sysGroupService.updateGroup(group));
    }

    /**
     * 删除分组管理
     */
    @PreAuthorize("@ss.hasPermi('system:group:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{groupIds}")
    public AjaxResult remove(@PathVariable Long[] groupIds) {
        return toAjax(sysGroupService.deleteGroupByIds(groupIds));
    }
}
