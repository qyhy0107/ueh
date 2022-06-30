package com.china.alert.project.components.controller;

import com.china.alert.project.components.domain.PageComponents;
import com.china.alert.project.components.service.IPageComponentsService;
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
@RequestMapping("/page/components")
public class PageComponentsController extends BaseController {
    @Autowired
    private IPageComponentsService pageComponentsService;


    /**
     * 查询所有组件定制化数据
     */
    @RequestMapping(value="/listByComponentsName",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult listByComponentsName(@RequestParam("componentsName") String componentsName) {
        return AjaxResult.success(pageComponentsService.selectPageComponentsByComponentsName(componentsName));
    }

    /**
     * 根据组件id获取详细信息
     */
    @RequestMapping(value="/getInfo",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult getInfo(@PathVariable @RequestParam("id") String id) {
        return AjaxResult.success(pageComponentsService.selectPageComponents(id));
    }


    /**
     * 新增组件定制化数据
     */
    @RequestMapping(value="/add",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult add(@Validated @RequestBody PageComponents customComponents) {
        return toAjax(pageComponentsService.insertPageComponents(customComponents));
    }

    /**
     * 修改组件定制化数据
     */
    @RequestMapping(value="/edit",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult edit(@Validated @RequestBody PageComponents customComponents) {
        return toAjax(pageComponentsService.updatePageComponents(customComponents));
    }

    /**
     * 删除
     */
    @RequestMapping(value="/remove",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult remove(@PathVariable @RequestParam("id") String id) {
        return toAjax(pageComponentsService.deletePageComponents(id));
    }


}
