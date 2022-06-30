package com.china.alert.framework.aspectj.lang.enums;

/**
 * 业务操作类型
 *
 * @author jz
 */
public enum BusinessType
{
    /**
     * 其它
     */
    OTHER,

    /**
     * 新增
     */
    INSERT,

    /**
     * 修改
     */
    UPDATE,

    /**
     * 删除
     */
    DELETE,

    /**
     * 授权
     */
    GRANT,

    /**
     * 导出
     */
    EXPORT,

    /**
     * 导入
     */
    IMPORT,

    /**
     * 强退
     */
    FORCE,

    /**
     * 生成代码
     */
    GENCODE,

    /**
     * 清空数据
     */
    CLEAN,

    /**
     * 数据服务
     */
    DATASERVICE,

    /**
     * 查询
     */
    QUERY,

    /**
     * 登录
     */
    LOGIN_SUCCESS,

    /**
     * 注销
     */
    LOGOUT,
    /**
     * 登录失败
     */
    LOGIN_FAILD,
    /**
     * 登录失败,账户锁定
     */
    LOGIN_FAILD_LOCK,
    TX
}
