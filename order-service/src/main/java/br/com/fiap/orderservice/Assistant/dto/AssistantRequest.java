package br.com.fiap.orderservice.Assistant.dto;

import jakarta.validation.constraints.NotBlank;

public record AssistantRequest(
        @NotBlank(message = "question is required")
        String question
) {
}
