package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.user.User;
import org.example.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService extends BasicService<User, Long> {

    private final UserRepository userRepository;

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }
}
