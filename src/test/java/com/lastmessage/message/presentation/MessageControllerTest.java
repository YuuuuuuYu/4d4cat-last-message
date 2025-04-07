package com.lastmessage.message.presentation;

import com.lastmessage.message.application.MessageService;
import com.lastmessage.message.domain.Message;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    private static final String TEST_CLIENT_ID = "test-client-id";
    private static final String TEST_CLIENT_IP = "192.168.1.1";

    @Test
    @DisplayName("홈 페이지 접속 시 메시지가 있으면 모델에 메시지가 추가되어야 한다")
    void home_WhenMessageExists_ShouldAddMessageToModel() throws Exception {
        // Given
        Message mockMessage = createTestMessage("Test message");
        when(messageService.getLastMessage(any(HttpServletRequest.class))).thenReturn(mockMessage);

        // When
        ResultActions result = mockMvc.perform(get("/")
                .cookie(new Cookie("lastMessageClientId", TEST_CLIENT_ID)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", mockMessage));

        verify(messageService).getLastMessage(any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("홈 페이지 접속 시 메시지가 없으면 모델에 null이 추가되어야 한다")
    void home_WhenNoMessage_ShouldAddNullToModel() throws Exception {
        // Given
        when(messageService.getLastMessage(any(HttpServletRequest.class))).thenReturn(null);

        // When
        ResultActions result = mockMvc.perform(get("/")
                .cookie(new Cookie("lastMessageClientId", TEST_CLIENT_ID)));

        // Then
        result.andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("message", nullValue()));

        verify(messageService).getLastMessage(any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("메시지 저장 요청 시 서비스가 호출되고 홈으로 리디렉션되어야 한다")
    void saveMessage_ShouldCallServiceAndRedirect() throws Exception {
        // Given
        String content = "New test message";

        // When
        ResultActions result = mockMvc.perform(post("/message")
                .param("content", content)
                .cookie(new Cookie("lastMessageClientId", TEST_CLIENT_ID)));

        // Then
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(messageService).saveMessage(eq(content), any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("빈 메시지 저장 요청 시에도 홈으로 리디렉션되어야 한다")
    void saveMessage_WithEmptyContent_ShouldRedirect() throws Exception {
        // Given
        String emptyContent = "";

        // When
        ResultActions result = mockMvc.perform(post("/message")
                .param("content", emptyContent)
                .cookie(new Cookie("lastMessageClientId", TEST_CLIENT_ID)));

        // Then
        result.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(messageService).saveMessage(eq(emptyContent), any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("클라이언트 ID 쿠키가 없을 때도 정상 작동해야 한다")
    void homeAndSaveMessage_WithoutClientIdCookie_ShouldWorkCorrectly() throws Exception {
        // Given
        Message mockMessage = createTestMessage("Test message");
        when(messageService.getLastMessage(any(HttpServletRequest.class))).thenReturn(mockMessage);

        // When - 홈 페이지 접속
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("message"));

        // When - 메시지 저장
        mockMvc.perform(post("/message").param("content", "Test content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Then
        verify(messageService).getLastMessage(any(HttpServletRequest.class));
        verify(messageService).saveMessage(eq("Test content"), any(HttpServletRequest.class));
    }

    // 테스트용 메시지 객체 생성 헬퍼 메서드
    private Message createTestMessage(String content) {
        Message message = new Message(content, TEST_CLIENT_IP);
        return message;
    }
}