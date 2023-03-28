package com.booking.service;

import com.booking.entity.domain.User;
import com.booking.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.findUserOrThrowException(username);

        return new org.springframework.security.core.userdetails.User(
                username, user.getPassword().replace("{bcrypt}", ""), user.isActive(),
                true, true, true,
                userService.getAuthoritiesByUsername(username).stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                        .collect(Collectors.toList()));
    }

    private User findUserOrThrowException(String username){
        return userService.find(username).orElseThrow(
                () -> new UsernameNotFoundException("Пользователь не найден с ником: " + username)
        );
    }
}
