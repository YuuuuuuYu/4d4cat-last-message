package com.lastmessage.message.domain;

import java.util.Optional;

public interface MessageRepository {

    void saveMessage(Message message);
    Optional<Message> getLastMessage();
}
