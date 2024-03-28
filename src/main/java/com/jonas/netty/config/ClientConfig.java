package com.jonas.netty.config;

import com.jonas.netty.client.ClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Netty Client 配置
 *
 * @author shenjy
 * @time 2024/3/28 14:49
 */
@Configuration
public class ClientConfig {

    @Autowired
    private ClientChannelInitializer clientChannelInitializer;

    @Bean
    public Bootstrap bootstrap() {
        EventLoopGroup groupEventLoopGroup = new NioEventLoopGroup(2);
        Bootstrap clientBootStrap = new Bootstrap();
        clientBootStrap.group(groupEventLoopGroup)
                // 连接超时300毫秒
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 300)
                .channel(NioSocketChannel.class)
                .handler(clientChannelInitializer);

        return clientBootStrap;
    }
}
