package com.godx.cloud.controller.filter;

import com.godx.cloud.constant.constant;
import com.godx.cloud.model.CommonResult;
import com.godx.cloud.model.User;
import com.godx.cloud.utils.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

@Component
@Slf4j
public class MyGatewayFilter implements GatewayFilter, Ordered, constant {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //拦截方法，拦截到请求后，自动执行
        log.info("filter 拦截方法，拦截到请求后，自动执行 ");
        //header上带token
        //根据token是否有空可以判断是否登录/鉴权
        String token =  exchange.getRequest().getHeaders().getFirst("Authorization");
        if(token == null || "".equals(token)){
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().setComplete();
            return chain.filter(exchange);
        } else {
            String tokenKey = RedisKeyUtil.getTokenKey(token.substring(7));
            if(redisTemplate==null){
                log.info("redis is null");
            }
            User user = (User) redisTemplate.opsForValue().get(tokenKey);
            if (user == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                exchange.getResponse().setComplete();
            }
            ServerHttpRequest request = exchange.getRequest().mutate().header("userId", String.valueOf(user.getId())).build();
            return chain.filter(exchange.mutate().request(request).build());//2 把新的request放回到过滤链，全部放行
        }
    }

    @Override
    public int getOrder() {
        //3:系统调用该方法获得过滤器的优先级
        return 1; //数字越小，优先级越高
    }

}
