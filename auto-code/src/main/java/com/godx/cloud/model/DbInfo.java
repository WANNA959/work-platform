package com.godx.cloud.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * (DbInfo)实体类
 *
 * @author makejava
 * @since 2021-03-29 21:32:53
 */
public class DbInfo implements Serializable {
    private static final long serialVersionUID = 937275796704639274L;
    /**
     * 主键
     */
    private Integer id;
    /**
     * 所属userId
     */
    private Integer userId;
    /**
     * 数据库名称
     */
    private String dbName;
    /**
     * 数据库描述
     */
    private String dbComment;
    /**
     * json格式表名列表
     */
    private String tables;
    /**
     * host
     */
    private String host;

    private String port;
    /**
     * 密码
     */
    private String password;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
    /**
     * 用户名
     */
    private String username;


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

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbComment() {
        return dbComment;
    }

    public void setDbComment(String dbComment) {
        this.dbComment = dbComment;
    }

    public String getTables() {
        return tables;
    }

    public void setTables(String tables) {
        this.tables = tables;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
