package com.godx.cloud.model;

import java.util.Date;
import java.io.Serializable;

/**
 * (Test)实体类
 *
 * @author makejava
 * @since 2021-03-23 17:30:55
 */
public class Test implements Serializable {
    private static final long serialVersionUID = 262915508271613338L;

    private Integer id;

    private String username;

    private String password;

    private String salt;

    private String email;
    /**
     * 0-普通用户; 1-超级管理员; 2-版主;
     */
    private Integer type;
    /**
     * 0-未激活; 1-已激活;
     */
    private Integer status;

    private String activationCode;

    private String headerUrl;

    private Date createTime;

    private Date updateTime;

    private String resetCode;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
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

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }

}
