package com.t4cloud.t.base.socket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.t4cloud.t.base.entity.dto.R;
import com.t4cloud.t.base.utils.ExceptionUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SOCKET TCP默认实现
 * <p>
 * --------------------
 *
 * @author Mawang
 * @date 2021/2/22 9:39
 */
public abstract class T4SocketTcpHandler {
    private static final Logger log = LoggerFactory.getLogger(T4SocketTcpHandler.class);

    @Autowired
    public T4SocketTcpHandler() {

    }

    @PostConstruct
    public void connect() {
        T4Socket annotation = this.getClass().getAnnotation(T4Socket.class);

        int port;
        String mode;

        if (annotation == null) {
            port = 17777;
            mode = "msg";
            log.warn("TCP Server Missing param ! @T4Socket …… Using default config！");
        } else {
            port = annotation.port();
            mode = annotation.mode().toLowerCase();
        }


        log.info("TCP Server starting …… -p " + port + " -m " + mode);
        SocketStartThread socketStartThread = new SocketStartThread(port, mode);
        socketStartThread.start();
    }

    /**
     * 对于接收到的MSG处理
     *
     * @param msg 收到的消息
     *            <p>
     * @return String 回复的消息
     * --------------------
     * @author TeaR
     * @date 2021/2/22 9:48
     */
    public abstract String handler(String msg);

    public abstract void handler(Socket client);


    //消息接收线程(只处理mode="msg"类型)
    class MsgThread extends Thread {
        InputStream inputStream;
        private Socket socket;

        public MsgThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                while (true) {
                    inputStream = socket.getInputStream();
                    byte[] bytes = new byte[1024];
                    inputStream.read(bytes);
                    String msg = new String(bytes, "gbk").trim();

                    String response = null;

                    try {
                        response = handler(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        R result = ExceptionUtil.getResult(e);
                        response = JSONUtil.toJsonStr(result);
                    }

                    if (StrUtil.isBlank(response)) {
                        response = "";
                    }
                    //向客户端发送消息(检查是否已断开连接)
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(response.getBytes());
                }

            } catch (Exception e) {
                log.error("客户端主动断开连接了");
                e.printStackTrace();
            }
            //操作结束，关闭socket
            try {
                log.info("操作结束");
                socket.close();
            } catch (IOException e) {
                log.error("关闭连接出现异常");
                e.printStackTrace();
            }
        }

    }


    //服务端连接线程类
    class SocketStartThread extends Thread {
        private int port;
        private String mode;

        public SocketStartThread(int port, String mode) {
            this.port = port;
            this.mode = mode;
        }

        @SneakyThrows
        public void run() {
            ServerSocket serverSocket = null;
            Socket socket = null;
            try {
                //建立服务器的Socket，并设定一个监听的端口PORT
                serverSocket = new ServerSocket(port);
                while (true) {//支持多个客户端连接
                    try {
                        //建立跟客户端的连接
                        socket = serverSocket.accept();
                    } catch (Exception e) {
                        log.error("建立与客户端的连接出现异常");
                        e.printStackTrace();
                    }
                    switch (mode) {
                        case "client":
                            handler(socket);
                            break;
                        case "msg":
                            MsgThread thread = new MsgThread(socket);
                            thread.start();
                            break;
                        default:
                            log.error("异常错误");
                    }
                }
            } catch (Exception e) {
                log.error("端口被占用");
                e.printStackTrace();
            } finally {
                serverSocket.close();
            }
        }
    }
}


