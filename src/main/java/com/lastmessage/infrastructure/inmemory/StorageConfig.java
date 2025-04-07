package com.lastmessage.infrastructure.inmemory;

import com.lastmessage.message.domain.MessageRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    @Bean
    public MessageRepository messageRepository() {
        return new InMemoryMessageRepository();
    }
}
