package com.t4cloud.t.base.socketio;

import lombok.Data;

import java.io.Serializable;

/**
 * WsMsg
 * <p>
 * 定义WS通讯的接受请求
 * <p>
 * ---------------------
 *
 * @author TeaR
 * @date 2020/10/15 9:56
 */
@Data
public class WsMsg<T> implements Serializable {

    protected static final long serialVersionUID = 1L;

    private String event;
    /**
     * 由于SOCKET-IO框架限制,目前此处的泛型，只支持基础类型和map
     */
    private T data;

}
