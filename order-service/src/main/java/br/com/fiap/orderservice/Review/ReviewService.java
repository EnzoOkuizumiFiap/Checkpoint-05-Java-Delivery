package br.com.fiap.orderservice.Review;

import br.com.fiap.orderservice.Dish.Dish;
import br.com.fiap.orderservice.Dish.DishRepository;
import br.com.fiap.orderservice.Review.dto.ReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final DishRepository dishRepository;
    private final ReviewPublisher publisher;

    public void createReview(ReviewRequest request) {
        Dish dish = dishRepository.findById(request.dishId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dish not found with id " + request.dishId()
                ));

        publisher.publish(request, dish);
    }
}
