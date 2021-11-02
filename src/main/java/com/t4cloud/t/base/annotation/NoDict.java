package com.t4cloud.t.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指定接口返回值不参与翻译
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/8/6
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoDict {
}
