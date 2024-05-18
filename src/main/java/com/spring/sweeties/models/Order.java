package com.spring.sweeties.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "total_price")
    private int totalPrice;

    @Column(name = "total_quantity")
    private int totalQuantity;

    @NotEmpty(message = "The city should not be empty")
    @Size(min = 2, max = 100, message = "The city must be between 2 and 100 characters long")
    @Column(name = "city")
    private String city;

    @NotEmpty(message = "The address should not be empty")
    @Size(min = 2, max = 100, message = "The address must be between 2 and 100 characters long")
    @Column(name = "address")
    private String address;

    @NotEmpty(message = "The phone number should not be empty")
    @Size(min = 2, max = 100, message = "The phone number must be between 2 and 100 characters long")
    @Column(name = "phoneNumber")
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;
}
