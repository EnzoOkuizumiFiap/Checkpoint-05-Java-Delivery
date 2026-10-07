package br.com.fiap.reviewservice.review;

import br.com.fiap.reviewservice.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ReviewConsumer {

    private final ConcurrentHashMap<Long, ReviewAccumulator> reviews = new ConcurrentHashMap<>();
    private final ReviewRepository reviewRepository;

    public ReviewConsumer(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public synchronized void consumeReview(ReviewInfo info) {
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
    public synchronized void flush() {
        if (reviews.isEmpty()) {
            return;
        }

        Map<Long, ReviewAccumulator> batch = new HashMap<>(reviews);
        reviews.clear();

        log.info("Flushing {} dish review accumulators", batch.size());
        batch.forEach(this::persist);
    }

    private void persist(Long dishId, ReviewAccumulator accumulator) {
        ReviewSummary review = reviewRepository.findById(dishId)
                .orElseGet(() -> new ReviewSummary(dishId, accumulator.getDishName(), 0.0, 0));

        review.setDishName(accumulator.getDishName());

        int newTotalCount = review.getCount() + accumulator.getCount();
        double totalRatingSum = (review.getAverage() * review.getCount()) + accumulator.getSumRating();
        double newAverage = totalRatingSum / newTotalCount;

        review.setCount(newTotalCount);
        review.setAverage(Math.round(newAverage * 10.0) / 10.0);

        reviewRepository.save(review);
    }
}
