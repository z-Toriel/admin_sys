package com.system.security;

import com.system.common.exception.CaptchaException;
import com.system.common.lang.Const;
import com.system.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 验证码认证的过滤器
@Component //交给spring容器管理
@Slf4j
public class CaptchaFilter extends OncePerRequestFilter {
    // 定义一个路径 /login
    private final String loginUrl = "/login";

    @Autowired
    RedisUtil redisUtil;

    //TODO 声明一个登录失败的处理器
    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1.获取请求路径，看看是否是/login
        String requestURL = request.getRequestURI();
        if(requestURL.equals(loginUrl) && request.getMethod().equals("POST")){
            log.info("获取login的请求，正在校验验证码---"+requestURL);
            try {
                validate(request);
                log.info("验证码正确-----------------------------");
            } catch (CaptchaException e) {
                log.info(e.getMessage());
                // TODO:执行登录的处理器
                loginFailureHandler.onAuthenticationFailure(request,response,e);
            }
        }
        // 跳转至下一个处理器
        filterChain.doFilter(request,response);
    }

    // 声明一个验证的方法：
    private void validate(HttpServletRequest request) throws CaptchaException {
        String key = request.getParameter("key");
        String code = request.getParameter("captchaCode");

        // 如果key或code为空则抛出异常
        if(StringUtils.isBlank(key) || StringUtils.isBlank(code)){
            throw new CaptchaException("验证码不能为空");
        }

        if(!code.equals(redisUtil.hget(Const.CAPTCHA_KEY,key)) ){
            throw new CaptchaException("验证码错误");
        }

        // redis中验证码的一次性的
        redisUtil.hdel(Const.CAPTCHA_KEY,key);

    }
}
