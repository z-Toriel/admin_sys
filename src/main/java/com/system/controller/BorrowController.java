package com.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.entity.Borrow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.system.common.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-01-08
 */
@RestController
@RequestMapping("/system/borrow")
public class BorrowController extends BaseController {
    @GetMapping("/list")
    public Result list(){
        Page borrowList = borrowService.page(getPage());
        return Result.success(borrowList);
    }
}
