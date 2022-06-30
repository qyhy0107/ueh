package com.china.alert.project.system.domain;

import com.china.alert.framework.web.domain.BaseEntity;

/**
 * @author: jz
 * @description: 参数分组信息
 * @date: create in 2019/12/5 0005 16:24
 */
public class SysGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long groupId;//分组id

    private String groupName;//分组名字

    private String flag;//是否可用

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }
}
