package com.booking;

import com.booking.entity.domain.Booking;
import com.booking.entity.domain.Ticket;
import com.booking.entity.domain.User;
import com.booking.exception.NoSuchDataException;
import com.booking.repository.BookingRepository;
import com.booking.repository.TicketRepository;
import com.booking.service.ticket.TicketServiceImpl;
import com.booking.validation.BookingValidator;
import com.booking.validation.TicketValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingValidator bookingValidator;
    private TicketValidator ticketValidator;
    @InjectMocks
    private TicketServiceImpl ticketService;

    private final static User USER = User.builder().username("test").password("test").build();
    private final static Long NONEXISTENT_TICKET_ID = 1L;

    @Test
    public void returnTickets() {
        when(ticketRepository.findAll())
                .thenReturn(Arrays.asList(Ticket.builder().departurePoint("Минск").build()));
        List<Ticket> tickets = ticketService.findAllTickets();

        assertEquals(1, tickets.size());
        assertEquals("Минск", tickets.get(0).getDeparturePoint());
    }

    @Test
    public void returnSingleBooking() {
        when(bookingRepository.findAll())
                .thenReturn(Arrays.asList(Booking.builder().address("Минск").name("Иванов Иван").build()));
        List<Booking> bookings = ticketService.findAllBooking();

        assertEquals(1, bookings.size());
        assertEquals("Минск", bookings.get(0).getAddress());
        assertEquals("Иванов Иван", bookings.get(0).getName());
    }

    @Test
    public void returnBookings() {
        when(bookingRepository.findAll())
                .thenReturn(Arrays.asList(
                        Booking.builder().address("Минск").name("Иванов Иван").build(),
                        Booking.builder().address("Витебск").name("Иванов Сергей").build()));
        List<Booking> bookings = ticketService.findAllBooking();

        assertEquals(2, bookings.size());
        assertEquals("Минск", bookings.get(0).getAddress());
        assertEquals("Иванов Иван", bookings.get(0).getName());
        assertEquals("Витебск", bookings.get(1).getAddress());
        assertEquals("Иванов Сергей", bookings.get(1).getName());
    }

    @Test
    public void returnSingleBooking_WhenUser() {
        when(bookingRepository.findAllByUser(USER))
                .thenReturn(Arrays.asList(Booking.builder().address("Минск").name("Иванов Иван").build()));
        List<Booking> bookings = ticketService.findAllBookingByUser(USER);

        assertEquals(1, bookings.size());
        assertEquals("Минск", bookings.get(0).getAddress());
        assertEquals("Иванов Иван", bookings.get(0).getName());
    }

    @Test
    public void returnBookings_WhenUser() {
        when(bookingRepository.findAllByUser(USER))
                .thenReturn(Arrays.asList(Booking.builder().address("Минск").name("Иванов Иван").build(),
                        Booking.builder().address("Витебск").name("Иванов Сергей").build()));
        List<Booking> bookings = ticketService.findAllBookingByUser(USER);

        assertEquals(2, bookings.size());
        assertEquals("Минск", bookings.get(0).getAddress());
        assertEquals("Иванов Иван", bookings.get(0).getName());
        assertEquals("Витебск", bookings.get(1).getAddress());
        assertEquals("Иванов Сергей", bookings.get(1).getName());
    }

    @Test
    public void throwNoSuchDataException_WhenTicketIdNonexistent() {
        assertThrows(NoSuchDataException.class, () -> ticketService.removeTicket(NONEXISTENT_TICKET_ID));
    }

    @Test
    public void throwIllegalStateException_WhenBookingDataNotValid() {
        assertThrows(IllegalStateException.class, () -> ticketService.addNewBooking(new Booking(), new User(), NONEXISTENT_TICKET_ID));
    }

    @Test
    public void throwNullPointerException_WhenTicketDataNotValid() {
        assertThrows(NullPointerException.class, () -> ticketService.addNewTicket(new Ticket()));
    }
}
