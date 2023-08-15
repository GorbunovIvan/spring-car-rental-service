package org.example.repository;

import org.example.entity.user.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByIdEagerly(Long id);
}
