package com.china.alert.project.system.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.china.alert.project.system.domain.SysRedisToDB;
import com.china.alert.project.system.mapper.SysRedisToDBMapper;
import com.china.alert.project.system.service.ISysRedisToDBService;
import com.china.alert.framework.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

@Service
public class SysRedisToDBServiceImpl implements ISysRedisToDBService {

    @Autowired
    public SysRedisToDBMapper sysRedisToDBMapper;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     * @return 缓存的对象
     */
    public int setCacheObject(String key, Object value,Long expireTime) {
        return sysRedisToDBMapper.updateCacheObject(key, JSONObject.toJSONString(value),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expireTime));
    }


    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     * @return 缓存的对象
     */
    public int setCacheObject(String key, Object value, Integer timeout, TimeUnit timeUnit,Long expireTime) {
        //清零过期令牌
        sysRedisToDBMapper.deleteExpireTime();
        SysRedisToDB bean = sysRedisToDBMapper.getCacheObject(key);
        if(bean!=null){
            return sysRedisToDBMapper.updateCacheObject(key, JSONObject.toJSONString(value),new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(expireTime));
        }
        SysRedisToDB sysRedisToDB = new SysRedisToDB();
        sysRedisToDB.setKey(key);
        sysRedisToDB.setValue(JSONObject.toJSONString(value));
        sysRedisToDB.setTimeout(timeout);
        sysRedisToDB.setTimeUnit(String.valueOf(timeUnit));
        sysRedisToDB.setExpireTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(expireTime)));
        return sysRedisToDBMapper.setCacheObject(sysRedisToDB);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public LoginUser getCacheObject(String key) {
        SysRedisToDB bean = sysRedisToDBMapper.getCacheObject(key);
        if(bean!=null){
            String value = bean.getValue();
            JSONObject jsonObject = JSONObject.parseObject(value);
            LoginUser loginUser = JSONObject.toJavaObject(jsonObject, LoginUser.class);
            //检验是否有效期
            Long expireTime = loginUser.getExpireTime();
            if(expireTime-System.currentTimeMillis()>0){
                return loginUser;
            }else{
                sysRedisToDBMapper.deleteObject(key);
            }
        }
        return null;
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public void deleteObject(String key) {
        sysRedisToDBMapper.deleteObject(key);
    }
}


