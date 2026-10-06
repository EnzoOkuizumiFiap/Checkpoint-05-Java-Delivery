package br.com.fiap.reviewservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "review-exchange";
    public static final String QUEUE_NAME = "review-queue";
    public static final String ROUTING_KEY = "review-key";

    @Bean
    public Queue reviewQueue(){
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange reviewExchange(){
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding reviewBinding(){
        return BindingBuilder
                .bind(reviewQueue())
                .to(reviewExchange())
                .with(ROUTING_KEY);
    }
}
