package com.lastmessage.message.application;

import com.lastmessage.message.domain.Message;
import com.lastmessage.message.domain.MessageRepository;
import com.lastmessage.message.validator.MessageValidator;
import com.lastmessage.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageRepository messageRepository;

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
            boolean isValid = MessageValidator.isValid(validContent);

            // When
            Message result = messageService.saveMessage(validContent, request);

            // Then
            assertThat(isValid).isTrue();
            assertThat(result.getContent()).isEqualTo(validContent);
            assertThat(result.getClientIp()).isEqualTo(WebUtils.getClientIp(request));
            verify(messageRepository).saveMessage(any(Message.class));
        }

        @Test
        @DisplayName("유효하지 않은 메시지인 경우 세션에 저장하지 않음")
        void saveMessage_InvalidContent_ShouldNotSaveToSession() {
            // Given
            String invalidContent = "Invalid !@#$% message";
            boolean isValid = MessageValidator.isValid(invalidContent);

            // When
            Message result = messageService.saveMessage(invalidContent, request);

            // Then
            assertThat(isValid).isFalse();
            assertThat(result).isNull();
            verifyNoMoreInteractions(messageRepository);
        }
    }

    @Nested
    @DisplayName("메시지 조회")
    class GetLastMessageTests {

        @Test
        @DisplayName("세션에 메시지가 있는 경우 해당 메시지를 반환")
        void getLastMessage_ExistingMessage_ShouldReturnMessage() {
            // Given
            Message mockMessage = new Message("Existing message", "1.2.3.4");
            when(messageRepository.getLastMessage()).thenReturn(Optional.of(mockMessage));

            // When
            Message result = messageService.getLastMessage(request);

            // Then
            assertThat(result).isSameAs(mockMessage);

            verify(messageRepository).getLastMessage();
        }

        @Test
        @DisplayName("세션에 메시지가 없는 경우 null을 반환")
        void getLastMessage_NoExistingMessage_ShouldReturnNull() {
            // Given
            when(messageRepository.getLastMessage()).thenReturn(Optional.empty());

            // When
            Message result = messageService.getLastMessage(request);

            // Then
            assertThat(result).isNull();

            verify(messageRepository).getLastMessage();
        }
    }
}