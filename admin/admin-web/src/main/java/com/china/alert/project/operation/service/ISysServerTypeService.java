package com.china.alert.project.operation.service;

import com.china.alert.project.operation.domain.SysServerType;

import java.util.List;

/**
 * 状态监控-服务器
 *
 * @author yht
 */
public interface ISysServerTypeService {
    /**
     * 查询服务器参数指标列表
     *
     * @param id  参数配置信息
     * @return 参数指标集合
     */
    public List<SysServerType> selectServerList(String id);
}
