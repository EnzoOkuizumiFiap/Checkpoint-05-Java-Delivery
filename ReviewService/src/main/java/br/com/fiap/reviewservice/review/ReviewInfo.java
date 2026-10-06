package br.com.fiap.reviewservice.review;

public record ReviewInfo(
        String dishId,
        String dishName,
        Integer rating,
        String comment
) {
}
