package com.lww.mina.job;

import com.lww.mina.config.MinaClientProperty;
import com.lww.mina.dto.MessageDO;
import com.lww.mina.event.ConfSendEvent;
import com.lww.mina.inject.MyInjectProcessor;
import com.lww.mina.util.CommonUtil;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author lww
 * @date 2020-07-09 11:34
 */
@Slf4j
@Component
public class CheckAndPullJob {

    @Resource
    private ApplicationContext context;

    @Resource
    private MinaClientProperty config;

    @Scheduled(cron = "0 * * * * ?")
    public void checkAndPull() {
        long now = System.currentTimeMillis();
        log.info("CheckAndPullJob_checkAndPull_start_time:{}", CommonUtil.getNowTimeString());
        Map<String, Object> configs = MyInjectProcessor.configs;
        for (Entry<String, Object> entry : configs.entrySet()) {
            log.info("发布事件 CheckAndPullJob_checkAndPull_entry:{}", entry.getValue().toString());
            MessageDO message = new MessageDO();
            message.setPropertyValue(entry.getValue().toString());
            message.setProjectName(config.getProjectName());
            message.setEnvValue(config.getEnv());
            context.publishEvent(new ConfSendEvent(message));
        }
        log.info("CheckAndPullJob_checkAndPull_end_time:{}", CommonUtil.getNowTimeString());
        log.info("CheckAndPullJob_checkAndPull_耗时:{}", (System.currentTimeMillis() - now) / 1000);
    }
}
