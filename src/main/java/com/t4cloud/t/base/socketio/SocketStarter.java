package com.t4cloud.t.base.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * socket服务启动器
 *
 * @author TeaR
 */
@Slf4j
@Order(value = 1)
@ConditionalOnProperty(value = "t4cloud.socket.open", havingValue = "true")
@Component
public class SocketStarter implements CommandLineRunner {

    private final SocketIOServer server;

    @Autowired
    public SocketStarter(SocketIOServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) {
        server.start();
        log.info("T4 Socket is running on - " + server.getConfiguration().getHostname() + ":" + server.getConfiguration().getPort());
    }

}