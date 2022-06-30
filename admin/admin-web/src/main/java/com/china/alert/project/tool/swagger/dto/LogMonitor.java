package com.china.alert.project.tool.swagger.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import cn.afterturn.easypoi.handler.inter.IExcelModel;
import com.alibaba.fastjson.JSON;

import javax.validation.constraints.NotBlank;

/**
 * date 2022/2/14 11:29
 *
 * @version V1.0
 * @Package com.china.alert.project.tool.swagger.dto
 */
public class LogMonitor implements IExcelModel {

    private Long id;
    @Excel(name = "监控类型 *")
    @NotBlank(message = "监控类型不能为空")
    private String monitorType;
    private String monitoredProxy;
    @Excel(name = "被管设备IP *")
    @NotBlank(message = "被管设备IP不能为空")
    private String monitoredHost;
    private String dayOfWeekAt;
    private String dayOfWeekUtil;
    private String executeAt;
    private String executeUtil;
    @ExcelEntity
    private LogMonitorParam monitorParam;
    private String monitorParamStr;
    private String isEnable;
    private Long projectId;
    @Excel(name = "备注")
    private String remark;
    private String lmTimestamp;
    @Excel(name = "名称 *")
    @NotBlank(message = "名称不能为空")
    private String monitorName;
    private String isEffect;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(String monitorType) {
        this.monitorType = monitorType;
    }

    public String getMonitoredProxy() {
        return monitoredProxy;
    }

    public void setMonitoredProxy(String monitoredProxy) {
        this.monitoredProxy = monitoredProxy;
    }

    public String getMonitoredHost() {
        return monitoredHost;
    }

    public void setMonitoredHost(String monitoredHost) {
        this.monitoredHost = monitoredHost;
    }

    public String getDayOfWeekAt() {
        return dayOfWeekAt;
    }

    public void setDayOfWeekAt(String dayOfWeekAt) {
        this.dayOfWeekAt = dayOfWeekAt;
    }

    public String getDayOfWeekUtil() {
        return dayOfWeekUtil;
    }

    public void setDayOfWeekUtil(String dayOfWeekUtil) {
        this.dayOfWeekUtil = dayOfWeekUtil;
    }

    public String getExecuteAt() {
        return executeAt;
    }

    public void setExecuteAt(String executeAt) {
        this.executeAt = executeAt;
    }

    public String getExecuteUtil() {
        return executeUtil;
    }

    public void setExecuteUtil(String executeUtil) {
        this.executeUtil = executeUtil;
    }

    public LogMonitorParam getMonitorParam() {
        return monitorParam;
    }

    public void setMonitorParam(LogMonitorParam monitorParam) {
        this.monitorParam = monitorParam;
    }

    public String getMonitorParamStr() {
        return monitorParamStr;
    }

    public void setMonitorParamStr(String monitorParamStr) {
        this.monitorParamStr = monitorParamStr;
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

    public String getLmTimestamp() {
        return lmTimestamp;
    }

    public void setLmTimestamp(String lmTimestamp) {
        this.lmTimestamp = lmTimestamp;
    }

    public String getMonitorName() {
        return monitorName;
    }

    public void setMonitorName(String monitorName) {
        this.monitorName = monitorName;
    }

    public String getIsEffect() {
        return isEffect;
    }

    public void setIsEffect(String isEffect) {
        this.isEffect = isEffect;
    }

    @Override
    public String toString() {
        return "LogMonitor{" +
                "id=" + id +
                ", monitorType='" + monitorType + '\'' +
                ", monitoredProxy='" + monitoredProxy + '\'' +
                ", monitoredHost='" + monitoredHost + '\'' +
                ", dayOfWeekAt='" + dayOfWeekAt + '\'' +
                ", dayOfWeekUtil='" + dayOfWeekUtil + '\'' +
                ", executeAt='" + executeAt + '\'' +
                ", executeUtil='" + executeUtil + '\'' +
                ", monitorParam=" + monitorParam +
                ", monitorParamStr='" + monitorParamStr + '\'' +
                ", isEnable='" + isEnable + '\'' +
                ", projectId=" + projectId +
                ", remark='" + remark + '\'' +
                ", lmTimestamp='" + lmTimestamp + '\'' +
                ", monitorName='" + monitorName + '\'' +
                ", isEffect='" + isEffect + '\'' +
                '}';
    }
    private String errorMsg;

    @Override
    public String getErrorMsg() {
        return errorMsg;
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
