package org.example.repository;

import org.example.entity.user.Lessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface LessorRepository extends JpaRepository<Lessor, Long> {

    @Query("FROM Lessor lessors " +
            "LEFT JOIN FETCH lessors.cars cars " +
            "LEFT JOIN FETCH cars.productCards productCards " +
            "WHERE lessors.id = :id")
    @NonNull
    Optional<Lessor> findById(@Param("id") @NonNull Long id);
}
