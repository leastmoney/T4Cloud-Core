package com.t4cloud.t.base.annotation.cache;


import com.t4cloud.t.base.constant.CacheConstant;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 开启字典缓存
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/03/15 19:40
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//@Cacheable(value = CacheConstant.SYS_DICT, key = "#p0 + ':group:'", unless = "#result == null")
@Cacheable(value = CacheConstant.SYS_DICT, key = "#p0 + ':all:'")
public @interface DictCacheable {

}
