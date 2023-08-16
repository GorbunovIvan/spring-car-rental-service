package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.car.Car;
import org.example.entity.car.CarBrand;
import org.example.entity.user.User;
import org.example.entity.user.UserType;
import org.example.service.CarService;
import org.example.utils.UsersUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Objects;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final UsersUtil usersUtil;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("cars", carService.getAll());
        model.addAttribute("currentUser", getCurrentUser());
        return "cars/cars";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        var car = carService.getById(id);
        if (car == null) {
            return "redirect:/cars";
        }
        model.addAttribute("car", car);
        model.addAttribute("currentUser", getCurrentUser());
        return "cars/car";
    }

    @GetMapping("/new")
    public String createForm(Model model) {

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to add new car");
        }

        model.addAttribute("car", new Car());
        model.addAttribute("brands", carService.getAllCarBrandNames());
        return "cars/new";
    }

    @PostMapping
    public String create(Model model,
                         @RequestParam(value = "brand", required = false) CarBrand carBrand,
                         @RequestParam(value = "model", required = false) String modelName,
                         @RequestParam(value = "horsePowers", required = false) Integer horsePowers,
                         @RequestParam(value = "year", required = false) Integer year) {

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to add new car");
        }

        BindingResult bindingResult = getBindingResultOfFields(carBrand, modelName, horsePowers, year);

        Car car = createCarWithFields(carBrand, modelName, horsePowers, year);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            model.addAttribute("car", car);
            model.addAttribute("brands", carService.getAllCarBrandNames());
            return "cars/new";
        }

        car.setLessor(currentUser.getLessor());
        carService.create(car);

        return "redirect:/cars";
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {

        var car = carService.getById(id);
        if (car == null) {
            throw new RuntimeException("Car with id '" + id + "' does not exist");
        }

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to edit car");
        }
        if (!car.getLessor().equals(currentUser.getLessor())) {
            throw new RuntimeException("You are trying to update NOT your car");
        }

        model.addAttribute("car", car);
        model.addAttribute("brands", carService.getAllCarBrandNames());
        return "cars/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id, Model model,
                         @RequestParam(value = "brand", required = false) CarBrand carBrand,
                         @RequestParam(value = "model", required = false) String modelName,
                         @RequestParam(value = "horsePowers", required = false) Integer horsePowers,
                         @RequestParam(value = "year", required = false) Integer year) {

        var carPersisted = carService.getById(id);
        if (carPersisted == null) {
            throw new RuntimeException("Car with id '" + id + "' does not exist");
        }

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to update car");
        }
        if (!carPersisted.getLessor().equals(currentUser.getLessor())) {
            throw new RuntimeException("You are trying to update NOT your car");
        }

        BindingResult bindingResult = getBindingResultOfFields(carBrand, modelName, horsePowers, year);

        Car car = createCarWithFields(carBrand, modelName, horsePowers, year);
        car.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult);
            model.addAttribute("car", car);
            model.addAttribute("brands", carService.getAllCarBrandNames());
            return "cars/edit";
        }

        carPersisted.setModel(car.getModel());

        carService.update(id, carPersisted);

        return "redirect:/cars/" + id;
    }

    private BindingResult getBindingResultOfFields(CarBrand carBrand, String modelName, Integer horsePowers, Integer year) {

        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "car");

        if (carBrand == null) {
            bindingResult.rejectValue("brand", "brand is empty");
        }

        if (Objects.requireNonNullElse(modelName, "").isBlank()) {
            bindingResult.rejectValue("model", "model is empty");
        }

        if (Objects.requireNonNullElse(horsePowers, 0).equals(0)) {
            bindingResult.rejectValue("horsePowers", "'horse-powers' field is empty");
        }

        if (Objects.requireNonNullElse(year, 0).equals(0)) {
            bindingResult.rejectValue("year", "year is empty");
        }

        return bindingResult;
    }

    private Car createCarWithFields(CarBrand carBrand, String modelName, Integer horsePowers, Integer year) {

        var carModel = new org.example.entity.car.Model();
        carModel.setCarBrand(carBrand);
        carModel.setModelName(modelName);
        carModel.setHorsePowers(horsePowers);
        carModel.setYear(year);

        Car car = new Car();
        car.setModel(carModel);

        return car;
    }

    private User getCurrentUser() {
        return usersUtil.getCurrentUser();
    }
}
