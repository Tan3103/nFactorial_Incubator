package com.spring.sweeties.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Sweetness")
public class Sweetness {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "The name should not be empty")
    @Size(min = 2, max = 100, message = "The name must be between 2 and 100 characters long")
    private String name;

    @NotEmpty(message = "The catalog should not be empty")
    @Size(min = 2, max = 100, message = "The catalog must be between 2 and 100 characters long")
    private String catalog;

    @Min(value = 0, message = "Price should be more 0")
    private int price;

    @Min(value = 0, message = "Quantity should be more 0")
    private int quantity;

    @Size(min = 10, max = 700, message = "The description must be between 10 and 700 characters long")
    private String description;

    private String image;

    @OneToMany(mappedBy = "sweetness", cascade = CascadeType.ALL)
    private List<Cart> carts;
}
