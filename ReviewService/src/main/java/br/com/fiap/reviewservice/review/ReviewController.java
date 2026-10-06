package br.com.fiap.reviewservice.review;

import br.com.fiap.reviewservice.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @GetMapping("/reviews/ranking")
    public List<Review> getAllReviews() { return repository.findAll(); }

    @PostMapping("reviews/{dishId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createReview(@PathVariable String dishId) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                RabbitConfig.ROUTING_KEY,
                dishId
        );
    }
}
