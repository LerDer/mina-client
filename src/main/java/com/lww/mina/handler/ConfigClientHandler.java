package com.lww.mina.handler;

import com.alibaba.fastjson.JSONObject;
import com.lww.mina.dto.MessageDO;
import com.lww.mina.event.ConfChangeEvent;
import com.lww.mina.protocol.MessagePack;
import com.lww.mina.session.SessionManager;
import com.lww.mina.util.SpringBeanFactoryUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.springframework.context.ApplicationContext;

/**
 * @author lww
 * @date 2020-07-08 19:32
 */
@Slf4j
public class ConfigClientHandler extends IoHandlerAdapter {

    private final MessagePack pack;

    public ConfigClientHandler(MessagePack pack) {
        this.pack = pack;
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        log.error("ConfigClientHandler_exceptionCaught_cause:{}", cause);
    }

    @Override
    public void sessionOpened(IoSession session) {
        //设置session
        SessionManager.setSession(session);
        String body = pack.getBody();
        log.info("ConfigClientHandler_sessionOpened_body:{}", body);
        session.write(pack);
    }

    @Override
    public void messageReceived(IoSession session, Object pack) {
        log.info("收到服务器响应消息 ConfigClientHandler_messageReceived_pack:{}", JSONObject.toJSONString(pack));
        if (pack instanceof MessagePack) {
            MessagePack minaPack = (MessagePack) pack;
            String body = minaPack.getBody();
            MessageDO message = JSONObject.parseObject(body, MessageDO.class);
            log.info("ConfigClientHandler_messageReceived_minaMessage:{}", JSONObject.toJSONString(message));
            ApplicationContext context = SpringBeanFactoryUtils.getApplicationContext();
            context.publishEvent(new ConfChangeEvent(message));
        }
    }
}
