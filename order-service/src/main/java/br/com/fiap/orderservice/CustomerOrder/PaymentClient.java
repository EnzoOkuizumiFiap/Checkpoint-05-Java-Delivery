package br.com.fiap.orderservice.CustomerOrder;

import br.com.fiap.orderservice.CustomerOrder.dto.PaymentRequest;
import br.com.fiap.orderservice.CustomerOrder.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestTemplate restTemplate;

    @Retryable(
            includes = {RestClientException.class},
            maxRetries = 3,
            delay = 500,
            jitter = 200,
            multiplier = 2,
            maxDelay = 5_000
    )
    public PaymentResponse tryPayment(BigDecimal amount) {
        log.info("Tentando processar pagamento no payment-service: R$ {}", amount);

        PaymentResponse response = restTemplate.postForObject(
                "http://PAYMENT-SERVICE/payments",
                new PaymentRequest(amount),
                PaymentResponse.class
        );

        if (response == null) {
            throw new RestClientException("Empty payment response received from payment-service");
        }

        log.info("Pagamento aprovado na instância {}: {}", response.instance(), response);
        return response;
    }
}
