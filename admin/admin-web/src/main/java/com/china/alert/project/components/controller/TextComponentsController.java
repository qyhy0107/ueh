package com.china.alert.project.components.controller;

import com.china.alert.project.components.domain.TextComponents;
import com.china.alert.project.components.service.ITextComponentsService;
import com.china.alert.framework.web.controller.BaseController;
import com.china.alert.framework.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 组件定制化 信息操作处理
 *
 * @author like
 */
@RestController
@RequestMapping("/text/components")
public class TextComponentsController extends BaseController {
    @Autowired
    private ITextComponentsService textComponentsService;


    /**
     * 查询所有组件定制化数据
     */
    @RequestMapping(value="/listByComponentsName",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult listByComponentsName(@RequestParam("componentsName") String componentsName) {
        return AjaxResult.success(textComponentsService.selectTextComponentsByComponentsName(componentsName));
    }

    /**
     * 根据组件id获取详细信息
     */
    @RequestMapping(value="/getInfo",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult getInfo(@PathVariable @RequestParam("id") String id) {
        return AjaxResult.success(textComponentsService.selectTextComponents(id));
    }

    /**
     * 修改组件定制化数据
     */
    @RequestMapping(value="/add",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult add(@Validated @RequestBody TextComponents customComponents) {
        return toAjax(textComponentsService.insertTextComponents(customComponents));
    }


    /**
     * 修改组件定制化数据
     */
    @RequestMapping(value="/edit",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult edit(@Validated @RequestBody TextComponents customComponents) {
        return toAjax(textComponentsService.updateTextComponents(customComponents));
    }

    /**
     * 删除
     */
    @RequestMapping(value="/remove",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult remove(@PathVariable @RequestParam("id") String id) {
        return toAjax(textComponentsService.deleteTextComponents(id));
    }


}
