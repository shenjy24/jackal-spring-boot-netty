package com.jonas.netty.client;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * NettyClient
 *
 * @author shenjy
 * @time 2024/3/28 19:51
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NettyClient {

    private final List<ClientSession> sessions = new CopyOnWriteArrayList<>();

    public List<ClientSession> listSession() {
        return sessions;
    }

    public void onConnect(Channel channel, String serverHost, int serverPort) {
        ClientSession clientSession = new ClientSession(channel, (InetSocketAddress) channel.localAddress(),
                new InetSocketAddress(serverHost, serverPort));
        sessions.add(clientSession);

        clientSession.onConnect();
    }

    /**
     * 发送消息
     *
     * @param sessionId 会话ID
     * @param data      二进制数据
     */
    public void sendMessage(String sessionId, Object data) {
        ClientSession session = getClientSessionBySessionId(sessionId);
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
        ClientSession session = getClientSessionByLocalAddress(clientAddress);
        if (session == null) {
            return;
        }
        session.sendMessage(data);
    }

    public ClientSession getClientSessionBySessionId(String sessionId) {
        for (ClientSession session : sessions) {
            if (session.getSessionId().equals(sessionId)) {
                return session;
            }
        }
        return null;
    }

    public ClientSession getClientSessionByLocalAddress(InetSocketAddress address) {
        for (ClientSession session : sessions) {
            if (session.getLocalAddress().equals(address)) {
                return session;
            }
        }
        return null;
    }

    public void removeSession(InetSocketAddress address) {
        sessions.removeIf(e -> e.getLocalAddress().equals(address));
    }
}
