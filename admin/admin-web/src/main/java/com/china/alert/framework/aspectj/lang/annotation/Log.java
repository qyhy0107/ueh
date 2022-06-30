package com.china.alert.framework.aspectj.lang.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.china.alert.framework.aspectj.lang.enums.BusinessType;
import com.china.alert.framework.aspectj.lang.enums.OperatorType;

/**
 * 自定义操作日志记录注解
 *
 * @author jz
 *
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log
{
    /**
     * 模块
     */
    public String title() default "";

    /**
     * @Method : subTitle
     * @Description : //TODO
     * @Date : 2020/6/4 2020/6/4
     * @param
     * @return java.lang.String
     **/
    String subTitle() default "";

    /**
     * 功能
     */
    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类别
     */
    public OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;
}
