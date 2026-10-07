package br.com.fiap.orderservice.CustomerOrder.dto;

import br.com.fiap.orderservice.CustomerOrder.CustomerOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerOrderResponse(
        Long id,
        Long dishId,
        int quantity,
        BigDecimal totalPrice,
        String status,
        LocalDateTime createdAt
) {
    public static CustomerOrderResponse from(CustomerOrder order) {
        return new CustomerOrderResponse(
                order.getId(),
                order.getDishId(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}
