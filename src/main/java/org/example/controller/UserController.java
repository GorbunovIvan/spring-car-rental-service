package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.entity.user.User;
import org.example.entity.user.UserType;
import org.example.service.UserService;
import org.example.utils.UsersUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final UsersUtil usersUtil;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        model.addAttribute("currentUser", getCurrentUser());
        return "users/users";
    }

    @GetMapping("/my-page")
    public String myPage() {
        var user = getCurrentUser();
        if (user == null) {
            return "redirect:/auth/login";
        }
        return "redirect:/users/" + user.getId();
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        var user = userService.getByIdEagerly(id);
        if (user == null) {
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        model.addAttribute("currentUser", getCurrentUser());
        return "users/user";
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {

        var currentUser = getCurrentUser();
        if (currentUser == null || !currentUser.getId().equals(id)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", currentUser);
        return "users/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id, Model model,
                         @ModelAttribute @Valid User user, BindingResult bindingResult,
                         @RequestParam(name = "userType", required = false) UserType userType) {

        var currentUser = getCurrentUser();
        if (currentUser == null || !currentUser.getId().equals(id)) {
            return "redirect:/auth/login";
        }

        var userPersisted = userService.getById(id);
        if (userPersisted == null) {
            throw new RuntimeException("User with id '" + id + "' does not exist");
        }

        if (userType== null) {
            bindingResult.rejectValue("type", "type is not chosen");
        }
        user.setType(userType);

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "users/edit";
        }

        userPersisted.setName(user.getName());
        userPersisted.setUsername(user.getUsername());
        userPersisted.setType(user.getType());

        if (!user.getPassword().equals(userPersisted.getPassword())
            && !passwordEncoder.matches(user.getPassword(), userPersisted.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userService.update(id, userPersisted);

        return "redirect:/users/" + id;
    }

    private User getCurrentUser() {
        return usersUtil.getCurrentUser();
    }
}
