package com.godx.cloud.util;

import com.godx.cloud.model.User;
import com.godx.cloud.utils.RedisKeyUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

public class UserUtil {


    public static User getUserInfo(RedisTemplate redisTemplate,String token){
        String tokenKey = RedisKeyUtil.getTokenKey(token.substring(7));
        User user= (User)redisTemplate.opsForValue().get(tokenKey);
        return user;
    }
}
