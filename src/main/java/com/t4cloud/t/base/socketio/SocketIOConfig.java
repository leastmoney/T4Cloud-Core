package com.t4cloud.t.base.socketio;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SocketIOConfig
 * <p>
 * netty socket 配置
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2020/10/14 14:08
 */
@Data
@Slf4j
@Configuration
@ConditionalOnProperty(value = "t4cloud.socket.open", havingValue = "true")
@ConfigurationProperties(prefix = "t4cloud.socket")
public class SocketIOConfig {

    private String host;
    private Integer port;

    /**
     * 注册netty-socket-io服务端
     */
    @Bean
    public SocketIOServer socketIOServer() {
        //填充socket配置
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port - 200);

        //配置netty相关配置
        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);

        /** DONE 全局异常处理
         * -by TeaR  -2020/10/14-17:46
         */
        config.setExceptionListener(new SocketExceptionListener());


        //全局用户鉴权
//        config.setAuthorizationListener(new AuthorizationListener() {
//            @Override
//            public boolean isAuthorized(HandshakeData data) {
//                //http://localhost:8081?username=test&password=test
//                //例如果使用上面的链接进行connect，可以使用如下代码获取用户密码信息，本文不做身份验证
//                 String token = data.getSingleUrlParam(RequestConstant.T_ACCESS_TOKEN);
//                 log.debug("用户token：" + token);
//                 log.debug("登录成功：" + token);
//
//
//                // String password = data.getSingleUrlParam("password");
//                return true;
//            }
//        });

        final SocketIOServer server = new SocketIOServer(config);
//        server.addEventInterceptor(new EventInterceptor() {
//            @Override
//            public void onEvent(NamespaceClient namespaceClient, String s, List<Object> list, AckRequest ackRequest) {
//                log.info("测试事件拦截器");
//            }
//        });
//        server.addEventListener("message", WsMsg.class, new SocketAuthzListener<WsMsg>());
        return server;
    }

    /**
     * tomcat启动时候，扫码socket服务器并注册
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }

}
