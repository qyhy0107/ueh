package com.china.alert.project.system.service;

import org.apache.poi.ss.formula.functions.T;

import java.util.concurrent.TimeUnit;

/**
 * @author: like
 * @description: 本地缓存
 * @date: create in 2019/12/23 0009 21:23
 */
public interface ISysRedisToDBService {

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return 缓存的对象
     */
    public int setCacheObject(String key, Object value,Long expireTime);

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     * @return 缓存的对象
     */
    public int setCacheObject(String key, Object value, Integer timeout, TimeUnit timeUnit,Long expireTime);

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(String key);

    /**
     * 删除单个对象
     *
     * @param key
     */
    public void deleteObject(String key);


}
