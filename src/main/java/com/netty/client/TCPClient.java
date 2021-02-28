package com.netty.client;

import java.util.concurrent.TimeUnit;

import com.netty.server.ServerChannelInitializer;
import org.apache.log4j.Logger;

import com.netty.client.handler.IdleClientHandler;
import com.netty.client.handler.LogicClientHandler;
import com.netty.common.protobuf.Message.MessageBase;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
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
