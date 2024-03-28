package com.jonas.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * ClientHandlerAdapter
 *
 * @author shenjy
 * @time 2024/3/28 19:46
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class ClientHandlerAdapter extends ChannelInboundHandlerAdapter {

    private final NettyClient nettyClient;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Channel channel = ctx.channel();
        log.info("channel active, {}", channel.remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        log.info("channel inactive, {}", channel.remoteAddress());
        InetSocketAddress clientAddress = (InetSocketAddress) channel.localAddress();

        ClientSession session = nettyClient.getClientSessionByLocalAddress(clientAddress);
        if (session != null) {
            session.onClose();
            nettyClient.removeSession(clientAddress);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        InetSocketAddress localAddress = getLocalAddress(ctx);
        ClientSession session = nettyClient.getClientSessionByLocalAddress(localAddress);
        if (session == null) {
            log.error("channelRead can not obtain session, so closed channel!");
            Channel channel = ctx.channel();
            if (channel != null) {
                channel.close();
            }
            return;
        }
        log.info("客户端收到信息：{}", msg.toString());

        // 读取数据
        if (msg instanceof ByteBuf bData) {
            session.onReceiveMessage(localAddress, bData.array());
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client channel exception: ", cause);
        if (ctx != null && ctx.channel() != null) {
            ClientSession decoClient = nettyClient.getClientSessionByLocalAddress(getLocalAddress(ctx));
            if (decoClient == null) {
                log.error("exceptionCaught can not obtain BungeeClient, so closed channel!");
                Channel channel = ctx.channel();
                if (channel != null) {
                    channel.close();
                }
            } else {
                decoClient.onClose();
            }
        }
        log.error("client exceptionCaught时候，没拿到ChannelHandlerContext的远程IP");
    }

    private InetSocketAddress getLocalAddress(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return (InetSocketAddress) channel.localAddress();
    }
}
