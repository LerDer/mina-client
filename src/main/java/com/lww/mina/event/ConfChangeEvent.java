package com.lww.mina.event;

import com.lww.mina.dto.MessageDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * 配置更新事件
 *
 * @author lww
 * @date 2020-07-09 11:26
 */
@Slf4j
public class ConfChangeEvent extends ApplicationEvent {

    private MessageDO message;

    public ConfChangeEvent(MessageDO message) {
        super(message);
        log.info("发布 ConfChangeEvent 事件：message:{} ", message);
        this.message = message;
    }

    public MessageDO getMessage() {
        return message;
    }

    public void setMessage(MessageDO message) {
        this.message = message;
    }

}

