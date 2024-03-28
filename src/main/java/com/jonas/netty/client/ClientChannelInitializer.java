package com.jonas.netty.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * ClientChannelInitializer
 *
 * @author shenjy
 * @time 2024/3/28 19:39
 */
@Component
@RequiredArgsConstructor
public class ClientChannelInitializer extends ChannelInitializer<Channel> {

    private final ClientHandlerAdapter clientHandlerAdapter;

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast("decoder", new StringDecoder(CharsetUtil.UTF_8))
                .addLast("encoder", new StringEncoder(CharsetUtil.UTF_8))
                .addLast(clientHandlerAdapter);
    }
}
