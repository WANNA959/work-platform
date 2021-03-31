package com.godx.cloud.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * (DownloadInfo)实体类
 *
 * @author makejava
 * @since 2021-03-25 19:37:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadInfo implements Serializable {
    private static final long serialVersionUID = -38620391113208322L;
    /**
     * 主键
     */
    private Integer id;
    /**
     * 用户id
     */
    private Integer userId;
    /**
     * oss地址&时间json数组
     */
    private String ossDetails;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 创建时间
     */
    private String host;
    private String username;
    private String password;
    private String dbName;
    private String tables;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;


}
