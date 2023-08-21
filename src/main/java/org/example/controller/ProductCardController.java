package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.entity.deals.ProductCard;
import org.example.entity.deals.RentalRecord;
import org.example.entity.user.User;
import org.example.entity.user.UserType;
import org.example.service.CarService;
import org.example.service.ProductCardService;
import org.example.utils.UsersUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
        model.addAttribute("currentUser", getCurrentUser());
        return "productCards/productCards";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        var productCard = productCardService.getById(id);
        if (productCard == null) {
            return "redirect:/product-cards";
        }
        model.addAttribute("productCard", productCard);
        model.addAttribute("currentUser", getCurrentUser());
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

        if (productCard.getCar() != null) {
            if (productCard.getCar().isInUsage()) {
                bindingResult.rejectValue("car", "car is in usage already");
            }
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("productCard", productCard);
            model.addAttribute("cars", carService.getAllNamesByLessor(currentUser.getLessor()));
            return "productCards/new";
        }

        productCardService.create(productCard);

        return "redirect:/users/" + currentUser.getId();
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to update product-card");
        }

        var productCard = getProductCardOrThrowException(id);
        if (productCard.isLeased()) {
            throw new RuntimeException("This car is leased already");
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

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to update product-card");
        }

        var productCardPersisted = getProductCardOrThrowException(id);
        if (productCardPersisted.isLeased()) {
            throw new RuntimeException("This car is leased already");
        }
        if (!productCardPersisted.getLessor().equals(currentUser.getLessor())) {
            throw new RuntimeException("You are trying to update NOT your product-card");
        }

        if (productCard.getCar() != null) {
            if (productCard.getCar().isInUsage()) {
                bindingResult.rejectValue("car", "car is in usage already");
            }
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

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to delete product-card");
        }

        var productCard = getProductCardOrThrowException(id);
        if (productCard.isLeased()) {
            throw new RuntimeException("This car is leased already");
        }
        if (!productCard.getLessor().equals(currentUser.getLessor())) {
            throw new RuntimeException("You are trying to delete NOT your product-card");
        }

        productCardService.deleteById(id);

        return "redirect:/users/" + currentUser.getId();
    }

    @PostMapping("/{id}/rent")
    public String rentCar(@PathVariable Long id) {

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.RENTER) {
            throw new RuntimeException("You are not renter to rent a car");
        }

        var productCard = getProductCardOrThrowException(id);
        if (productCard.isLeased()) {
            throw new RuntimeException("This car is leased already");
        }

        var rentalRecord = new RentalRecord();
        rentalRecord.setProductCard(productCard);
        rentalRecord.setRenter(currentUser.getRenter());

        productCard.setRentalRecord(rentalRecord);
        productCardService.update(id, productCard);

        return "redirect:/product-cards/" + id;
    }

    @PatchMapping("/{id}/return")
    public String returnCar(@PathVariable Long id) {

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.RENTER) {
            throw new RuntimeException("You are not renter");
        }

        var productCard = getProductCardOrThrowException(id);
        if (!productCard.isLeased()) {
            throw new RuntimeException("This car was not leased yet to return it");
        }

        var rentalRecord = productCard.getRentalRecord();
        if (!rentalRecord.getRenter().equals(currentUser.getRenter())) {
            throw new RuntimeException("You did not rent this car");
        }

        if (rentalRecord.isReturned()) {
            throw new RuntimeException("This car was already returned at " + rentalRecord.getReturnedAt());
        }

        rentalRecord.setReturnedAt(LocalDateTime.now());
        productCardService.update(id, productCard);

        return "redirect:/product-cards/" + id;
    }

    private ProductCard getProductCardOrThrowException(Long id) {
        var productCard = productCardService.getById(id);
        if (productCard == null) {
            throw new RuntimeException("Product-card with id '" + id + "' does not exist");
        }
        return productCard;
    }

    private User getCurrentUser() {
        return usersUtil.getCurrentUser();
    }
}
