package com.china.alert.project.operation.service;

import com.china.alert.project.operation.domain.SysNetWorkType;

import java.util.List;

/**
 * 状态监控-网络设备
 *
 * @author dongjiangjiang
 */
public interface ISysNetWorkTypeService {

    /**
     * 查询设备参数指标列表
     *
     * @param id  参数配置信息
     * @return 参数指标集合
     */
    public List<SysNetWorkType> selectNetWorkList(String id);
}
