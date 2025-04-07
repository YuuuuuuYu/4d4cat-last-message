package com.lastmessage.message.application;

import com.lastmessage.message.domain.Message;
import com.lastmessage.message.domain.MessageRepository;
import com.lastmessage.message.validator.MessageValidator;
import com.lastmessage.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final String SESSION_MESSAGE_KEY = "lastMessage";

    private final MessageValidator messageValidator;
    private final MessageRepository messageRepository;

    public Message saveMessage(String content, HttpServletRequest request) {
        if (!messageValidator.isValid(content)) {
            return null;
        }

        String clientIp = WebUtils.getClientIp(request);
        Message message = new Message(content, clientIp);
        messageRepository.saveMessage(message);

        return message;
    }

    public Message getLastMessage(HttpServletRequest request) {
        Optional<Message> message = messageRepository.getLastMessage();
        if (message.isPresent()) {
            return message.get();
        } else {
            return null;
        }
    }
}