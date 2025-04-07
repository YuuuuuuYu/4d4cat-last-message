package com.lastmessage.aop;

import com.lastmessage.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MessageLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(MessageLoggingAspect.class);

    // 메시지 저장 성공 후 실행
    @AfterReturning(value = "execution(* com.lastmessage.message.application.MessageService.saveMessage(..)) && args(content, session, request)",
            argNames = "content,session,request")
    public void afterSaveMessage(String content, HttpSession session, HttpServletRequest request) {
        String clientIp = WebUtils.getClientIp(request);
        logger.info("Message successfully processed. IP: '{}', Content: '{}'", clientIp, content);
    }

    // 메시지 저장 중 예외 발생 시 실행
    @AfterThrowing(pointcut = "execution(* com.lastmessage.message.application.MessageService.saveMessage(..))", throwing = "ex")
    public void afterSaveMessageException(Exception ex) {
        logger.error("Error occurred while saving message: {}", ex.getMessage(), ex);
    }

    // 메시지 조회 시 실행
    @AfterReturning(pointcut = "execution(* com.lastmessage.message.application.MessageService.getLastMessage(..)) && args(session, request)",
            returning = "result", argNames = "result,session,request")
    public void afterGetMessage(Object result, HttpSession session, HttpServletRequest request) {
        String clientIp = WebUtils.getClientIp(request);
        if (result != null) {
            logger.debug("Retrieved message from clientIp: '{}'", clientIp);
        } else {
            logger.debug("No message found for clientIp: '{}'", clientIp);
        }
    }
}