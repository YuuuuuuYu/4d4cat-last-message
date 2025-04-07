package com.lastmessage.message;

import com.lastmessage.message.domain.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MessageIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("초기 메시지 저장 및 조회")
    void fullCycle_SaveAndRetrieveMessage() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Step 1: 초기 메시지는 null이어야 함
        MvcResult result = mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("message", nullValue()))
                .andReturn();

        assertNull(result.getModelAndView().getModel().get("message"));

        // Step 2: 메시지 저장
        String validMessage = "Hello World";
        mockMvc.perform(post("/message")
                        .param("content", validMessage)
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        // Step 3: 메시지 조회
        result = mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andReturn();

        Object messageObj = result.getModelAndView().getModel().get("message");
        assertTrue(messageObj instanceof Message);
        assertEquals(validMessage, ((Message) messageObj).getContent());

        // Step 4: 유효하지 않은 메시지 저장
        String invalidMessage = "Invalid@Message!";
        mockMvc.perform(post("/message")
                        .param("content", invalidMessage)
                        .session(session))
                .andExpect(status().is3xxRedirection());

        // Step 5: 메시지 조회 (수정 되지 않아야 함)
        result = mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andReturn();

        messageObj = result.getModelAndView().getModel().get("message");
        assertEquals(validMessage, ((Message) messageObj).getContent());
    }
}