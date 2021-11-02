package com.t4cloud.t.base.authc;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.t4cloud.t.base.constant.RequestConstant;
import com.t4cloud.t.base.constant.ResultConstant;
import com.t4cloud.t.base.utils.SpringContextUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * TokenFeignIntecepor
 * <p>
 * 用户实现feign自动加载header中的token
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2020/3/2 16:13
 */
@Slf4j
public class TokenFeignIntecepor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //判断是否携带参数
        HttpServletRequest httpServletRequest = SpringContextUtil.getHttpServletRequest();
        if (httpServletRequest != null) {
            //获取token
            String token = httpServletRequest.getHeader(RequestConstant.T_ACCESS_TOKEN);
            String uuid = httpServletRequest.getHeader(RequestConstant.T_UUID);
            String tenant = httpServletRequest.getHeader(RequestConstant.T_TENANT);

            //传递token 、 time 、 uuid
            if (StrUtil.isNotBlank(token)) {
                requestTemplate.header(RequestConstant.T_ACCESS_TOKEN, token);
            }
//            if (StrUtil.isNotBlank(time)) { 注释掉，是因为感觉只要是调用feign产生的，都可以自动携带上这个时间戳
            //时间戳，更换成新的，以保证链路调用顺利
            requestTemplate.header(RequestConstant.T_TIME, DateUtil.current() + StrUtil.EMPTY);
//            }

            if (StrUtil.isNotBlank(uuid)) {
                requestTemplate.header(RequestConstant.T_UUID, uuid);
                log.debug("uuid:" + uuid);
            } else {
                requestTemplate.header(RequestConstant.T_UUID, ResultConstant.T_CLIENT_TYPE);
                log.debug("uuid:" + ResultConstant.T_CLIENT_TYPE);
            }
            if (StrUtil.isNotBlank(tenant)) {
                requestTemplate.header(RequestConstant.T_TENANT, tenant);
            }

            //传递各类APP-ID设置
            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                String value = httpServletRequest.getHeader(key);
                if (StrUtil.startWithIgnoreCase(key, RequestConstant.T_APP_PREFIX) && StrUtil.isNotBlank(value)) {
                    requestTemplate.header(key, value);
                }
            }

        } else {
            //直接在应用内部发起的请求（定时任务等）
            requestTemplate.header(RequestConstant.T_TIME, DateUtil.current() + StrUtil.EMPTY);
            requestTemplate.header(RequestConstant.T_UUID, ResultConstant.T_CLIENT_TYPE);
            log.debug("headers:" + JSONUtil.toJsonStr(requestTemplate.headers()));
        }
        //声明feign调用（让字典失效，如果字典翻译的话，会导致JSON序列和反序列化异常）
        requestTemplate.header(ResultConstant.T_CLIENT_TYPE, ResultConstant.T_CLIENT_TYPE);
    }
}
