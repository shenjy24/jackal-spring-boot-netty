package com.jonas.netty.controller;

import com.jonas.netty.client.ClientSession;
import com.jonas.netty.client.NettyClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ClientController
 *
 * @author shenjy
 * @time 2024/3/28 16:47
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/netty/client")
public class ClientController {

    private final NettyClient nettyClient;
    private final Bootstrap clientBootStrap;

    @PostMapping("/listSession")
    public List<SessionVo> listSession() {
        List<ClientSession> sessions = nettyClient.listSession();
        return toClientSessionVos(sessions);
    }

    @PostMapping("/connect")
    public void connect(String serverHost, int serverPort) {
        try {
            Channel channel = clientBootStrap.connect(serverHost, serverPort).sync().channel();
            nettyClient.onConnect(channel, serverHost, serverPort);
        } catch (InterruptedException e) {
            log.error("client connect failed! {}:{}", serverHost, serverPort);
        }
    }

    @PostMapping("/sendMessage")
    public void sendMessage(String sessionId, String message) {
        nettyClient.sendMessage(sessionId, message);
    }

    private List<SessionVo> toClientSessionVos(List<ClientSession> sessions) {
        if (CollectionUtils.isEmpty(sessions)) {
            return Collections.emptyList();
        }
        List<SessionVo> sessionVos = new ArrayList<>();
        for (ClientSession session : sessions) {
            SessionVo sessionVo = new SessionVo();
            sessionVo.setSessionId(session.getSessionId());
            sessionVo.setHost(session.getLocalAddress().getHostString());
            sessionVo.setPort(session.getLocalAddress().getPort());
            sessionVos.add(sessionVo);
        }
        return sessionVos;
    }
}
