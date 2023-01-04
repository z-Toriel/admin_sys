package com.system.mapper;

import com.system.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Byterain
 * @since 2022-11-29
 */
public interface UserMapper extends BaseMapper<User> {
    // 通过userid用户编号，查询该用户的权限菜单
    List<Long> getNavMenuIds(Long userId);

    // 用过menuId查找与该查单编号相关联的所有用户
    List<User> getUserListByMenuId(Long menuid);
}
