package com.system.controller;

import cn.hutool.core.map.MapUtil;
import com.system.common.Result;
import com.system.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/pass/{pwd}")
    public Result passEncoder(@PathVariable String pwd){
        // 加密后的密码
        String pass = bCryptPasswordEncoder.encode(pwd);
        log.info("加密后的密码：{}",pass);

        // 密码验证
        boolean b = bCryptPasswordEncoder.matches(pwd, pass);

        return Result.success(
                MapUtil.builder()
                .put("pass",pass)
                .put("b",b).build()
        );
    }
}
