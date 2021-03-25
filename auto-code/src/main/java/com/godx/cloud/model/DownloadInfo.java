package com.godx.cloud.model;

import java.io.Serializable;
import java.util.Date;

/**
 * (DownloadInfo)实体类
 *
 * @author makejava
 * @since 2021-03-25 19:37:39
 */
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
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOssDetails() {
        return ossDetails;
    }

    public void setOssDetails(String ossDetails) {
        this.ossDetails = ossDetails;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}
