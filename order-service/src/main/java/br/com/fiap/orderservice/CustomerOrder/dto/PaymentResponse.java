package br.com.fiap.orderservice.CustomerOrder.dto;

public record PaymentResponse(
        String status,
        Integer instance
) {
}
