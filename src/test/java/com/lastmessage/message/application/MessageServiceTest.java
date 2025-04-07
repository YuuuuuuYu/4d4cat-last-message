package com.lastmessage.message.application;

import com.lastmessage.message.domain.Message;
import com.lastmessage.message.validator.MessageValidator;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageValidator messageValidator;

    @Mock
    private HttpSession session;

    @InjectMocks
    private MessageService messageService;

    @Nested
    @DisplayName("메시지 저장")
    class SaveMessageTests {

        @Test
        @DisplayName("유효한 메시지인 경우 세션에 저장")
        void saveMessage_ValidContent_ShouldSaveToSession() {
            // Given
            String validContent = "message";
            when(messageValidator.isValid(validContent)).thenReturn(true);

            // When
            messageService.saveMessage(session, validContent);

            // Then
            verify(messageValidator).isValid(validContent);
            verify(session).setAttribute(eq("lastMessage"), any(Message.class));
        }

        @Test
        @DisplayName("유효하지 않은 메시지인 경우 세션에 저장하지 않음")
        void saveMessage_InvalidContent_ShouldNotSaveToSession() {
            // Given
            String invalidContent = "Invalid !@#$% message";
            when(messageValidator.isValid(invalidContent)).thenReturn(false);

            // When
            messageService.saveMessage(session, invalidContent);

            // Then
            verify(messageValidator).isValid(invalidContent);
            verifyNoMoreInteractions(session);
        }
    }

    @Nested
    @DisplayName("메시지 조회")
    class GetLastMessageTests {

        @Test
        @DisplayName("세션에 메시지가 있는 경우 해당 메시지를 반환")
        void getLastMessage_ExistingMessage_ShouldReturnMessage() {
            // Given
            Message mockMessage = new Message("Existing message");
            when(session.getAttribute("lastMessage")).thenReturn(mockMessage);

            // When
            Message result = messageService.getLastMessage(session);

            // Then
            assertEquals(mockMessage, result);
            verify(session).getAttribute("lastMessage");
        }

        @Test
        @DisplayName("세션에 메시지가 없는 경우 null을 반환")
        void getLastMessage_NoExistingMessage_ShouldReturnNull() {
            // Given
            when(session.getAttribute("lastMessage")).thenReturn(null);

            // When
            Message result = messageService.getLastMessage(session);

            // Then
            assertNull(result);
            verify(session).getAttribute("lastMessage");
        }
    }
}