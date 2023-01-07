package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.entity.Books;
import com.system.entity.BooksEvaluate;
import com.system.entity.Fans;
import com.system.service.BooksEvaluateService;
import com.system.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-01-07
 */
@RestController
@RequestMapping("/system/booksEvaluate")
public class BooksEvaluateController extends BaseController {

    @GetMapping("/list")
    public Result list(String comment,String uname,String bname){
        List<Long> uids = new ArrayList<>();    //用于保存根据username模糊查询出来的uids
        List<Long> bids = new ArrayList<>();    //用于保存根据bookname模糊查询出来的bids

        // 获取到所有的Fans对象
        List<Fans> userListByUname = fansService.list(new QueryWrapper<Fans>().like(StrUtil.isNotBlank(uname), "username", uname));
        // 将所有的Fans对象中的id拿出来
        userListByUname.forEach(fans -> {
            Long uid = fans.getId();
            uids.add(uid);
        });

        // 获取到所有的Books对象
        List<Books> BooksListByBname = booksService.list(new QueryWrapper<Books>().like(StrUtil.isNotBlank(bname), "name", bname));
        // 将所有的Fans对象中的id拿出来
        BooksListByBname.forEach(book -> {
            Long bid = book.getId();
            bids.add(bid);
        });



        Page<BooksEvaluate> booksEvaluateList = booksEvaluateService.page(getPage(), new QueryWrapper<BooksEvaluate>()
                .like(StrUtil.isNotBlank(comment), "comment", comment)
                .in(uids.size()>0, "uid", uids)
                .in(bids.size()>0, "bid", bids)
        );

        booksEvaluateList.getRecords().forEach(item->{
            // 1.根据uid查到fans对象，获取uname
            Fans fansById = fansService.getById(item.getUid());
            String username = fansById.getUsername();
            item.setUname(username);

            // 2.根据bid查找books对象，获取bname
            Books booksById = booksService.getById(item.getBid());
            String bookName = booksById.getName();
            item.setBname(bookName);
        });


        return Result.success(booksEvaluateList);
    }


    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        boolean b = booksEvaluateService.removeByIds(Arrays.asList(ids));
        if (b){
            return Result.success(ids);
        }else{
            return Result.fail("删除失败");
        }
    }
}
