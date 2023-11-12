package com.hmall.gateway.filters;

import cn.hutool.core.text.AntPathMatcher;
import com.hmall.common.exception.UnauthorizedException;
import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginGlobalFilter implements GlobalFilter, Ordered {
    private final AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final JwtTool jwtTool;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request
        ServerHttpRequest request = exchange.getRequest();
        //2.判断当前请求是否需要拦截
        if(isAllowPath(request)){
            //无需拦截，放行
            return chain.filter(exchange);
        }
        //3.获取token
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if(headers!=null){
            token = headers.get(0);
        }
        //4.需要拦截，解析token
        Long userId = null;
        try {
            userId = jwtTool.parseToken(token);
            System.out.println("userId = " + userId);
        }catch (Exception e){
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401);
            return response.setComplete();
        }
        //5.传递用户信息到下游服务
        String userInfo = userId.toString();
        ServerWebExchange exc = exchange.mutate()
                .request(builder -> builder.header("user-info", userInfo)).build();
        //6.放行
        return chain.filter(exc);
    }

    private boolean isAllowPath(ServerHttpRequest request) {
        boolean flag = false;
        //获取方法和路径
        String path = request.getPath().toString();
        //判断是否匹配
        for(String excludePath:authProperties.getExcludePaths()){
            boolean isMatch = antPathMatcher.match(excludePath,path);
            if(isMatch){
                flag=true;
                break;
            }
        }
        return flag;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
