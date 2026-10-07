package br.com.fiap.orderservice.Review;

import br.com.fiap.orderservice.Dish.Dish;
import br.com.fiap.orderservice.Review.dto.ReviewRequest;
import br.com.fiap.orderservice.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(ReviewRequest request, Dish dish) {
        ReviewMessage message = new ReviewMessage(
                request.dishId(),
                dish.getName(),
                request.rating(),
                request.comment()
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                message
        );
    }

    public record ReviewMessage(
            Long dishId,
            String dishName,
            Integer rating,
            String comment
    ) {
    }
}
