package com.aac.kpi.system;

import com.aac.kpi.system.property.SSOProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties({SSOProperties.class})
public class KpiSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(KpiSystemApplication.class, args);
    }
}
