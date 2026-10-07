package br.com.fiap.orderservice.CustomerOrder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerOrderController {
    private final CustomerOrderService service;

    public record PurchaseResponse(String status) {}

    @PostMapping("orders")
    public ResponseEntity<PurchaseResponse> purchase(Long id, int quantity){
        try {
            service.purchase(id, quantity);
            return ResponseEntity.ok(new PurchaseResponse("Dish purchased successfully for id: " + id));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(new PurchaseResponse("ERROR: " + ex.getMessage()));
        }
    }
}
