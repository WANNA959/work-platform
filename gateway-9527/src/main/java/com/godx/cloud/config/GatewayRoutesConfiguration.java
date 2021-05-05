package com.godx.cloud.config;

import com.godx.cloud.controller.filter.MyGatewayFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Deprecated
public class GatewayRoutesConfiguration {

    /**
     * java 配置 server 服务路由
     * @param builder
     * @return
     */
//    @Bean
//    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
//        log.info("in MyGatewayFilter");
//        return builder.routes()
//                .route(r ->
//                        r.path("/api/**")
//                                .filters(
//                                        f -> f.stripPrefix(1)
//                                                .filters(new MyGatewayFilter())
//                                )
//                                .uri("lb://auto-code")
//                )
//                .build();
//    }
}
