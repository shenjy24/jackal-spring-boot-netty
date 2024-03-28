package com.jonas.netty.server;

import com.jonas.netty.session.ServerSession;
import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ReceiveService
 *
 * @author shenjy
 * @time 2024/3/28 14:39
 */
@Component
public class NettyServer {

    // 保存所有会话，分布式可以使用数据库等存储
    private final List<ServerSession> sessions = new CopyOnWriteArrayList<>();

    public List<ServerSession> listSession() {
        return sessions;
    }

    /**
     * 发送消息
     *
     * @param sessionId 会话ID
     * @param data      二进制数据
     */
    public void sendMessage(String sessionId, Object data) {
        ServerSession session = getSessionById(sessionId);
        if (session == null) {
            return;
        }
        session.sendMessage(data);
    }

    /**
     * 发送消息
     *
     * @param clientAddress 客户端地址
     * @param data          二进制数据
     */
    public void sendMessage(InetSocketAddress clientAddress, Object data) {
        ServerSession session = getSessionByAddress(clientAddress);
        if (session == null) {
            return;
        }
        session.sendMessage(data);
    }

    public void onReceiveMsg(Channel channel, InetSocketAddress sender, byte[] array) {
        // todo 可以使用队列进行缓冲
    }

    /**
     * 连接建立后的回调
     *
     * @param channel       客户端连接
     * @param clientAddress 客户端地址
     */
    public void onConnect(Channel channel, InetSocketAddress clientAddress) {
        if (containsSession(clientAddress)) {
            return;
        }
        ServerSession session = new ServerSession(channel, clientAddress);
        sessions.add(session);

        session.onConnect();
    }

    /**
     * 客户端断开连接的回调
     *
     * @param channel       客户端连接
     * @param clientAddress 客户端地址
     */
    public void onClose(Channel channel, InetSocketAddress clientAddress) {
        ServerSession session = getSessionByAddress(clientAddress);
        if (session == null) {
            return;
        }
        if (channel.isActive() || !session.onClose()) {
            channel.close();
        }
        removeSession(clientAddress);
    }

    public ServerSession getSessionByAddress(InetSocketAddress address) {
        for (ServerSession session : sessions) {
            if (session.getAddress().equals(address)) {
                return session;
            }
        }
        return null;
    }

    public ServerSession getSessionById(String sessionId) {
        for (ServerSession session : sessions) {
            if (session.getSessionId().equals(sessionId)) {
                return session;
            }
        }
        return null;
    }

    public boolean containsSession(InetSocketAddress address) {
        return getSessionByAddress(address) != null;
    }

    public boolean containsSession(String sessionId) {
        return getSessionById(sessionId) != null;
    }

    public void removeSession(InetSocketAddress address) {
        sessions.removeIf(e -> e.getAddress().equals(address));
    }
}
