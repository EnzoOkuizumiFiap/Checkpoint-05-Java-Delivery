package br.com.fiap.orderservice.Review.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull(message = "dishId is required")
        Long dishId,

        @NotNull(message = "rating is required")
        Integer rating,

        @NotNull(message = "comment is required")
        String comment
) {
    public
}
