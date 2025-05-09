package com.lastmessage.message.presentation;

import com.lastmessage.message.application.MessageService;
import com.lastmessage.message.domain.Message;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        Message lastMessage = messageService.getLastMessage(request);
        model.addAttribute("message", lastMessage);
        return "index";
    }

    @PostMapping("/message")
    public String saveMessage(@RequestParam("content") String content, HttpServletRequest request) {
        messageService.saveMessage(content, request);
        return "redirect:/";
    }
}