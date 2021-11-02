package com.t4cloud.t.base.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Spring Boot 2.0 解决跨域问题
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/2/20 21:19
 */
@Data
@Slf4j
@Configuration
@ConditionalOnProperty(value = "t4cloud.job.open", havingValue = "true")
@ConfigurationProperties(prefix = "t4cloud.job")
public class XxlJobConfig {

    private String address;
    private Integer port;
    private String appName;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() throws UnknownHostException {
        log.info(">>>>>>>>>>> job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(address);
        xxlJobSpringExecutor.setAppName(appName.toLowerCase());
        xxlJobSpringExecutor.setIp(InetAddress.getLocalHost().getHostAddress());
        xxlJobSpringExecutor.setPort(port - 100);
        xxlJobSpringExecutor.setAccessToken(null);
        xxlJobSpringExecutor.setLogPath("/TeaR-APP/logs/" + appName + "/XXL-Job-Log/");
        xxlJobSpringExecutor.setLogRetentionDays(3);
        return xxlJobSpringExecutor;
    }


}
