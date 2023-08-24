package org.example.repository;

import org.example.entity.deals.Address;
import org.example.entity.deals.ProductCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ProductCardRepository extends JpaRepository<ProductCard, Long>, ProductCardRepositoryCustom {

    @Query("FROM ProductCard productCards " +
            "LEFT JOIN FETCH productCards.rentalRecord rentalRecords " +
            "LEFT JOIN FETCH productCards.address addresses " +
            "LEFT JOIN FETCH productCards.car cars " +
            "LEFT JOIN FETCH cars.model cars_models " +
            "LEFT JOIN FETCH cars_models.carBrand cars_models_carBrands")
    @NonNull
    List<ProductCard> findAll();

    @Query("FROM ProductCard productCards " +
            "LEFT JOIN FETCH productCards.rentalRecord rentalRecords " +
            "LEFT JOIN FETCH productCards.address addresses " +
            "LEFT JOIN FETCH productCards.car cars " +
            "LEFT JOIN FETCH cars.lessor lessors " +
            "LEFT JOIN FETCH cars.model cars_models " +
            "LEFT JOIN FETCH cars_models.carBrand cars_models_carBrands " +
            "WHERE productCards.id = :id")
    @NonNull
    Optional<ProductCard> findById(@Param("id") @NonNull Long id);

    @Query("FROM Address " +
            "WHERE country = :country " +
            "AND town = :town " +
            "AND street = :street " +
            "AND buildingNumber = :buildingNumber")
    Optional<Address> findAddressByFields(@Param("country") String country,
                                          @Param("town") String town,
                                          @Param("street") String street,
                                          @Param("buildingNumber") String buildingNumber);
}
