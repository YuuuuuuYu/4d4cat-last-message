package com.lastmessage.infrastructure.redis;

import com.lastmessage.message.domain.Message;
import com.lastmessage.message.domain.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMessageRepository implements MessageRepository {

    private final Map<String, Message> messageStore = new ConcurrentHashMap<>();

    /**
     * 클라이언트 ID로 메시지를 저장합니다.
     *
     * @param clientId 클라이언트 고유 식별자
     * @param message 저장할 메시지 객체
     */
    @Override
    public void saveMessage(String clientId, Message message) {
        messageStore.put(clientId, message);
    }

    /**
     * 클라이언트 ID로 마지막 메시지를 조회합니다.
     *
     * @param clientId 클라이언트 고유 식별자
     * @return 마지막 메시지, 없으면 빈 Optional
     */
    @Override
    public Optional<Message> getLastMessage(String clientId) {
        return Optional.ofNullable(messageStore.get(clientId));
    }

    /**
     * 클라이언트 ID에 해당하는 메시지를 삭제합니다.
     *
     * @param clientId 클라이언트 고유 식별자
     */
    @Override
    public void deleteMessage(String clientId) {
        messageStore.remove(clientId);
    }
}