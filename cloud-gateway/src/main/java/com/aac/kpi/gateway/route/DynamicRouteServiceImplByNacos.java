package com.aac.kpi.gateway.route;

import com.aac.kpi.gateway.config.GatewayConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 通过nacos下发动态路由配置,监听Nacos中gateway-route配置
 */
@Component
@Slf4j
@DependsOn({"gatewayConfig"}) // 依赖于gatewayConfig bean
public class DynamicRouteServiceImplByNacos {

    @Resource
    private DynamicRouteServiceImpl dynamicRouteService;

    private ConfigService configService;

    @PostConstruct
    public void init() {
        log.info("初始化网关路由配置");
        try {
            configService = initConfigService();
            if (configService == null) {
                log.warn("initConfigService fail");
                return;
            }
            String configInfo = configService.getConfig(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP, GatewayConfig.DEFAULT_TIMEOUT);
            log.info("获取网关当前路由配置:\r\n{}", configInfo);
            this.updateRoutes(configInfo);
        } catch (Exception e) {
            log.error("初始化网关路由配置时发生错误", e);
        }
        dynamicRouteByNacosListener(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP);
    }

    /**
     * 监听 Nacos 下发的动态路由配置
     *
     * @param dataId
     * @param group
     */
    public void dynamicRouteByNacosListener(String dataId, String group) {
        try {
            DynamicRouteServiceImplByNacos service = this;
            configService.addListener(dataId, group, new Listener() {
                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("网关路由配置发生更新:\n\r{}", configInfo);
                    service.updateRoutes(configInfo);
                }

                @Override
                public Executor getExecutor() {
                    log.info("getExecutor\n\r");
                    return null;
                }
            });
        } catch (NacosException e) {
            log.error("从nacos接收动态路由配置出错!!!", e);
        }
    }

    /**
     * 初始化网关路由 nacos config
     */
    private ConfigService initConfigService() {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", GatewayConfig.NACOS_SERVER_ADDR);
            properties.setProperty("namespace", GatewayConfig.NACOS_NAMESPACE);
            return configService = NacosFactory.createConfigService(properties);
        } catch (Exception e) {
            log.error("初始化网关路由时发生错误", e);
            return null;
        }
    }

    private void updateRoutes(String configInfo){
        List<RouteDefinition> definitionList = JSON.parseArray(configInfo, RouteDefinition.class);
        if(CollectionUtils.isEmpty(definitionList)){
            definitionList = new ArrayList<>();
        }
        dynamicRouteService.update(definitionList);
    }
}
