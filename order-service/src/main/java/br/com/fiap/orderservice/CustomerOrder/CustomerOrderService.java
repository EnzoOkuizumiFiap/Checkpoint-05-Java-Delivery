package br.com.fiap.orderservice.CustomerOrder;

import br.com.fiap.orderservice.CustomerOrder.dto.CustomerOrderRequest;
import br.com.fiap.orderservice.CustomerOrder.dto.PaymentResponse;
import br.com.fiap.orderservice.Dish.Dish;
import br.com.fiap.orderservice.Dish.DishRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerOrderService {

    private final DishRepository dishRepository;
    private final CustomerOrderRepository orderRepository;
    private final PaymentClient paymentClient;

    // Pedidos dos clientes
    @Transactional
    public CustomerOrder createOrder(CustomerOrderRequest request) {
        // 1. Busca o prato com lock pessimista
        Dish dish = dishRepository.findDishById(request.dishId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found with id " + request.dishId())
        );

        // 2. Valida se há estoque suficiente
        if (dish.getStock() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dish out of stock");
        }

        // 3. Reserva o estoque e calcula o valor total
        dish.setStock(dish.getStock() - request.quantity());
        dishRepository.save(dish);

        BigDecimal totalPrice = dish.getPrice().multiply(BigDecimal.valueOf(request.quantity()));

        // 4. Processa o pagamento via microsserviço com Retry
        PaymentResponse paymentResponse;
        try {
            paymentResponse = paymentClient.tryPayment(totalPrice);
        } catch (Exception ex) {
            log.error("Todas as tentativas de pagamento falharam: {}", ex.getMessage());
            // Ao lançar a exceção, o @Transactional dá rollback automático (estoque permanece intacto)
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Payment failed: " + ex.getMessage());
        }

        log.info("Pagamento aprovado com sucesso pela instância: {}", paymentResponse.instance());

        // 5. Pagamento aprovado -> cria e salva o pedido confirmado
        CustomerOrder order = request.toEntity(totalPrice);
        return orderRepository.save(order);
    }

    public CustomerOrder getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id " + id)
        );
    }
}
