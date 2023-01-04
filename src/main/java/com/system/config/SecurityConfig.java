package com.system.config;

import com.system.security.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Slf4j
// Spring Security核心配置类，所有定义过滤器、处理器都在这里配置执行
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true) // 方法级别的权限的认证
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // 用来加密密码的对象
    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        return  jwtAuthenticationFilter;
    }
    // 声明需要配置过滤器和处理器对象
    @Autowired
    CaptchaFilter captchaFilter;
    @Autowired
    LoginFailureHandler loginFailureHandler;
    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;    //jwt验证失败的处理器
    @Autowired
    JwtAccessDeniedHandler jwtAccessDeniedHandler;  // 权限不够异常的处理器
    @Autowired
    LogoutSuccessHandler logoutSuccessHandler;

    // 白名单
    public static final String[] URL_WHITE_LIST = {
            "/login",
            "/logout",
            "/captcha",
            "/upload/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        log.info("bug--------------------------------------------------------------------------");
//        // 关闭跨域和csrf防护
//        http.cors().and().csrf().disable()
//                // 登录的配置
//            .formLogin()
//            .failureHandler(loginFailureHandler) // 配置登录失败的处理器对象
//            .successHandler(loginSuccessHandler) // 配置登录成功的处理器对象
//
//            .and()
//            .authorizeRequests() //对所有请求url进行验证
//            .antMatchers(URL_WHITE_LIST)
//            .permitAll() //对所有人应用这些规则
//            .anyRequest() //表示匹配任意url请求
//            .authenticated() // 表示所有匹配到的Url需要被认证才能访问
//
//            // 禁用session
//                /**
//                 SessionCreationPolicy.STATELESS 永远不会创建httpsession
//                 SessionCreationPolicy.ALWAYS 总是创建session
//                 SessionCreationPolicy.IF_REQUIRED 只会在需要的时候创建一个session
//                 SessionCreationPolicy.NEVER 不会创建session，但如果已经存在，那么可以使用
//                **/
//            .and()
//            .sessionManagement()
//            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//
//            // 配置自定义的过滤器
//            .and()
//            .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);
//            // captchaFilter验证码过滤器必须在UsernamePasswordAuthenticationFilter验证登录密码之前执行
//        log.info("bug1--------------------------------------------------------------------------");

        //关闭跨域和CSRF防护


        http.cors().and().csrf().disable()
                .formLogin()
                .failureHandler(loginFailureHandler)//配置登陆失败的处理器对象
                .successHandler(loginSuccessHandler)//配置登陆成功的处理器对象

                // 登出操作配置
                .and()
                .logout()
                .logoutSuccessHandler(logoutSuccessHandler)

//配置拦截规则
                .and()
                .authorizeRequests() //对所有的请求URL进行权限验证
                .antMatchers(URL_WHITE_LIST) //请求放行的规则设置，采用白名单
                .permitAll() //对所有人应用这些规则
                .anyRequest() //表示匹配任意URL请求
                .authenticated() //表示所有匹配到的URL需要被认证才能访问

//禁用session
                /*
                 * SessionCreationPolicy.STATELESS --- 永远不会创建HttpSession对象
                 * SessionCreationPolicy.ALWAYS --- 总是创建Session
                 * SessionCreationPolicy.IF_REQUIRED --- 只会在需要的时候创建一个session
                 * SessionCreationPolicy.NEVER --- 不会创建session，但是如果session已经存在，那么可以使用
                 * */
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 配置异常的处理器
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint) // sc会捕获authenticationException，使用对应的jwt
                .accessDeniedHandler(jwtAccessDeniedHandler)

                //配置自定义的过滤器
                .and()
                .addFilter(jwtAuthenticationFilter())
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class);
        //captchaFilter 验证码过滤器必须在UsernamePasswordAuthenticationFilter验证登录前配置
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
