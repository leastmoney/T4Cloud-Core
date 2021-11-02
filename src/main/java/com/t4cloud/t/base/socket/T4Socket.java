package com.t4cloud.t.base.socket;

import java.lang.annotation.*;

/**
 * SOCKET 参数配置
 * <p>
 * --------------------
 *
 * @author Mawang
 * @date 2021/2/22 9:39
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface T4Socket {
    int port() default 60023;

    String mode() default "msg";
}


