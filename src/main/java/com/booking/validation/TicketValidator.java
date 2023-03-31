package com.booking.validation;

import com.booking.entity.domain.Ticket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.sql.Timestamp;
//для чтения свойств из файла validation.properties.
@PropertySource("classpath:validation.properties")
@Component
public class TicketValidator implements Validator {
    // создание env, которая будет использоваться для получения свойств, которые были объявлены в файле
    private final Environment env;
// для получения значения из файла validation.properties.
    @Value("${length.min}")
    private int minLength;

    @Value("${length.max}")
    private int maxLength;

    public TicketValidator(Environment env) {
        this.env = env;
    }
    //метод supports интерфейса Validator, определяет, поддерживает ли данный валидатор валидацию объектов определенного класса.
    @Override
    public boolean supports(Class<?> clazz) {
        return Ticket.class.equals(clazz);
    }
    //Метод validate проверяет поля объекта на корректность и в случае некорректности выдает ошибку
    @Override
    public void validate(Object target, Errors errors) {
        Ticket ticket = (Ticket) target;
        if (ticket.getArrivalPoint().length() < minLength || ticket.getArrivalPoint().length() > maxLength)
            errors.rejectValue("arrival_point", "arrival_point.not_valid", "Неверный формат пункта отправления");

        if (ticket.getDeparturePoint().length() < minLength || ticket.getDeparturePoint().length() > maxLength)
            errors.rejectValue("departure_point", "departure_point.not_valid", "Неверный формат пункта прибытия");

        if (ticket.getAirline().length() < minLength || ticket.getAirline().length() > maxLength)
            errors.rejectValue("airline", "airline.not_valid", "Неверный формат перевозчика");

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (ticket.getDepartureTime().before(timestamp) || ticket.getArrivalTime().before(timestamp))
            errors.rejectValue("departureTime", "date.not_valid", "Время отправления\\прибытия не могут иметь такое значение");

        if (ticket.getCost() < 0)
            errors.rejectValue("cost", "cost.not_valid", "Стоимость не может быть отрицательной");

    }
}
