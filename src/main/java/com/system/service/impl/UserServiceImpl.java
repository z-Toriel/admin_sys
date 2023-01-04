package com.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.entity.Menu;
import com.system.entity.Role;
import com.system.entity.User;
import com.system.mapper.UserMapper;
import com.system.service.MenuService;
import com.system.service.RoleService;
import com.system.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Byterain
 * @since 2022-11-29
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    RoleService roleService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    MenuService menuService; // 菜单的业务对象

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String getUserAuthorityInfo(Long userId) {
        // 生成权限列表信息：
        // ROLE_admin,ROLE_nomal,sys:user:list,sys:role:list
        String authority = ""; //存储权限列表字符串
        User user = userMapper.selectById(userId);
        if (redisUtil.hasKey(user.getUsername())) {   //如果redis中存在，无需查询直接读取
            authority = (String) redisUtil.get(user.getUsername());
        } else {
            // 获得当前登录用户的所有角色对象
            List<Role> roles = roleService.listRolesByUserId(userId);

            if (roles.size() > 0) { //如果查询出的角色集合个数大于0，代表有角色信息
                String rolecods = roles.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
                // ROLE_admin,ROLE_test

                rolecods = rolecods.concat(","); //concat()拼接在末尾，最后是ROLE_admin,ROLE_test,
                authority = authority.concat(rolecods);
                log.info("角色权限：" + rolecods);

            }

            // 生成权限信息:sys:manage,sys:user:list,
            List<Long> MenuIds = userMapper.getNavMenuIds(userId); // 根据用户编号userid查询该用户能操作的id
            if (MenuIds.size() > 0) {   //如果不是空
                // 通过菜单的编号menuid,查询菜单对象的详细信息
                List<Menu> menuslist = menuService.listByIds(MenuIds);
                // 将menusList中的数据取出，拼接成字符串:sys:manage,sys:role:list
                String menuPerms = menuslist.stream().map(m -> m.getPerms()).collect(Collectors.joining(","));
                // 将权限的列表和角色的列表连接起来
                authority = authority.concat(menuPerms);
                log.info("角色和权限的列表字符串最终结果:" + authority);
            }

        }


        // 座一层缓存，将角色和权限存储到redis中
        redisUtil.set(user.getUsername(), authority); // 存储到redis中
        return authority;
    }

    @Override
    public void clearUserAuthorityInfo(String username) {
        redisUtil.del(username);
    }


    // 根据菜单编号，找到与该菜单关联的用户名，通过用户名清空相关用户的权限缓存
    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId) {
        List<User> users = userMapper.getUserListByMenuId(menuId);
        users.forEach(u->{
            this.clearUserAuthorityInfo(u.getUsername());
        });
    }

    // 角色管理：如果角色删除，根据删除的角色的编号roleId，查找赋值了该角色的用户，并清除这些权限缓存
    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId) {
        // 查找和角色编号roleID相关的用户信息
        List<User> users = this.list(new QueryWrapper<User>().inSql("id", "select user_id from sys_user_role where role_id=" + roleId));
        users.forEach(u->{
            this.clearUserAuthorityInfo(u.getUsername());
        });
    }



    @Override
    public User getByUsername(String username) {
        return this.getOne(new QueryWrapper<User>().eq("username", username));
    }
}
