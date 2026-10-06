package br.com.fiap.orderservice.data;

import br.com.fiap.orderservice.Dish.Dish;
import br.com.fiap.orderservice.Dish.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoader {
    private final DishRepository dishRepository;

    @Bean
    CommandLineRunner loadDishes() {
        return args -> {
            if (dishRepository.count() > 0) {
                return;
            }

            dishRepository.saveAll(List.of(
                    new Dish(null, "House Burger", "Brioche bun", new BigDecimal("39.90"), 10),
                    new Dish(null, "Classic Cheeseburger", "Beef burger with cheddar cheese", new BigDecimal("34.90"), 10),
                    new Dish(null, "Crispy Chicken", "Crispy chicken sandwich with lettuce", new BigDecimal("32.90"), 8),
                    new Dish(null, "Veggie Burger", "Vegetable patty with fresh salad", new BigDecimal("29.90"), 6),
                    new Dish(null, "Bacon Burger", "Beef burger with bacon and barbecue sauce", new BigDecimal("42.90"), 7)
            ));
        };
    }
}
