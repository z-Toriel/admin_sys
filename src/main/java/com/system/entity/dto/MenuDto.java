package com.system.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MenuDto implements Serializable {
    private Long id;
    private String title;   //多的属性，数据库menu表中没有
    private String icon;
    private String path;
    private String name;
    private String component;
    List<MenuDto> children = new ArrayList<>();
}
