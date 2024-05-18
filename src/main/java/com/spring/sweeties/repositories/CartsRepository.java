package com.spring.sweeties.repositories;

import com.spring.sweeties.models.Cart;
import com.spring.sweeties.models.Person;
import com.spring.sweeties.models.Sweetness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartsRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByPerson(Person person);

    Cart findByPersonAndSweetness(Person person, Sweetness sweetness);
}
