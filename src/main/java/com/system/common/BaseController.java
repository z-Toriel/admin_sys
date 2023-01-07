package com.system.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.service.*;
import com.system.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseController {
//每个控制器用到的对象
    @Autowired
    public RedisUtil redisUtil;
    @Autowired
    public HttpServletRequest request;
    @Autowired
    public UserService userService;
    @Autowired
    public MenuService menuService;
    @Autowired
    public RoleService roleService; //角色业务对象
    @Autowired //role和menu关联表对象
    public RoleMenuService roleMenuService;
    @Autowired
    public UserRoleService userRoleService;
    @Autowired
    public BooksService booksService;
    @Autowired
    public CategoryService categoryService;
    @Autowired
    public FansService fansService;
    @Autowired
    public BooksEvaluateService booksEvaluateService;

    // 获得分页数据 前端请求的时候会传递参数：current,size,得到参数封装给page对象（mp分页使用）
    public Page getPage(){
        // 得到分页的当前页码
        int current = ServletRequestUtils.getIntParameter(request, "current", 1);
        int size = ServletRequestUtils.getIntParameter(request, "size", 5);
        return new Page(current,size);
    }
}
