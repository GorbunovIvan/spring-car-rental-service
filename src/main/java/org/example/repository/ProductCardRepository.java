package org.example.repository;

import org.example.entity.deals.ProductCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductCardRepository extends JpaRepository<ProductCard, Long> {

    @Query("FROM ProductCard productCards " +
            "LEFT JOIN FETCH productCards.rentalRecords rentalRecords " +
            "WHERE productCards.id = :id")
    Optional<ProductCard> findById(Long id);
}
