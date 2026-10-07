package br.com.fiap.orderservice.Dish;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DishController {
    private final DishRepository repository;

    @GetMapping("/dishes")
    public List<Dish> getAllDishes() {
        return repository.findAll();
    }

    @GetMapping("/dishes/{id}")
    public Dish getDishById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dish not found with id " + id));
    }
}
