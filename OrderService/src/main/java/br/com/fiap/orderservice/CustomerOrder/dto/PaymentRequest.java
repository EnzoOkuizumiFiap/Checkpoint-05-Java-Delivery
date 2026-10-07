package br.com.fiap.orderservice.CustomerOrder.dto;

import java.math.BigDecimal;

public record PaymentRequest(BigDecimal amount) {
}
