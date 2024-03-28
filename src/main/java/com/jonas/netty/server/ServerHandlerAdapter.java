package com.jonas.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * ServerHandlerAdapter
 *
 * @author shenjy
 * @time 2024/3/28 14:09
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class ServerHandlerAdapter extends ChannelInboundHandlerAdapter {

    private final NettyServer nettyServer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        log.info("channel active，客户端信息:{}", channel.remoteAddress());
        InetSocketAddress clientAddress = (InetSocketAddress) channel.remoteAddress();

        nettyServer.onConnect(channel, clientAddress);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        Channel channel = ctx.channel();
        log.info("channel active，客户端信息:{}", channel.remoteAddress());
        InetSocketAddress clientAddress = (InetSocketAddress) channel.remoteAddress();

        nettyServer.onClose(channel, clientAddress);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("服务器收到信息：{}", msg.toString());
        // 读取数据
        if (msg instanceof ByteBuf byteBuf) {
            Channel channel = ctx.channel();
            SocketAddress remote1 = channel.remoteAddress();
            InetSocketAddress sender = (InetSocketAddress) remote1;
            nettyServer.onReceiveMsg(channel, sender, byteBuf.array());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (ctx != null && ctx.channel() != null) {
            SocketAddress remote1 = ctx.channel().remoteAddress();
            if (remote1 != null) {
                InetSocketAddress sender = (InetSocketAddress) remote1;
                log.error("exceptionCaught, sender[{}]", sender, cause);
                return;
            }
        }
        log.error("exceptionCaught时候，没拿到ChannelHandlerContext的远程IP");
    }
}
