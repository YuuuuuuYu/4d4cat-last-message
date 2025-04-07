package com.lastmessage.message.domain;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String content;
    private final String clientIp;
    private final LocalDateTime timestamp;

    protected Message() {
        this.content = null;
        this.clientIp = null;
        this.timestamp = null;
    }

    public Message(String content, String clientIp) {
        this.content = content;
        this.clientIp = clientIp;
        this.timestamp = LocalDateTime.now();
    }
}