package com.godx.cloud.utils;

import com.godx.cloud.model.User;
import org.springframework.data.redis.core.RedisTemplate;

public class UserUtil {


    public static User getUserInfo(RedisTemplate redisTemplate,String token){
        String tokenKey = RedisKeyUtil.getTokenKey(token.substring(7));
        User user= (User)redisTemplate.opsForValue().get(tokenKey);
        return user;
    }
}
