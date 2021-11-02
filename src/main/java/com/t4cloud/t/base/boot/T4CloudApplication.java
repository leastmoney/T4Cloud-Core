package com.t4cloud.t.base.boot;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * T4CloudAppStart
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2020/12/7 21:36
 */
@Slf4j
public class T4CloudApplication {

    public static SocketIOServer socketIoServer;

    @Autowired
    public T4CloudApplication(SocketIOServer server) {
        socketIoServer = server;
    }

    /**
     * 标准启动器
     *
     * @param primarySource 启动类
     *                      <p>
     * @return ConfigurableApplicationContext
     * --------------------
     * @author TeaR
     * @date 2020/12/7 21:38
     */
    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) throws UnknownHostException {

        ConfigurableApplicationContext application = SpringApplication.run(new Class[]{primarySource}, args);

        //输出标准信息
        Environment env = application.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        String name = env.getProperty("spring.application.name");
        log.info("\n----------------------------------------------------------\n\t" +
                "Application " + name + " is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + (StringUtils.isEmpty(path) ? "" : path) + "/\n\t" +
                "External: \thttp://" + ip + ":" + port + (StringUtils.isEmpty(path) ? "" : path) + "/\n\t" +
                "swagger-ui: \thttp://" + ip + ":" + port + (StringUtils.isEmpty(path) ? "" : path) + "/swagger-ui.html\n\t" +
                "Doc: \t\thttp://" + ip + ":" + port + (StringUtils.isEmpty(path) ? "" : path) + "/doc.html\n" +
                "----------------------------------------------------------");

        //注册监听器
//        application.addApplicationListener(new T4CloudApplicationCloseListener(socketIoServer));

        return application;
    }


}
