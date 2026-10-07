package br.com.fiap.reviewservice.review;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository repository;

    @GetMapping("/reviews/ranking")
    public List<ReviewSummary> getRanking() {
        return repository.findAllByOrderByAverageDesc();
    }
}
