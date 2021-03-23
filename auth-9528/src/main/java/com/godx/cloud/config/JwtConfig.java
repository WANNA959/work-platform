package com.godx.cloud.config;

import com.godx.cloud.constant.constant;
import com.godx.cloud.model.User;
import com.godx.cloud.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Deprecated
public class JwtConfig extends JwtAccessTokenConverter implements constant {

    @Resource
    private UserService userService;
    //可以用来固定添加user的相关信息
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInformation = new LinkedHashMap<>();
        Map<String, Object> info = new LinkedHashMap<>();

//        boolean expired=false;
//        Map<String, String> map = userService.login(username, password, expired);

//        User user =(User)authentication.getPrincipal();
//        log.info(user.toString());
        info.put("author", "江南一点雨");
        info.put("email", "wangsong0210@gmail.com");
        info.put("site", "www.javaboy.org");
        info.put("weixin", "a_java_boy2");
        info.put("WeChat Official Accounts", "江南一点雨");
        info.put("GitHub", "https://github.com/lenve");
        info.put("user", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        additionalInformation.put("info", info);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
        return super.enhance(accessToken, authentication);
    }
}
