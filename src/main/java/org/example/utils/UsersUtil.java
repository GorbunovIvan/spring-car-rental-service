package org.example.utils;

import lombok.RequiredArgsConstructor;
import org.example.entity.user.User;
import org.example.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersUtil {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user;
        } else if (principal instanceof UserDetails user) {
            return userRepository.findByUsernameEagerly(user.getUsername())
                    .orElse(null);
        }

        return null;
    }
}
