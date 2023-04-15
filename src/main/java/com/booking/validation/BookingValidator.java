package com.booking.validation;

import com.booking.entity.domain.Booking;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@PropertySource("classpath:validation.properties")
@Component
public class BookingValidator implements Validator {
    private final Environment env;

    @Value("${length.min}")
    private int minLength;

    @Value("${length.max}")
    private int maxLength;

    public BookingValidator(Environment env) {
        this.env = env;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Booking.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Booking booking = (Booking) target;
        //проверка поля name на соответствие размеру
        if (booking.getName().length() < minLength || booking.getName().length() > maxLength)
            errors.rejectValue("name", "name.not_valid", "Неверный формат имени");
        //проверка поля surname на соответствие размеру
        if (booking.getSurname().length() < minLength || booking.getSurname().length() > maxLength)
            errors.rejectValue("surname", "surname.not_valid", "Неверный формат фамилии");
        //проверка поля passport на соответствие регулярному выражению
        if (checkForRegexp(booking.getPassport(), env.getProperty("passport.id")) ||
                booking.getPassport().length() != 9)
            errors.rejectValue("passport", "passport.not_valid", "Неверный формат номера паспорта");
    }
//проверяет, соответствует ли строка (input) регулярному выражению (regexp), и возвращает true
    private boolean checkForRegexp(String input, String regexp) {
        return !input.matches(regexp);
    }
}
