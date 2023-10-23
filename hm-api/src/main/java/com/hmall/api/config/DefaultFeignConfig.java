package com.hmall.api.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    //openfeign的日志，由于没加@Configuration，需在需要日志的微服务启动类上的@EnableFeignClients中添加defaultConfiguration属性
    @Bean
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
