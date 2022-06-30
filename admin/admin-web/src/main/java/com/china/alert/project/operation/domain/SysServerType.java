package com.china.alert.project.operation.domain;

import com.china.alert.framework.web.domain.BaseEntity;

/**
 * 状态监控-服务器
 *
 * @author yht
 */
public class SysServerType extends BaseEntity {

    private  String cpuInfo;

    public String getCpuInfo() {
        return cpuInfo;
    }

    public void setCpuInfo(String cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    @Override
    public String toString() {
        return "SysServerType{" +
                "cpuInfo='" + cpuInfo + '\'' +
                '}';
    }


}
