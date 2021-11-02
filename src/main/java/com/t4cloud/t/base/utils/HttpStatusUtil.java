package com.t4cloud.t.base.utils;

import com.t4cloud.t.base.entity.dto.R;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;

/**
 * HTTP状态码翻译 工具类
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/12/9 16:04
 */
public class HttpStatusUtil {

    /**
     * 将业务状态码自动翻译成HTTP STATUS
     *
     * @param result 业务返回值
     *               <p>
     * @return org.springframework.http.HttpStatus
     * --------------------
     * @author TeaR
     * @date 2020/12/9 16:06
     */
    public static void setHttpStatus(R result) {

        HttpStatus httpStatus;
        try {
            //获取对应的状态码
            httpStatus = HttpStatus.valueOf(result.getCode());
        } catch (Exception e) {
            //没有对应的状态码，默认左右200/500
            if (result.isSuccess()) {
                httpStatus = HttpStatus.OK;
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            }
        }

        //对外输出
        HttpServletResponse httpServletResponse = SpringContextUtil.getHttpServletResponse();
        if (httpServletResponse != null) {
            httpServletResponse.setStatus(httpStatus.value());
        }

    }

}
