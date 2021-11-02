package com.t4cloud.t.base.socketio;

import cn.hutool.core.collection.CollectionUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.t4cloud.t.base.entity.dto.R;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Web Socket IO 工具类
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/11/2
 */
@Slf4j
public class SocketUtil {

    /**
     * 用户的SOCKET储存
     * <p>
     * 第一层为租户ID，下层为用户ID
     */
    public static ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, SocketIOClient>>> tenantUserSocketMap = new ConcurrentHashMap<>();

    /**
     * 发送WS消息 - 指定客户端
     *
     * @param clientList 指定客户端
     * @param event      频道
     * @param data       消息结构体
     *                   <p>
     * @return boolean 消息发送是否成功
     * --------------------
     * @author TeaR
     * @date 2020/11/5 20:19
     */
    public static boolean send(Collection<SocketIOClient> clientList, String event, R data) {
        boolean flag = false;
        //进入所有租户进行遍历
        if (CollectionUtil.isEmpty(clientList)) {
            return false;
        }
        //开始发送
        for (SocketIOClient client : clientList) {
            client.sendEvent(event, data);
            flag = true;
        }
        return flag;
    }

    /**
     * 发送WS消息 - 指定用户ID
     *
     * @param userId 用户ID
     * @param event  频道
     * @param data   消息结构体
     *               <p>
     * @return boolean 消息发送是否成功
     * --------------------
     * @author TeaR
     * @date 2020/11/5 20:19
     */
    public static boolean send(String userId, String event, R data) {
        //查看是否有连接
        if (CollectionUtil.isEmpty(tenantUserSocketMap)) {
            return false;
        }

        boolean flag = false;
        //进入所有租户进行遍历
        for (ConcurrentHashMap<String, ConcurrentHashMap<String, SocketIOClient>> userSocketMap : tenantUserSocketMap.values()) {
            ConcurrentHashMap<String, SocketIOClient> clientMap = userSocketMap.get(userId);
            //该用户当前没有连上WS
            if (CollectionUtil.isEmpty(clientMap)) {
                continue;
            }
            //开始发送
            send(clientMap.values(), event, data);
            flag = true;
        }
        return flag;
    }

    /**
     * 发送WS消息 - 发给指定租户下的所有用户
     *
     * @param tenantId 租户
     * @param event    频道
     * @param data     消息结构体
     *                 <p>
     * @return boolean 消息发送是否成功
     * --------------------
     * @author TeaR
     * @date 2020/11/5 20:19
     */
    public static boolean sendTenant(String tenantId, String event, R data) {
        //查看是否有连接
        if (CollectionUtil.isEmpty(tenantUserSocketMap)) {
            return false;
        }

        //获取该租户下的所有用户连接
        ConcurrentHashMap<String, ConcurrentHashMap<String, SocketIOClient>> userSocketMap = tenantUserSocketMap.get(tenantId);

        if (CollectionUtil.isEmpty(userSocketMap)) {
            return false;
        }

        //给每个租户下的所有连接发消息
        for (String userId : userSocketMap.keySet()) {
            send(userId, event, data);
        }
        return true;
    }


    /**
     * 发送WS消息 - 发给所有用户
     *
     * @param event 频道
     * @param data  消息结构体
     *              <p>
     * @return boolean 消息发送是否成功
     * --------------------
     * @author TeaR
     * @date 2020/11/5 20:19
     */
    public static boolean sendAll(String event, R data) {

        //查看是否有连接
        if (CollectionUtil.isEmpty(tenantUserSocketMap)) {
            return false;
        }

        //给每个租户下的所有连接发消息
        for (String tenantId : tenantUserSocketMap.keySet()) {
            sendTenant(tenantId, event, data);
        }

        return true;
    }

    /**
     * 关闭WS连接 - 关闭所有的WS链接
     *
     * <p>
     *
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/12/7 21:47
     */
    public static void close() {

        //查看是否有连接
        if (CollectionUtil.isEmpty(tenantUserSocketMap)) {
            return;
        }

        //依次关闭所有租户的链接
        for (ConcurrentHashMap<String, ConcurrentHashMap<String, SocketIOClient>> userSocketMap : tenantUserSocketMap.values()) {
            //依次关闭用户的链接
            for (ConcurrentHashMap<String, SocketIOClient> userTokenSocket : userSocketMap.values()) {
                //依次关闭每个client
                for (SocketIOClient socketIOClient : userTokenSocket.values()) {
                    socketIOClient.disconnect();
                }
            }
        }

    }


}
