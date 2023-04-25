package com.booking.controller;

import com.booking.entity.domain.Profile;
import com.booking.entity.domain.User;
import com.booking.exception.NoSuchDataException;
import com.booking.exception.OperationExecutionException;
import com.booking.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private UserService userService;

    @GetMapping("/all")
    public String showUsersPage(Model model) {
        model.addAttribute("users", userService.findAllProfiles());
        return "users/users_list";
    }

    @PostMapping("/activate")
    public String activateUser(@RequestParam("username") String username, @RequestParam("is_active") boolean isActive, Model model) {
        try {
            if(!userService.changeActiveStatus(username, isActive))
                throw new OperationExecutionException("Ошибка активации");
        } catch (OperationExecutionException | NoSuchDataException e) {
            model.addAttribute("users", userService.findAllProfiles());
            model.addAttribute("error", e.getMessage());
            return "/users/users_list";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка активации");
            model.addAttribute("users", userService.findAllProfiles());
            return "/users/users_list";
        }
        return "redirect:/user/all";
    }

    @GetMapping("/profile")
    public String showUserProfilePage(Model model) {
        User user = userService.find(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new NoSuchDataException("Пользователь не найден"));
        model.addAttribute("profile", userService.findProfileByUser(user));
        return "users/profile";
    }

    @PostMapping("/profile/edit")
    public String editProfileData(Profile profile, Model model) {
        try {
            User user = userService.find(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(() -> new NoSuchDataException("Пользователь не найден"));
            userService.editProfileData(profile, user);
        } catch (Exception e){
            model.addAttribute("error", e.getMessage());
        }
        User user = userService.find(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new NoSuchDataException("Пользователь не найден"));
        model.addAttribute("profile", userService.findProfileByUser(user));
        return "users/profile";
    }

    @PostMapping("/profile/edit_password")
    public String editUserPass(@RequestParam("oldPass") String oldPass,
                               @RequestParam("newPass") String newPass, Model model) {
        User user = userService.find(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new NoSuchDataException("Пользователь не найден"));
        try {
            userService.changeAuthData(user, oldPass, newPass);
        } catch (Exception e){
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("profile", userService.findProfileByUser(user));
        return "users/profile";
    }
}
