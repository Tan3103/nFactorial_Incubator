package com.spring.sweeties.controllers;

import com.spring.sweeties.models.Person;
import com.spring.sweeties.models.Sweetness;
import com.spring.sweeties.services.OrdersService;
import com.spring.sweeties.services.PeopleService;
import com.spring.sweeties.services.SweetsService;
import jakarta.validation.Valid;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final SweetsService sweetsService;
    private final OrdersService ordersService;
    private final PeopleService peopleService;

    @Value("${file.img.uploadPath}")
    private String uploadPath;

    @Autowired
    public AdminController(SweetsService sweetsService, OrdersService ordersService, PeopleService peopleService) {
        this.sweetsService = sweetsService;
        this.ordersService = ordersService;
        this.peopleService = peopleService;
    }

    @GetMapping("/add")
    public String newPerson(@ModelAttribute("sweetness") Sweetness sweetness) {
        return "admin/add";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("sweetness") @Valid Sweetness sweetness, BindingResult bindingResult,
                         @RequestParam(name = "file") MultipartFile file) throws IOException {
        if (bindingResult.hasErrors())
            return "admin/add";

        if (Objects.equals(file.getContentType(), "image/jpeg") || Objects.equals(file.getContentType(), "image/png")) {
            try {
                String picName = DigestUtils.sha1Hex("sweetness" + sweetness.getName());

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                sweetness.setImage(picName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sweetsService.save(sweetness);

        return "redirect:/";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        sweetsService.delete(id);
        return "redirect:/";
    }

    @PatchMapping("/update/{id}")
    public String update(@ModelAttribute("sweetness") @Valid Sweetness sweetness, BindingResult bindingResult,
                         @PathVariable("id") int id,
                         @RequestParam(name = "file") MultipartFile file) throws IOException {

        if (bindingResult.hasErrors())
            return "admin/update";

        if (Objects.equals(file.getContentType(), "image/jpeg") || file.getContentType().equals("image/png")) {
            try {
                String picName = DigestUtils.sha1Hex("sweetness" + sweetness.getName());

                byte[] bytes = file.getBytes();
                Path path = Paths.get(uploadPath + picName + ".jpg");
                Files.write(path, bytes);

                sweetness.setImage(picName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sweetsService.update(id, sweetness);
        return "redirect:/";
    }

    @GetMapping("/update/{id}")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("sweetness", sweetsService.findOne(id));

        return "admin/update";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", ordersService.findAll());

        return "admin/order";
    }

    ////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/user")
    public String users(Model model) {
        model.addAttribute("people", peopleService.findAll());

        return "admin/users";
    }

    @GetMapping("/user/update/{id}")
    public String userEdit(Model model, @PathVariable("id") int id) {
        model.addAttribute("person", peopleService.findOne(id));

        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");

        model.addAttribute("roles", roles);

        return "admin/userUpdate";
    }

    @DeleteMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        peopleService.delete(id);
        return "redirect:/";
    }

    @PatchMapping("/user/update/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "admin/userUpdate";

        peopleService.update(id, person);
        return "redirect:/";
    }
}
