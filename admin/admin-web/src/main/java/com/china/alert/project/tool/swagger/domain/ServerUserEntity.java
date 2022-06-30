package com.china.alert.project.tool.swagger.domain;

/**
 * date 2022/2/14 11:17
 *
 * @version V1.0
 * @Package com.china.alert.project.tool.swagger.domain
 * 服务器登录用户
 */
public class ServerUserEntity {
    private String userName;
    private String passWord;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        return "ServerUser{" +
                "userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }
}
