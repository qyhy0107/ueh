package com.china.alert.framework.security.service;

import com.china.alert.common.enums.UserStatus;
import com.china.alert.common.exception.BaseException;
import com.china.alert.common.utils.StringUtils;
import com.china.alert.project.system.domain.SysDictData;
import com.china.alert.project.system.domain.SysUser;
import com.china.alert.project.system.service.ISysDictDataService;
import com.china.alert.project.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.china.alert.framework.security.LoginUser;

import java.util.List;

/**
 * 用户验证处理
 *
 * @author jz
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysDictDataService dictDataService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        SysUser user = userService.selectUserByUserName(username);
        List<SysDictData> list = dictDataService.selectDictDataByType("sys_login_fail_acount");
        int sys_acount = Integer.parseInt(list.get(0).getDictValue());
        if (StringUtils.isNull(user))
        {
            log.info("登录用户：{} 不存在.", username);
            throw new BaseException("登录用户：" + username + " 不存在");
        }
        else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            log.info("登录用户：{} 已被删除.", username);
            throw new BaseException("对不起，您的账号：" + username + " 已被删除");
        }else if (user.getErrAcount().equals(sys_acount)){
            throw new BaseException("对不起，您的账号：" + username + " 已锁定");
        }
        else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            log.info("登录用户：{} 已被停用.", username);
            throw new BaseException("用户名/密码错误");
            //throw new BaseException("对不起，您的账号：" + username + " 已停用");
        }else if (user.getStatus().equals(UserStatus.LOCK.getCode())){
            log.info("登录用户：{} 已被锁定.", username);
            throw new BaseException("对不起，您的账号：" + username + " 已锁定");
        }

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user)
    {
        return new LoginUser(user, permissionService.getMenuPermission(user));
    }
}
