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

    private final ConcurrentHashMap<String, ReviewAccumulator> reviews = new ConcurrentHashMap<>();
    private final ReviewRepository reviewRepository;

    public ReviewConsumer(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void consumeReview(ReviewInfo info) {
        reviews.compute(info.dishId(), (key, accumulator) -> {
            if (accumulator == null) {
                return new ReviewAccumulator(info.dishName(), 1, info.rating());
            }
            accumulator.setCount(accumulator.getCount() + 1);
            accumulator.setSumRating(accumulator.getSumRating() + info.rating());
            return accumulator;
        });
        log.info("Review received for dish: {}, rating: {}", info.dishId(), info.rating());
    }

    @Scheduled(fixedDelay = 5_000)
    public void flush(){
        if (reviews.isEmpty()) return;

        log.info("Reviews flush");
        reviews.forEach(this::persist);
        reviews.clear();
    }

    private void persist(String dishId, ReviewAccumulator accumulator){
        var review = reviewRepository.findById(dishId).orElseGet(
                () -> new Review(dishId, accumulator.getDishName(), 0.0, 0)
        );

        review.setDishName(accumulator.getDishName());
        int newTotalCount = review.getCount() + accumulator.getCount();
        double totalRatingSum = (review.getAverage() * review.getCount()) + accumulator.getSumRating();
        double newAverage = newTotalCount > 0 ? totalRatingSum / newTotalCount : 0.0;

        review.setCount(newTotalCount);
        review.setAverage(Math.round(newAverage * 10.0) / 10.0);

        reviewRepository.save(review);
    }
}
