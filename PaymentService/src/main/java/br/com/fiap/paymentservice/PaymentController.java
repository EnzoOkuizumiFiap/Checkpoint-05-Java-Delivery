package br.com.fiap.paymentservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Value("${server.port:8081}")
    private int port;

    private final Random random = new Random();

    public record PaymentRequest(BigDecimal amount) {}
    public record PaymentResponse(String status, int instance) {}

    @PostMapping
    public PaymentResponse processPayment(@RequestBody(required = false) PaymentRequest request) {
        log.info("[Porta {}] Recebida solicitação de pagamento: {}", port, request);

        if (random.nextDouble() < 0.5) {
            log.warn("[Porta {}] 🔴 Falha simulada no pagamento (500)", port);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Simulated payment failure");
        }

        log.info("[Porta {}] 🟢 Pagamento aprovado com sucesso", port);
        return new PaymentResponse("APPROVED", port);
    }
}

