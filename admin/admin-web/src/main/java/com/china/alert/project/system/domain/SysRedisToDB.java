package com.china.alert.project.system.domain;

/**
 * 字典类型表 sys_redis_to_db
 *
 * 去掉redis的存储，用本地存储
 *
 * @author jz
 */

public class SysRedisToDB  {

    /** redis的key */
    private   String key;
    /** redis的value */
    private   String value;
    /** 有效期*/
    private   int timeout;
    /** 有效期单位*/
    private   String timeUnit;
    /** 过期时间*/
    private  String expireTime;



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public SysRedisToDB() {}


}
