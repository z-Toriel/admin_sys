package com.system.security;

import cn.hutool.json.JSONUtil;
import com.system.common.Result;
import com.system.service.UserService;
import com.system.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
//        response.setContentType("application/json;charset=utf-8");
//        log.info("成功拦截器---------------");
//        ServletOutputStream outputStream = response.getOutputStream();
//
//        // 先获得用户名
//        // authentication就是springSecurity封装登录用户信息,getName()获得登录输入的用户名
//        String username = authentication.getName();
//        String jwt = jwtUtils.generateToken(username);
//
//
//        // 在响应头中，设置返回的token
//        response.setHeader(jwtUtils.getHeader(),jwt);
//        // TODO:通过username获得当前用户的详细信息。需要在UserService查询
//        Result result = Result.success(null); // 返回的是查询出的user对象
//        log.info("============================================"+result.toString());
//        // 将result对象转为json写回vue
//        outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
//        outputStream.flush();
//        outputStream.close()
//    }



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        ServletOutputStream outputStream = response.getOutputStream();

        // 先获得用户名
        // authentication就是springSecurity封装登录用户信息,getName()获得登录输入的用户名
        String username = authentication.getName();
        String jwt = jwtUtils.generateToken(username);


        // 在响应头中，设置返回的token
        response.setHeader(jwtUtils.getHeader(),jwt);
        // TODO:通过username获得当前用户的详细信息。需要在UserService查询
        Result result = Result.success(null); // 返回的是查询出的user对象
        log.info("============================================"+result.toString());
        // 将result对象转为json写回vue
        outputStream.write(JSONUtil.toJsonStr(result).getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }



}
