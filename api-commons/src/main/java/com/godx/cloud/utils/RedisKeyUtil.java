package com.godx.cloud.utils;

public class RedisKeyUtil {

    private static final String SPLIT=":";

    private static final String PREFIX_ENTITY_LIKE="like:entity";
    private static final String PREFIX_USER_LIKE="like:user";
    private static final String PREFIX_FOLLOWER="follower";
    private static final String PREFIX_FOLLOWEE="followee";
    private static final String PREFIX_KAPTCHAR="kaptcha";
    private static final String PREFIX_TICKET="ticket";
    private static final String PREFIX_USER="user";
    private static final String PREFIX_TOKEN="token";

    //某个实体的赞
    public static String getPrefixEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    //某个用户的赞
    public static String getPrefixUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    //某个用户关注的实体 key:entityId
    public static String getPrefixFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //某个实体拥有的粉丝 key:userId
    public static String getPrefixFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    //验证码
    public static String getKaptcharKey(String owner){
        return PREFIX_KAPTCHAR+SPLIT+owner;
    }

    //登录凭证
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }

    //用户
    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }

    public static String getTokenKey(String token){
        return PREFIX_TOKEN+SPLIT+token;
    }
}
