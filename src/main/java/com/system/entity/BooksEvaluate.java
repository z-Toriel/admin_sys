package com.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
 * @since 2023-01-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_books_evaluate")
public class BooksEvaluate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("bid")
    private Long bid;

    @TableField(exist = false)
    private String bname;

    @TableField("uid")
    private Long uid;

    @TableField(exist = false)
    private String uname;

    @TableField("star")
    private Integer star;

    @TableField("comment")
    private String comment;


}
