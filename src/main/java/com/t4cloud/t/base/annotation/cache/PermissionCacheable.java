package com.t4cloud.t.base.annotation.cache;


import com.t4cloud.t.base.constant.CacheConstant;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户权限缓存记录
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/02/21 16:58
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Cacheable(value = CacheConstant.SYS_USER_PERMISSIONS)
public @interface PermissionCacheable {


}
