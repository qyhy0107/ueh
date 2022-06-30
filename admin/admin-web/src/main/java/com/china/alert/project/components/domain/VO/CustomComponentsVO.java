package com.china.alert.project.components.domain.VO;

import java.util.Date;
import java.util.List;

public class CustomComponentsVO {

    /** 组件ID */
    private String componentsId;
    /** 组件名称 */
    private String componentsName;
    /** 页面布局组件数量 */
    private int componentsSize;
    /** 页面布局组件数量 */
    private int componentsCount;
    /** 创建者*/
    private String create_by;
    /** 创建时间*/
    private Date create_time;
    /**更新者*/
    private String update_by;
    /**更新时间*/
    private Date update_time;
    /** 备注*/
    private String remark;

    private List data;

    public CustomComponentsVO() {
    }
    public int getComponentsCount() {
        return componentsCount;
    }

    public void setComponentsCount(int componentsCount) {
        this.componentsCount = componentsCount;
    }

    public String getComponentsId() {
        return componentsId;
    }

    public void setComponentsId(String componentsId) {
        this.componentsId = componentsId;
    }

    public String getComponentsName() {
        return componentsName;
    }

    public void setComponentsName(String componentsName) {
        this.componentsName = componentsName;
    }

    public int getComponentsSize() {
        return componentsSize;
    }

    public void setComponentsSize(int componentsSize) {
        this.componentsSize = componentsSize;
    }

    public String getCreate_by() {
        return create_by;
    }

    public void setCreate_by(String create_by) {
        this.create_by = create_by;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getUpdate_by() {
        return update_by;
    }

    public void setUpdate_by(String update_by) {
        this.update_by = update_by;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }
}
