package br.com.fiap.reviewservice.review;

import br.com.fiap.reviewservice.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ReviewConsumer {

    private final ConcurrentHashMap<String, Integer> reviews = new ConcurrentHashMap<>();
    private final ReviewRepository reviewRepository;

    public ReviewConsumer(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consumeReview(String dishId) {
        reviews.merge(dishId, 1, Integer::sum);
        log.info("Review sent to dish: {}, total: {}", dishId, reviews.get(dishId));
    }

    @Scheduled(fixedDelay = 3_000)
    public void flush(){
        log.info("Reviews flush");
        reviews.forEach(this::persist);
        reviews.clear();
    }

    private void persist(String dishId, Integer totalReviews){
        var review = reviewRepository.findById(dishId).orElseGet(
                () -> new Review(dishId, 0)
        );

        review.setTotalReviews(review.getTotalReviews() + totalReviews);

        reviewRepository.save(review);
    }
}
