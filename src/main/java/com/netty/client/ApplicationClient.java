package com.netty.client;

import com.netty.client.utils.Configure;
import com.netty.server.ChannelRepository;
import com.netty.server.ServerChannelInitializer;
import com.netty.server.TCPServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Shanqiang Ke
 * @version 1.0.0
 * @blog http://nosqlcoco.cnblogs.com
 * @since 2016-10-15
 */
@SpringBootApplication
@ComponentScan(value = "com.netty.client")
@PropertySource(value = "classpath:/application.properties")
public class ApplicationClient {
    @Configuration
    @Profile("production")
    @PropertySource("classpath:/application.properties")
    static class Production {
    }

    @Configuration
    @Profile("local")
    @PropertySource({"classpath:/application.properties"})
    static class Local {
    }

    @Bean(name = "channelClientRepository")
    public ChannelClientRepository channelClientRepository() {
        return new ChannelClientRepository();
    }


    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(ApplicationClient.class, args);
        TCPClient tcpClient = context.getBean(TCPClient.class);
        tcpClient.start();
    }
}