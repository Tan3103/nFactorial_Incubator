package com.spring.sweeties.controllers;

import com.spring.sweeties.models.Cart;
import com.spring.sweeties.models.Person;
import com.spring.sweeties.models.Sweetness;
import com.spring.sweeties.services.CartsService;
import com.spring.sweeties.services.OrdersService;
import com.spring.sweeties.services.PeopleService;
import com.spring.sweeties.services.SweetsService;
import jakarta.validation.Valid;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Controller
public class MainController {

    @Value("${file.img.viewPath}")
    private String viewPath;

    @Value("${file.img.defaultPicture}")
    private String defaultPicture;

    @Value("${file.img.uploadPath}")
    private String uploadPath;

    private final SweetsService sweetsService;
    private final PeopleService peopleService;
    private final CartsService cartsService;
    private final OrdersService ordersService;

    @Autowired
    public MainController(SweetsService sweetsService, PeopleService peopleService, CartsService cartsService, OrdersService ordersService) {
        this.sweetsService = sweetsService;
        this.peopleService = peopleService;
        this.cartsService = cartsService;
        this.ordersService = ordersService;
    }

    @GetMapping(value = "/about")
    public String about() {
        return "user/aboutUs";
    }

    @GetMapping("/")
    public String index(Model m) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Person person = peopleService.findByUsername(currentPrincipalName);
        m.addAttribute("sweetnessList", sweetsService.findAll());

        if (person != null && person.getRole().equals("ROLE_ADMIN")) {
            return "admin/index.html";
        }

        return "user/index.html";
    }

    @GetMapping(value = "/sweetness/{id}")
    public String details(Model model, @PathVariable(name = "id") int id) {
        Sweetness sweetness = sweetsService.findById(id);
        model.addAttribute("sweetness", sweetness);

        return "user/details";
    }

    @PostMapping(value = "/sweetness/{id}")
    public String addBasket(@PathVariable(name = "id") int id, @RequestParam(name = "count") int count) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = peopleService.findByUsername(authentication.getName());

        Sweetness sweetness = sweetsService.findById(id);
        Cart cart = cartsService.findByPersonAndSweetness(person, sweetness);

        if (person != null && sweetness != null && cart == null) {
            Cart newCart = new Cart();
            newCart.setPerson(person);
            newCart.setSweetness(sweetness);
            newCart.setCount(count);

            cartsService.saveOrder(newCart);
            sweetness.setQuantity(sweetness.getQuantity() - count);
            sweetsService.update(sweetness.getId(), sweetness);
        } else if (person != null && sweetness != null) {
            sweetness.setQuantity(cart.getCount() + sweetness.getQuantity());
            if (count > 0) {
                cart.setCount(count);
            }

            sweetness.setQuantity(sweetness.getQuantity() - count);
            cartsService.update(cart.getId(), cart);
            sweetsService.update(sweetness.getId(), sweetness);
        }

        return "redirect:/";
    }

    @DeleteMapping("/sweetness/{id}")
    public String delete(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Person person = peopleService.findByUsername(currentPrincipalName);
        Sweetness sweetness = sweetsService.findById(id);

        cartsService.delete(person, sweetness);

        return "redirect:/basket";
    }

    @GetMapping(value = "/cart")
    public String cart(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();

        Person person = peopleService.findByUsername(currentPrincipalName);
        List<Cart> carts = cartsService.findByPerson(person);

        if (carts != null) {
            int total = 0;
            int price = 0;
            for (Cart cart : carts) {
                total += cart.getCount();
                price += cart.getSweetness().getPrice() * cart.getCount();
            }

            model.addAttribute("total", total);
            model.addAttribute("price", price);
            model.addAttribute("cartList", carts);
        }

        return "user/cart";
    }

    @GetMapping(value = "/{catalog}")
    public String getCat(Model model, @PathVariable(name = "catalog") String catalog) {
        model.addAttribute("sweetnessList", sweetsService.findByCatalog(catalog));

        return "user/index";
    }

    @GetMapping(value = "/viewPhoto/{url}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public @ResponseBody byte[] viewPicture(@PathVariable(name = "url") String url) throws IOException {
        String pictureURL = viewPath + defaultPicture;

        if (url != null && !url.equals("null") && !url.isEmpty()) {
            pictureURL = viewPath + url + ".jpg";
        }
        InputStream in;

        try {
            ClassPathResource resource = new ClassPathResource(pictureURL);
            in = resource.getInputStream();
        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource(viewPath + defaultPicture);
            in = resource.getInputStream();
            e.printStackTrace();
        }
        return IOUtils.toByteArray(in);
    }

    @PatchMapping("/profile")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @RequestParam(name = "file") MultipartFile file) {
        if (bindingResult.hasErrors()) {
            return "user/profileEdit";
        }

        if (!file.isEmpty() && (Objects.equals(file.getContentType(), "image/jpeg") || Objects.equals(file.getContentType(), "image/png"))) {
            try {
                String picName = DigestUtils.sha1Hex("img" + person.getEmail() + "ava");

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                person.setAvatar(picName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        peopleService.updatePerson(person.getId(), person);
        return "redirect:/profile";
    }

    @GetMapping(value = "/profile")
    public String myProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Person person = peopleService.findByUsername(authentication.getName());
        model.addAttribute("person", person);

        return "user/profile";
    }

    @GetMapping(value = "/profile/edit")
    public String profileEdit(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Person person = peopleService.findByUsername(authentication.getName());
        model.addAttribute("person", person);

        return "user/profileEdit";
    }

    @GetMapping(value = "/delivery")
    public String delivery(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = peopleService.findByUsername(authentication.getName());

        model.addAttribute("orders", ordersService.findByPerson(person));

        return "user/delivery";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<Sweetness> searchResults = sweetsService.findByName(query);
        model.addAttribute("sweetnessList", searchResults);
        return "user/index";
    }
}
