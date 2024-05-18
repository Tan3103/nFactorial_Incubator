package com.spring.sweeties.services;

import com.spring.sweeties.models.Order;
import com.spring.sweeties.models.Person;
import com.spring.sweeties.repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrdersService {
    private final OrdersRepository ordersRepository;

    @Autowired
    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    @Transactional
    public void saveOrder(Order order) {
        ordersRepository.save(order);
    }

    public List<Order> findByPerson(Person person) {
        return ordersRepository.findByPerson(person);
    }

    public List<Order> findAll() {
        return ordersRepository.findAll();
    }
}
