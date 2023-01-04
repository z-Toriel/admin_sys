package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Role;
import com.system.entity.User;
import com.system.entity.UserRole;
import com.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2022-11-29
 */
@RestController
@RequestMapping("/system/user")
public class UserController extends BaseController {
    /**
     * @PreAuthorize 方法执行之前进行权限检查
     * @PostAuthorize 之后进行检查
     *
     * @PreAuthorize(hasRole("admin")) 该方法必须具有admin的角色权限才能访问
     * @PreAuthorize(hasAuthority("sys:user:list"))
     * */
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    // 获得所有的用户信息
    @GetMapping("list")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result list(String name){
        Page<User> pageUsers = userService.page(getPage(), new QueryWrapper<User>().like(StrUtil.isNotBlank(name), "username", name));

        pageUsers.getRecords().forEach(user->{
            // 查询每个用户所具备角色都西昂信息
            user.setRoles(roleService.listRolesByUserId(user.getId()));
        });

        return Result.success(pageUsers);
    }

    //分配角色和编辑用户 通过用户编号id获得用户的信息（该用户所具有的角色）
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result info(@PathVariable Long id){
        User user = userService.getById(id);
        List<Role> roles = roleService.listRolesByUserId(user.getId());
        user.setRoles(roles);
        return Result.success(user);
    }

    // 保存用户
    @PostMapping("/save")
//    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result save(@RequestBody User user){
        // 设置保存新用户的时间
        user.setCreated(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(Const.DEFAULT_PWD));// 设置初始密码
        user.setStatu(Const.STATUS_ON); // 设置新建用户的状态
//        user.setAvatar(Const.DEFAULT_AVATAR);

        userService.save(user);
        return Result.success(user);
    }

    // 跟新用户
    @PostMapping("/update")
//    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result update(@RequestBody User user){
        // 设置保存新用户的时间
        user.setUpdated(LocalDateTime.now());
        userService.updateById(user);
        return Result.success(user);
    }

    // 删除用户
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    @Transactional
    public Result del(@RequestBody Long[] ids){
        userService.removeByIds(Arrays.asList(ids));
        // 同步相关数据，用sys_user_role关联表去掉相关数据
        userRoleService.remove(new QueryWrapper<UserRole>().in("user_id", ids));
        return Result.success("");

    }

    // 分配权限
    @PostMapping("/role/{userId}")
    @PreAuthorize("hasAuthority('sys:user:role')")
    public Result rolePerm(@PathVariable Long userId,@RequestBody Long[] roleIds){
        // 创建一个存储user_role管理表的数据对象集合
        List<UserRole> userRoleList = new ArrayList<>();

        Arrays.stream(roleIds).forEach(role -> {
            UserRole userRole = new UserRole();
            userRole.setRoleId(role);
            userRole.setUserId(userId);

            userRoleList.add(userRole);
        });

        // 从sys_user_role关联表中先清空与该用户相关的角色
        userRoleService.remove(new QueryWrapper<UserRole>().eq("user_id", userId));
        // 再按照上面userRoleList重新录入进去
        userRoleService.saveBatch(userRoleList);

        // 删除缓存
        User user = userService.getById(userId);
        userService.clearUserAuthorityInfo(user.getUsername()); //按照用户名清空权限对象
        return Result.success("");
    }


    @PostMapping("/repass/{id}")
    public Result repass(@PathVariable Long id){
        User user = userService.getById(id);
        user.setPassword(passwordEncoder.encode(Const.DEFAULT_PWD)); //将用户的密码设置为初始的123,并加密
        user.setUpdated(LocalDateTime.now());
        userService.updateById(user);

        return Result.success("");
    }


}
