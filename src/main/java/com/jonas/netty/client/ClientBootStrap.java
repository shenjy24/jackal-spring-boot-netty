package com.jonas.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@RequiredArgsConstructor
public class ClientBootStrap {

    @Getter
    private Bootstrap clientBootStrap;
    private EventLoopGroup groupEventLoopGroup;

    private final ClientChannelInitializer clientChannelInitializer;

    @PostConstruct
    private void init() {
        this.groupEventLoopGroup = new NioEventLoopGroup(2);
        this.clientBootStrap = new Bootstrap()
                .group(groupEventLoopGroup)
                // 连接超时300毫秒
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300)
                .channel(NioSocketChannel.class)
                .handler(clientChannelInitializer);
    }

    public void shutdown() {
        if (this.groupEventLoopGroup != null) {
            this.groupEventLoopGroup.shutdownGracefully();
        }
    }
}
