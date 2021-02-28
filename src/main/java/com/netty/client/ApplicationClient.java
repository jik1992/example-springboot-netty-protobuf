package com.netty.client;

import com.netty.client.netty.ChannelClientRepository;
import com.netty.client.netty.TCPClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;

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