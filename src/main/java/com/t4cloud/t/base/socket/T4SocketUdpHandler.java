package com.t4cloud.t.base.socket;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * SOCKET UDP默认实现处理
 * <p>
 * --------------------
 *
 * @author Mawang
 * @date 2021/2/22 9:39
 */
public abstract class T4SocketUdpHandler {
    public static final int MAX_UDP_DATA_SIZE = 4096;
    private static final Logger log = LoggerFactory.getLogger(T4SocketUdpHandler.class);

    @Autowired
    public T4SocketUdpHandler() {
    }

    @PostConstruct
    public void connect() {
        try {
            T4Socket annotation = this.getClass().getAnnotation(T4Socket.class);

            int port;
            String mode;

            if (annotation == null) {
                port = 27777;
                mode = "msg";
                log.warn("UDP Server Missing param ! @T4Socket …… Using default config！");
            } else {
                port = annotation.port();
                mode = annotation.mode().toLowerCase();
            }

            log.info("UDP Server starting …… -p " + port + " -m " + mode);
            new Thread(new UDPProcess(port, mode)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public abstract void handler(String msg);

    public abstract void handler(DatagramPacket client);

    class UDPProcess implements Runnable {
        private int port;
        private String mode;

        public UDPProcess(int port, String mode) {
            this.port = port;
            this.mode = mode;
        }

        @SneakyThrows
        @Override
        public void run() {
            DatagramSocket socket = new DatagramSocket(port);
            byte[] buffer = new byte[MAX_UDP_DATA_SIZE];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
//                    log.info(socket+"=======此方法在接收到数据报之前会一直阻塞======");
                    socket.receive(packet);
                    switch (mode) {
                        case "client":
                            handler(packet);
                            break;
                        case "msg":
                            byte[] msgBuffer = packet.getData();// 接收到的UDP信息，然后解码

                            String msg = new String(msgBuffer, "gbk").trim();
                            handler(msg);
                            break;
                        default:
                            log.error("异常错误");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}


