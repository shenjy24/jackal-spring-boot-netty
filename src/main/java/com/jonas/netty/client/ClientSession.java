package com.jonas.netty.client;

import com.jonas.netty.server.session.SessionStatus;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * 服务端会话信息
 * 可以存储在数据库里面的，这里只为模拟
 *
 * @author shenjy
 * @time 2024/3/28 15:50
 */
@Slf4j
public class ClientSession {

    @Getter
    private final String sessionId;

    @Getter
    private final Channel channel;

    // 本地地址
    @Getter
    private final InetSocketAddress localAddress;

    @Getter
    private final InetSocketAddress serverAddress;

    private SessionStatus sessionStatus = SessionStatus.INIT;

    public ClientSession(Channel channel, InetSocketAddress localAddress, InetSocketAddress serverAddress) {
        this.sessionId = UUID.randomUUID().toString().replace("-", "");
        this.channel = channel;
        this.localAddress = localAddress;
        this.serverAddress = serverAddress;
    }

    public void onConnect() {
        sessionStatus = SessionStatus.CONNECTED;
    }

    public void onClose() {
        if (channel != null) {
            log.info("client close, {}", localAddress);
            sessionStatus = SessionStatus.DISCONNECTED;
            channel.close();
        }
    }

    /**
     * 发送数据
     *
     * @param data 二进制数据
     */
    public void sendMessage(Object data) {
        if (this.channel == null || this.localAddress == null) {
            return;
        }
        if (this.sessionStatus != SessionStatus.CONNECTED) {
            log.error("session client[{}] send message error, ths session status is [{}]",
                    this.localAddress, this.sessionStatus.status);
            return;
        }
        if (!this.isChannelOpen()) {
            log.error("session to client[{}] is not open, send message failed!", this.localAddress);
            return;
        }

        ChannelFuture channelFuture = channel.writeAndFlush(data);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("send netty message failed to {}", localAddress);
            }
        });
    }

    /**
     * 判断channel是否open
     *
     * @return channel是否open
     */
    private boolean isChannelOpen() {
        if (channel == null) {
            return false;
        }
        return channel.isOpen();
    }

    public void onReceiveMessage(InetSocketAddress localAddress, byte[] array) {

    }
}
