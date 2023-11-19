package com.hmall.api.config;

import com.hmall.api.client.fallback.ItemClientFallbackFactory;
import com.hmall.api.interceptors.UserInfoInterceptor;
import feign.Logger;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    //openfeign的日志，由于没加@Configuration，需在需要日志的微服务启动类上的@EnableFeignClients中添加defaultConfiguration属性
    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor userInfoInterceptor(){
        return new UserInfoInterceptor();
    }

    @Bean
    public ItemClientFallbackFactory itemClientFallbackFactory(){
        return new ItemClientFallbackFactory();
    }
}
