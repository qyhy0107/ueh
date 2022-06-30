package com.china.alert.project.system.controller;

import java.util.List;
import java.util.Set;

import com.china.alert.common.constant.Constants;
import com.china.alert.common.utils.ServletUtils;
import com.china.alert.project.system.domain.SysMenu;
import com.china.alert.project.system.domain.SysUser;
import com.china.alert.project.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.china.alert.framework.security.LoginUser;
import com.china.alert.framework.security.service.SysLoginService;
import com.china.alert.framework.security.service.SysPermissionService;
import com.china.alert.framework.security.service.TokenService;
import com.china.alert.framework.web.domain.AjaxResult;

/**
 * 登录验证
 *
 * @author jz
 */
@RestController
public class SysLoginController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(String username, String password,String isDomain) {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(username, password,isDomain);
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        // 用户信息
        SysUser user = loginUser.getUser();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(user.getUserId());
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
