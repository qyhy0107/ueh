package com.china.alert.project.operation.domain;

/**
 * @author: weijingyu
 * @description:
 * @date: create in 2020/7/13 0013 10:51
 */
public class ZabbixMacroParam {

    private String kpiDefId;//指标映射id
    private String value;//指标阈值

    public String getKpiDefId() {
        return kpiDefId;
    }

    public void setKpiDefId(String kpiDefId) {
        this.kpiDefId = kpiDefId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
