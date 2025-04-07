package com.lastmessage.message.application;

import com.lastmessage.message.domain.Message;
import com.lastmessage.message.validator.MessageValidator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private static final String SESSION_MESSAGE_KEY = "lastMessage";

    private final MessageValidator messageValidator;

    public void saveMessage(HttpSession session, String content) {
        if (!messageValidator.isValid(content)) {
            return;
        }

        Message message = new Message(content);
        session.setAttribute(SESSION_MESSAGE_KEY, message);
    }

    public Message getLastMessage(HttpSession session) {
        return (Message) session.getAttribute(SESSION_MESSAGE_KEY);
    }
}