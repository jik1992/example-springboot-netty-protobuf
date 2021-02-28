package com.netty.client.handler;


import com.netty.client.ChannelClientRepository;
import com.netty.client.utils.Configure;
import com.netty.server.ChannelRepository;
import io.netty.channel.ChannelHandler;
import org.apache.log4j.Logger;

import com.netty.common.protobuf.Command.CommandType;
import com.netty.common.protobuf.Message.MessageBase;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("logicClientHandler")
@ChannelHandler.Sharable
public class LogicClientHandler extends SimpleChannelInboundHandler<MessageBase> {
    public Logger log = Logger.getLogger(this.getClass());



    @Autowired
    @Qualifier("channelClientRepository")
    ChannelClientRepository channelClientRepository;

    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        MessageBase.Builder authMsg = MessageBase.newBuilder();
        authMsg.setClientId(Configure.CLIENT_ID);
        authMsg.setCmd(CommandType.AUTH);
        authMsg.setData("This is auth data");
        ctx.writeAndFlush(authMsg.build());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("连接断开 ");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageBase msg) throws Exception {
        if (msg.getCmd().equals(CommandType.AUTH_BACK)) {
            log.debug("验证成功");
            channelClientRepository.put(msg.getClientId(), ctx.channel());
        } else if (msg.getCmd().equals(CommandType.PING)) {
            //接收到server发送的ping指令
            log.info(msg.getData());

        } else if (msg.getCmd().equals(CommandType.PONG)) {
            //接收到server发送的pong指令
            log.info(msg.getData());

        } else if (msg.getCmd().equals(CommandType.PUSH_DATA)) {
            //接收到server推送数据
            log.info(msg.getData());

        } else if (msg.getCmd().equals(CommandType.PUSH_DATA_BACK)) {
            //接收到server返回数据
            log.info(msg.getData());

        } else {
            log.info(msg.getData());
        }
    }
}
