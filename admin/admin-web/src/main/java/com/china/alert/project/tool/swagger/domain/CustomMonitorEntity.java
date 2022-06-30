package com.china.alert.project.tool.swagger.domain;

/**
 * date 2022/2/14 11:21
 *
 * @version V1.0
 * @Package com.china.alert.project.tool.swagger.domain
 * 自定义监控配置类
 */
public class CustomMonitorEntity {
    private Long id;
    private String monitorType;
    private String monitoredProxy;
    private String monitoredHost;
    private String dayOfWeekAt;
    private String dayOfWeekUtil;
    private String executeAt;
    private String executeUtil;
    private String monitorParam;
    private String isEnable;
    private Long projectId;
    private String remark;
    private String lmTimestamp;
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

    public String getMonitorParam() {
        return monitorParam;
    }

    public void setMonitorParam(String monitorParam) {
        this.monitorParam = monitorParam;
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
        return "CustomMonitorEntity{" +
                "id=" + id +
                ", monitorType='" + monitorType + '\'' +
                ", monitoredProxy='" + monitoredProxy + '\'' +
                ", monitoredHost='" + monitoredHost + '\'' +
                ", dayOfWeekAt='" + dayOfWeekAt + '\'' +
                ", dayOfWeekUtil='" + dayOfWeekUtil + '\'' +
                ", executeAt='" + executeAt + '\'' +
                ", executeUtil='" + executeUtil + '\'' +
                ", monitorParam='" + monitorParam + '\'' +
                ", isEnable='" + isEnable + '\'' +
                ", projectId=" + projectId +
                ", remark='" + remark + '\'' +
                ", lmTimestamp='" + lmTimestamp + '\'' +
                ", monitorName='" + monitorName + '\'' +
                ", isEffect='" + isEffect + '\'' +
                '}';
    }
}
