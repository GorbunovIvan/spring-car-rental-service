package org.example.repository;

import org.example.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("FROM User users " +
            "LEFT JOIN FETCH users.lessor lessors " +
            "LEFT JOIN FETCH users.renter rentors " +
            "LEFT JOIN FETCH lessors.cars lessors_cars " +
            "LEFT JOIN FETCH rentors.rentalRecords rentors_rentalRecords " +
            "LEFT JOIN FETCH lessors_cars.productCards lessors_productCards " +
            "LEFT JOIN FETCH rentors_rentalRecords.productCard rentors_productCards " +
            "WHERE users.id = :id")
    Optional<User> findById(Long id);
}
