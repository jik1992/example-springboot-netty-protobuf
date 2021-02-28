package com.netty.client.netty;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TCPClient {

    public Logger log = Logger.getLogger(this.getClass());

    private final static String HOST = "127.0.0.1";
    private final static int PORT = 8090;
    @Autowired
    @Qualifier("clientChannelInitializer")
    private ClientChannelInitializer clientChannelInitializer;

    private EventLoopGroup loop = new NioEventLoopGroup();

    public void start() throws Exception {
        try {
            doConnect(new Bootstrap(), loop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * netty client 连接，连接失败10秒后重试连接
     */
    public Bootstrap doConnect(Bootstrap bootstrap, EventLoopGroup eventLoopGroup) {
        if (bootstrap != null) {
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(clientChannelInitializer);
            bootstrap.remoteAddress(HOST, PORT);
            ChannelFuture f = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
                final EventLoop eventLoop = futureListener.channel().eventLoop();
                if (!futureListener.isSuccess()) {
                    log.warn("Failed to connect to server, try connect after 10s");
                    futureListener.channel().eventLoop().schedule(() -> doConnect(new Bootstrap(), eventLoop), 10, TimeUnit.SECONDS);
                }
            });
        }
        return bootstrap;
    }
}
