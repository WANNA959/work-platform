package com.godx.cloud.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	/**
	 * token服务配置
	 */
    @Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenServices(tokenServices());
	}

	/**
	 * 路由安全认证配置
	 */
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				// 配置hello打头的路由需要安全认证
				// todo
				.antMatchers("/api/code/**").authenticated()
				.and().csrf().disable();
	}

	/**
	 * 调用微服务
	 * @return
	 */
	@LoadBalanced
	@Bean
	public RestTemplate restTemplate() {
		//httpRequestFactory()
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}

	/**
	 * 资源服务令牌解析服务
	 */
	@Bean
	@Primary
	public ResourceServerTokenServices tokenServices() {
		RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
		remoteTokenServices.setCheckTokenEndpointUrl("http://gateway-service/oauth/check_token");
		remoteTokenServices.setRestTemplate(restTemplate());
		remoteTokenServices.setClientId("client_1");
		remoteTokenServices.setClientSecret("123456");
		return remoteTokenServices;
	}
}
