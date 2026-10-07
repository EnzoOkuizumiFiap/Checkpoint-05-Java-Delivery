package br.com.fiap.reviewservice.review;

public record ReviewInfo(
        Long dishId,
        String dishName,
        Integer rating,
        String comment
) {
}
