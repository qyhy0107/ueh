package com.china.alert.project.tool.swagger.domain;

import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * date 2022/2/14 11:21
 *
 * @version V1.0
 * @Package com.china.alert.project.tool.swagger.domain
 * 自定义监控配置类
 */
public class CustomMonitoroLineEntity {
    private Long id;
    @Excel(name = "设备类型")
    private Long classId;
    private Long resId;
    @Excel(name = "端口名称")
    private String interfaceName;
    @Excel(name = "设备IP")
    private String interfaceBizName;
    @Excel(name = "上行")
    private Integer inSpeed;
    @Excel(name = "下行")
    private Integer outSpeed;
    private String isEnable;
    private Long projectId;
    @Excel(name = "设备")
    private String remark;
    @Excel(name = "设备")
    private String monitorName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getResId() {
        return resId;
    }

    public void setResId(Long resId) {
        this.resId = resId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceBizName() {
        return interfaceBizName;
    }

    public void setInterfaceBizName(String interfaceBizName) {
        this.interfaceBizName = interfaceBizName;
    }

    public Integer getInSpeed() {
        return inSpeed;
    }

    public void setInSpeed(Integer inSpeed) {
        this.inSpeed = inSpeed;
    }

    public Integer getOutSpeed() {
        return outSpeed;
    }

    public void setOutSpeed(Integer outSpeed) {
        this.outSpeed = outSpeed;
    }

    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }
}
