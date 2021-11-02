package com.t4cloud.t.base.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.context.annotation.Bean;

/**
 * ribbon 负载均衡策略（默认）
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/3/17 17:46
 */
//@Configuration
public class RibbonLoadBalancingConfig {

    /**
     * 随机规则
     */
    @Bean
    public IRule ribbonRule() {
        return new RoundRobinRule();
    }

}