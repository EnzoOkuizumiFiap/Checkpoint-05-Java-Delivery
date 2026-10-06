package br.com.fiap.orderservice.Dish;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/")
public class DishController {
    private DishRepository repository;

    // All Dishes
    @RequestMapping("dishes")
    public List<Dish> getAllDishes() {
        return repository.findAll();
    }

    //

}
