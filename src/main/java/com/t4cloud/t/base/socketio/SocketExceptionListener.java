package com.t4cloud.t.base.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ExceptionListener;
import com.t4cloud.t.base.utils.ExceptionUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * SocketExceptionListener
 * <p>
 * Socket 通讯异常处理
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2020/10/15 9:32
 */
@Slf4j
public class SocketExceptionListener implements ExceptionListener {
    @Override
    public void onEventException(Exception e, List<Object> list, SocketIOClient socketIOClient) {
        e.printStackTrace();
        log.error(e.getMessage());

        //自动回传异常信息
        for (Object o : list) {
            if (o instanceof WsMsg) {
                String event = ((WsMsg<?>) o).getEvent();
                socketIOClient.sendEvent(event, ExceptionUtil.getResult(e));
            }
        }
    }

    @Override
    public void onDisconnectException(Exception e, SocketIOClient socketIOClient) {

    }

    @Override
    public void onConnectException(Exception e, SocketIOClient socketIOClient) {

    }

    @Override
    public void onPingException(Exception e, SocketIOClient socketIOClient) {

    }

    @Override
    public boolean exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
        return false;
    }

}
