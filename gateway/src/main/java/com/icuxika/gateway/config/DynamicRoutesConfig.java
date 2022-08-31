package com.icuxika.gateway.config;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;
import reactor.core.publisher.Mono;

import java.util.concurrent.Executor;

/**
 * Gateway 动态路由配置
 */
@Configuration
public class DynamicRoutesConfig implements InitializingBean {

    private static final Logger L = LoggerFactory.getLogger(DynamicRoutesConfig.class);

    @Autowired
    private NacosConfigManager nacosConfigManager;

    @Autowired
    private RouteDefinitionWriter routeDefinitionWriter;

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;

    private static final String GATEWAY_DYNAMIC_ROUTES_FILE = "gateway-dynamic-routes.yml";
    private static final String GATEWAY_DYNAMIC_ROUTES_FILE_GROUP = "DEFAULT_GROUP";

    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigService configService = nacosConfigManager.getConfigService();
        String content = configService.getConfig(GATEWAY_DYNAMIC_ROUTES_FILE, GATEWAY_DYNAMIC_ROUTES_FILE_GROUP, 5000);
        L.info("开始加载网关动态路由");
        updateRoutes(content);
        L.info("网关动态路由加载完成");
        configService.addListener(GATEWAY_DYNAMIC_ROUTES_FILE, GATEWAY_DYNAMIC_ROUTES_FILE_GROUP, new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                L.info("开始清除旧的路由数据");
                clean();
                L.info("旧的路由数据清除完成");
                L.info("开始更新网关动态路由");
                updateRoutes(configInfo);
                L.info("网关动态路由更新完成");
            }
        });
    }

    public void clean() {
        routeDefinitionLocator.getRouteDefinitions().subscribe(routeDefinition -> {
            L.info("正在清除路由：" + routeDefinition.getId() + ", uri 是 " + routeDefinition.getUri());
            routeDefinitionWriter.delete(Mono.just(routeDefinition.getId())).subscribe();
        });
    }

    private void updateRoutes(String routes) {
        Yaml yaml = new Yaml();
        RouteDefinitionList routeDefinitionList = yaml.loadAs(routes, RouteDefinitionList.class);
        routeDefinitionList.getRoutes().forEach(routeDefinition -> {
            L.info("正在更新路由：" + routeDefinition.getId() + ", uri 是 " + routeDefinition.getUri());
            routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
        });
    }
}
