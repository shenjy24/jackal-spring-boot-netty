package com.jonas.netty.server.session;

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
public class ServerSession {

    @Getter
    private final String sessionId;

    private final Channel channel;

    @Getter
    private final InetSocketAddress address;

    private SessionStatus sessionStatus = SessionStatus.INIT;

    public ServerSession(Channel channel, InetSocketAddress address) {
        this.sessionId = UUID.randomUUID().toString().replace("-", "");
        this.channel = channel;
        this.address = address;
    }

    public void onConnect() {
        sessionStatus = SessionStatus.CONNECTED;
    }

    public boolean onClose() {
        if (channel != null) {
            sessionStatus = SessionStatus.DISCONNECTED;
            channel.close();
        }
        return false;
    }

    /**
     * 发送数据
     *
     * @param data 二进制数据
     */
    public void sendMessage(Object data) {
        if (this.channel == null || this.address == null) {
            return;
        }
        if (this.sessionStatus != SessionStatus.CONNECTED) {
            log.error("session client[{}] send message error, ths session status is [{}]",
                    this.address, this.sessionStatus.status);
            return;
        }
        if (!this.isChannelOpen()) {
            log.error("session to client[{}] is not open, send message failed!", this.address);
            return;
        }

        ChannelFuture channelFuture = channel.writeAndFlush(data);
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                log.error("send netty message failed to {}", address);
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

}
