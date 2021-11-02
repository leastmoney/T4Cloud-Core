package com.t4cloud.t.base.socketio;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthzListener
 * <p>
 * 用户鉴权拦截器
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2020/10/15 11:11
 */
@Slf4j
public class SocketAuthzListener<T> implements DataListener<T> {


    @Override
    public void onData(SocketIOClient socketIOClient, T o, AckRequest ackRequest) throws Exception {
        log.info("测试" + o);
    }
}
