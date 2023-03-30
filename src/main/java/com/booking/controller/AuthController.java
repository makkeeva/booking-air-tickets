package com.booking.controller;

import com.booking.entity.domain.Profile;
import com.booking.exception.ExistingDataException;
import com.booking.exception.IncorrectDataException;
import com.booking.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String showRegistrationPage(Model model) {
        model.addAttribute("profile", new Profile());
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String userRegistration(@ModelAttribute("profile") Profile profile, Model model) {
        try {
            userService.registerUser(profile);
        } catch (IncorrectDataException | ExistingDataException e) {
            model.addAttribute("error", e.getMessage());
            return "/auth/registration";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка регистрации");
            return "/auth/registration";
        }
        return "redirect:/auth/login";
    }
}
