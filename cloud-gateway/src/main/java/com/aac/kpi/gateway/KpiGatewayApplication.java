package com.aac.kpi.gateway;

import com.aac.kpi.gateway.property.AuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties(AuthProperties.class)
public class KpiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(KpiGatewayApplication.class, args);
    }
}
