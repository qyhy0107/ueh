package com.china.alert.project.operation.domain;

/**
 * @author: weijingyu
 * @description:
 * @date: create in 2020/3/9 0009 9:42
 */
public class ProjectInfoEntity {

    private int id;
    private String name;
    private String owner;
    private String phone;
    private String projectInfo;
    private int sysDeptId;
    private String isEnable;
    private String remark;

    public void setSysDeptId(int sysDeptId) {
        this.sysDeptId = sysDeptId;
    }

    public int getSysDeptId() {
        return sysDeptId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setProjectInfo(String projectInfo) {
        this.projectInfo = projectInfo;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getPhone() {
        return phone;
    }

    public String getProjectInfo() {
        return projectInfo;
    }

    public String getIsEnable() {
        return isEnable;
    }

    public String getRemark() {
        return remark;
    }
}
