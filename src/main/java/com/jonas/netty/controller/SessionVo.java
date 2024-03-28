package com.jonas.netty.controller;

import lombok.Data;

/**
 * ServerSessionVo
 *
 * @author shenjy
 * @time 2024/3/28 16:48
 */
@Data
public class SessionVo {
    private String sessionId;
    private String host;
    private Integer port;
}
