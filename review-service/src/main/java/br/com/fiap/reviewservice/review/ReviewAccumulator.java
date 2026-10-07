package br.com.fiap.reviewservice.review;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewAccumulator {
    private String dishName;
    private int count;
    private int sumRating;
}