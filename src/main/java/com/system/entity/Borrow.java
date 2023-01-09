package com.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableId;
import com.system.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Byterain
 * @since 2023-01-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_borrow")
public class Borrow extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableField("uid")
    private Long uid;

    @TableField(exist = false)
    private String uname;

    /**
     * 书籍id
     */
    @TableField("bid")
    private Long bid;

    @TableField(exist = false)
    private String bname;

    /**
     * 借阅日期
     */
    @TableField("borrow_date")
    private LocalDate borrowDate;

    @TableField(exist = false)
    private Long days;

    /**
     * 预计还书日期
     */
    @TableField("return_date")
    private LocalDate returnDate;


    /**
     * 实际还书日期
     */
    @TableField("real_return_date")
    private LocalDate realReturnDate;


    /**
     * 赔偿金额
     */
    @TableField("compensation")
    private Integer compensation;



}
