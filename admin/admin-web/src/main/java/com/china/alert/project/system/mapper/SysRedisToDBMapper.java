package com.china.alert.project.system.mapper;

import com.china.alert.project.system.domain.SysRedisToDB;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author: like
 * @description: 本地缓存
 * @date: create in 2019/12/23 0009 21:21
 */
@Mapper
public interface SysRedisToDBMapper {
    SysRedisToDB getCacheObject(@Param("key") String key);
//    /**
//     * 缓存基本的对象，Integer、String、实体类等
//     *
//     * @param key   缓存的键值
//     * @param value 缓存的值
//     * @return 缓存的对象
//     */

    int updateCacheObject(@Param("key") String key, @Param("value") String value, @Param("expireTime") String expireTime);

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param sysRedisToDB 缓存的对象
     * @return 缓存的对象
     */
    int setCacheObject(SysRedisToDB sysRedisToDB);

    /**
     * 删除单个对象
     *
     * @param key
     */
     int deleteObject(@Param("key") String key);

    /**
     *  删除过期信息
     * @return
     */
    int deleteExpireTime();
}
