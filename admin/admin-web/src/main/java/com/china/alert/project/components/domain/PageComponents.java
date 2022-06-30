package com.china.alert.project.components.domain;

import java.util.Date;

public class PageComponents {

    String  id;
    String  componentsName;
    String  componentsId;
    String  componentsType;
    String  refreshTime;
    String  json_components;
    String  dataSet;
    Date    create_time;
    String  remark;

    public PageComponents() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComponentsName() {
        return componentsName;
    }

    public void setComponentsName(String componentsName) {
        this.componentsName = componentsName;
    }

    public String getComponentsId() {
        return componentsId;
    }

    public void setComponentsId(String componentsId) {
        this.componentsId = componentsId;
    }

    public String getComponentsType() {
        return componentsType;
    }

    public void setComponentsType(String componentsType) {
        this.componentsType = componentsType;
    }

    public String getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(String refreshTime) {
        this.refreshTime = refreshTime;
    }

    public String getJson_components() {
        return json_components;
    }

    public void setJson_components(String json_components) {
        this.json_components = json_components;
    }

    public String getDataSet() {
        return dataSet;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
