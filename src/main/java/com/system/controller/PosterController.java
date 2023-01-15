package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Poster;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-01-15
 */
@RestController
@RequestMapping("/system/poster")
public class PosterController extends BaseController {
    //   获得菜单的所有数据
    @GetMapping("/list")
//    @PreAuthorize("hasAuthority('sys:poster:list')")
    public Result list(String name){  //name就是搜索栏中的内容，有name就是模糊查询，没有就是全部检索出来
        //用于查询分页的方法 page()   查询出来的数据不是List而是Page<Role>
        //Page<Role>对象  就是带有分页数据的List  查询的角色集合在records中 其他就是分页数据
        Page<Poster> posterPage = posterService.page(getPage(), new QueryWrapper<Poster>()
                .like(StrUtil.isNotBlank(name), "title", name)
                .last("order by created desc ")
        );
        return Result.success(posterPage);
    }
    //通过分类编号id获得该分类的信息
    @GetMapping("/info/{id}")
//    @PreAuthorize("hasAuthority('sys:user:list')")
    public Result info(@PathVariable Long id){
        Poster poster = posterService.getById(id);
        return Result.success(poster);
    }
    //保存分类
    @PostMapping("save")
//    @PreAuthorize("hasAuthority('sys:poster:save')")
    public Result save(@RequestBody Poster poster){
        //设置保存新分类的时间为当前时间
        poster.setCreated(LocalDateTime.now());
        poster.setStatu(Const.STATUS_ON);   //设置分类状态
        posterService.save(poster);
        return Result.success(poster);

    }


    //更新分类
    @PostMapping("/update")
//    @PreAuthorize("hasAuthority('sys:poster:update')")
    public Result update(@RequestBody  Poster poster){
        //更新菜单的更新时间
        poster.setUpdated(LocalDateTime.now());
        posterService.updateById(poster);
        return Result.success(poster);
    }

    //删除分类
    @PostMapping("delete")
//    @PreAuthorize("hasAuthority('sys:poster:delete')")
    public Result update(@RequestBody Long[] ids){
        posterService.removeByIds(Arrays.asList(ids));
        return Result.success("");

    }

}
