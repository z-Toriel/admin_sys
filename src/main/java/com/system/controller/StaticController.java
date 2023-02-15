package com.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.system.common.BaseController;
import com.system.common.Result;
import com.system.entity.Borrow;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.reflect.generics.tree.Tree;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

@RestController
@RequestMapping("/static")
public class StaticController extends BaseController {
    // 获取读者数量
    @GetMapping("fansNum")
    public Result getFansNum(){
        int count = fansService.count();
        return Result.success(count);
    }

    // 获取工作人员数量
    @GetMapping("workerNum")
    public Result getWorkerNum(){
        int count = userService.count();
        return Result.success(count);
    }

    // 获取图书数量
    @GetMapping("booksNum")
    public Result getbooksNum(){
        int count = booksService.count();
        return Result.success(count);
    }

    // 获取借阅数量
    @GetMapping("borrowNum")
    public Result getBorrowNum(){
        int count = borrowService.count();
        return Result.success(count);
    }

    // 获取每日的借阅数量，用于折线图
    @GetMapping("dayilyBorrow")
    public Result getDayilyBorrow(){
        ArrayList<String> dataList = new ArrayList<>();     // 用于保存日期
        ArrayList<Integer> borrowNum = new ArrayList<>();   // 用于保存每日的借阅数量
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 根据借阅表的创建日期降序排序获取列表
        List<Borrow> borrowsDesc = borrowService.list(new QueryWrapper<Borrow>().last("order by borrow_date desc"));

        // 创建一个TreeSet用于去重
        TreeSet<String> borrowsDateDescSet = new TreeSet<>();

        borrowsDesc.forEach(item -> {   // 循环列表，获取到日期，格式化后放到set中去重
            LocalDate borrowDate = item.getBorrowDate();
            String format = formatter.format(borrowDate);
            borrowsDateDescSet.add(format);
        });

        borrowsDateDescSet.forEach(item -> {
//            int created = borrowService.count(new QueryWrapper<Borrow>().ge("created", item).le("created",item + " 23:59:59"));  // 根据日期取出每天借阅的数量
            int created = borrowService.count(new QueryWrapper<Borrow>().eq("borrow_date", item));
            borrowNum.add(created);
            dataList.add(item);
        });

        TreeMap<String, Object> map = new TreeMap<>();
        map.put("date",dataList);
        map.put("num",borrowNum);

        return Result.success(map);
    }
}
