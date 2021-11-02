package com.t4cloud.t.base.config;

import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ActuatorConfig
 * <p>
 * 针对端点的开启
 * --------------------
 *
 * @author TeaR
 * @date 2020/3/26 21:47
 */
@Configuration
@ConditionalOnProperty(value = "t4cloud.actuator.open", havingValue = "true")
public class ActuatorConfig {

    /**
     * 开启httptrace端点
     * <p>
     * --------------------
     *
     * @author TeaR
     * @date 2020/3/26 15:38
     */
    @ConditionalOnMissingBean
    @Bean
    public HttpTraceRepository httpTraceRepository() {
        return new InMemoryHttpTraceRepository();
    }

}
