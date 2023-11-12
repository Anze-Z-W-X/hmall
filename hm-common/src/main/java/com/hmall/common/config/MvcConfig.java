package com.hmall.common.config;

import com.hmall.common.interceptor.UserInfoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//需要在  /META-INF/spring.factories 中进行配置才能让此配置类生效，或者使用自定义注解来使它看情况生效
@Configuration
@ConditionalOnClass(DispatcherServlet.class)//只有在有DispatcherServlet的环境下才生效
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInfoInterceptor());
    }
}
