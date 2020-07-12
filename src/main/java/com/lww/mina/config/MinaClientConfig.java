package com.lww.mina.config;

import com.alibaba.fastjson.JSONObject;
import com.lww.mina.dto.MessageDO;
import com.lww.mina.filter.ClientKeepAliveFactoryImpl;
import com.lww.mina.handler.ConfigClientHandler;
import com.lww.mina.protocol.MessagePack;
import com.lww.mina.protocol.MessageProtocolCodecFactory;
import com.lww.mina.util.Const;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.Assert;

/**
 * 客户端Mina配置
 *
 * @author lww
 * @date 2020-07-08 19:17
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties(MinaClientProperty.class)
@ComponentScan(basePackages = "com.lww.mina")
public class MinaClientConfig {

    @Resource
    private MinaClientProperty config;

    /**
     * 配置mina的日志过滤器
     */
    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    /**
     * 编解码器filter
     */
    @Bean
    public ProtocolCodecFilter protocolCodecFilter() {
        return new ProtocolCodecFilter(new MessageProtocolCodecFactory());
    }

    /**
     * 心跳检测
     */
    @Bean
    public ClientKeepAliveFactoryImpl keepAliveFactoryImpl() {
        return new ClientKeepAliveFactoryImpl();
    }

    /**
     * 心跳filter
     */
    @Bean
    public KeepAliveFilter keepAliveFilter(ClientKeepAliveFactoryImpl keepAliveFactory) {
        // 注入心跳工厂，读写空闲
        KeepAliveFilter filter = new KeepAliveFilter(keepAliveFactory, IdleStatus.BOTH_IDLE);
        // 设置是否forward到下一个filter
        filter.setForwardEvent(true);
        // 设置心跳频率 5秒一次
        filter.setRequestInterval(Const.HEART_BEAT_RATE);
        return filter;
    }

    /**
     * 将过滤器注入到mina的链式管理器中
     */
    @Bean
    public DefaultIoFilterChainBuilder defaultIoFilterChainBuilder(LoggingFilter loggingFilter,
            ProtocolCodecFilter protocolCodecFilter, KeepAliveFilter keepAliveFilter) {
        DefaultIoFilterChainBuilder chainBuilder = new DefaultIoFilterChainBuilder();
        Map<String, IoFilter> filters = new LinkedHashMap<>();
        //日志
        filters.put("logger", loggingFilter);
        //编码 解码
        filters.put("codec", protocolCodecFilter);
        //心跳
        filters.put("keepAliveFilter", keepAliveFilter);
        chainBuilder.setFilters(filters);
        return chainBuilder;
    }

    @Bean
    public InetSocketAddress inetSocketAddress() {
        return new InetSocketAddress(config.getServerAddress(), config.getMinaPort());
    }

    /**
     * 开启mina的client服务，并设置对应的参数
     */
    @Bean
    public IoConnector ioConnector(DefaultIoFilterChainBuilder filterChainBuilder, InetSocketAddress inetSocketAddress) {
        Assert.isTrue(StringUtils.isNotBlank(config.getProjectName()), "项目名称不能为空！");
        //1、创建客户端IoService  非阻塞的客户端
        IoConnector connector = new NioSocketConnector();
        //客户端链接超时时间  设置超时时间
        connector.setConnectTimeoutMillis(config.getTimeout());
        //2、客户端过滤器  设置编码解码器
        connector.setFilterChainBuilder(filterChainBuilder);
        connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, config.getIdelTimeOut());
        //第一次连接 在服务端校验这个值，不做处理，在客户端为了绑定session
        MessageDO message = new MessageDO();
        message.setProjectName(config.getProjectName());
        message.setPropertyValue(Const.CONF);
        message.setEnvValue(config.getEnv());
        MessagePack pack = new MessagePack(Const.BASE, JSONObject.toJSONString(message));
        //设置handler 发送消息
        connector.setHandler(new ConfigClientHandler(pack));
        //连接服务端
        connector.connect(inetSocketAddress);
        return connector;
    }
}
