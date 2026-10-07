package br.com.fiap.orderservice.Review;

import br.com.fiap.orderservice.Review.dto.ReviewRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService service;

    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createReview(@Valid @RequestBody ReviewRequest request) {
        service.createReview(request);
    }
}
