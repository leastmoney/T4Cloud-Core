package com.t4cloud.t.base.socketio;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.t4cloud.t.base.constant.CacheConstant;
import com.t4cloud.t.base.constant.RequestConstant;
import com.t4cloud.t.base.entity.LoginUser;
import com.t4cloud.t.base.exception.T4CloudNoAuthzException;
import com.t4cloud.t.base.utils.JwtUtil;
import com.t4cloud.t.base.utils.RedisUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.concurrent.ConcurrentHashMap;

/**
 * SocketHandler
 * <p>
 * socket服务基础处理类
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/10/14 14:59
 */
@NoArgsConstructor
@ConditionalOnProperty(value = "t4cloud.socket.open", havingValue = "true")
@Slf4j
public abstract class T4SocketIOHandler {

    public static SocketIOServer socketIoServer;

    @Autowired
    public T4SocketIOHandler(SocketIOServer server) {
        socketIoServer = server;
    }

    /**
     * 客户端连接的时候触发，前端js触发：socket = io.connect("http://127.0.0.1:8080");
     *
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {

        String token = client.getHandshakeData().getSingleUrlParam(RequestConstant.T_ACCESS_TOKEN);

        LoginUser userInfo = getUserInfo(client);
        String userId = userInfo.getId();
        String tenantId = userInfo.getTenantId();

        /** TODO 检查token有效性，并刷新token
         *
         * -by TeaR  -2020/10/14-15:10
         */

        //将客户端存入缓存中
        //先获取租户的Map
        ConcurrentHashMap<String, ConcurrentHashMap<String, SocketIOClient>> userSocketMap = SocketUtil.tenantUserSocketMap.get(tenantId);
        if (CollectionUtil.isEmpty(userSocketMap)) {
            userSocketMap = new ConcurrentHashMap<>();
        }

        //再获取用户本身的Map
        ConcurrentHashMap<String, SocketIOClient> clientMap = userSocketMap.get(userId);
        if (CollectionUtil.isEmpty(clientMap)) {
            clientMap = new ConcurrentHashMap<>();
        }

        //将本次连接放入
        clientMap.put(token, client);
        userSocketMap.put(userId, clientMap);
        SocketUtil.tenantUserSocketMap.put(tenantId, userSocketMap);

        log.debug("客户端:" + token + "已连接,userId=" + userId + ",username=" + userInfo.getUsername());
    }

    /**
     * 客户端关闭连接时触发：前端js触发：socket.disconnect();
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String token = client.getHandshakeData().getSingleUrlParam(RequestConstant.T_ACCESS_TOKEN);

        LoginUser userInfo = getUserInfo(client);
        String userId = userInfo.getId();
        String tenantId = userInfo.getTenantId();

        //先获取租户的Map
        ConcurrentHashMap<String, ConcurrentHashMap<String, SocketIOClient>> userSocketMap = SocketUtil.tenantUserSocketMap.get(tenantId);
        if (CollectionUtil.isEmpty(userSocketMap)) {
            userSocketMap = new ConcurrentHashMap<>();
        }

        //再获取用户本身的Map
        ConcurrentHashMap<String, SocketIOClient> clientMap = userSocketMap.get(userId);
        if (CollectionUtil.isEmpty(clientMap)) {
            clientMap = new ConcurrentHashMap<>();
        }

        //移除本次客户端
        clientMap.remove(token);

        //将本次连接放入
        userSocketMap.put(userId, clientMap);
        SocketUtil.tenantUserSocketMap.put(tenantId, userSocketMap);

        log.debug("客户端:" + token + "断开连接");
    }

    /**
     * 获取当前通讯的用户信息
     *
     * <p>
     *
     * @return com.t4cloud.t.base.entity.LoginUser
     * --------------------
     * @author TeaR
     * @date 2020/10/14 15:50
     */
    protected LoginUser getUserInfo(SocketIOClient client) {

        String token = client.getHandshakeData().getSingleUrlParam(RequestConstant.T_ACCESS_TOKEN);
        // 解密获得username，用于和数据库进行对比
        String userId = JwtUtil.getUserId(token);
        if (userId == null) {
            throw new T4CloudNoAuthzException("token非法！");
        }

        String cacheToken = (String) RedisUtil.get(CacheConstant.SYS_USERS_TOKEN + userId + "-" + token);

        //检查token有效性
        if (StrUtil.isBlank(cacheToken)) {
            throw new T4CloudNoAuthzException("无法从Redis中获取有效token，请检查Redis连接状态或token已失效");
        }

        if (token == null || cacheToken == null || !token.equalsIgnoreCase(cacheToken)) {
            throw new T4CloudNoAuthzException("token已失效，请重新登录！");
        }

        //获取用户信息
        LoginUser loginUser = (LoginUser) RedisUtil.get(CacheConstant.SYS_USERS_CACHE + userId);

        if (loginUser == null) {
            throw new T4CloudNoAuthzException("用户登录已失效，请重新登录！");
        }

        /** TODO 缺少ws下的token刷新操作
         *
         * -by TeaR  -2020/10/14-16:46
         */

        return loginUser;
    }

}