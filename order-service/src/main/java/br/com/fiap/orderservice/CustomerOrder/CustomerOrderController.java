package br.com.fiap.orderservice.CustomerOrder;

import br.com.fiap.orderservice.CustomerOrder.dto.CustomerOrderRequest;
import br.com.fiap.orderservice.CustomerOrder.dto.CustomerOrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService service;
    private final OrderRateLimiter rateLimiter;

    @PostMapping
    public ResponseEntity<CustomerOrderResponse> createOrder(@Valid @RequestBody CustomerOrderRequest request) {
        // 1. Verificação do Rate Limiting (máximo 20 requisições por segundo)
        if (!rateLimiter.tryAcquire()) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded. Maximum 20 requests per second."
            );
        }

        // 2. Executa a regra de negócio do pedido
        CustomerOrder order = service.createOrder(request);

        // 3. Retorna HTTP 201 Created contendo os dados do pedido confirmado
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerOrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderResponse> getOrderById(@PathVariable Long id) {
        CustomerOrder order = service.getOrderById(id);
        return ResponseEntity.ok(CustomerOrderResponse.from(order));
    }
}
