package com.china.alert.framework.aspectj;

import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.PageDataSetEntity;
import cn.mcinfotech.data.service.service.PageDataSetService;
import com.alibaba.fastjson.JSON;
import com.china.alert.common.enums.HttpMethod;
import com.china.alert.common.utils.ServletUtils;
import com.china.alert.common.utils.StringUtils;
import com.china.alert.common.utils.ip.IpUtils;
import com.china.alert.common.utils.spring.SpringUtils;
import com.china.alert.project.monitor.domain.SysOperLog;
import com.china.alert.project.system.domain.SysUser;
import com.china.alert.project.system.service.ISysDictDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.china.alert.framework.aspectj.lang.annotation.Log;
import com.china.alert.framework.aspectj.lang.enums.BusinessStatus;
import com.china.alert.framework.aspectj.lang.enums.BusinessType;
import com.china.alert.framework.manager.AsyncManager;
import com.china.alert.framework.manager.factory.AsyncFactory;
import com.china.alert.framework.security.LoginUser;
import com.china.alert.framework.security.service.TokenService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 操作日志记录处理
 *
 * @author jz
 */
@Aspect
@Component
public class LogAspect {
    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    ISysDictDataService dictDataService;

    // 配置织入点
    @Pointcut("@annotation(com.china.alert.framework.aspectj.lang.annotation.Log)")
    public void logPointCut() {
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        handleLog(joinPoint, null, jsonResult);
    }

    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
        handleLog(joinPoint, e, null);
    }

    protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
        try {
            // 获得注解
            Log controllerLog = getAnnotationLog(joinPoint);
            if (controllerLog == null) {
                return;
            }

            // 获取当前的用户
            LoginUser loginUser = SpringUtils.getBean(TokenService.class).getLoginUser(ServletUtils.getRequest());


            // *========数据库日志=========*//
            SysOperLog operLog = new SysOperLog();
            operLog.setOperatorType(1);
            operLog.setSubtitle(controllerLog.subTitle());
            operLog.setStatus(BusinessStatus.SUCCESS.ordinal());
            // 请求的地址
            String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
            operLog.setOperIp(ip);
            // 返回参数
            operLog.setJsonResult(JSON.toJSONString(jsonResult));

            operLog.setOperUrl(ServletUtils.getRequest().getRequestURI());
            if (loginUser != null) {
                operLog.setOperName(loginUser.getUsername());
            }else {
                ObjectMapper objectMapper = new ObjectMapper();
                SysUser user = objectMapper.convertValue(jsonResult, SysUser.class);
                operLog.setOperName(user.getUserName());
            }

            if (e != null) {
                operLog.setStatus(BusinessStatus.FAIL.ordinal());
                operLog.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
            // 设置方法名称
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(methodName);
            // 设置请求方式
            operLog.setRequestMethod(ServletUtils.getRequest().getMethod());
            // 处理设置注解上的参数
            if (controllerLog.businessType().equals(BusinessType.DATASERVICE)) {
                getControllerMethidDescriptionForDataService(joinPoint, controllerLog, operLog);
            } else {
                getControllerMethodDescription(joinPoint, controllerLog, operLog);
            }
            // 保存数据库,查询不保存日志,后续需优化
            if (operLog.getBusinessType()!=11) {
                AsyncManager.me().execute(AsyncFactory.recordOper(operLog));
            }
        } catch (Exception exp) {
            // 记录本地异常日志
            log.error("==前置通知异常==");
            log.error("异常信息:{}", exp.getMessage());
            exp.printStackTrace();
        }
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param log     日志
     * @param operLog 操作日志
     * @throws Exception
     */
    public void getControllerMethodDescription(JoinPoint joinPoint, Log log, SysOperLog operLog) throws Exception {
        // 设置action动作
        operLog.setBusinessType(log.businessType().ordinal());
        // 设置标题
        operLog.setTitle(log.title());
        // 设置操作人类别
        operLog.setOperatorType(log.operatorType().ordinal());
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, operLog);
        }
    }

    public void getControllerMethidDescriptionForDataService(JoinPoint joinPoint, Log log, SysOperLog sysOperLog) throws Exception {
        String params = argsArrayToString(joinPoint.getArgs());
        DataLoadParams dataLoadParams = JSON.parseObject(params, DataLoadParams.class);
        PageDataSetEntity pageDataSetEntity = PageDataSetService.selectOne(dataSource,dataLoadParams.getProjectId(),null,dataLoadParams.getDcName());
        sysOperLog.setBusinessType(log.businessType().ordinal());
        // 设置操作人类别
        sysOperLog.setOperatorType(log.operatorType().ordinal());
        if (StringUtils.isNotNull(pageDataSetEntity)) {
            // 设置标题
            sysOperLog.setTitle(pageDataSetEntity.getDsGroup());
            //设置子标题
            sysOperLog.setSubtitle(pageDataSetEntity.getLabelText());
            if (pageDataSetEntity.getExecType().equals("QUERY")) {
                sysOperLog.setBusinessType(BusinessType.QUERY.ordinal());
            } else if (pageDataSetEntity.getExecType().equals("ADD")) {
                sysOperLog.setBusinessType(BusinessType.INSERT.ordinal());
            } else if (pageDataSetEntity.getExecType().equals("UPDATE")) {
                sysOperLog.setBusinessType(BusinessType.UPDATE.ordinal());
            } else if (pageDataSetEntity.getExecType().equals("DELETE")) {
                sysOperLog.setBusinessType(BusinessType.DELETE.ordinal());
            }
        } else {
            sysOperLog.setTitle("异常");
            sysOperLog.setStatus(1);
            sysOperLog.setErrorMsg("异常");
            //sysOperLog.setBusinessType(BusinessType.QUERY.ordinal());
        }
        // 是否需要保存request，参数和值
        if (log.isSaveRequestData()) {
            // 获取参数的信息，传入到数据库中。
            setRequestValue(joinPoint, sysOperLog);
        }
    }

    /**
     * 获取请求的参数，放到log中
     *
     * @param operLog 操作日志
     * @throws Exception 异常
     */
    private void setRequestValue(JoinPoint joinPoint, SysOperLog operLog) throws Exception {
        String requestMethod = operLog.getRequestMethod();
        if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
            String params = argsArrayToString(joinPoint.getArgs());
            operLog.setOperParam(StringUtils.substring(params, 0, 2000));
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) ServletUtils.getRequest().getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            operLog.setOperParam(StringUtils.substring(paramsMap.toString(), 0, 2000));
        }
    }

    /**
     * 是否存在注解，如果存在就获取
     */
    private Log getAnnotationLog(JoinPoint joinPoint) throws Exception {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            return method.getAnnotation(Log.class);
        }
        return null;
    }

    /**
     * 参数拼装
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (int i = 0; i < paramsArray.length; i++) {
                if (!isFilterObject(paramsArray[i])) {
                    Object jsonObj = JSON.toJSON(paramsArray[i]);
                    params += jsonObj.toString() + " ";
                }
            }
        }
        return params.trim();
    }

    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    public boolean isFilterObject(final Object o) {
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse;
    }
}
