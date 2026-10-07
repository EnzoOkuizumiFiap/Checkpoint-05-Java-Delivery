package br.com.fiap.paymentservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {
    public record PaymentResponse(String status) {}

    @PostMapping
    public PaymentResponse processPayment() {
        var random = Math.random();

        if (random < 0.5) {
            log.info("\uD83D\uDD34 Payment processing failed. Please try again.\"");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "🔴 Payment processing failed. Please try again.");
        }
        log.info("\uD83D\uDFE2 Payment processed sucessfully.");
        return new PaymentResponse("🟢 Payment processed successfully.");
    }
}

