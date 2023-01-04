package com.system.security;

import cn.hutool.json.JSONUtil;
import com.system.common.Result;
import com.system.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 只要请求地址中包含/logout,自动springsecurity执行退出的操作（清空登录时封装用户信息对象）,并且操作成功后执行logoutSuccessSystemHandler中onLogoutSuccess

@Component
public class LogoutSuccessSystemHandler implements LogoutSuccessHandler {
    @Autowired
    JwtUtils jwtUtils;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 理论上到该方法退出的操作已经执行成功，authentication是null
        // 如果不是空，
        if(authentication!=null){
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }
        response.setContentType("application/json;charset=utf-8");
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 响应状态码401
        response.setHeader(jwtUtils.getHeader(),"");
        ServletOutputStream out = response.getOutputStream();

        Result result = Result.success("登出成功");
        out.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        out.flush();
        out.close();
    }
}
