package com.t4cloud.t.base.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * socket服务平滑关闭
 *
 * @author TeaR
 */
@Slf4j
@Order(value = 1)
@ConditionalOnProperty(value = "t4cloud.socket.open", havingValue = "true")
@Component
public class SocketCloser {

    private SocketIOServer server;

    @Autowired
    public SocketCloser(SocketIOServer server) {
        this.server = server;
    }

    /**
     * 关闭WS监听服务
     * <p>
     * --------------------
     *
     * @author TeaR
     * @date 2020/12/7 23:35
     */
    @PreDestroy
    public void destroy() {
        if (server != null) {
            try {
                log.info("T4 Socket is stopping on - " + server.getConfiguration().getHostname() + ":" + server.getConfiguration().getPort());
                server.stop();
                server = null;
                log.info("T4 Socket is stopped");
            } catch (Exception e) {

            }

        }
    }

}