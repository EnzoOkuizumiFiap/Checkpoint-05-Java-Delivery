package br.com.fiap.reviewservice.review;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReviewSummary {

    @Id
    private Long dishId;
    private String dishName;
    private Double average;
    private Integer count;
}
