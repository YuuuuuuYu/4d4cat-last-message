package com.lastmessage.message.presentation;

import com.lastmessage.message.application.MessageService;
import com.lastmessage.message.domain.Message;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    @DisplayName("세션에 메시지가 있으면 모델에 메시지가 추가되어야 함")
    void homePage_WithExistingMessage_ShouldAddMessageToModel() throws Exception {
        // Given
        Message mockMessage = new Message("Test message");
        when(messageService.getLastMessage(any(HttpSession.class))).thenReturn(mockMessage);

        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", mockMessage));

        verify(messageService).getLastMessage(any(HttpSession.class));
    }

    @Test
    @DisplayName("세션에 메시지가 없으면 모델에 null이 추가되어야 함")
    void homePage_WithNoMessage_ShouldAddNullToModel() throws Exception {
        // Given
        when(messageService.getLastMessage(any(HttpSession.class))).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("message", nullValue()));

        verify(messageService).getLastMessage(any(HttpSession.class));
    }

    @Test
    @DisplayName("유효한 메시지 저장 요청 시 서비스가 호출되고 홈으로 리다이렉트되어야 함")
    void saveMessage_WithValidContent_ShouldCallServiceAndRedirect() throws Exception {
        // Given
        String validContent = "Valid message content";

        // When & Then
        mockMvc.perform(post("/message")
                        .param("content", validContent))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(messageService).saveMessage(any(HttpSession.class), eq(validContent));
    }

    @Test
    @DisplayName("비어있는 메시지 저장 요청 시 서비스가 호출되지 않고 홈으로 리다이렉트되어야 함")
    void saveMessage_WithEmptyContent_ShouldNotCallServiceAndRedirect() throws Exception {
        // Given
        String emptyContent = "";

        // When & Then
        mockMvc.perform(post("/message")
                        .param("content", emptyContent))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verifyNoInteractions(messageService);
    }

    @Test
    @DisplayName("공백만 있는 메시지 저장 요청 시 서비스가 호출되지 않고 홈으로 리다이렉트되어야 함")
    void saveMessage_WithBlankContent_ShouldNotCallServiceAndRedirect() throws Exception {
        // Given
        String blankContent = " ";

        // When & Then
        mockMvc.perform(post("/message")
                        .param("content", blankContent))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verifyNoInteractions(messageService);
    }

    @Test
    @DisplayName("content 파라미터가 없는 요청 시 서비스가 호출되지 않고 BadRequest 응답을 반환해야 함")
    void saveMessage_WithNoContentParam_ShouldNotCall() throws Exception {
        // When & Then
        mockMvc.perform(post("/message"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(messageService);
    }
}