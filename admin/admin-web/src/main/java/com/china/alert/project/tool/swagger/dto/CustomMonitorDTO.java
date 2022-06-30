package com.china.alert.project.tool.swagger.dto;

import com.china.alert.project.tool.swagger.domain.CustomMonitorEntity;
import com.china.alert.project.tool.swagger.domain.ServerUserEntity;

/**
 * date 2022/2/14 11:29
 * @version V1.0
 * @Package com.china.alert.project.tool.swagger.dto
 */
public class CustomMonitorDTO {
    //自定义监控配置
    private CustomMonitorEntity customMonitorEntity;
    //服务器用户信息
    private ServerUserEntity serverUserEntity;

    public CustomMonitorEntity getCustomMonitorEntity() {
        return customMonitorEntity;
    }

    public void setCustomMonitorEntity(CustomMonitorEntity customMonitorEntity) {
        this.customMonitorEntity = customMonitorEntity;
    }

    public ServerUserEntity getServerUserEntity() {
        return serverUserEntity;
    }

    public void setServerUserEntity(ServerUserEntity serverUserEntity) {
        this.serverUserEntity = serverUserEntity;
    }

    @Override
    public String toString() {
        return "CustomMonitorDTO{" +
                "customMonitorEntity=" + customMonitorEntity +
                ", serverUserEntity=" + serverUserEntity +
                '}';
    }
}
