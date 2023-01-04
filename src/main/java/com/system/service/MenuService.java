package com.system.service;

import com.system.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.system.entity.dto.MenuDto;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Byterain
 * @since 2022-11-29
 */
public interface MenuService extends IService<Menu> {
    // 获得用户登录后的菜单信息
    public List<MenuDto> getCurrentUserNav(String username);

    // 获得菜单表格（tree)的数据
    public List<Menu> tableTree();
}
