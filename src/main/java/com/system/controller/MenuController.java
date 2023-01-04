package com.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.common.Result;
import com.system.entity.Menu;
import com.system.entity.RoleMenu;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
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
@RequestMapping("/system/menu")
public class MenuController extends BaseController {

    // 获得所有的菜单数据
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result list(){
        List<Menu> menus = menuService.tableTree();
        return Result.success(menus);
    }


    // 编辑菜单-通过菜单编号获得菜单详细信息数据
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result info(@PathVariable Long id){
        // 调用mp生成的方法，根据id查询单个
        return Result.success(menuService.getById(id));
    }

    // 保存新菜单
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result save(@RequestBody Menu menu){
        menu.setCreated(LocalDateTime.now()); //设置新增的菜单添加时间
        menuService.save(menu);     // save()保存新菜单
        return Result.success(menu);
    }

    // 更新菜单
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result update(@RequestBody Menu menu){
        menu.setUpdated(LocalDateTime.now());
        menuService.updateById(menu);
        //  需要清除与该菜单相关的权限缓存
        userService.clearUserAuthorityInfoByMenuId(menu.getId());
        return Result.success(menu);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result del(@PathVariable Long id){
        // 通过id查询产出的菜单，有多少子菜单
        int count = menuService.count(new QueryWrapper<Menu>().eq("parent_id", id));
        if(count>0){
            return Result.fail("请先删除子菜单");
        }

        //  清除与该菜单关联的权限
        userService.clearUserAuthorityInfoByMenuId(id);
        // 删除菜单
        menuService.removeById(id);

        //  如果菜单删除，中间表sys_role_menu数据也要同步删除
        // 删除sys_role_meun表中相关的数据
        roleMenuService.remove(new QueryWrapper<RoleMenu>().eq("menu_id",id));

        return Result.success("");
    }
}
