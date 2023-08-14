package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.entity.user.User;
import org.example.entity.user.UserType;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("users", userService.getAll());
        return "users/users";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        return "users/user";
    }

    @GetMapping("/register")
    public String createForm(Model model) {
        model.addAttribute("user", new User());
        return "users/registration";
    }

    @PostMapping("/register")
    public String create(Model model,
                         @ModelAttribute @Valid User user, BindingResult bindingResult,
                         @RequestParam(name = "userType", required = false) UserType userType) {

        if (userType== null) {
            bindingResult.rejectValue("type", "type is not chosen");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "users/registration";
        }

        user.setType(userType);

        userService.create(user);

        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String updateForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        return "users/edit";
    }

    @PatchMapping("/{id}")
    public String update(@PathVariable Long id, Model model,
                         @ModelAttribute @Valid User user, BindingResult bindingResult) {

        var userPersisted = userService.getById(id);
        if (userPersisted == null) {
            throw new RuntimeException("User with id '" + id + "' does not exist");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "users/edit";
        }

        userPersisted.setName(user.getName());
        userPersisted.setUsername(user.getUsername());

        userService.update(id, userPersisted);

        return "redirect:/users/" + id;
    }
}
