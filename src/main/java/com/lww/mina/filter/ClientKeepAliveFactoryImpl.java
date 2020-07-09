package com.lww.mina.filter;

import com.lww.mina.protocol.MessagePack;
import com.lww.mina.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * 客户端 被动型心跳机制
 *
 * @author lww
 * @date 2020-07-08 19:22
 */
@Slf4j
public class ClientKeepAliveFactoryImpl implements KeepAliveMessageFactory {

    /**
     * 服务器不会给客户端发送请求包，因此不关注请求包，直接返回 false
     */
    @Override
    public boolean isRequest(IoSession session, Object message) {
        return false;
    }

    /**
     * 客户端关注请求反馈，因此判断 mesaage 是否是反馈包
     */
    @Override
    public boolean isResponse(IoSession session, Object message) {
        MessagePack pack = (MessagePack) message;
        log.info("ClientKeepAliveFactoryImpl_isResponse_pack:{}", "反馈心跳包");
        return pack.getModule() == Const.HEART_BEAT;
    }

    /**
     * 获取心跳请求包 non-null
     */
    @Override
    public Object getRequest(IoSession session) {
        MessagePack pack = new MessagePack(Const.HEART_BEAT, "heart");
        log.info("ClientKeepAliveFactoryImpl_getRequest_pack:{}", "获取心跳包");
        return pack;
    }

    /**
     * 服务器不会给客户端发送心跳请求，客户端当然也不用反馈，该方法返回 null
     */
    @Override
    public Object getResponse(IoSession session, Object request) {
        return null;
    }
}
