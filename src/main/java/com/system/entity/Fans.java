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
 * @since 2022-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_fans")
public class Fans extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("id")
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("avatar")
    private String avatar;

    @TableField("email")
    private String email;

    @TableField("sex")
    private Integer sex;

    @TableField("info")
    private String info;

    @TableField("delTag")
    @TableLogic(value = "1",delval = "0")
    private Integer deltag;

    @TableField("remainBorrowNumber")
    private Integer remainBorrowNumber;


}
