package com.jonas.netty.config;

import com.jonas.netty.server.ServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Netty Server 配置
 *
 * @author shenjy
 * @time 2024/3/28 14:49
 */
@Configuration
public class ServerConfig {

    @Value("${netty.server.port}")
    private int port;

    @Autowired
    private ServerChannelInitializer serverInitializer;

    @Bean
    public ServerBootstrap serverBootstrap() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(serverInitializer);

        return serverBootstrap;
    }

    @Bean
    public ChannelFuture channelFuture() throws InterruptedException {
        return serverBootstrap().bind(port).sync();
    }
}
