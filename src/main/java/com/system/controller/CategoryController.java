package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Category;
import com.system.entity.User;
import com.system.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2022-12-07
 */
@RestController
@RequestMapping("/system/category")
public class CategoryController extends BaseController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/list")
    public Result list(String name){
        Page<Category> categoryList = categoryService.page(getPage(), new QueryWrapper<Category>().like(StrUtil.isNotBlank(name), "name", name));
        return Result.success(categoryList);
    }

    @GetMapping("/info/{id}")
    public Result list(@PathVariable Long id){
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Category category){
        category.setCreated(LocalDateTime.now());
        category.setStatu(Const.STATUS_ON);
        categoryService.save(category);
        return Result.success(category);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Category category){
        categoryService.updateById(category);
        return Result.success(category);
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] categoryIds){
        categoryService.removeByIds(Arrays.asList(categoryIds));
        return Result.success(categoryIds);
    }
}

