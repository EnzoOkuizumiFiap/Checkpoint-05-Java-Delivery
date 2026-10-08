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

    /**
     * Tenta processar o pagamento chamando o endpoint POST /payments do payment-service.
     *
     * Configuração do @Retryable:
     * - includes: captura RestClientException (erros 5xx lançados pelo RestTemplate ao receber falha simulada).
     * - maxRetries = 3: tenta até 3 repetições adicionais após a falha inicial.
     * - delay = 500: espera inicial de 500 milissegundos antes da 1ª repetição.
     * - multiplier = 2: backoff exponencial (o tempo dobra a cada nova tentativa: 500ms -> 1000ms -> 2000ms).
     * - jitter = 200: variação aleatória de até 200ms para evitar que múltiplos clientes repitam no exato mesmo instante.
     * - maxDelay = 5_000: tempo máximo de espera limitado a 5 segundos.
     */
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

        // Chamada HTTP via nome de serviço registrado no Eureka (Load Balanced)
        PaymentResponse response = restTemplate.postForObject(
                "http://PAYMENT-SERVICE/payments",
                new PaymentRequest(amount),
                PaymentResponse.class
        );

        // Se o payment-service retornar corpo vazio, lança exceção para disparar o retry
        if (response == null) {
            throw new RestClientException("Empty payment response received from payment-service");
        }

        log.info("Pagamento aprovado na instância {}: {}", response.instance(), response);
        return response;
    }
}
