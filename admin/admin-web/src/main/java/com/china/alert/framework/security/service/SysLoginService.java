package com.china.alert.framework.security.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.china.alert.common.constant.Constants;
import com.china.alert.common.exception.CustomException;
import com.china.alert.common.exception.user.UserPasswordNotMatchException;
import com.china.alert.common.utils.MessageUtils;
import com.china.alert.framework.manager.AsyncManager;
import com.china.alert.framework.manager.factory.AsyncFactory;
import com.china.alert.framework.security.LoginUser;
import com.china.alert.project.system.domain.SysUser;
import com.china.alert.project.system.mapper.SysUserMapper;
import com.china.alert.project.system.mapper.SysUserRoleMapper;
import com.china.alert.project.system.service.ISysUserService;

/**
 * 登录校验方法
 *
 * @author jz
 */
@Component
public class SysLoginService {
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    /**
     * 登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @return 结果
     */
    public String login(String username, String password, String isDomain) {
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            } else if (e instanceof InternalAuthenticationServiceException) {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new CustomException(e.getMessage());
            } else {
                if (e.getMessage().equals("对不起，您的账号：" + username + " 已锁定")) {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.WARNING, e.getMessage()));
                } else {
                    AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                }
                throw new CustomException(e.getMessage());
            }
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        //维护用户登录参数
        SysUser user = userService.selectUserByUserName(username);
        user.setErrAcount(0);
        user.setLoginDate(new Date());
        //userService.updateUser(user);
        userMapper.updateUser(user);

        // 生成token
        return tokenService.createToken(loginUser);
    }
}
