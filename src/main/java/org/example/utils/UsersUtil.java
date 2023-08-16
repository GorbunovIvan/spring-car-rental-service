package org.example.utils;

import lombok.RequiredArgsConstructor;
import org.example.entity.user.User;
import org.example.service.UserService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersUtil {

    private final UserService userService;

    public User getCurrentUser() {
//        return null;
        return userService.getByIdEagerly(4L); // lessor
//        return userService.getByIdEagerly(3L); // renter
    }
}
