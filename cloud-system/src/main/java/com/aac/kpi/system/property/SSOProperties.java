package com.aac.kpi.system.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "sso")
public class SSOProperties {

    /**
     * 是否开启
     */
    private Boolean isOpen;

    /**
     * 登录地址
     */
    private String loginUrl;

    /**
     * webservice 地址
     */
    private String validateUrl;

    private String wsdlUrl;

    /**
     * 注销地址
     */
    private String logoutUrl;

    private String username = "ADUser";
    private String realname = "UserName";
    private String status = "Status";
}
