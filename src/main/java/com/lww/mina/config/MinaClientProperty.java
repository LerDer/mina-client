package com.lww.mina.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mina客户端配置
 *
 * @author lww
 * @date 2020-07-08 19:17
 */
@ConfigurationProperties(prefix = "mina.client")
public class MinaClientProperty {

    /**
     * 服务器监听端口，默认 9123 Mian监听端口
     */
    private Integer minaPort = 9123;

    /**
     * http端口，默认8080
     */
    private Integer port = 8080;

    /**
     * 服务器ip地址，默认 127.0.0.1
     */
    private String serverAddress = "127.0.0.1";

    /**
     * 缓冲区大小，默认2048
     */
    private Integer readBufferSize = 2048;

    /**
     * 空闲时间，单位秒 默认 5 秒没操作就进入空闲状态
     */
    private Integer idelTimeOut = 10;

    /**
     * 客户端链接超时时间 设置超时时间，默认 30000 毫秒
     */
    private Long timeout = 30000L;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 当前环境，默认 local
     */
    private String env = "local";

    /**
     * server端 server.servlet.context-path 的值
     */
    private String serverPath;

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getMinaPort() {
        return minaPort;
    }

    public void setMinaPort(Integer minaPort) {
        this.minaPort = minaPort;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Integer getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(Integer readBufferSize) {
        this.readBufferSize = readBufferSize;
    }

    public Integer getIdelTimeOut() {
        return idelTimeOut;
    }

    public void setIdelTimeOut(Integer idelTimeOut) {
        this.idelTimeOut = idelTimeOut;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }
}
