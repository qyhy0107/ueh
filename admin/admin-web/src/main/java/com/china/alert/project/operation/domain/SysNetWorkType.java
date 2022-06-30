package com.china.alert.project.operation.domain;

import com.china.alert.framework.web.domain.BaseEntity;

/**
 * 状态监控-网络设备
 *
 * @author jz
 */
public class SysNetWorkType extends BaseEntity {

    private  String cpuInfo;

    public String getCpuInfo() {
        return cpuInfo;
    }

    public void setCpuInfo(String cpuInfo) {
        this.cpuInfo = cpuInfo;
    }

    @Override
    public String toString() {
        return "SysNetWorkType{" +
                "cpuInfo='" + cpuInfo + '\'' +
                '}';
    }
}
