package com.booking.validation;

import com.booking.entity.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@PropertySource("classpath:validation.properties")
@Component
public class UserValidator implements Validator {
    private final Environment env;

    @Value("${length.min}")
    private int minLength;

    @Value("${length.max}")
    private int maxLength;

    public UserValidator(Environment env) {
        this.env = env;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (checkForRegexp(user.getUsername(), env.getProperty("latin.letters.with.numbers.and.symbols"))
                || user.getUsername().length() < minLength
                || user.getUsername().length() > maxLength)
            errors.rejectValue("username", "username.not_matches", "Неверный формат логина");

        if (checkForRegexp(user.getPassword(), env.getProperty("latin.letters.with.numbers.and.symbols"))
                || user.getPassword().length() < minLength
                || user.getPassword().length() > maxLength)
            errors.rejectValue("password", "password.not_matches", "Неверный формат пароля");
    }

    private boolean checkForRegexp(String input, String regexp) {
        return !input.matches(regexp);
    }
}
