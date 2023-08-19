package org.example;

import lombok.Getter;
import org.example.entity.car.Car;
import org.example.entity.car.CarBrand;
import org.example.entity.car.Image;
import org.example.entity.car.Model;
import org.example.entity.deals.Address;
import org.example.entity.deals.ProductCard;
import org.example.entity.deals.RentalRecord;
import org.example.entity.user.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class TestDataProvider {

    private final List<CarBrand> carBrands;
    private final List<Model> models;
    private final List<Image> images;
    private final List<Car> cars;

    private final List<User> users;
    private final List<Lessor> lessors;
    private final List<Renter> renters;

    private final List<Address> addresses;
    private final List<ProductCard> productCards;
    private final List<RentalRecord> rentalRecords;

    public TestDataProvider() {

        // users
        users = List.of(
                new User(1L, "testUsername1", "password", "test 1 name", UserType.LESSOR, LocalDate.now(), Set.of(Role.USER), true, null, null),
                new User(2L, "testUsername2", "password", "test 2 name", UserType.LESSOR, LocalDate.now(), Set.of(Role.USER), true, null, null),
                new User(3L, "testUsername3", "password", "test 3 name", UserType.RENTER, LocalDate.now(), Set.of(Role.USER), true, null, null),
                new User(4L, "testUsername4", "password", "test 4 name", UserType.RENTER, LocalDate.now(), Set.of(Role.USER), true, null, null)
        );

        lessors = List.of(
                new Lessor(1L, users.get(0), new ArrayList<>()),
                new Lessor(2L, users.get(1), new ArrayList<>())
        );

        renters = List.of(
                new Renter(1L, users.get(2), new ArrayList<>()),
                new Renter(2L, users.get(3), new ArrayList<>())
        );

        // cars
        carBrands = List.of(
                new CarBrand(1, "test 1 car-brand"),
                new CarBrand(2, "test 2 car-brand"),
                new CarBrand(3, "test 3 car-brand")
        );

        models = List.of(
                new Model(1L, carBrands.get(1), "test 1 model", 2001, 100, new ArrayList<>()),
                new Model(2L, carBrands.get(2), "test 2 model", 2002, 200, new ArrayList<>()),
                new Model(3L, carBrands.get(0), "test 3 model", 2003, 300, new ArrayList<>())
        );

        cars = List.of(
                new Car(1L, models.get(1), lessors.get(0), new ArrayList<>(), new ArrayList<>()),
                new Car(2L, models.get(2), lessors.get(1), new ArrayList<>(), new ArrayList<>()),
                new Car(3L, models.get(0), lessors.get(1), new ArrayList<>(), new ArrayList<>())
        );

        images = List.of(
                new Image(1L, "test 1 name", "test 1 orig.filename", "test 1 content-type", 100L, new byte[10], cars.get(0)),
                new Image(2L, "test 2 name", "test 2 orig.filename", "test 2 content-type", 200L, new byte[20], cars.get(0)),
                new Image(3L, "test 3 name", "test 3 orig.filename", "test 3 content-type", 300L, new byte[30], cars.get(0)),
                new Image(4L, "test 4 name", "test 4 orig.filename", "test 4 content-type", 400L, new byte[40], cars.get(1)),
                new Image(4L, "test 4 name", "test 4 orig.filename", "test 4 content-type", 400L, new byte[40], cars.get(1))
        );

        // deals
        addresses = List.of(
                new Address(1L, "US", "Los angeles", "Avenue", "5"),
                new Address(2L, "Fr", "Lille", "Vencile", "12")
        );

        productCards = List.of(
                new ProductCard(1L, cars.get(1), BigDecimal.valueOf(100), addresses.get(1), LocalDateTime.now(), null),
                new ProductCard(2L, cars.get(2), BigDecimal.valueOf(200), addresses.get(0), LocalDateTime.now(), null),
                new ProductCard(3L, cars.get(0), BigDecimal.valueOf(300), addresses.get(0), LocalDateTime.now(), null)
        );

        rentalRecords = List.of(
                new RentalRecord(1L, productCards.get(0), renters.get(0), LocalDateTime.now(), LocalDateTime.now().plusSeconds(1L)),
                new RentalRecord(1L, productCards.get(1), renters.get(1), LocalDateTime.now(), null)
        );

        configureAllTies();
    }

    private void configureAllTies() {

        for (var lessor : lessors) {
            lessor.getUser().setLessor(lessor);
        }

        for (var renter : renters) {
            renter.getUser().setRenter(renter);
        }

        for (var model : models) {
            model.setCars(cars.stream()
                    .filter(c -> c.getModel().equals(model))
                    .collect(Collectors.toList()));
        }

        for (var model : models) {
            model.setCars(cars.stream()
                    .filter(c -> c.getModel().equals(model))
                    .collect(Collectors.toList()));
        }

        for (var car : cars) {
            car.setProductCards(productCards.stream()
                    .filter(p -> p.getCar().equals(car))
                    .collect(Collectors.toList()));
        }

        for (var car : cars) {
            car.setImages(images.stream()
                    .filter(im -> im.getCar().equals(car))
                    .collect(Collectors.toList()));
        }

        for (var lessor : lessors) {
            lessor.setCars(cars.stream()
                    .filter(c -> c.getLessor().equals(lessor))
                    .collect(Collectors.toList()));
        }

        for (var renter : renters) {
            renter.setRentalRecords(rentalRecords.stream()
                    .filter(r -> r.getRenter().equals(renter))
                    .collect(Collectors.toList()));
        }

        for (var productCard : productCards) {
            productCard.setRentalRecord(rentalRecords.stream()
                    .filter(r -> r.getProductCard().equals(productCard))
                    .findAny()
                    .orElse(null));
        }
    }
}
