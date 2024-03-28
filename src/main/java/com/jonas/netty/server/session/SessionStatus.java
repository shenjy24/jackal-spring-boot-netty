package com.jonas.netty.server.session;

import lombok.Getter;

/**
 * 会话状态
 *
 * @author shenjy
 * @time 2024/3/28 16:02
 */
@Getter
public enum SessionStatus {
    INIT(0),
    CONNECTED(1),
    DISCONNECTED(2);

    public final int status;

    SessionStatus(int status) {
        this.status = status;
    }
}
