package com.booking.service.ticket;

import com.booking.entity.domain.Booking;
import com.booking.entity.domain.Ticket;
import com.booking.entity.domain.User;

import java.util.List;

public interface TicketService {
//возвращает список всех имеющихся в наличии билетов в системе.
    List<Ticket> findAllTickets();
    //возвращает список всех бронирований, сделанных пользователем "user".
    List<Booking> findAllBookingByUser(User user);
    //добавляет новый билет в систему.
    Ticket addNewTicket(Ticket ticket);
    //добавляет новое бронирование билета "ticket_id" пользователем "user" в систему.
    Booking addNewBooking(Booking booking, User user, Long ticket_id);
    boolean removeTicket(Long id);
}
