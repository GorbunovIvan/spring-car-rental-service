package org.example.repository;

import org.example.entity.car.Car;
import org.example.entity.car.CarBrand;
import org.example.entity.car.Model;
import org.example.entity.user.Lessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("FROM Car cars " +
            "LEFT JOIN FETCH cars.productCards productCards " +
            "LEFT JOIN FETCH cars.model models " +
            "LEFT JOIN FETCH cars.lessor lessors " +
            "LEFT JOIN FETCH productCards.address productCards_addresses " +
            "LEFT JOIN FETCH productCards.rentalRecord rentalRecords " +
            "LEFT JOIN FETCH models.carBrand models_carBrand")
    @NonNull
    List<Car> findAll();

    @Query("FROM Car cars " +
            "LEFT JOIN FETCH cars.productCards productCards " +
            "LEFT JOIN FETCH cars.model models " +
            "LEFT JOIN FETCH cars.lessor lessors " +
            "LEFT JOIN FETCH productCards.address productCards_addresses " +
            "LEFT JOIN FETCH productCards.rentalRecord rentalRecords " +
            "LEFT JOIN FETCH models.carBrand models_carBrand " +
            "WHERE cars.lessor = :lessor")
    List<Car> findAllByLessor(@Param("lessor") Lessor lessor);

    @Query("FROM Car cars " +
            "LEFT JOIN FETCH cars.productCards productCards " +
            "LEFT JOIN FETCH cars.model models " +
            "LEFT JOIN FETCH cars.lessor lessors " +
            "LEFT JOIN FETCH productCards.address productCards_addresses " +
            "LEFT JOIN FETCH productCards.rentalRecord rentalRecords " +
            "LEFT JOIN FETCH models.carBrand models_carBrand " +
            "WHERE cars.model = :model")
    @NonNull
    List<Car> findAllByModel(@Param("model") Model model);

    @Query("FROM Car cars " +
            "LEFT JOIN FETCH cars.productCards productCards " +
            "LEFT JOIN FETCH cars.model models " +
            "LEFT JOIN FETCH cars.lessor lessors " +
            "LEFT JOIN FETCH productCards.address productCards_addresses " +
            "LEFT JOIN FETCH productCards.rentalRecord rentalRecords " +
            "LEFT JOIN FETCH models.carBrand models_carBrand " +
            "WHERE cars.id = :id")
    @NonNull
    Optional<Car> findById(@Param("id") @NonNull Long id);

    @Query("FROM CarBrand WHERE name = :name")
    Optional<CarBrand> findCarBrandByName(@Param("name") String name);

    @Query("FROM CarBrand brands")
    List<CarBrand> findAllCarBrands();

    @Query("FROM Model " +
            "WHERE carBrand.name = :carBrand " +
            "AND modelName = :modelName " +
            "AND year = :year")
    Optional<Model> findModelByMainFields(@Param("carBrand") String carBrand,
                                          @Param("modelName") String modelName,
                                          @Param("year") Integer year);
}
