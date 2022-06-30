package com.china.alert.framework.security.listener;

import com.china.alert.common.constant.Constants;
import com.china.alert.common.exception.CustomException;
import com.china.alert.common.utils.DateUtils;
import com.china.alert.framework.manager.AsyncManager;
import com.china.alert.project.system.domain.SysDictData;
import com.china.alert.project.system.domain.SysUser;
import com.china.alert.project.system.mapper.SysUserMapper;
import com.china.alert.project.system.service.ISysDictDataService;
import com.china.alert.project.system.service.ISysUserService;
import com.china.alert.framework.manager.factory.AsyncFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * 监听登录失败
 */
@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDictDataService dictDataService;
    @Autowired
    private SysUserMapper userMapper;


    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent) {
        String account = authenticationFailureBadCredentialsEvent.getAuthentication().getPrincipal().toString();
        Date afterDate = null;
        String message = "用户不存在/密码错误";
        SysUser user = userService.selectUserByUserName(account);
        if (user != null) {
            //第一次密码错误
            if(user.getErrAcount() == 0){
                user.setLoginDate(new Date());
            }
            if (user.getLoginDate() != null){
                afterDate = new Date(user.getLoginDate().getTime() + 300000);
            }else {
                afterDate = new Date(new Date().getTime() + 300000);
            }
            //超出5分钟
            if (DateUtils.getDateDiff(afterDate,new Date())){
                // 获取用户失败次数及配置失败次数
                //int err_acount = user.getErrAcount() == null ? 1 : user.getErrAcount();
                user.setErrAcount(user.getErrAcount()+1);
                //获取系统配置的次数
                List<SysDictData> list = dictDataService.selectDictDataByType("sys_login_fail_acount");
                int sys_acount = Integer.parseInt(list.get(0).getDictValue());
                // 超出失败次数，停用账户
                if (user.getErrAcount() == sys_acount){
                    System.out.println("开始上锁");
                    user.setStatus("3");
                    user.setLockTime(new Date());
                    //message = "对不起，您的账号："+user.getUserName()+" 已锁定";
                }
            }
            //userService.updateUser(user);
            userMapper.updateUser(user);
        }
        if(user.getStatus().equals("3")){
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(account, Constants.WARNING, message));
        }else {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(account, Constants.LOGIN_FAIL, message));
        }
        throw new CustomException(message);
    }
}
