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

    /**
     * Fluxo completo de criação de pedido:
     * 1. Lock Pessimista: bloqueia a linha do prato no banco (SELECT ... FOR UPDATE) para evitar race condition.
     * 2. Checagem de Estoque: se não houver quantidade suficiente, lança HTTP 409 Conflict.
     * 3. Reserva do Estoque: deduz a quantidade do prato e calcula o valor total monetário.
     * 4. Chamada de Pagamento com Retry: chama o payment-service (com até 3 retries e backoff exponencial).
     *    - Se o pagamento falhar: lança HTTP 502 Bad Gateway e o @Transactional faz ROLLBACK (estoque fica intacto).
     * 5. Confirmação do Pedido: se o pagamento foi aprovado, persiste o CustomerOrder com status CONFIRMED.
     */
    @Transactional
    public CustomerOrder createOrder(CustomerOrderRequest request) {
        // 1. Busca o prato com lock pessimista
        Dish dish = dishRepository.findDishById(request.dishId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found with id " + request.dishId())
        );

        // 2. Valida se o estoque disponível atende a quantidade solicitada
        if (dish.getStock() < request.quantity()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Dish out of stock");
        }

        // 3. Reserva o estoque abatendo a quantidade pedida
        dish.setStock(dish.getStock() - request.quantity());
        dishRepository.save(dish);

        // Calcula o valor total usando BigDecimal para garantir precisão monetária
        BigDecimal totalPrice = dish.getPrice().multiply(BigDecimal.valueOf(request.quantity()));

        // 4. Processa o pagamento chamando o microsserviço payment-service via Eureka (Load Balanced)
        PaymentResponse paymentResponse;
        try {
            paymentResponse = paymentClient.tryPayment(totalPrice);
        } catch (Exception ex) {
            // Se todas as tentativas de pagamento falharem: registramos o erro detalhado nos logs do backend
            log.error("Todas as tentativas de pagamento falharam: {}", ex.getMessage());
            // Ao lançar a exceção (502 Bad Gateway), o @Transactional dá rollback automático (estoque intacto)
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Payment failed");
        }

        // Registra no log qual instância (8081 ou 8082) atendeu a chamada (evidência de Load Balance)
        log.info("Pagamento aprovado com sucesso pela instância: {}", paymentResponse.instance());

        // 5. Pagamento aprovado! Converte o DTO para Entidade e grava o pedido no banco H2
        CustomerOrder order = request.toEntity(totalPrice);
        return orderRepository.save(order);
    }

    public CustomerOrder getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with id " + id)
        );
    }
}
