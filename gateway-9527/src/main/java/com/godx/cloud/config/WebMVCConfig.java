package com.godx.cloud.config;


import com.godx.cloud.controller.filter.MyGatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Deprecated
public class WebMVCConfig implements WebMvcConfigurer {

    @Bean
    public MyGatewayFilter getMyGatewayFilter(){
        return new MyGatewayFilter();
    }
}