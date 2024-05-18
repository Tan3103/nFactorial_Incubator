package com.spring.sweeties.controllers;

import com.spring.sweeties.models.Cart;
import com.spring.sweeties.models.Order;
import com.spring.sweeties.models.Person;
import com.spring.sweeties.services.CartsService;
import com.spring.sweeties.services.OrdersService;
import com.spring.sweeties.services.PeopleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final OrdersService ordersService;
    private final PeopleService peopleService;
    private final CartsService basketService;

    @Autowired
    public OrderController(OrdersService ordersService, PeopleService peopleService, CartsService basketService) {
        this.ordersService = ordersService;
        this.peopleService = peopleService;
        this.basketService = basketService;
    }

    @GetMapping
    public String order(@ModelAttribute("order") Order order) {
        return "user/order";
    }

    @PostMapping(value = "/new")
    public String addOrder(@ModelAttribute("order") @Valid Order order, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "user/order";

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        System.out.println("OKOKOKOKOK");

        Person person = peopleService.findByUsername(currentPrincipalName);
        List<Cart> baskets = basketService.findByPerson(person);

        order.setPerson(person);

        if (baskets != null) {
            int price = 0;
            int quantity = 0;

            for (Cart b : baskets) {
                price += b.getSweetness().getPrice() * b.getCount();
                quantity += b.getCount();

                basketService.delete(b.getId());
            }
            order.setTotalPrice(price);
            order.setTotalQuantity(quantity);
        }

        ordersService.saveOrder(order);

        return "redirect:/";
    }
}
