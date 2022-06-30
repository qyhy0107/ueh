package com.china.alert.project.system.controller;

import com.china.alert.common.constant.UserConstants;
import com.china.alert.common.utils.SecurityUtils;
import com.china.alert.common.utils.poi.ExcelUtil;
import com.china.alert.project.system.domain.SysDictData;
import com.china.alert.project.system.domain.SysDictType;
import com.china.alert.project.system.service.ISysDictDataService;
import com.china.alert.project.system.service.ISysDictTypeService;
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
 * 数据字典信息
 *
 * @author jz
 */
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController {
    @Autowired
    private ISysDictTypeService dictTypeService;

    @Autowired
    private ISysDictDataService dictDataService;

    /*@PreAuthorize("@ss.hasPermi('system:group:list')")*/
    @GetMapping("/list")
    public TableDataInfo list(SysDictType dictType) {
        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return getDataTable(list);
    }

    @Log(title = "配置参数组管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:group:export')")
    @GetMapping("/export")
    public AjaxResult export(SysDictType dictType) {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        return util.exportExcel(list, "配置参数组管理");
    }

    /**
     * 查询字典类型详细
     */
    @PreAuthorize("@ss.hasPermi('system:group:query')")
    @GetMapping(value = "/{dictId}")
    public AjaxResult getInfo(@PathVariable Long dictId) {
        return AjaxResult.success(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:group:add')")
    @Log(title = "配置参数组管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDictType dict) {
        if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict))) {
            return AjaxResult.error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:group:edit')")
    @Log(title = "配置参数组管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDictType dict) {
        if (UserConstants.NOT_UNIQUE.equals(dictTypeService.checkDictTypeUnique(dict))) {
            return AjaxResult.error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     */
    @PreAuthorize("@ss.hasPermi('system:group:remove')")
    @Log(title = "配置参数组管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public AjaxResult remove(@PathVariable Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictType sysDictType = dictTypeService.selectDictTypeById(dictId);
            SysDictData sysDictData = dictDataService.selectDictDataByDictType(sysDictType.getDictType());
            if (sysDictData != null) {
                return AjaxResult.error("存在子配置参数,不允许删除,请核实");
            }
        }
        return toAjax(dictTypeService.deleteDictTypeByIds(dictIds));
    }
}
