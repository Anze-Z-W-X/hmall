package com.hmall.api.interceptors;

import com.hmall.common.utils.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class UserInfoInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long userId = UserContext.getUser();
        if(userId!=null){
            requestTemplate.header("user-info",userId.toString());
        }
    }
}
