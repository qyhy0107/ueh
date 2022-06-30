package com.china.alert.project.tool.swagger;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.china.alert.framework.aspectj.lang.annotation.Log;
import com.china.alert.framework.aspectj.lang.enums.BusinessType;
import com.china.alert.framework.web.controller.BaseController;

import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.ResultPattern;
import cn.mcinfotech.data.service.exception.DataServiceException;
import cn.mcinfotech.data.service.util.DataServiceUtils;

/**
 * @author: jz
 * @description: 数据集
 * @date: create in 2019/10/8 0008 14:26
 */
@RestController
@RequestMapping("/data")
public class DataloadController extends BaseController {
    @Autowired
    private DataSource dataSource;
    /**
     * 获取多数据集的数据
     *
     * @param str 传入的json对象
     * @return
     * @throws Exception
     */
    @Log(businessType = BusinessType.DATASERVICE)
    @PostMapping(value = "/load")
    public ResultPattern loadComplexData(@RequestBody String str) {
        ResultPattern resultPattern=new ResultPattern();
        DataLoadParams loadParams=new DataLoadParams();
        try {
            loadParams = JSON.parseObject(str, DataLoadParams.class);// jsonStr
            if (loadParams.getProjectId()==null||loadParams.getProjectId() < 1) {
                throw new DataServiceException("project id is not setup, please check it !");
            }
            if (StringUtils.isBlank(loadParams.getDcName())) {
                throw new DataServiceException("dataset is not setup, please check it !");
            }
            resultPattern = DataServiceUtils.dataLoad(dataSource, loadParams);
        } catch (Exception e) {
            resultPattern.setDcName(loadParams.getDcName());
            resultPattern.setFilter(loadParams.getFilter());
            resultPattern.setSuccess(false);
            resultPattern.setEmpty(true);
            resultPattern.setErrorMsg(e.getMessage());
            e.printStackTrace();

        }
        return resultPattern;
    }
    /**
     * 获取顺序执行的数据集返回结果
     *
     * @param str 传入的json对象
     * @return
     * @throws Exception [{
     *                   "sort":1,
     *                   "params":'class_id',
     *                   "dcName":'test4'
     *                   },
     *                   {
     *                   "sort":2
     *                   "dcName":'test7'
     *                   }
     *                   ]
     */
    /*@ApiOperation("按顺序执行数据集返回结果：多个数据集执行")
    @PostMapping(value = "/dataFromOrder")
    @Transactional
    public ResultPattern loadDataFromOrder(@RequestBody String str) throws Exception {
        List<DataLoadParams> list = JSON.parseArray(str, DataLoadParams.class);
        ResultPattern resultPattern = dataFacadeService.loadDataFromOrder(list);
        return resultPattern;
    }*/

    /**
     * [
     *        {
     *       	"sort":1,
     *      	"dcName":'test4'
     *       },
     *      {
     *      	"sort":2,
     *      	"dcName":'test3'
     * }
     * ]
     * @param str
     * @return
     * @throws Exception
     */
//    @ApiOperation("按顺序执行数据集返回结果并保存：目前是两个数据集之间的操作，可根据需求进行扩展")
//    @PostMapping(value = "/loadDataFromOrderSave")
//    @Transactional
//    public ResultPattern loadDataFromOrderSave(@RequestBody String str) throws Exception{
//        List<DataLoadParams> list = JSON.parseArray(str, DataLoadParams.class);
//        ResultPattern resultPattern = dataFacadeService.loadDataFromOrderSave(list);
//        return resultPattern;
//    }
}
