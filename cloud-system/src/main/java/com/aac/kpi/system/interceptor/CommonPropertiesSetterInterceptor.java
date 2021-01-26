package com.aac.kpi.system.interceptor;

import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.exception.BizException;
import com.aac.kpi.system.entity.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Properties;

/**
 * TODO: 当前用户如何获取
 * 自动注入创建人、创建时间、修改人、修改时间
 */
@Slf4j
@Component
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class CommonPropertiesSetterInterceptor implements Interceptor {

    private static final String CREATE_BY = "createBy";
    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_BY = "updateBy";
    private static final String UPDATE_TIME = "updateTime";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();

        Object entity = invocation.getArgs()[1];
        if (entity == null) {
            return invocation.proceed();
        }

        if (SqlCommandType.INSERT == sqlCommandType || SqlCommandType.UPDATE == sqlCommandType) {
            log.info("sqlId:{};",mappedStatement.getId());
            if(SqlCommandType.INSERT == sqlCommandType){
                log.info("sqlCommandType:{}, {};", sqlCommandType, "insert");

                // 获取公共的字段，目前设计父类（BaseEntity）字段均为公共字段
                Field[] publicFields = entity.getClass().getFields();

                for (Field field : publicFields) {
                    try {
                        //注入创建人和修改人
                        if (CREATE_BY.equals(field.getName()) || UPDATE_BY.equals(field.getName())) {
                            setUsernameIfEmpty(entity, field);
                        }
                        //注入创建时间和修改时间
                        if (CREATE_TIME.equals(field.getName()) || UPDATE_TIME.equals(field.getName())) {
                            field.setAccessible(true);
                            Object date = field.get(entity);
                            if (date == null) {
                                field.set(entity, new Date());
                            }
                            field.setAccessible(false);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(),e);
                        log.error("Mybatis-公共属性设置拦截器-拦截SQL插入语句时发生异常：{}", e.getMessage());
                    }
                }
            }else{
                log.info("sqlCommandType:{}, {};", sqlCommandType, "update");
                Field[] fields = null;
                if (entity instanceof MapperMethod.ParamMap) {
                    MapperMethod.ParamMap<?> p = (MapperMethod.ParamMap<?>) entity;
                    // 当用 mybatis-plus 中 service 层 saveOrUpdateBatch 方法时，ParamMap 中只有 "et" 的键值对，没有 "param1" 的键值对
                    if(p.containsKey("param1")){
                        entity = p.get("param1");
                    }else{
                        entity = p.get("et");
                    }
                    fields = entity.getClass().getFields();
                } else {
                    fields = entity.getClass().getFields();
                }

                for (Field field : fields) {
                    try {
                        //注入修改人
                        if (UPDATE_BY.equals(field.getName())) {
                            setUsernameIfEmpty(entity, field);
                        }
                        //注入修改时间
                        if (UPDATE_TIME.equals(field.getName())) {
                            field.setAccessible(true);
                            Object date = field.get(entity);
                            if (date == null) {
                                field.set(entity, new Date());
                            }
                            field.setAccessible(false);
                        }
                    } catch (Exception e) {
                        log.error("Mybatis-公共属性设置拦截器-拦截SQL更新语句时发生异常：{}", e.getMessage());
                    }
                }
            }

        }
        return invocation.proceed();
    }

    /**
     * 如果为空，则设置当前用户，如果获取不到当前用户，则设置为 system
     */
    private void setUsernameIfEmpty(Object entity, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        String username = (String) field.get(entity);
        if(StrUtil.isEmpty(username)){
            //获取登录用户信息
            SysUser currentUser = null;
            try{
//                currentUser = authClientWrapper.getCurrentUser();
            }catch (BizException e){
                log.error(e.getMessage(),e);
                log.error("Mybatis-公共属性设置拦截器-获取当前用户时发生异常：{}", e.getMessage());
            }
            if (currentUser != null) {
                username = currentUser.getUsername();
            }else{
                // 默认
                username = "system";
            }
            field.set(entity, username);
        }
        field.setAccessible(false);
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
