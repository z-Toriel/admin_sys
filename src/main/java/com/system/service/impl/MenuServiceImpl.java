package com.system.service.impl;

import com.system.entity.Menu;
import com.system.entity.User;
import com.system.entity.dto.MenuDto;
import com.system.mapper.MenuMapper;
import com.system.mapper.UserMapper;
import com.system.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Byterain
 * @since 2022-11-29
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;

    @Override
    public List<MenuDto> getCurrentUserNav(String username) {
        User user = userService.getByUsername(username);

        // 通过用户的id获得用户能够操作的菜单
        List<Long> menuIds = userMapper.getNavMenuIds(user.getId());
        // 该用户能够操作的菜单id，通过id查询所有的菜单信息,调用mp中生成的方法
        List<Menu> menus = this.listByIds(menuIds); // 获得所有的菜单对象集合

        // 需要自定义方法：构建子菜单
        List<Menu> new_menu = this.buildChildrenMenu(menus);
        // 调用构建：menuDto集合


        return this.buildTreeMenu(new_menu);

    }

    @Override
    public List<Menu> tableTree() {
        List<Menu> menuList = this.list();
        // 调用我们自定义的方法buildChildrenMenu

        return buildChildrenMenu(menuList);
    }

    // 构建MenuDto菜单
    public List<MenuDto> buildTreeMenu(List<Menu> new_menu) {
        List<MenuDto> tree_menu = new ArrayList<>();
        new_menu.forEach(m -> {
            MenuDto temp = new MenuDto();
            temp.setId(m.getId());
            temp.setName(m.getPerms());
            temp.setTitle(m.getName());
            temp.setComponent(m.getComponent());
            temp.setPath(m.getPath());
            temp.setIcon(m.getIcon());

            if (m.getChildren().size() > 0) {
                // 递归调用，buildTreeMenu将子菜单转换为MenuDto
                temp.setChildren(buildTreeMenu(m.getChildren()));
            }
            tree_menu.add(temp);
        });

        return tree_menu;
    }

    public List<Menu> buildChildrenMenu(List<Menu> menus) {
        List<Menu> new_menu = new ArrayList<>();
        // 循环菜单集合，查找每个菜单下的子菜单
        for (Menu m : menus) {
            for (Menu e : menus) {
                if (m.getId() == e.getParentId()) {
                    m.getChildren().add(e); // 将e存入到m的child中，成为m的子菜单
                }
            }
            // 就是系统菜单，级别最高的
            if (m.getParentId() == 0L) {
                new_menu.add(m);
            }
        }


        return new_menu;
    }
}
