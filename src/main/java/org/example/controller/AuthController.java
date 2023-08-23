package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.entity.user.Role;
import org.example.entity.user.User;
import org.example.entity.user.UserType;
import org.example.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/registration";
    }

    @PostMapping("/register")
    public String register(Model model,
                           @ModelAttribute @Valid User user, BindingResult bindingResult,
                           @RequestParam(name = "userType", required = false) UserType userType) {

        if (userType == null) {
            bindingResult.rejectValue("type", "type is not chosen");
        } else {
            user.setType(userType);
        }

        if (userService.getByUsername(user.getUsername()) != null) {
            bindingResult.rejectValue("username", "username is not free");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "auth/registration";
        }

        user.addRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);

        userService.create(user);

        return "redirect:/auth/login";
    }
}
