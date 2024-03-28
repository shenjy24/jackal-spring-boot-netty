package com.jonas.netty.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 初始化通道加载器
 *
 * @author shenjy
 * @time 2024/3/28 14:02
 */
@Component
@RequiredArgsConstructor
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ServerHandlerAdapter serverHandlerAdapter;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 获取管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
                .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
                .addLast(serverHandlerAdapter);
    }
}
