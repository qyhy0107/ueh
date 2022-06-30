package com.china.alert.project.components.controller;


import com.china.alert.framework.web.domain.AjaxResult;
import com.china.alert.project.components.service.ICustomComponentsService;
import com.china.alert.framework.web.controller.BaseController;
import com.china.alert.framework.web.domain.AjaxResult;
import com.china.alert.project.components.domain.CustomComponents;
import com.china.alert.project.components.domain.VO.CustomComponentsVO;
import com.china.alert.project.components.service.ICustomComponentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
;


/**
 * 组件定制化 信息操作处理
 *
 * @author like
 */
@RestController
@RequestMapping("/custom/components")
public class CustomComponentsController extends BaseController {
    @Autowired
    private ICustomComponentsService customComponentsService;

    /**
     * 查询所以组件定制化数据
     */
    @RequestMapping(value="/list",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult list() {
        return AjaxResult.success(customComponentsService.selectCustomComponentsList());
    }

    /**
     * 查询所有组件定制化数据
     */
    @RequestMapping(value="/listByComponentsName",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult listByComponentsName(@RequestParam("componentsName") String componentsName) {
        return AjaxResult.success(customComponentsService.selectCustomComponentsListByName(componentsName));
    }

    /**
     * 根据组件id获取详细信息
     */
    @RequestMapping(value="/getInfo",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult getInfo(@PathVariable @RequestParam("componentsId") String componentsId) {
        return AjaxResult.success(customComponentsService.selectCustomComponents(componentsId));
    }

    /**
     * 新增组件定制化数据
     */
    @RequestMapping(value="/add",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult add(@Validated @RequestBody CustomComponents customComponents) {
        return toAjax(customComponentsService.insertCustomComponents(customComponents));
    }

    /**
     * 修改组件定制化数据
     */
    @RequestMapping(value="/edit",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult edit(@Validated @RequestBody CustomComponents customComponents) {
        return toAjax(customComponentsService.updateCustomComponents(customComponents));
    }

    /**
     * 修改组件定制化的发布状态
     */
    @RequestMapping(value="/editPubish",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult editPubish(@Validated @RequestBody CustomComponents customComponents) {
        return toAjax(customComponentsService.updateCustomComponentsByComponentsName(customComponents));
    }


    /**
     * 删除参数配置
     */
    @RequestMapping(value="/remove",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult remove(@PathVariable String customComponents) {
        return toAjax(customComponentsService.deleteCustomComponents(customComponents));
    }

    /**
     * 查询所以组件定制化数据的总和
     */
    @RequestMapping(value="/listCount",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult listCount() {
        return AjaxResult.success(customComponentsService.selectCustomComponentsCount());
    }

    /**
     * 根据组件名称查询所有组件定制化数据
     */
    @RequestMapping(value="/listCountByComponentsName",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult listCountByComponentsName(@RequestParam("componentsName") String componentsName) {
        return AjaxResult.success(customComponentsService.selectCustomComponentsCountBycomponentsName(componentsName));
    }


    /**
     * 根据组件名称删除参数配置
     */
    @RequestMapping(value="/removeComponentsName",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult removeComponentsName(@RequestParam("componentsName") String componentsName) {
        return toAjax(customComponentsService.deleteCustomComponentsBycomponentsName(componentsName));
    }

    /**
     * 根据组件名称查询是否存在
     */
    @RequestMapping(value="/flagByComponentsName",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult flagByComponentsName(@RequestParam("componentsName") String componentsName) {
        return AjaxResult.success(customComponentsService.selectCustomComponentsListByName(componentsName).size()>0?true:false);
    }
//
//    /**
//     * 获取多数据集的数据
//     *
//     * @param str 传入的json对象
//     * @return
//     * @throws Exception
//     */
//    @ApiOperation("获取多数据集的数据")
//    @RequestMapping(value="/dateSet",method = {RequestMethod.GET,RequestMethod.POST})
//    @Log(businessType = BusinessType.DATASERVICE)
//    public ResultPattern loadComplexData(@RequestBody String str) {
//        DataLoadParams loadParams = JSONObject.parseObject(str, DataLoadParams.class);
//        ResultPattern resultPattern = DataServiceUtil.dataLoad(loadParams);
//        return resultPattern;
//    }


    /**
     * 假数据测试(后续会删掉的)
     *
     */
    @RequestMapping(value="/testData",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult testData() {
        Map<String,String> map =  new HashMap<String,String>();
        map.put("title","系统访问人数");
        map.put("content",String.valueOf(System.currentTimeMillis()).substring(String.valueOf(System.currentTimeMillis()).length()-6,String.valueOf(System.currentTimeMillis()).length()));
        return AjaxResult.success(map);
    }

    /**
     * 表格假数据测试(后续会删掉的)
     *
     */
    @RequestMapping(value="/testTableColumnComment",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult testTableColumnComment() {
        List list = customComponentsService.selectColumnComment();
        return AjaxResult.success(list);
    }

    /**
     * 表格假数据测试(后续会删掉的)
     *
     */
    @RequestMapping(value="/testTableData",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult testTableData() {
        List<CustomComponentsVO> customComponentsVOS = customComponentsService.selectCustomComponentsList();
        return AjaxResult.success(customComponentsVOS);
    }

    /**
     *  echars假数据测试(后续会删掉的)
     *
     */
    @RequestMapping(value="/testEchartsData",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult testEchartsData() {
        Map<String,Object> map =  new HashMap<String,Object>();
        List<Object> listXAxis = new ArrayList<Object>();
        listXAxis.add("Mon");
        listXAxis.add("Tue");
        listXAxis.add("Wed");
        listXAxis.add("Thu");
        listXAxis.add("Fri");
        listXAxis.add("Sat");
        listXAxis.add("Sun");
        map.put("xAxis",listXAxis);
        List<String> listXYxis = new ArrayList<String>();
        Random random = new Random();
        listXYxis.add(String.valueOf(random.nextInt(1000)));
        listXYxis.add(String.valueOf(random.nextInt(1000)));
        listXYxis.add(String.valueOf(random.nextInt(1000)));
        listXYxis.add(String.valueOf(random.nextInt(1000)));
        listXYxis.add(String.valueOf(random.nextInt(1000)));
        listXYxis.add(String.valueOf(random.nextInt(1000)));
        listXYxis.add(String.valueOf(random.nextInt(1000)));
        map.put("yAxis",listXYxis);
        return AjaxResult.success(map);
    }


    /**
     * 假数据饼图测试(后续会删掉的)
     *
     */
    @RequestMapping(value="/testPieData",method = {RequestMethod.GET,RequestMethod.POST})
    public AjaxResult testPieData() {
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();
        Map<String,String> map =  new HashMap<String,String>();
        Random random = new Random();
        map.put("name","直接访问");
        map.put("value",String.valueOf(random.nextInt(1000)));
        list.add(map);
        Map<String,String> map1 =  new HashMap<String,String>();
        map1.put("name","邮件营销");
        map1.put("value",String.valueOf(random.nextInt(1000)));
        list.add(map1);
        Map<String,String> map2 =  new HashMap<String,String>();
        map2.put("name","联盟广告");
        map2.put("value",String.valueOf(random.nextInt(1000)));
        list.add(map2);
        Map<String,String> map3 =  new HashMap<String,String>();
        map3.put("name","视频广告");
        map3.put("value",String.valueOf(random.nextInt(1000)));
        list.add(map3);
        Map<String,String> map4=  new HashMap<String,String>();
        map4.put("name","搜索引擎");
        map4.put("value",String.valueOf(random.nextInt(1000)));
        list.add(map4);
        return AjaxResult.success(list);
    }



}
