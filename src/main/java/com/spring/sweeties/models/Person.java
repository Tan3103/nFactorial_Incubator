package com.spring.sweeties.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Person")
public class Person {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "The firstname should not be empty")
    @Size(min = 2, max = 100, message = "The firstname must be between 2 and 100 characters long")
    @Column(name = "firstname")
    private String firstname;

    @NotEmpty(message = "The lastname should not be empty")
    @Size(min = 2, max = 100, message = "The lastname must be between 2 and 100 characters long")
    @Column(name = "lastname")
    private String lastname;

    @NotEmpty(message = "The email should not be empty")
    @Email
    @Column(name = "email")
    private String email;

    @NotEmpty(message = "The password should not be empty")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    private String avatar;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Cart> carts;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<Order> orders;
}
