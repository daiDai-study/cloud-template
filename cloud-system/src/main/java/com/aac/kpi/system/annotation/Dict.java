package com.aac.kpi.system.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Dict {

    @AliasFor("dictCode")
    String value() default "";

    @AliasFor("value")
    String dictCode() default "";

    String dictText() default "";

    String dictTable() default "";
}
