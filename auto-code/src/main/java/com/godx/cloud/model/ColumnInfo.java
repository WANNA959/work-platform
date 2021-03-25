package com.godx.cloud.model;

import lombok.Data;

import java.util.Map;

/**
 * 列信息
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
@Data
public class ColumnInfo {
    /**
     * 原名称
     */
    private String rawName;
    /**
     * 名称
     */
    private String name;
    /**
     * 注释
     */
    private String comment;
    /**
     * 全类型 java.lang.String
     */
    private String type;
    /**
     * 短类型 String
     */
    private String shortType;
    /**
     * 标记是否为自定义附加列
     */
    private boolean custom;
    /**
     * 扩展数据
     */
    private Map<String, Object> ext;
}
