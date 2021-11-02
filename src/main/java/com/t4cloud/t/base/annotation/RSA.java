package com.t4cloud.t.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要解密的参数
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/2/9 12:39
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RSA {

}
