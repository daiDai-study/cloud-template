package com.aac.kpi.gateway.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 动态更新路由网关service
 * 1）实现一个Spring提供的事件推送接口ApplicationEventPublisherAware
 * 2）提供动态路由的基础方法，可通过获取bean操作该类的方法。该类提供新增路由、更新路由、删除路由，然后实现发布的功能。
 */
@Slf4j
@Service
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {

    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;

    @Resource
    private RouteDefinitionLocator routeDefinitionLocator;

    /**
     * 发布事件
     */
    @Resource
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * 删除路由
     * @param definition
     * @return
     */
    private boolean delete(RouteDefinition definition) {
        try {
            log.info("gateway delete route: {}",definition);
            this.routeDefinitionWriter.delete(Mono.just(definition.getId())).subscribe();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }
    /**
     * 更新路由列表
     * @param definitionList
     * @return
     */
    public void update(List<RouteDefinition> definitionList) {
        log.info("begin update routes");
        List<RouteDefinition> definitionListExisted = this.list();

        Set<RouteDefinition> definitionSetToAdd = definitionList.stream().filter(d -> !definitionListExisted.contains(d)).collect(Collectors.toSet());
        Set<RouteDefinition> definitionSetToRemove = definitionListExisted.stream().filter(d -> !definitionList.contains(d)).collect(Collectors.toSet());

        boolean result = true;

        // 必须先删除再添加，否则有可能出现添加（会根据id更新）之后被删除（根据id删除）
        for (RouteDefinition definition : definitionSetToRemove) {
            if (!this.delete(definition)) {
                result = false;
                log.error("删除路由失败：{}", definition);
            }
        }
        for (RouteDefinition definition : definitionSetToAdd) {
            if (!this.add(definition)) {
                result = false;
                log.error("添加路由失败：{}", definition);
            }
        }

        // 刷新路由
        this.publisher.publishEvent(new RefreshRoutesEvent(this));

        if(result){
            log.info("update routes successfully");
        }else{
            log.info("update routes unsuccessfully");
        }
    }

    /**
     * 增加路由
     * @param definition
     * @return
     */
    private boolean add(RouteDefinition definition) {
        try {
            log.info("gateway add route {}",definition);
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * 当前路由列表
     * @return
     */
    public List<RouteDefinition> list(){
        List<RouteDefinition> list = routeDefinitionLocator.getRouteDefinitions().collectList().block();
        if(!CollectionUtils.isEmpty(list)){
            log.info("现有路由的个数：{}", list.size());
            log.info("他们分别为：");
            for (RouteDefinition routeDefinition : list) {
                log.info(routeDefinition.toString());
            }
        }else{
            log.info("现有路由的个数：0");
            list = new ArrayList<>();
        }
        return list;
    }
}
