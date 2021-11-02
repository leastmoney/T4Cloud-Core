package com.t4cloud.t.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多个字典注解的容器
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2021/7/30 23:41
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dicts {

    Dict[] value();

}
