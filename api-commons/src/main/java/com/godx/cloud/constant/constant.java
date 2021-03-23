package com.godx.cloud.constant;

public interface constant {

    /**
     * 状态码
     */
    int STATUS_SUC=200;
    int STATUS_BADREQUEST=400;

    /**
     * Common Message
     */
    String MESSAGE_OK="ok";

    /**
     * 邮件激活
     */
    //激活成功
    int ACTIVATION_SUCCESS=0;
    //重复激活
    int ACTIVATION_REPEAT=1;
    //激活失败
    int ACTIVATION_FAILURE=2;

    /**
     * 修改密码
     */
    //修改成功
    int MODIFY_SUCCESS=0;
    //重复确认修改
    int MODIFY_REPEAT=1;
    //修改失败
    int MODIFY_FAILURE=2;

    /**
     * 登录问题
     */
    //默认 登陆凭证超时时间
    int DEFAULT_EXPIRED_SECONDS=3600*12;//默认12小时
    //记住 登录凭证超时时间
    int REMEMBER_EXPIRED_SECONDS=3600*24*14;//默认60天

    /**
     * 实体类型
     */
    //帖子
    int ENTITY_TYPE_POST=1;
    //评论
    int ENTITY_TYPE_COMMENT=2;
    //用户
    int ENTITY_TYPE_USER=3;

    /**
     * 事件主题相关
     */
    String TOPIC_COMMENT="comment";
    String TOPIC_LIKE="like";
    String TOPIC_FOLLOW="follow";
    String TOPIC_PUBLISH="publish";
    String TOPIC_DELETE="delete";//删帖事件

    /**
     * 系统用户
     */
    int SYSTEM_USER_ID=1;

    /**
     * 权限
     */
    String AUTHORITY_USER="user";
    String AUTHORITY_ADMIN="admin";
    String AUTHORITY_MODERATOR="moderator";

}
