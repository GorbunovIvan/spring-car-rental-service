package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.entity.deals.ProductCard;
import org.example.entity.user.User;
import org.example.entity.user.UserType;
import org.example.service.CarService;
import org.example.service.ProductCardService;
import org.example.utils.UsersUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/product-cards")
@RequiredArgsConstructor
public class ProductCardController {

    private final ProductCardService productCardService;
    private final CarService carService;

    private final UsersUtil usersUtil;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("productCards", productCardService.getAllAvailable());
        return "productCards/productCards";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        model.addAttribute("productCard", productCardService.getById(id));
        model.addAttribute("currentUser",getCurrentUser());
        return "productCards/productCard";
    }

    @GetMapping("/new")
    public String createForm(Model model) {

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to add new product-card");
        }

        model.addAttribute("productCard", new ProductCard());
        model.addAttribute("cars", carService.getAllNamesByLessor(currentUser.getLessor()));
        return "productCards/new";
    }

    @PostMapping
    public String create(Model model,
                         @ModelAttribute @Valid ProductCard productCard, BindingResult bindingResult) {

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to add new product-card");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("productCard", productCard);
            model.addAttribute("cars", carService.getAllNamesByLessor(currentUser.getLessor()));
            return "productCards/new";
        }

        productCardService.create(productCard);

        return "redirect:/product-cards";
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {

        var productCard = productCardService.getById(id);
        if (productCard == null) {
            throw new RuntimeException("Product-card with id '" + id + "' does not exist");
        }

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to update product-card");
        }
        if (!productCard.getLessor().equals(currentUser.getLessor())) {
            throw new RuntimeException("You are trying to update NOT your product-card");
        }

        model.addAttribute("productCard", productCard);
        model.addAttribute("cars", carService.getAllNamesByLessor(currentUser.getLessor()));
        return "productCards/edit";
    }

    @PatchMapping("/{id}")
    public String update(Model model, @PathVariable Long id,
                         @ModelAttribute @Valid ProductCard productCard, BindingResult bindingResult) {

        var productCardPersisted = productCardService.getById(id);
        if (productCardPersisted == null) {
            throw new RuntimeException("Product-card with id '" + id + "' does not exist");
        }

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to update product-card");
        }
        if (!productCardPersisted.getLessor().equals(currentUser.getLessor())) {
            throw new RuntimeException("You are trying to update NOT your product-card");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("productCard", productCard);
            model.addAttribute("cars", carService.getAllNamesByLessor(currentUser.getLessor()));
            return "productCards/edit";
        }

        productCardPersisted.setCar(productCard.getCar());
        productCardPersisted.setAddress(productCard.getAddress());
        productCardPersisted.setPrice(productCard.getPrice());

        productCardService.update(id, productCardPersisted);

        return "redirect:/product-cards/" + id;
    }

    private User getCurrentUser() {
        return usersUtil.getCurrentUser();
    }
}
