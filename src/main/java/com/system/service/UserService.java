package com.system.service;

import com.system.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Byterain
 * @since 2022-11-29
 */
public interface UserService extends IService<User> {
    // 获取当前登陆的用户的权限信息的方法
    public String getUserAuthorityInfo(Long userId);

    // 用户退出，清除缓存
    public void clearUserAuthorityInfo(String username);

    // 根据菜单编号，清空redis中用户权限缓存
    public void clearUserAuthorityInfoByMenuId(Long menuId);

    // 根据角色的编号，清除权限缓存
    public void clearUserAuthorityInfoByRoleId(Long roleId);

    // 通过用户名获得该用户对象
    public User getByUsername(String username);
}
