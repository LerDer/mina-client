package com.lww.mina.listener;

import com.alibaba.fastjson.JSONObject;
import com.lww.mina.dto.MessageDO;
import com.lww.mina.event.ConfSendEvent;
import com.lww.mina.protocol.MessagePack;
import com.lww.mina.session.SessionManager;
import com.lww.mina.util.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author lww
 * @date 2020-07-09 11:27
 */
@Slf4j
@Component
public class ConfSendMessageListener {

    @EventListener
    public void onApplicationEvent(ConfSendEvent event) {
        MessageDO message = event.getMessage();
        log.info("ConfSendMessageListener_onApplicationEvent_message:{}", JSONObject.toJSONString(message));
        MessagePack pack = new MessagePack(Const.CONFIG_MANAGE, JSONObject.toJSONString(message));
        IoSession session = SessionManager.getSession();
        if (session != null) {
            session.write(pack);
        } else {
            log.error("ConfSendMessageListener_onApplicationEvent_session is null");
        }
    }
}