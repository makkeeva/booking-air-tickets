package com.booking.service.ticket;

import com.booking.entity.domain.Booking;
import com.booking.entity.domain.Ticket;
import com.booking.entity.domain.User;

import javax.mail.MessagingException;
import java.util.List;

public interface TicketService {
//возвращает список всех имеющихся в наличии билетов в системе.
    List<Ticket> findAllTickets();
    //возвращает список всех бронирований, сделанных пользователем "user".
    List<Booking> findAllBookingByUser(User user);
    //возвращает весь список броинрований
    List<Booking> findAllBooking();
    //добавляет новый билет в систему.
    Ticket addNewTicket(Ticket ticket);
    //добавляет новое бронирование билета "ticket_id" пользователем "user" в систему.
    Booking addNewBooking(Booking booking, User user, Long ticket_id);

    void sendEmailBooking(Long bookingId, String email) throws MessagingException;

    void renderBookingPdf(Long bookingId);

    boolean removeTicket(Long id);
}
