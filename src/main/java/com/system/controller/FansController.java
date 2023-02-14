package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.common.lang.Const;
import com.system.entity.Category;
import com.system.entity.Fans;
import com.system.entity.User;
import com.system.mapper.FansMapper;
import com.system.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
 * @since 2022-12-08
 */
@RestController
@RequestMapping("/system/fans")
public class FansController extends BaseController {
    //SC加密解密的工具
    @Autowired
    BCryptPasswordEncoder passwordEncoder;


    @GetMapping("/list")
    public Result list(String name) {
        Page<Fans> fansList = fansService.page(getPage(), new QueryWrapper<Fans>().like(StrUtil.isNotBlank(name), "username", name));
        return Result.success(fansList);
    }


    @PostMapping("/save")
    public Result save(@RequestBody Fans fans){
        // 1.先从数据库里删除该用户的id，
        Fans fansById = fansService.getById(fans.getId());
        if(fansById==null){ // 2.如果没有找到，则创建
            //设置保存新用户的时间为当前时间
            fans.setCreated(LocalDateTime.now());
            fans.setUpdated(LocalDateTime.now());
            fans.setPassword(passwordEncoder.encode(Const.DEFAULT_PWD));
            fans.setStatu(Const.STATUS_ON);   //设置用户 状态
            fans.setDeltag(Const.STATUS_ON);    //设置用户的逻辑删除
            fans.setRemainBorrowNumber(Const.DEFAULT_BORROW_NUMBER); // 设置用户的借阅数量
            fansService.save(fans);
            return Result.success(fans);
        }else { //3如果存在则返回失败
            return Result.fail("用户已存在，创建失败");
        }
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable Long id){
        Fans fans = fansService.getById(id);
        //查询用户所具有的角色
        return Result.success(fans);
    }

    //更新用户
    @PostMapping("/update")
    public Result update(@RequestBody Fans fans){
        //更新用户的更新时间
        fans.setUpdated(LocalDateTime.now());
        fansService.update(fans, new QueryWrapper<Fans>().eq("id",fans.getId()));
        return Result.success(fans);
    }

    @PostMapping("delete")
    public Result delete(@RequestBody Long[] ids){
        boolean b = fansService.removeByIds(Arrays.asList(ids));
        if(b) {
            return Result.success(ids);
        }else{
            return Result.fail("删除失败");
        }
    }

    @PostMapping("/repass/{id}")
    public Result repass(@PathVariable Long id){
        Fans fans = fansService.getById(id);
        fans.setPassword(passwordEncoder.encode(Const.DEFAULT_PWD)); //将用户的密码设置为初始的123,并加密
        fans.setUpdated(LocalDateTime.now());
        fansService.updateById(fans);
        return Result.success("初始密码成功");
    }
}
