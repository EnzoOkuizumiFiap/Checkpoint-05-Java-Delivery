package br.com.fiap.reviewservice.review;

import br.com.fiap.orderservice.Dish.Dish;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review {
    @Id
    private String dishId;
    private String dishName;
    private Double average;
    private Integer count;
}
