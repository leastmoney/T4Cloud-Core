package com.t4cloud.t.base.annotation;

import java.lang.annotation.*;

/**
 * 自定义字典注解，可将被注解的属性进行翻译
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/2/9 12:39
 */
@Repeatable(Dicts.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {

    /**
     * 字典code
     */
    String code();

    /**
     * 指定表
     */
    String table() default "";

    /**
     * 指定属性
     */
    String prop() default "";

    /**
     * 不做翻译，仅查询使用
     */
    boolean onlyQuery() default false;

}
