package org.example.repository;

import org.example.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    @Query("FROM User users " +
            "LEFT JOIN FETCH users.roles roles " +
            "LEFT JOIN FETCH users.lessor lessors " +
            "LEFT JOIN FETCH users.renter renters " +
            "LEFT JOIN FETCH lessors.cars lessors_cars " +
            "WHERE users.id = :id")
    @NonNull
    Optional<User> findById(@Param("id") @Nullable Long id);
}
