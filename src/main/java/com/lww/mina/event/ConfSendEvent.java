package com.lww.mina.event;

import com.lww.mina.dto.MessageDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * 消息发送事件
 *
 * @author lww
 * @date 2020-07-09 11:26
 */
@Slf4j
public class ConfSendEvent extends ApplicationEvent {

    private MessageDO message;

    public ConfSendEvent(MessageDO message) {
        super(message);
        this.message = message;
        log.info("发布 ConfSendEvent 事件：message:{} ", message);
    }

    public MessageDO getMessage() {
        return message;
    }

    public void setMessage(MessageDO message) {
        this.message = message;
    }
}