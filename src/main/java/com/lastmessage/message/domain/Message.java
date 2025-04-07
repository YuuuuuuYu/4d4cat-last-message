package com.lastmessage.message.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Message {

    private final String content;
    private final LocalDateTime timestamp;

    public Message(String content) {
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
}