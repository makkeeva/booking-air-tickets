package com.booking.configuration;

import com.booking.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/resources/**", "/public/**", "/auth/**").permitAll() //Доступ разрешён всем пользователям
                .anyRequest().authenticated().and() //Все остальные страницы требуют аутентификации
                .formLogin().loginPage("/auth/login") //Определение страницы авторизации
                .defaultSuccessUrl("/ticket/all") //Перенарпавление на главную страницу после успешного входа
                .permitAll().and()
                .logout().logoutUrl("/logout").permitAll().logoutSuccessUrl("/auth/login");
    }
}
