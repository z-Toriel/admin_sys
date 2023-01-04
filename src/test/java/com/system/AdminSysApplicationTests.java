package com.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.system.entity.Role;
import com.system.entity.User;
import com.system.mapper.UserMapper;
import com.system.service.RoleService;
import com.system.service.UserService;
import com.system.util.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class AdminSysApplicationTests {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    RoleService roleService; // 角色的业务对象

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void contextLoads() {

        // 测试userService中的权限代码
        userService.getUserAuthorityInfo(1L);
        // insql相当于sql语句中的in子查询结果
//        List<Role> roles = roleService.list(new QueryWrapper<Role>().inSql("id", "select role_id from sys_user_role where user_id = " + 1));
//        System.out.println(roles);

        // 产生新的字符串
//        List num = Arrays.asList(1,2,3,4,5); // 下面的代码是产生新的字符串1a,2a,3a
//        Object str = num.stream().map(n -> n + "a").collect(Collectors.joining(","));
//        System.out.println(str);


        // 测试加密
//        String pwd = "123";
//        String pass = bCryptPasswordEncoder.encode(pwd);    //加密
//        System.out.println("pass=="+pass);
//        boolean b = bCryptPasswordEncoder.matches(pwd, pass); //测试密文
//        System.out.println("b="+b);


        // 测试根据username查询
//        User one = userService.getOne(new QueryWrapper<User>().eq("username", "admin"));
//        System.out.println(one);
//        System.out.println(jwtUtils.generateToken("admin"));
        // 1.插入一个用户
//        User user = new User();
//        user.setUsername("gen");
//        user.setPassword("123");
//        user.setAvatar("https://img1.baidu.com/it/u=1811445190,4171898561&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=500");
//        user.setStatu(1);
//        userMapper.insert(user);

        // 2.根据id修改一个用户
//        User user = new User();
//        user.setId(6L);
//        user.setPassword("666666");
//        int update = userMapper.updateById(user);

        // 3.根据条件修改一个用户
//        User user = new User();
//        user.setPassword("22222");
//        userMapper.update(user,new UpdateWrapper<User>().ge("id",3));


//        User user = new User();
//        user.setUsername("root");
//        user.setPassword("8848");
//        userService.save(user);
//
//        List<User> list = new ArrayList<>();
//        list.add(user);
//        userService.saveBatch(list); // 批量保存


//        // 根据id查询
//        User user = userMapper.selectById(3L);
//        System.out.println(user);

        // 根据一组id查询
//        ArrayList list = new ArrayList();
//        list.add(2);
//        list.add(3);
//        List<User> users = userMapper.selectBatchIds(list);
//        System.out.println(users);

        // 根据条件查询
//        List<User> ids = userMapper.selectList(new QueryWrapper<User>().gt("id", 1));
//        System.out.println(ids);




    }


}
