package com.godx.cloud.controller.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godx.cloud.constant.constant;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.User;
import com.godx.cloud.utils.RedisKeyUtil;
import com.godx.cloud.utils.UserUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
@Slf4j
public class MyGatewayFilter implements GlobalFilter, Ordered, constant {

    private ObjectMapper objectMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();

        log.info("filter here :"+url);
        //跳过不需要验证的路径
        if (url.startsWith("/oauth/")) {
            return chain.filter(exchange);
        }

        //获取token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        ServerHttpResponse resp = exchange.getResponse();
        if (StringUtils.isBlank(token)) {
            //没有token
            return authErro(resp, "请登陆");
        } else {
            //有token
            User user = UserUtil.getUserInfo(redisTemplate, token);
            if (user != null) {
                log.info("认证成功");
                return chain.filter(exchange);
            } else {
                return authErro(resp, "认证失败");
            }
        }
    }


    private Mono<Void> authErro(ServerHttpResponse resp, String mess) throws JsonProcessingException {
//        resp.setStatusCode(HttpStatus.UNAUTHORIZED);
        resp.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//        String returnStr = "";
//        returnStr = objectMapper.writeValueAsString(mess);
        CommonResult message = new CommonResult(401, mess);
        String json = JSON.toJSON(message).toString();
        DataBuffer buffer = resp.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));

        return resp.writeWith(Flux.just(buffer));
    }

    @Override
    public int getOrder() {
        //3:系统调用该方法获得过滤器的优先级
        return 1; //数字越小，优先级越高
    }

}
