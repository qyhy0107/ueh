package com.china.alert.framework.manager.factory;

import java.util.TimerTask;

import com.china.alert.common.constant.Constants;
import com.china.alert.common.utils.LogUtils;
import com.china.alert.common.utils.ServletUtils;
import com.china.alert.common.utils.ip.AddressUtils;
import com.china.alert.common.utils.ip.IpUtils;
import com.china.alert.common.utils.spring.SpringUtils;
import com.china.alert.framework.aspectj.lang.enums.BusinessType;
import com.china.alert.project.monitor.domain.SysOperLog;
import com.china.alert.project.monitor.service.ISysOperLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * 异步工厂（产生任务用）
 *
 * @author jz
 */
public class AsyncFactory {
    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * 记录登陆信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息
     * @param args     列表
     * @return 任务task
     */
    public static TimerTask recordLogininfor(final String username, final String status, final String message,
                                             final Object... args) {
        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = IpUtils.getIpAddr(ServletUtils.getRequest());
        String method = ServletUtils.getRequest().getMethod();
        String url = ServletUtils.getRequest().getRequestURI();
        return new TimerTask() {
            @Override
            public void run() {
                String address = AddressUtils.getRealAddressByIP(ip);
                StringBuilder s = new StringBuilder();
                s.append(LogUtils.getBlock(ip));
                s.append(address);
                s.append(LogUtils.getBlock(username));
                s.append(LogUtils.getBlock(status));
                s.append(LogUtils.getBlock(message));
                // 打印信息到日志
                sys_user_logger.info(s.toString(), args);
                // 获取客户端操作系统
                String os = userAgent.getOperatingSystem().getName();
                // 获取客户端浏览器
                String browser = userAgent.getBrowser().getName();
                // 封装对象
                SysOperLog sysOperLog = new SysOperLog();
                sysOperLog.setOperName(username);
                sysOperLog.setErrorMsg(message);
                sysOperLog.setTitle("登录模块");
                sysOperLog.setOperIp(ip);
                sysOperLog.setRequestMethod(method);
                sysOperLog.setOperUrl(url);
                sysOperLog.setOperLocation(address);
                // 日志状态
                if (Constants.LOGIN_SUCCESS.equals(status)) {
                    sysOperLog.setBusinessType(BusinessType.LOGIN_SUCCESS.ordinal());
                    sysOperLog.setStatus(Integer.parseInt(Constants.SUCCESS));
                    sysOperLog.setSubtitle("系统登录");
                } else if (Constants.LOGIN_FAIL.equals(status)) {
                    sysOperLog.setOperatorType(1);
                    sysOperLog.setBusinessType(BusinessType.LOGIN_FAILD.ordinal());
                    sysOperLog.setSubtitle("登录失败");
                    sysOperLog.setStatus(Integer.parseInt(Constants.FAIL));
                } else if (Constants.LOGOUT.equals(status)){
                    sysOperLog.setBusinessType(BusinessType.LOGOUT.ordinal());
                    sysOperLog.setStatus(Integer.parseInt(Constants.SUCCESS));
                    sysOperLog.setSubtitle("系统注销");
                }else if (Constants.WARNING.equals(status)){
                    sysOperLog.setOperatorType(1);
                    sysOperLog.setBusinessType(BusinessType.LOGIN_FAILD_LOCK.ordinal());
                    sysOperLog.setStatus(Integer.parseInt(Constants.WARNING));
                    sysOperLog.setSubtitle("账号锁定");
                }
                // 插入数据
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(sysOperLog);
            }
        };
    }

    /**
     * 操作日志记录
     *
     * @param operLog 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final SysOperLog operLog) {
        return new TimerTask() {
            @Override
            public void run() {
                // 远程查询操作地点
                operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
                SpringUtils.getBean(ISysOperLogService.class).insertOperlog(operLog);
            }
        };
    }
}
