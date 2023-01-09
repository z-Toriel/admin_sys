package com.system.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.system.common.Result;
import com.system.entity.Books;
import com.system.entity.Borrow;
import com.system.entity.Fans;
import org.springframework.web.bind.annotation.*;

import com.system.common.BaseController;

import javax.swing.text.rtf.RTFEditorKit;
import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Byterain
 * @since 2023-01-08
 */
@RestController
@RequestMapping("/system/borrow")
public class BorrowController extends BaseController {
    @GetMapping("/list")
    public Result list(String bname, String uname) {
        List<Long> bids = new ArrayList<>();
        List<Long> uids = new ArrayList<>();

        List<Books> bookList = booksService.list(new QueryWrapper<Books>().like(StrUtil.isNotBlank(bname), "name", bname));
        bookList.forEach(book -> {
            Long bid = book.getId();
            bids.add(bid);
        });

        List<Fans> userList = fansService.list(new QueryWrapper<Fans>().like(StrUtil.isNotBlank(uname), "username", uname));
        userList.forEach(user -> {
            Long uid = user.getId();
            uids.add(uid);
        });

        Page<Borrow> borrowList = borrowService.page(getPage(), new QueryWrapper<Borrow>()
                .in(bids.size() > 0, "bid", bids)
                .in(uids.size() > 0, "uid", uids)
                .last("order by statu")
        );

        borrowList.getRecords().forEach(item -> {
            Long uid = item.getUid();
            Long bid = item.getBid();

            Fans fansById = fansService.getById(uid);
            Books bookById = booksService.getById(bid);

            String username = fansById.getUsername();
            String bookName = bookById.getName();

            item.setUname(username);
            item.setBname(bookName);
        });
        return Result.success(borrowList);
    }


    @PostMapping("/save")
    public Result save(@RequestBody Borrow borrow) {
        Long bid = borrow.getBid();
        Long uid = borrow.getUid();
        Books booksById = booksService.getById(bid);
        Fans fansByID = fansService.getById(uid);
        if (booksById == null) {
            return Result.fail("书籍不存在");
        } else if (fansByID == null) {
            return Result.fail("用户不存在");
        } else {
            Integer remain = booksById.getRemain();
            if (remain == 0) {
                return Result.fail("该书籍已经没有了");
            }
            booksById.setRemain(remain - 1);   //设置该书籍的剩余数量 -1
            booksService.update(booksById, new QueryWrapper<Books>().eq("id", booksById.getId()));   // 保存该书籍
            borrow.setCreated(LocalDateTime.now()); //设置创建时间
            borrow.setUpdated(LocalDateTime.now()); // 设置跟新时间
            borrow.setBorrowDate(LocalDate.now());  // 设置借阅时间
            LocalDate borrowDate = borrow.getBorrowDate();  // 获取借阅日期
            LocalDate returnDate = borrowDate.plusDays(borrow.getDays());   // 得到归还日期
            borrow.setReturnDate(returnDate);   // 设置归还日期
            borrow.setStatu(0); // 设置状态，0表示正在借阅
            boolean b = borrowService.save(borrow);
            if (b) {
                return Result.success(borrow);
            } else {
                return Result.fail("数据添加失败");
            }
        }
    }


    @PostMapping("/normalReturn/{id}")
    public Result nomalReturn(@PathVariable Long id) {
        Borrow borrowById = borrowService.getById(id);  // 根据传过来的id获取到借阅对象
        borrowById.setStatu(1); // 设置借阅状态，1表示正常归还
        borrowById.setUpdated(LocalDateTime.now()); // 设置更新日期
        borrowById.setRealReturnDate(LocalDate.now()); // 设置实际还书日期
        // 将新的借阅信息更新进去
        boolean b = borrowService.update(borrowById, new QueryWrapper<Borrow>().eq("id", borrowById.getId()));

        Books bookByID = booksService.getById(borrowById.getBid()); // 根据借阅对象的bid获取到book对象
        Integer remain = bookByID.getRemain();  // 获取到剩余的数量
        bookByID.setRemain(remain + 1);   // 将剩余的数量 +1
        // 将信息的书籍信息跟新进去
        boolean b1 = booksService.update(bookByID, new QueryWrapper<Books>().eq("id", bookByID.getId()));

        if (b && b1) {
            return Result.success("归还成功");
        } else {
            return Result.fail("归还失败");
        }
    }

    @PostMapping("/overdueReturn/{id}/{overdueDays}")
    public Result overdueReturn(@PathVariable Long id, @PathVariable Integer overdueDays) {   // overdueDays是逾期了几天
        Borrow borrowById = borrowService.getById(id);
        borrowById.setCompensation(overdueDays);    // 设置赔偿金额1天1块
        borrowById.setUpdated(LocalDateTime.now());
        borrowById.setRealReturnDate(LocalDate.now());
        borrowById.setStatu(2); // 2是逾期归还
        boolean b = borrowService.update(borrowById, new QueryWrapper<Borrow>().eq("id", id));

        // 获取图书对象，将图书对象的剩余书籍+1
        Long bid = borrowById.getBid();
        Books bookById = booksService.getById(bid);
        bookById.setRemain(bookById.getRemain() + 1);
        boolean b1 = booksService.update(bookById, new QueryWrapper<Books>().eq("id", bid));
        if (b && b1) {
            return Result.success("逾期归还成功");
        } else {
            return Result.fail("逾期归还失败");
        }
    }

    @PostMapping("/breakReturn/{id}")
    public Result breakReturn(@PathVariable Long id) {
        Borrow borrowById = borrowService.getById(id);  // 获取借阅对象
        borrowById.setUpdated(LocalDateTime.now()); // 设置更新时间
        borrowById.setStatu(3); // 设置状态 3表示书籍破损
        borrowById.setRealReturnDate(LocalDate.now());  // 设置实际归还时间

        Long bid = borrowById.getBid();
        Books bookById = booksService.getById(bid); // 获取书籍对象
        Integer price = bookById.getPrice();    // 获取书籍价格
        price = price / 2;
        borrowById.setCompensation(price); // 设置赔偿金额

        boolean b = borrowService.update(borrowById, new QueryWrapper<Borrow>().eq("id", id));
        if(b){
            return Result.success("书籍归还成功(破损)");
        }else{
            return Result.fail("书籍归还失败(破损)");
        }
    }

    @PostMapping("/lost/{id}")
    public Result lost(@PathVariable Long id) {
        Borrow borrowById = borrowService.getById(id);  // 获取借阅对象
        borrowById.setUpdated(LocalDateTime.now()); // 设置更新时间
        borrowById.setStatu(4); // 设置状态 3表示书籍破损
        borrowById.setRealReturnDate(LocalDate.now());  // 设置实际归还时间

        Long bid = borrowById.getBid();
        Books bookById = booksService.getById(bid); // 获取书籍对象
        Integer price = bookById.getPrice();    // 获取书籍价格
        borrowById.setCompensation(price); // 设置赔偿金额

        boolean b = borrowService.update(borrowById, new QueryWrapper<Borrow>().eq("id", id));
        if(b){
            return Result.success("书籍丢失");
        }else{
            return Result.fail("书籍丢失失败");
        }
    }
}
