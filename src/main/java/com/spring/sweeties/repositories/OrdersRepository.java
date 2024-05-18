package com.spring.sweeties.repositories;

import com.spring.sweeties.models.Order;
import com.spring.sweeties.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Integer> {
    List<Order> findByPerson(Person person);
}
