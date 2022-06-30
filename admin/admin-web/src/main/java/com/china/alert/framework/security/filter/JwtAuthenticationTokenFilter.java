package com.china.alert.framework.security.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.china.alert.common.constant.HttpStatus;
import com.china.alert.common.utils.SecurityUtils;
import com.china.alert.common.utils.ServletUtils;
import com.china.alert.common.utils.StringUtils;
import com.china.alert.framework.security.LoginUser;
import com.china.alert.framework.security.service.TokenService;
import com.china.alert.framework.web.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * token过滤器 验证token有效性
 *
 * @author jz
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        //使用过滤器设置Access-Control-Allow-Origin解决跨域请求
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,Content-Type,Access-Token");
        response.setHeader("Access-Control-Expose-Headers", "*");
        response.setContentType("text/plain;charset=utf-8");
        response.setCharacterEncoding("utf-8");

        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication())) {
            //tokenService.verifyToken(loginUser);
            //验证token令牌是否过期
            long expireTime = loginUser.getExpireTime();
            long currentTime = System.currentTimeMillis();
            if (expireTime - currentTime > 0) {
                //刷新令牌
                tokenService.refreshToken(loginUser);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else{
                //令牌过期则进入注销程序
                // 删除用户缓存记录
                tokenService.delLoginUser(loginUser.getToken());
                ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(HttpStatus.SUCCESS, "退出成功")));
            }
        }
        chain.doFilter(request, response);
    }
}
