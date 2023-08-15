package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.entity.car.Car;
import org.example.entity.deals.ProductCard;
import org.example.entity.deals.RentalRecord;
import org.example.entity.user.User;
import org.example.entity.user.UserType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> findByIdEagerly(Long id) {

        User user = entityManager.createQuery("FROM User users " +
                        "LEFT JOIN FETCH users.lessor lessors " +
                        "LEFT JOIN FETCH users.renter renters " +
                        "LEFT JOIN FETCH lessors.cars lessors_cars " +
                        "WHERE users.id = :id", User.class)
                .setParameter("id", id)
                .getResultList().stream()
                .findAny()
                .orElse(null);

        if (user == null) {
            return Optional.empty();
        }

        // Other tables are fetched with different queries (one for lessor and one to renter)
        // because in one general query it shows error - "Could not generate fetch : org.example.entity.user.User(users) -> lessor"
        // I don't know why this error occurs
        if (user.getType() == UserType.LESSOR) {

            var lessor = user.getLessor();

            Map<Car, List<ProductCard>> productCards = entityManager.createQuery(
                    "FROM ProductCard productCards " +
                           "WHERE productCards.car.lessor = :lessor", ProductCard.class)
                    .setParameter("lessor", lessor)
                    .getResultList().stream()
                    .collect(Collectors.groupingBy(ProductCard::getCar, Collectors.toList()));

            for (var car : lessor.getCars()) {
                if (productCards.containsKey(car)) {
                    car.setProductCards(productCards.get(car));
                } else {
                    car.setProductCards(new ArrayList<>());
                }
            }

        } else if (user.getType() == UserType.RENTER) {

            var renter = user.getRenter();

            List<RentalRecord> rentalRecords = entityManager.createQuery(
                            "FROM RentalRecord rentalRecord " +
                                   "WHERE rentalRecord.renter = :renter", RentalRecord.class)
                    .setParameter("renter", renter)
                    .getResultList();

            renter.setRentalRecords(rentalRecords);
        }

        return Optional.of(user);
    }
}
