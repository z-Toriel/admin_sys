package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Role;
import com.system.entity.RoleMenu;
import com.system.entity.UserRole;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2022-11-29
 */
@RestController
@RequestMapping("/system/role")
public class RoleController extends BaseController {
    // 获取所有角色信息
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result list(String name){ // name就是搜索栏中内容，有name就模糊查询
        // 查询分页数据用.page();查询出的数据不是list

        Page<Role> roles = roleService.page(getPage(), new QueryWrapper<Role>().like(StrUtil.isNotBlank(name), "name", name));
        return Result.success(roles);
    }

    // 编辑角色：根据角色编号id获得角色信息
    @GetMapping("/info/{id}")
//    @PreAuthorize("hasAuthority()")
    public Result list(@PathVariable Long id){
        // 使用mp封装的方法，通过id获得对象
        Role role = roleService.getById(id);

        // 从关联表sys_role_menu中根据role_id查询匹配的信息
        List<RoleMenu> roleMenu = roleMenuService.list(new QueryWrapper<RoleMenu>().eq("role_id", id));
        // .stream()把一个数据源转换为流操作
        // .map() 将映射每个元素转换到处理结果
        List<Long> menuIds = roleMenu.stream().map(rm -> rm.getMenuId()).collect(Collectors.toList());
        role.setMenuIds(menuIds);
        return Result.success(role);
    }

    // 编辑角色：更新角色
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result update(@RequestBody Role role){
        // 设置角色的跟新时间
        role.setUpdated(LocalDateTime.now());
        roleService.updateById(role);
        return Result.success(role);
    }

    // 新建角色
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:role:save')")
    public Result save(@RequestBody Role role){
        // 设置角色创建的时间为当前时间
        role.setCreated(LocalDateTime.now());
        // 设置新建角色的状态为1，正常可用
        role.setStatu(Const.STATUS_ON);
        roleService.save(role);
        return Result.success(role);
    }

    // 角色管理：根据id删除角色信息 参数：角色id数据，可以单个删除，也可以批量删除
    // 事务的隔离性和事务的传播级别
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @Transactional  //表示数据库的事务
    public Result del(@RequestBody Long[] roleIds){
        // Arryas.asList（数组），将数组转换为List Array是数组的工具类
        roleService.removeByIds(Arrays.asList(roleIds));
        // 同步删除其他的数据
        // 根据role_id删除sys_role_menu同步的数据
        roleMenuService.remove(new QueryWrapper<RoleMenu>().in("role_id",roleIds));
        // 根据role_id角色编号，从sys_user_role删除同步的数据
        userRoleService.remove(new QueryWrapper<UserRole>().in("role_id",roleIds));

        return Result.success(roleIds);
    }

    // 分配权限：保存分配给角色的权限信息 保存sys_role_menu
    // 参数：保存角色编号 roleId 参数：权限菜单的编号menuIds[]
    @PostMapping("/perm/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:perm')")
    @Transactional
    public Result perm(@PathVariable Long roleId,@RequestBody Long[] menuIds){
        // 角色编号3，还有一堆菜单编号
        List<RoleMenu> roleMenuList = new ArrayList<>();
        Arrays.stream(menuIds).forEach(menuId -> {
            RoleMenu new_rm = new RoleMenu();
            new_rm.setMenuId(menuId);
            new_rm.setRoleId(roleId);

            roleMenuList.add(new_rm);
        }); // 角色3 菜单2


        // 如果从现有权限中去掉权限，例如权限2，3，4去掉3，4
        // 从sys_role_menu表中删除，菜单编号为去掉权限的编号3,4
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("role_id",roleId));

        //saveBatch() 批量保存(参数是一个集合)
        roleMenuService.saveBatch(roleMenuList);


        // 清空与该角色相关用户在redis缓存中的权限
        // 根据roleid角色编号，删除该用户的redis权限缓存
        userService.clearUserAuthorityInfoByRoleId(roleId);
        return Result.success(menuIds);
    }
}
