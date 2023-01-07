package com.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.system.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Byterain
 * @since 2022-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_books")
public class Books extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("name")
    private String name;

    @TableField("author")
    private String author;

    @TableField("category_id")
    private Long categoryId;

    @TableField(exist = false)
    private String category;

    @TableField("region")
    private String region;

    @TableField("cover")
    private String cover;

    /**
     * 出版社
     */
    @TableField("press")
    private String press;

    /**
     * 数量
     */
    @TableField("total")
    private Integer total;


    @TableField("is_delete")
    @TableLogic(value = "1",delval = "0")
    private Integer isDelete;

    @TableField(exist = false)
    private String location;


}
