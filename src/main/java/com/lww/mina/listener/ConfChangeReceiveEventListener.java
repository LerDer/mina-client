package com.lww.mina.listener;

import static com.lww.mina.util.Const.CONF;

import com.alibaba.fastjson.JSONObject;
import com.lww.mina.dto.MessageDO;
import com.lww.mina.event.ConfChangeEvent;
import com.lww.mina.init.MinaInitializer;
import com.lww.mina.util.Const;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

/**
 * 使用反射修改注入到对象中的值，修改环境中的值
 *
 * @author lww
 * @date 2020-07-09 11:27
 */
@Slf4j
@Component
public class ConfChangeReceiveEventListener {

    @Resource
    private ApplicationContext applicationContext;

    @EventListener
    public void onApplicationEvent(ConfChangeEvent event) throws Exception {
        log.info("接收到事件 ConfChangeReceiveEventListener_onApplicationEvent_event:{}", event.getClass());
        MessageDO message = event.getMessage();
        Map<String, Object> componentBeans = applicationContext.getBeansWithAnnotation(Component.class);
        changeValue(componentBeans, message);
    }

    private void changeValue(Map<String, Object> beans, MessageDO message) throws IllegalAccessException {
        log.info("ConfChangeReceiveEventListener_changeValue_message:{}", JSONObject.toJSONString(message));
        //获取当前环境
        ConfigurableEnvironment environment = (ConfigurableEnvironment) applicationContext.getEnvironment();
        //循环bean
        for (Object value : beans.values()) {
            Class<?> clazz = value.getClass();
            //获取所有字段
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                //设置访问权限
                field.setAccessible(true);
                //获取注解
                Value value1 = field.getAnnotation(Value.class);
                if (value1 != null) {
                    //获取注解的value
                    String value2 = value1.value();
                    //去掉 ${}
                    String replace = value2.replace(Const.PLACEHOLDER_PREFIX, "").replace(Const.PLACEHOLDER_SUFFIX, "").trim();
                    //是否是 mina.config 开头，mina.config.* 的字段是要从配置服务器获取的
                    if (replace.contains(CONF)) {
                        String property = environment.getProperty(replace);
                        //根据此值能从 environment 中取到 并且有配置
                        if (StringUtils.isNotBlank(property) && StringUtils.isNotBlank(message.getConfigValue())) {
                            log.info("原始值 ConfChangeReceiveEventListener_changeValue_replace:{}, property:{}", replace, property);
                            //反射修改已经注入到对象中的值
                            field.set(value, message.getConfigValue());


                            Properties props = new Properties();
                            props.put(replace, message.getConfigValue());
                            //修改 Environment 中的值，否则从 Environment 中获取，还是原来的值
                            environment.getPropertySources().addFirst(new PropertiesPropertySource(CONF, props));
                        }
                    }
                }
            }
        }
        Map<String, Object> configs = MinaInitializer.configs;
        for (Entry<String, Object> entry : configs.entrySet()) {
            String nowValue = environment.getProperty(entry.getKey());
            log.info("Environment 中 ConfChangeReceiveEventListener_changeValue_propertity:{}, nowValue:{}", entry.getKey(), nowValue);
        }
    }
}