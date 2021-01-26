package com.aac.kpi.gateway.property;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.Arrays;

@Setter
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    /**
     * 免认证 URL
     */
    private String anonUrl;

    /**
     * JWT 认证
     */
    private String jwtUrl;

    public String[] getAnonUrls(){
        return getUrls(this.anonUrl);
    }

    public String[] getJwtUrls(){
        return getUrls(this.jwtUrl);
    }

    private String[] getUrls(String urlWithDelimiter){
        if (StrUtil.isNotEmpty(urlWithDelimiter)) {
            String[] split = StringUtils.splitByWholeSeparatorPreserveAllTokens(urlWithDelimiter, ",");
            String[] trim = new String[split.length];
            for (int i = 0; i < split.length; i++) {
                trim[i] = StrUtil.trim(split[i]);
            }
            return trim;
        }
        return new String[0];
    }
}
