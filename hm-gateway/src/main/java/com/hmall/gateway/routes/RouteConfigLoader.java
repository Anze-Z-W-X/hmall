package com.hmall.gateway.routes;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class RouteConfigLoader {
    private final NacosConfigManager configManager;

    private final RouteDefinitionWriter writer;

    private final String DATAID = "gateway-routes.json";
    private final String GROUP = "DEFAULT_GROUP";

    private Set<String > routeIds = new HashSet<>();

    @PostConstruct  //在bean被创建后并且成员变量被注入后执行
    public void initRouteConfiguration() throws NacosException {
        //1.第一次启动时，拉取路由表，并且添加监听器
        String configInfo = configManager.getConfigService().getConfigAndSignListener(DATAID, GROUP, 1000, new Listener() {
            @Override
            public Executor getExecutor() {
                return Executors.newSingleThreadExecutor(); //线程池，此处是单线程
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                //TODO 监听到路由变更，更新路由表
                UpdateRouteConfigInfo(configInfo);
            }
        });
        //TODO 2.写入路由表
        UpdateRouteConfigInfo(configInfo);
    }

    private void UpdateRouteConfigInfo(String configInfo) {
        //1.解析路由信息
        List<RouteDefinition> routeDefinitions = JSONUtil.toList(configInfo, RouteDefinition.class);
        //2.更新路由表
        //2.1 删除旧路由
        for (String routeId : routeIds) {
            writer.delete(Mono.just(routeId)).subscribe();
        }
        routeIds.clear();
        //2.2 判断是否有新路由
        if(routeDefinitions.isEmpty()){
            //无效的路由
            return;
        }
        //3 写入新路由表
        for (RouteDefinition routeDefinition : routeDefinitions) {
            writer.save(Mono.just(routeDefinition)).subscribe();
            routeIds.add(routeDefinition.getId());
        }
    }
}
