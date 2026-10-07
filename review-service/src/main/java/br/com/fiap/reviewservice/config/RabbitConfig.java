package br.com.fiap.reviewservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "delivery.exchange";
    public static final String QUEUE_NAME = "reviews.queue";
    public static final String ROUTING_KEY = "reviews.new";

    @Bean
    public Queue reviewQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange reviewExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding reviewBinding() {
        return BindingBuilder
                .bind(reviewQueue())
                .to(reviewExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }
}
