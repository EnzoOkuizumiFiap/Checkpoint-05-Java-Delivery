package br.com.fiap.orderservice.CustomerOrder;

import br.com.fiap.orderservice.Dish.Dish;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private Dish dishId;
}
