package com.netty.client.service;

import com.netty.client.ChannelClientRepository;
import com.netty.client.utils.Configure;
import com.netty.common.protobuf.Command;
import com.netty.common.protobuf.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class EchoService {
    @Autowired
    @Qualifier("channelClientRepository")
    ChannelClientRepository channelClientRepository;


    @Scheduled(fixedRate = 1000)
    private void echo() {
        if (channelClientRepository != null && channelClientRepository.get(Configure.CLIENT_ID) != null) {
            channelClientRepository.get(Configure.CLIENT_ID).writeAndFlush(
                    Message.MessageBase.newBuilder()
                            .setClientId(Configure.CLIENT_ID)
                            .setCmd(Command.CommandType.PUSH_DATA)
                            .setData("This is upload data")
                            .build()
            );
        }
    }
}

