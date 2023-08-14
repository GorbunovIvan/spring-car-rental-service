package org.example.repository;

import org.example.entity.user.Renter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface RenterRepository extends JpaRepository<Renter, Long> {

    @Query("FROM Renter renters " +
            "LEFT JOIN FETCH renters.rentalRecords rentalRecords " +
            "LEFT JOIN FETCH rentalRecords.productCard productCards " +
            "WHERE renters.id = :id")
    @NonNull
    Optional<Renter> findById(@Param("id") @Nullable Long id);
}
