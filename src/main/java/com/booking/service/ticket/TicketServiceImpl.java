package com.booking.service.ticket;

import com.booking.entity.domain.Booking;
import com.booking.entity.domain.Ticket;
import com.booking.entity.domain.User;
import com.booking.exception.NoSuchDataException;
import com.booking.exception.OperationExecutionException;
import com.booking.repository.BookingRepository;
import com.booking.repository.TicketRepository;
import com.booking.utils.ValidationUtil;
import com.booking.validation.BookingValidator;
import com.booking.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
//класс управляет бизнес-логикой приложения
@Service
//каждая операция к базе данных оборачивается в  транзакцию, кот предотвращает появление ошибок
@Transactional
public class TicketServiceImpl implements TicketService{

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TicketValidator ticketValidator;

    @Autowired
    private BookingValidator bookingValidator;

    @Override
    public boolean removeTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new NoSuchDataException("Данного билета не существует"));
//если билет удаляется, то и удаляется вся бронь
        bookingRepository.findAllByTicket(ticket).forEach(booking -> bookingRepository.delete(booking));
        //проверка удалился ли
        if (ticketRepository.removeById(ticket.getId()) < 1)
            throw new OperationExecutionException("Билет не удалён");
        return true;
    }

    @Override
    public Booking addNewBooking(Booking booking, User user, Long ticket_id) {
        ValidationUtil.validate(booking, bookingValidator);
        Ticket ticket = ticketRepository.findById(ticket_id)
                .orElseThrow(()->new NoSuchDataException("Данного билета не существует"));
        booking.setUser(user);
        booking.setTicket(ticket);
        return bookingRepository.save(booking);
    }
//возвращает список всех бронирований, связанных с указанным пользователем.
    @Override
    public List<Booking> findAllBookingByUser(User user) {
        return bookingRepository.findAllByUser(user);
    }
//добавляет новый билет в базу данных
    @Override
    public Ticket addNewTicket(Ticket ticket) {
        ValidationUtil.validate(ticket, ticketValidator);
        return ticketRepository.save(ticket);
    }
//возвращает список всех билетов в базе данных
    @Override
    public List<Ticket> findAllTickets() {
        return ticketRepository.findAll();
    }
}
