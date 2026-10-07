package br.com.fiap.orderservice.CustomerOrder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClient {
    private final RestTemplate restTemplate;
    public record PaymentResponse(String status, String instance) {}

    @Retryable(
            includes = {ResponseStatusException.class},
            maxRetries = 3,
            delay = 500,
            jitter = 200,
            multiplier = 2,
            maxDelay = 5_000
    )
    public PaymentResponse tryPayment() {

        PaymentResponse response = restTemplate.postForObject(
                "http://PAYMENT-SERVICE/payments",
                null,
                PaymentResponse.class
        );

        if (response == null ) {
            log.info("\uD83D\uDD34 Payment service did not respond.\"");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment service did not respond");
        }

        return response;
    }
}
