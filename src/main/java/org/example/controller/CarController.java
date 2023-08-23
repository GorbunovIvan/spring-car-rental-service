package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.entity.car.Car;
import org.example.entity.car.CarBrand;
import org.example.entity.car.Image;
import org.example.entity.user.User;
import org.example.entity.user.UserType;
import org.example.service.CarService;
import org.example.service.ImageService;
import org.example.utils.UsersUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;
    private final ImageService imageService;

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
        model.addAttribute("carImages", getCollectionOfImagesForCar(car));
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
        model.addAttribute("carImages", getCollectionOfImagesForCar(null, 3));
        model.addAttribute("brands", carService.getAllCarBrandNames());
        return "cars/new";
    }

    @PostMapping
    public String create(Model model,
                         @RequestParam(value = "brand", required = false) CarBrand carBrand,
                         @RequestParam(value = "model", required = false) String modelName,
                         @RequestParam(value = "horsePowers", required = false) Integer horsePowers,
                         @RequestParam(value = "year", required = false) Integer year,
                         @RequestParam(value = "image1", required = false) MultipartFile image1,
                         @RequestParam(value = "image2", required = false) MultipartFile image2,
                         @RequestParam(value = "image3", required = false) MultipartFile image3) {

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
            model.addAttribute("carImages", getCollectionOfImagesForCar(car, 3));
            model.addAttribute("brands", carService.getAllCarBrandNames());
            return "cars/new";
        }

        car.setLessor(currentUser.getLessor());
        car = carService.create(car);

        var images = filesToList(image1, image2, image3);
        imageService.addImagesToCar(car, images);

        return "redirect:/users/my-page";
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to edit car");
        }

        var car = carService.getById(id);
        if (car == null) {
            throw new RuntimeException("Car with id '" + id + "' does not exist");
        }

        if (!car.getLessor().equals(currentUser.getLessor())) {
            throw new RuntimeException("You are trying to update NOT your car");
        }

        model.addAttribute("car", car);
        model.addAttribute("carImages", getCollectionOfImagesForCar(car, 3));
        model.addAttribute("brands", carService.getAllCarBrandNames());
        return "cars/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id, Model model,
                         @RequestParam(value = "brand", required = false) CarBrand carBrand,
                         @RequestParam(value = "model", required = false) String modelName,
                         @RequestParam(value = "horsePowers", required = false) Integer horsePowers,
                         @RequestParam(value = "year", required = false) Integer year,
                         @RequestParam(value = "image1", required = false) MultipartFile image1,
                         @RequestParam(value = "image2", required = false) MultipartFile image2,
                         @RequestParam(value = "image3", required = false) MultipartFile image3) {

        var currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        if (currentUser.getType() != UserType.LESSOR) {
            throw new RuntimeException("You are not lessor to update car");
        }

        var carPersisted = carService.getById(id);
        if (carPersisted == null) {
            throw new RuntimeException("Car with id '" + id + "' does not exist");
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
            model.addAttribute("carImages", getCollectionOfImagesForCar(car, 3));
            model.addAttribute("brands", carService.getAllCarBrandNames());
            return "cars/edit";
        }

        carPersisted.setModel(car.getModel());
        carPersisted = carService.update(id, carPersisted);

        var images = filesToList(image1, image2, image3);
        imageService.addImagesToCar(carPersisted, images);

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

    private List<Image> getCollectionOfImagesForCar(Car car) {

        if (car == null) {
            return new ArrayList<>();
        }

        return imageService.getAllByCar(car);
    }

    private List<Image> getCollectionOfImagesForCar(Car car, int numberOfImages) {

        if (car == null) {
            var images = new ArrayList<Image>();
            for (int i = 0; i < numberOfImages; i++) {
                images.add(new Image());
            }
            return images;
        }

        List<Image> images = imageService.getAllByCar(car);

        while (images.size() < numberOfImages) {
            images.add(new Image());
        }

        if (images.size() > numberOfImages) {
            images = images.stream()
                        .limit(numberOfImages)
                        .toList();
        }

        return images;
    }

    private List<MultipartFile> filesToList(MultipartFile... files) {
        return Arrays.stream(files)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        return usersUtil.getCurrentUser();
    }
}
