package org.example.repository;

import org.example.entity.car.Car;
import org.example.entity.car.CarBrand;
import org.example.entity.car.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("FROM Car cars " +
            "LEFT JOIN FETCH cars.productCards productCards " +
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
                                          @Param("year") int year);
}
