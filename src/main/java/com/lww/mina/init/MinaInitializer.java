package com.lww.mina.init;

import com.alibaba.fastjson.JSONObject;
import com.lww.mina.dto.MessageDO;
import com.lww.mina.util.Const;
import com.lww.mina.util.HttpUtils;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.util.Assert;

/**
 * 在 org.springframework.boot.SpringApplication#prepareContext 中执行，
 * 在bean创建注入之前，从服务器获取配置信息，如数据库等配置信息
 *
 * @author lww
 * @date 2020-07-11 16:50
 */
public class MinaInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final String PROPERTY_SOURCE_NAME = "applicationConfig";

    private static final String ENV_KEY = "mina.client.env";

    private static final String PROJECT_NAME = "mina.client.project-name";

    private static final String PORT = "mina.client.port";

    private static final String SERVER_ADDRESS = "mina.client.server-address";

    public static Map<String, Object> configs = new ConcurrentHashMap<>(16);

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        ConfigurableEnvironment environment = context.getEnvironment();
        MutablePropertySources sources = environment.getPropertySources();
        //遍历 Environment
        for (Object property : sources) {
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

        final String env = StringUtils.isNotBlank(environment.getProperty(ENV_KEY)) ? environment.getProperty(ENV_KEY) : "local";
        final String port = StringUtils.isNotBlank(environment.getProperty(PORT)) ? environment.getProperty(PORT) : "8080";
        final String address = StringUtils.isNotBlank(environment.getProperty(SERVER_ADDRESS)) ? environment.getProperty(SERVER_ADDRESS) : "127.0.0.1";
        final String projectName = environment.getProperty(PROJECT_NAME);
        final String remoteAddr = address.trim() + ":" + port.trim();
        //通过http请求获取配置，修改配置的值
        for (Entry<String, Object> entry : configs.entrySet()) {
            String value = entry.getValue().toString();
            String param = "projectName=" + projectName + "&env=" + env + "&propertyValue=" + value;
            String result = HttpUtils.sendGetHttp("http://" + remoteAddr + "/message/conf", param, null);
            if (StringUtils.isNotBlank(result)) {
                MessageDO messageDO = JSONObject.parseObject(result, MessageDO.class);
                Properties props = new Properties();
                props.put(entry.getKey(), messageDO.getConfigValue());
                //修改 Environment 中的值，否则从 Environment 中获取，还是原来的值
                environment.getPropertySources().addFirst(new PropertiesPropertySource(Const.CONF, props));
            } else {
                Assert.isTrue(false, "获取配置信息失败！");
            }
        }
    }
}
