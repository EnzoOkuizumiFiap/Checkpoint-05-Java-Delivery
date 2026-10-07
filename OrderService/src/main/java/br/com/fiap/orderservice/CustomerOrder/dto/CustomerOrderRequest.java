package br.com.fiap.orderservice.CustomerOrder.dto;

import br.com.fiap.orderservice.CustomerOrder.CustomerOrder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerOrderRequest(
        @NotNull(message = "dishId is required")
        Long dishId,

        @NotNull(message = "quantity is required")
        @Min(value = 1, message = "quantity must be at least 1")
        Integer quantity
) {
    public CustomerOrder toEntity(BigDecimal totalPrice) {
        return new CustomerOrder(
                null,
                this.dishId,
                this.quantity,
                totalPrice,
                "CONFIRMED",
                LocalDateTime.now()
        );
    }
}
