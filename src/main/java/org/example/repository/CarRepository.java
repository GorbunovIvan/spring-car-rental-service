package org.example.repository;

import org.example.entity.car.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("FROM Car cars " +
            "LEFT JOIN FETCH cars.productCards productCards " +
            "LEFT JOIN FETCH productCards.rentalRecords rentalRecords " +
            "WHERE cars.id = :id")
    Optional<Car> findById(Long id);
}
