package br.com.fiap.orderservice.Dish;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DishController {
    private final DishRepository repository;

    @GetMapping("/dishes")
    public List<Dish> getAllDishes() {
        return repository.findAll();
    }
}
