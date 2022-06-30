package com.china.alert.project.components.domain;

import java.util.Date;

public class PageView {

    String  id;
    String  componentsName;
    String  componentsId;
    Date    create_time;
    String  remark;

    public PageView(){}

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
