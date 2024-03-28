package com.jonas.netty.controller;

import com.jonas.netty.server.NettyServer;
import com.jonas.netty.server.session.ServerSession;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ServerController
 *
 * @author shenjy
 * @time 2024/3/28 16:47
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/netty/server")
public class ServerController {

    private final NettyServer nettyServer;

    @PostMapping("/listSession")
    public List<ServerSessionVo> listSession() {
        List<ServerSession> sessions = nettyServer.listSession();
        return toServerSessionVos(sessions);
    }

    @PostMapping("/sendMessage")
    public void sendMessage(String sessionId, String message) {
        nettyServer.sendMessage(sessionId, message);
    }

    private List<ServerSessionVo> toServerSessionVos(List<ServerSession> sessions) {
        if (CollectionUtils.isEmpty(sessions)) {
            return Collections.emptyList();
        }
        List<ServerSessionVo> sessionVos = new ArrayList<>();
        for (ServerSession session : sessions) {
            ServerSessionVo sessionVo = new ServerSessionVo();
            sessionVo.setSessionId(session.getSessionId());
            sessionVo.setHost(session.getAddress().getHostString());
            sessionVo.setPort(session.getAddress().getPort());
            sessionVos.add(sessionVo);
        }
        return sessionVos;
    }
}
