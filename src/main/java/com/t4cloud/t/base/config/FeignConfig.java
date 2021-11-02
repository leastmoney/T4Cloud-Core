package com.t4cloud.t.base.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.t4cloud.t.base.exception.T4CloudServiceException;
import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.Retryer;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * FEIGN 全局配置
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/12/11 14:56R
 */
@Slf4j
@Configuration
public class FeignConfig extends ErrorDecoder.Default {

    /**
     * 自定义重试机制
     *
     * <p>
     *
     * @return feign.Retryer
     * --------------------
     * @author TeaR
     * @date 2020/12/11 14:59
     */
    @Bean
    public Retryer feignRetry() {
        //最大请求次数为5，初始间隔时间为100ms，下次间隔时间1.5倍递增，重试间最大间隔时间为1s，
        return new Retryer.Default();
    }

    /**
     * feign自定义异常处理
     *
     * <p>
     *
     * @return feign.codec.ErrorDecoder
     * --------------------
     * @author TeaR
     * @date 2020/12/11 15:04
     */
    @Override
    public Exception decode(String methodKey, Response response) {

//        log.error("response : " + JSONUtil.toJsonStr(response));

        Exception exception = super.decode(methodKey, response);

        // 如果是RetryableException，则返回继续重试
        if (exception instanceof RetryableException) {
            return exception;
        }

//        log.error("response error: " + JSONUtil.toJsonStr(exception));

        if (exception instanceof FeignException) {

            String result = ((FeignException) exception).contentUTF8();
            Integer code = null;
            String message = null;

            try {
                JSONObject parse = JSONUtil.parseObj(result);

                //状态码
                code = parse.getInt("code");
                message = parse.getStr("message");

            } catch (Exception e) {
                code = ((FeignException) exception).status();
                message = ((FeignException) exception).contentUTF8();
            }

            if (code == null) {
                code = response.status();
            }

            if (StrUtil.isBlank(message)) {
                message = response.reason();
            }

            // 其他异常交给Default去解码处理
            // 这里使用单例即可，Default不用每次都去new
            return new T4CloudServiceException(code, message);
        }

        log.error(exception.getMessage(), exception);
        return exception;
    }

}