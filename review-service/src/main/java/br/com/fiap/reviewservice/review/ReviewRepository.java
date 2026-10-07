package br.com.fiap.reviewservice.review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<ReviewSummary, Long> {
    List<ReviewSummary> findAllByOrderByAverageDesc();
}
