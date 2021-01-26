package com.aac.kpi.system.config;

import com.aac.kpi.system.interceptor.CommonPropertiesSetterInterceptor;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = {"com.aac.**.mapper*"})
public class mybatisPlusConfig {

    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource, CommonPropertiesSetterInterceptor commonPropertiesSetterInterceptor) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:com/aac/**/mapper/xml/*.xml"));
        factoryBean.setConfiguration(mybatisConfiguration());
        factoryBean.setTypeAliasesPackage("com.aac.**.entity");
        factoryBean.setPlugins(mybatisPlusInterceptor(), commonPropertiesSetterInterceptor);
        return factoryBean;
    }

    @Bean
    public MybatisConfiguration mybatisConfiguration() {
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        // 设置二级缓存
        mybatisConfiguration.setCacheEnabled(true);
        return mybatisConfiguration;
    }

    /**
     * mybatis-plus 提供的 分页拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);
        return mybatisPlusInterceptor;
    }
}
