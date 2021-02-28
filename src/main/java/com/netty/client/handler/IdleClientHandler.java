package com.netty.client.handler;

import java.util.concurrent.TimeUnit;

import com.netty.client.utils.Configure;
import io.netty.channel.ChannelHandler;
import org.apache.log4j.Logger;

import com.netty.common.protobuf.Message;
import com.netty.client.TCPClient;
import com.netty.common.protobuf.Command.CommandType;
import com.netty.common.protobuf.Message.MessageBase;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("idleClientHandler")
@ChannelHandler.Sharable
public class IdleClientHandler extends SimpleChannelInboundHandler<Message> {
    public Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private TCPClient TCPClient;
    private int heartbeatCount = 0;


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            String type = "";
            if (event.state() == IdleState.READER_IDLE) {
                type = "read idle";
            } else if (event.state() == IdleState.WRITER_IDLE) {
                type = "write idle";
            } else if (event.state() == IdleState.ALL_IDLE) {
                type = "all idle";
            }
            log.debug(ctx.channel().remoteAddress() + "超时类型：" + type);
            sendPingMsg(ctx);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 发送ping消息
     *
     * @param context
     */
    protected void sendPingMsg(ChannelHandlerContext context) {
        context.writeAndFlush(
                MessageBase.newBuilder()
                        .setClientId(Configure.CLIENT_ID)
                        .setCmd(CommandType.PING)
                        .setData("This is a ping msg")
                        .build()
        );
        heartbeatCount++;
        log.info("Client sent ping msg to " + context.channel().remoteAddress() + ", count: " + heartbeatCount);
    }

    /**
     * 处理断开重连
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(() -> TCPClient.doConnect(new Bootstrap(), eventLoop), 10L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

    }
}
