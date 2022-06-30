package com.china.alert.project.tool.swagger.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;

import javax.validation.constraints.NotBlank;

/**
 * @author yanzeyang
 * date 2022/3/2 17:14
 * @version V1.0
 * @Package com.china.alert.project.tool.swagger.dto
 */
public class ProcMonitorParam {
    @Excel(name="告警触发值 *")
    @NotBlank(message = "告警触发值不能为空")
    private String threshold;
    @Excel(name = "告警触发条件 *")
    @NotBlank(message = "告警触发条件不能为空")
    private String alarm_operator;
    @Excel(name = "告警级别 *")
    @NotBlank(message = "告警级别不能为空")
    private String alarm_severity;
    @Excel(name = "父进程ID")
    private String parent_proc_id;
    @Excel(name = "进程运行用户")
    private String proc_user;
    @Excel(name = "进程标识 *")
    @NotBlank(message = "进程标识不能为空")
    private String proc_flag;
    private String port;
    private String logkey;
    private String logpath;
    private String logtype;

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getAlarm_operator() {
        return alarm_operator;
    }

    public void setAlarm_operator(String alarm_operator) {
        this.alarm_operator = alarm_operator;
    }

    public String getAlarm_severity() {
        return alarm_severity;
    }

    public void setAlarm_severity(String alarm_severity) {
        this.alarm_severity = alarm_severity;
    }

    public String getParent_proc_id() {
        return parent_proc_id;
    }

    public void setParent_proc_id(String parent_proc_id) {
        this.parent_proc_id = parent_proc_id;
    }

    public String getProc_user() {
        return proc_user;
    }

    public void setProc_user(String proc_user) {
        this.proc_user = proc_user;
    }

    public String getProc_flag() {
        return proc_flag;
    }

    public void setProc_flag(String proc_flag) {
        this.proc_flag = proc_flag;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getLogkey() {
        return logkey;
    }

    public void setLogkey(String logkey) {
        this.logkey = logkey;
    }

    public String getLogpath() {
        return logpath;
    }

    public void setLogpath(String logpath) {
        this.logpath = logpath;
    }

    public String getLogtype() {
        return logtype;
    }

    public void setLogtype(String logtype) {
        this.logtype = logtype;
    }

    @Override
    public String toString() {
        return "MonitorParamImport{" +
                "threshold='" + threshold + '\'' +
                ", alarm_operator='" + alarm_operator + '\'' +
                ", alarm_severity='" + alarm_severity + '\'' +
                ", parent_proc_id='" + parent_proc_id + '\'' +
                ", proc_user='" + proc_user + '\'' +
                ", proc_flag='" + proc_flag + '\'' +
                ", port='" + port + '\'' +
                ", logkey='" + logkey + '\'' +
                ", logpath='" + logpath + '\'' +
                ", logtype='" + logtype + '\'' +
                '}';
    }
}
