package com.spring.sweeties.services;

import com.spring.sweeties.models.Cart;
import com.spring.sweeties.models.Person;
import com.spring.sweeties.models.Sweetness;
import com.spring.sweeties.repositories.CartsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CartsService {
    private final CartsRepository basketRepository;

    @Autowired
    public CartsService(CartsRepository basketRepository) {
        this.basketRepository = basketRepository;
    }

    @Transactional
    public void saveOrder(Cart basket) {
        basketRepository.save(basket);
    }

    public List<Cart> findByPerson(Person person) {
        List<Cart> baskets = basketRepository.findByPerson(person);
        List<Sweetness> products = new ArrayList<>();

        for (Cart b : baskets) {
            products.add(b.getSweetness());
        }

        return baskets;
    }

    @Transactional
    public void delete(Person person, Sweetness product) {
        Cart basket = basketRepository.findByPersonAndSweetness(person, product);
        basketRepository.deleteById(basket.getId());
    }

    public Cart findByPersonAndSweetness(Person person, Sweetness product) {
        return basketRepository.findByPersonAndSweetness(person, product);
    }

    @Transactional
    public void update(int id, Cart updatedBasket) {
        updatedBasket.setId(id);
        basketRepository.save(updatedBasket);
    }

    @Transactional
    public void delete(int id) {
        basketRepository.deleteById(id);
    }
}
