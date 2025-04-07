package com.lastmessage.infrastructure.inmemory;

import com.lastmessage.message.domain.Message;
import com.lastmessage.message.domain.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMessageRepository implements MessageRepository {

    private static final String CLIENT_ID_COOKIE_NAME = "lastMessageClientId";

    private final Map<String, Message> messageStore = new ConcurrentHashMap<>();

    @Override
    public void saveMessage(Message message) {
        messageStore.put(CLIENT_ID_COOKIE_NAME, message);
    }

    @Override
    public Optional<Message> getLastMessage() {
        return Optional.ofNullable(messageStore.get(CLIENT_ID_COOKIE_NAME));
    }
}