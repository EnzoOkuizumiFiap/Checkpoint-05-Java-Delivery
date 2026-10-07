package br.com.fiap.orderservice.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        You are the restaurant's customer service assistant.
                        Answer only questions related to the restaurant menu, dishes, prices, stock,
                        ingredients and suggestions based on the current menu.
                        Answer briefly and always in Portuguese.
                        If the question is unrelated to the restaurant, politely refuse and bring the
                        conversation back to the restaurant menu.
                        Never invent dishes, prices or stock. Use only the menu supplied in the user message.
                        """)
                .build();
    }
}
