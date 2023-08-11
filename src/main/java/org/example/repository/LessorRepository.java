package org.example.repository;

import org.example.entity.user.Lessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LessorRepository extends JpaRepository<Lessor, Long> {

    @Query("FROM Lessor lessors " +
            "LEFT JOIN FETCH lessors.cars cars " +
            "LEFT JOIN FETCH cars.productCards productCards " +
            "LEFT JOIN FETCH productCards.rentalRecords rentalRecords " +
            "WHERE lessors.id = :id")
    Optional<Lessor> findById(Long id);
}
