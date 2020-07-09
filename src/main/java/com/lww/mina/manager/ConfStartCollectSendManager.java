package com.lww.mina.manager;

import com.lww.mina.dto.MessageDO;
import com.lww.mina.event.ConfSendEvent;
import com.lww.mina.util.Const;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

/**
 * @author lww
 * @date 2020-07-09 11:33
 */
@Slf4j
public class ConfStartCollectSendManager implements SpringApplicationRunListener {

    public ConfStartCollectSendManager(SpringApplication application, String[] args) {
        super();
    }

    public static Map<String, Object> configs = new ConcurrentHashMap<>(16);

    private static final String PROPERTY_SOURCE_NAME = "applicationConfig";

    private static final String ENV_KEY = "mina.client.env";

    private static final String PROJECT_NAME = "mina.client.project-name";

    @Override
    public void started(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        MutablePropertySources propertySources = environment.getPropertySources();
        //遍历 Environment
        for (Object property : propertySources) {
            if (property instanceof MapPropertySource) {
                MapPropertySource propertySource = (MapPropertySource) property;
                //取到 applicationConfig 这个配置对象
                if (propertySource.getName().contains(PROPERTY_SOURCE_NAME)) {
                    String[] properties = propertySource.getPropertyNames();
                    for (String s : properties) {
                        //如果是以 mina.config 开头的，保存到 configs map中
                        if (s.startsWith(Const.CONF)) {
                            configs.put(s, propertySource.getProperty(s));
                        }
                    }
                }
            }
        }
        //发消息
        for (Entry<String, Object> entry : configs.entrySet()) {
            MessageDO message = new MessageDO();
            message.setProjectName(environment.getProperty(PROJECT_NAME));
            message.setPropertyValue(entry.getValue().toString());
            message.setEnvValue(StringUtils.isNotBlank(environment.getProperty(ENV_KEY)) ? environment.getProperty(ENV_KEY) : "local");
            //发送消息
            context.publishEvent(new ConfSendEvent(message));
        }
    }
}
