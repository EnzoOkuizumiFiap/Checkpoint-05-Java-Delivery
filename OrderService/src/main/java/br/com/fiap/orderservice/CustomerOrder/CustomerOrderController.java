package br.com.fiap.orderservice.CustomerOrder;

import br.com.fiap.orderservice.CustomerOrder.dto.CustomerOrderRequest;
import br.com.fiap.orderservice.CustomerOrder.dto.CustomerOrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService service;

    @PostMapping
    public ResponseEntity<CustomerOrderResponse> createOrder(@Valid @RequestBody CustomerOrderRequest request) {
        CustomerOrder order = service.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerOrderResponse.from(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderResponse> getOrderById(@PathVariable Long id) {
        CustomerOrder order = service.getOrderById(id);
        return ResponseEntity.ok(CustomerOrderResponse.from(order));
    }
}
