package br.com.fiap.orderservice.CustomerOrder;

import br.com.fiap.orderservice.Dish.Dish;
import br.com.fiap.orderservice.Dish.DishRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {
    private final DishRepository dishRepository;

    // Pedidos dos clientes
    @Transactional
    public void purchase(Long id, int quantity) {
        Dish dish = dishRepository.findDishById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dish not found with id " + id )
        );

        if (dish.getStock() < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dish not available with id " + id);
        }



        dish.setStock(dish.getStock() - quantity);



    }
}
