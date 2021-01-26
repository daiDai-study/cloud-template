package com.aac.kpi.gateway.controller;

import com.aac.kpi.gateway.property.AuthProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Slf4j
@RequestMapping("/config")
@RefreshScope
public class ConfigClientController {

    @Value("${config.info}")
    private String configInfo;

    @Resource
    private AuthProperties authProperties;

    @Resource
    private ObjectMapper objectMapper;

    @GetMapping("/info")
    public String info(){
        return configInfo;
    }

    @GetMapping("/auth")
    public JsonNode auth(){
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putPOJO("anonUrl", authProperties.getAnonUrls());
        objectNode.putPOJO("jwtUrl", authProperties.getJwtUrls());
        return objectNode;
    }
}
