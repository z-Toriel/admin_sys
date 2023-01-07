package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.entity.Books;
import com.system.entity.Category;
import com.system.entity.Fans;
import com.system.service.BooksService;
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
 * @since 2022-12-31
 */
@RestController
@RequestMapping("/system/book")
public class BooksController extends BaseController {


    @GetMapping("/list")
    public Result list(String name,Long categoryId) {
        Page<Books> booksList = booksService.page(getPage(), new QueryWrapper<Books>()
                .like(StrUtil.isNotBlank(name), "name", name)   // 如果有名字传过来则根据名字查询对于的书籍
                .eq(categoryId!=null,"category_id",categoryId)  // 如果有分类的id传过来，则查询对应的书籍
        );
        // 获取所有查询到的书籍，遍历它们。根据categoryId放入对应的categoryName以及location
        booksList.getRecords().forEach(book->{
            Category categorybyId = categoryService.getById(book.getCategoryId());  // 获取对应的category对象
            String categoryName = categorybyId.getName();   // 拿到categoryName
            book.setCategory(categoryName);     // 设置book的categoryName

            String location = categorybyId.getLocation();   // 拿到该分类在图书馆的具体位置
            book.setLocation(location);

        });
        return Result.success(booksList);
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable Long id){
        Books bookByid = booksService.getById(id);
        Long categoryId = bookByid.getCategoryId(); // 获取分类id
        Category categoryById = categoryService.getById(categoryId);    // 获取分类对象
        String categoryByIdName = categoryById.getName();   // 获取分类名字
        bookByid.setCategory(categoryByIdName); // 设置分类名字
        return Result.success(bookByid);
    }

    @PostMapping("/save")
    public Result save(@RequestBody Books books){
        books.setIsDelete(1);
        books.setCreated(LocalDateTime.now());
        if(booksService.save(books)){
            return Result.success(books);
        }else {
            return Result.fail("保存失败");
        }

    }

    @PostMapping("/update")
    public Result update(@RequestBody Books books){
        books.setUpdated(LocalDateTime.now());
        boolean b = booksService.updateById(books);
        if(b){
            return Result.success(books);
        }else {
            return Result.fail("更新失败");
        }
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
        boolean b = booksService.removeByIds(Arrays.asList(ids));
        if (b){
            return Result.success(ids);
        }else{
            return Result.fail("删除失败");
        }
    }


}
