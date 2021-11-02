package com.t4cloud.t.base.utils;

import cn.hutool.core.util.StrUtil;
import com.t4cloud.t.base.entity.dto.R;
import com.t4cloud.t.base.exception.T4CloudBadRequestException;
import com.t4cloud.t.base.exception.T4CloudDecryptException;
import com.t4cloud.t.base.exception.T4CloudNoAuthzException;
import com.t4cloud.t.base.exception.T4CloudValidException;
import org.apache.shiro.authz.UnauthorizedException;

/**
 * 异常处理工具类
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2021/2/22 22:36
 */
public class ExceptionUtil {

    /**
     * 解析异常的返回值
     *
     * <p>
     *
     * @return com.t4cloud.t.base.entity.dto.R
     * --------------------
     * @author TeaR
     * @date 2020/10/15 10:27
     */
    public static R getResult(Exception exception) {

        //401响应值
        if (exception instanceof T4CloudNoAuthzException || (StrUtil.isNotBlank(exception.getMessage()) && exception.getMessage().contains("T4CloudNoAuthzException"))) {
            return R.noLogin(exception.getMessage());
        }

        //403响应值
        if (exception instanceof UnauthorizedException) {
            return R.noAuth(exception.getMessage());
        }

        //400响应值
        if (exception instanceof T4CloudBadRequestException ||
                exception instanceof T4CloudValidException ||
                exception instanceof T4CloudDecryptException ||
                (StrUtil.isNotBlank(exception.getMessage()) &&
                        (exception.getMessage().contains("T4CloudBadRequestException") ||
                                exception.getMessage().contains("T4CloudValidException") ||
                                exception.getMessage().contains("T4CloudDecryptException")))
        ) {
            return R.error(400, exception.getMessage());
        }

        //通用的500响应值
        return R.error(exception.getMessage());
    }

}
