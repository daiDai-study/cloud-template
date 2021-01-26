package com.aac.kpi.system.aspect;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.system.annotation.Dict;
import com.aac.kpi.system.service.SysDictService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Aspect
@Component
@Slf4j
public class DictAspect {
    @Resource
    private SysDictService dictService;
    @Resource
    private ObjectMapper objectMapper;
    private static final String TEXT_SUFFIX = "_Text";

    /**
     * 定义切点Pointcut
     */
    @Pointcut("@annotation(com.aac.kpi.system.annotation.DictToData)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        this.parseDictText(result);
        return result;
    }


    private void parseDictText(Object result) {
        Object data = null;
        if (result instanceof ApiResult) {
            data = ((ApiResult) result).getData();
            if (data instanceof IPage) {
                List<ObjectNode> array = new ArrayList<>();
                IPage pageData = (IPage) (data);
                for (Object record : pageData.getRecords()) {
                    addDictText(array, record);
                }
                ((IPage) (((ApiResult) result).getData())).setRecords(array);
            } else if(data instanceof Collection){
                List<ObjectNode> array = new ArrayList<>();
                final Collection list = (Collection) (data);
                if(list.size() > 0){
                    for (Object record : (List)((ApiResult) result).getData()) {
                        addDictText(array, record);
                    }
                    ((ApiResult) result).setData(array);
                }
            }else if(!(data instanceof Collection)){
                Object record = ((ApiResult) result).getData();
                ObjectNode item = addDictText(record);
                ((ApiResult) result).setData(item);
            }
        }
    }

    private void addDictText(List<ObjectNode> array, Object record){
        ObjectNode item = addDictText(record);
        array.add(item);
    }

    private ObjectNode addDictText(Object record){
        ObjectNode item = null;
        try {
            String json = objectMapper.writeValueAsString(record);
            item = (ObjectNode) objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (item == null) {
            return null;
        }

        for (Field field : ReflectUtil.getFields(record.getClass())) {
            Dict annotation = field.getAnnotation(Dict.class);
            if (annotation != null) {
                String code = annotation.value();
                if(StrUtil.isEmpty(code)){
                    code = annotation.dictCode();
                }
                String text = annotation.dictText();
                String table = annotation.dictTable();
                String key = String.valueOf(item.get(field.getName()));

                //翻译字典值对应的txt
                String textValue = translateDictValue(code, text, table, key);
                item.put(field.getName() + TEXT_SUFFIX, textValue);
            }
        }
        return item;
    }

    /**
     * 翻译字典文本
     */
    private String translateDictValue(String code, String text, String table, String key) {
        if (StrUtil.isEmpty(key)) {
            return null;
        }
        StringBuilder textValue = new StringBuilder();
        String[] keys = key.split(",");
        for (String k : keys) {
            String tmpValue = null;
            log.debug(" 字典 key : " + k);
            if (k.trim().length() == 0) {
                continue; //跳过循环
            }
            if (!StringUtils.isEmpty(table)) {
                tmpValue = dictService.queryTableDictTextByKey(table, text, code, k.trim());
            } else {
                tmpValue = dictService.queryDictTextByKey(code, k.trim());
            }

            if (tmpValue != null) {
                if (!"".equals(textValue.toString())) {
                    textValue.append(",");
                }
                textValue.append(tmpValue);
            }

        }
        return textValue.toString();
    }
}
